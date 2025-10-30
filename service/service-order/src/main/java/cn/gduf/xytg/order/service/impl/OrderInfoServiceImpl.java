package cn.gduf.xytg.order.service.impl;

import cn.gduf.xytg.activity.client.ActivityFeignClient;
import cn.gduf.xytg.cart.client.CartFeignClient;
import cn.gduf.xytg.client.product.ProductFeignClient;
import cn.gduf.xytg.client.user.UserFeignClient;
import cn.gduf.xytg.common.auth.AuthContextHolder;
import cn.gduf.xytg.common.constant.MqConst;
import cn.gduf.xytg.common.constant.RedisConst;
import cn.gduf.xytg.common.exception.XytgException;
import cn.gduf.xytg.common.result.ResultCodeEnum;
import cn.gduf.xytg.common.service.RabbitService;
import cn.gduf.xytg.common.utils.DateUtil;
import cn.gduf.xytg.enums.*;
import cn.gduf.xytg.model.activity.ActivityRule;
import cn.gduf.xytg.model.activity.CouponInfo;
import cn.gduf.xytg.model.order.CartInfo;
import cn.gduf.xytg.model.order.OrderInfo;
import cn.gduf.xytg.model.order.OrderItem;
import cn.gduf.xytg.order.mapper.OrderInfoMapper;
import cn.gduf.xytg.order.mapper.OrderItemMapper;
import cn.gduf.xytg.order.service.OrderInfoService;
import cn.gduf.xytg.vo.order.CartInfoVo;
import cn.gduf.xytg.vo.order.OrderConfirmVo;
import cn.gduf.xytg.vo.order.OrderSubmitVo;
import cn.gduf.xytg.vo.order.OrderUserQueryVo;
import cn.gduf.xytg.vo.product.SkuStockLockVo;
import cn.gduf.xytg.vo.user.LeaderAddressVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 订单信息服务实现类
 * @date 2025/10/29 10:32
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 确认订单
     *
     * @return OrderConfirmVo 订单确认页面所需数据对象
     */
    @Override
    public OrderConfirmVo confirmOrder() {
        // 获取当前登录用户ID
        Long userId = AuthContextHolder.getUserId();

        // 使用线程安全的Map替代HashMap
        ConcurrentHashMap<String, Object> result = new ConcurrentHashMap<>();

        // 并行获取用户收货地址和购物车选中商品信息
        CompletableFuture<Void> leaderAddressFuture =
                CompletableFuture.runAsync(() -> {
                    try {
                        result.put("leaderAddressVo", userFeignClient.getUserAddressByUserId(userId));
                    } catch (Exception e) {
                        throw new XytgException(ResultCodeEnum.CREATE_ORDER_FAIL); // 抛出异常以便被allOf感知
                    }
                }, threadPoolExecutor);

        CompletableFuture<Void> cartInfoFuture =
                CompletableFuture.runAsync(() -> {
                    try {
                        result.put("cartInfoList", cartFeignClient.getCartCheckedList(userId));
                    } catch (Exception e) {
                        throw new XytgException(ResultCodeEnum.CREATE_ORDER_FAIL); // 抛出异常以便被allOf感知
                    }
                }, threadPoolExecutor);

        // 设置超时时间防止无限等待
        try {
            CompletableFuture.allOf(leaderAddressFuture,
                            cartInfoFuture)
                    .get(5, TimeUnit.SECONDS); // 最多等待5秒
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new XytgException(ResultCodeEnum.CREATE_ORDER_FAIL);
        }

        // 类型安全地取出结果
        Object addrObj = result.get("leaderAddressVo");
        Object cartObj = result.get("cartInfoList");

        if (!(addrObj instanceof LeaderAddressVo) || !(cartObj instanceof List)) {
            throw new XytgException(ResultCodeEnum.CREATE_ORDER_FAIL);
        }

        LeaderAddressVo leaderAddressVo = (LeaderAddressVo) addrObj;
        @SuppressWarnings("unchecked")
        List<CartInfo> cartInfoList = (List<CartInfo>) cartObj;

        // 订单号生成
        String orderNo = System.currentTimeMillis() + "";
        redisTemplate.opsForValue().set(RedisConst.ORDER_REPEAT + orderNo, orderNo,
                24, TimeUnit.HOURS);

        // 存储订单号到Redis防止重复提交
        try {
            redisTemplate.opsForValue().set(
                    RedisConst.ORDER_REPEAT + orderNo,
                    orderNo,
                    24,
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            // 清理已生成的订单号或其他补偿措施
            throw new XytgException(ResultCodeEnum.CREATE_ORDER_FAIL); // 抛出异常以便被allOf感知
        }

        // 查询购物车商品的活动和优惠券信息
        OrderConfirmVo orderConfirmVo;
        try {
            orderConfirmVo = activityFeignClient.findCartActivityAndCoupon(userId, cartInfoList);
        } catch (Exception e) {
            throw new XytgException(ResultCodeEnum.CREATE_ORDER_FAIL); // 抛出异常以便被allOf感知
        }

        // 设置收货地址和订单号
        orderConfirmVo.setLeaderAddressVo(leaderAddressVo);
        orderConfirmVo.setOrderNo(orderNo);

        return orderConfirmVo;
    }

    /**
     * 生成订单
     *
     * @param orderParamVo 订单参数对象，包含用户提交的订单信息
     * @return 订单ID，表示成功创建的订单唯一标识
     */
    @Override
    public Long submitOrder(OrderSubmitVo orderParamVo) {
        // 获取当前登录用户的ID，并设置到订单参数中
        Long userId = AuthContextHolder.getUserId();
        orderParamVo.setUserId(userId);

        // 校验订单编号是否为空，若为空则抛出非法请求异常
        String orderNo = orderParamVo.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {
            throw new XytgException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        // 构造 Lua 脚本用于防止重复提交：
        // 只有当 Redis 中 key 对应的值与参数一致时才删除该 key
        String script =
                "if(redis.call('get', KEYS[1]) == ARGV[1]) " +
                        "then" +
                        " return redis.call('del', KEYS[1]) " +
                        "else" +
                        " return 0 end";

        // 执行 Redis 脚本校验是否为重复提交，若不是则继续处理
        Boolean flag = (Boolean) redisTemplate.execute(
                new DefaultRedisScript(script, Boolean.class),
                Arrays.asList(RedisConst.ORDER_REPEAT + orderNo), orderNo
        );

        if (!flag) {
            throw new XytgException(ResultCodeEnum.REPEAT_SUBMIT);
        }

        // 查询用户选中的购物车商品列表
        List<CartInfo> cartInfoList =
                cartFeignClient.getCartCheckedList(userId);

        // 筛选出普通类型的商品（非特殊类型）
        List<CartInfo> commonSkuList = cartInfoList.stream()
                .filter(cartInfo -> cartInfo.getSkuType() == SkuType.COMMON.getCode())
                .collect(Collectors.toList());

        // 若存在普通商品，则进行库存锁定操作
        if (!CollectionUtils.isEmpty(commonSkuList)) {
            // 将购物车信息转换为库存锁定所需的 VO 列表
            List<SkuStockLockVo> commonStockLockVoList =
                    commonSkuList.stream().map(cartInfo -> {
                        SkuStockLockVo skuStockLockVo = new SkuStockLockVo();
                        skuStockLockVo.setSkuId(cartInfo.getSkuId());
                        skuStockLockVo.setSkuNum(cartInfo.getSkuNum());
                        return skuStockLockVo;
                    }).collect(Collectors.toList());

            // 调用商品服务检查并锁定库存
            Boolean isLockSuccess =
                    productFeignClient.checkAndLock(commonStockLockVoList,
                            orderNo);
            if (!isLockSuccess) {
                throw new XytgException(ResultCodeEnum.ORDER_STOCK_FALL);
            }
        }

        // 保存订单数据并获取订单ID
        Long orderId = this.saveOrder(orderParamVo, cartInfoList);

        // 发送消息通知清除用户购物车数据
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER_DIRECT,
                MqConst.ROUTING_DELETE_CART, orderParamVo.getUserId());

        return orderId;
    }

    /**
     * 保存订单数据
     *
     * @param orderParamVo 订单参数对象，包含用户提交的订单信息（如收货人、团长ID等）
     * @param cartInfoList 购物车商品列表，用于构建订单项及计算金额
     * @return 订单ID，表示成功保存的订单唯一标识
     */
    @Transactional(rollbackFor = {Exception.class})
    public Long saveOrder(OrderSubmitVo orderParamVo, List<CartInfo> cartInfoList) {
        // 参数校验：订单参数和购物车列表不能为空
        if (orderParamVo == null || CollectionUtils.isEmpty(cartInfoList)) {
            throw new XytgException(ResultCodeEnum.DATA_ERROR);
        }

        // 获取当前登录用户的ID，并通过Feign调用获取该用户的团长地址信息
        Long userId = AuthContextHolder.getUserId();
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        if (leaderAddressVo == null) {
            throw new XytgException(ResultCodeEnum.DATA_ERROR);
        }

        // 计算参与营销活动的商品分摊金额
        Map<String, BigDecimal> activitySplitAmount = this.computeActivitySplitAmount(cartInfoList);

        // 根据优惠券ID计算各商品分摊的优惠金额
        Map<String, BigDecimal> couponInfoSplitAmount = this.computeCouponInfoSplitAmount(cartInfoList, orderParamVo.getCouponId());

        // 构建订单项列表
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartInfo cartInfo : cartInfoList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(null);
            orderItem.setCategoryId(cartInfo.getCategoryId());
            if (cartInfo.getSkuType() == SkuType.COMMON.getCode()) {
                orderItem.setSkuType(SkuType.COMMON);
            } else {
                orderItem.setSkuType(SkuType.SECKILL);
            }
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuPrice(cartInfo.getCartPrice());
            orderItem.setImgUrl(cartInfo.getImgUrl());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setLeaderId(orderParamVo.getLeaderId());

            // 设置营销活动分摊金额
            BigDecimal activityAmount =
                    activitySplitAmount.get("activity:" + orderItem.getSkuId());
            if (activityAmount == null) {
                activityAmount = new BigDecimal(0);
            }
            orderItem.setSplitActivityAmount(activityAmount);

            // 设置优惠券分摊金额
            BigDecimal couponAmount = couponInfoSplitAmount.get("coupon:" + orderItem.getSkuId());
            if (couponAmount == null) {
                couponAmount = new BigDecimal(0);
            }
            orderItem.setSplitCouponAmount(couponAmount);

            // 计算单品总价与实际支付价
            BigDecimal skuTotalAmount = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum()));
            BigDecimal splitTotalAmount = skuTotalAmount.subtract(activityAmount).subtract(couponAmount);
            orderItem.setSplitTotalAmount(splitTotalAmount);
            orderItemList.add(orderItem);
        }

        // 创建订单主信息对象并填充基础字段
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId); // 用户ID
        orderInfo.setOrderNo(orderParamVo.getOrderNo()); // 订单编号
        orderInfo.setOrderStatus(OrderStatus.UNPAID); // 初始状态为未支付
        orderInfo.setLeaderId(orderParamVo.getLeaderId()); // 团长ID
        orderInfo.setLeaderName(leaderAddressVo.getLeaderName()); // 团长姓名
        orderInfo.setLeaderPhone(leaderAddressVo.getLeaderPhone());
        orderInfo.setTakeName(leaderAddressVo.getTakeName());
        orderInfo.setReceiverName(orderParamVo.getReceiverName());
        orderInfo.setReceiverPhone(orderParamVo.getReceiverPhone());
        orderInfo.setReceiverProvince(leaderAddressVo.getProvince());
        orderInfo.setReceiverCity(leaderAddressVo.getCity());
        orderInfo.setReceiverDistrict(leaderAddressVo.getDistrict());
        orderInfo.setReceiverAddress(leaderAddressVo.getDetailAddress());
        orderInfo.setWareId(cartInfoList.get(0).getWareId()); // 使用第一个商品的仓库ID作为订单仓库ID
        orderInfo.setProcessStatus(ProcessStatus.UNPAID); // 处理状态初始化为未支付

        // 计算订单原始总金额、活动减免金额、优惠券减免金额以及最终应付金额
        BigDecimal originalTotalAmount = this.computeTotalAmount(cartInfoList);
        BigDecimal activityAmount = activitySplitAmount.get("activity:total");
        if (null == activityAmount) {
            activityAmount = new BigDecimal(0);
        }
        BigDecimal couponAmount = couponInfoSplitAmount.get("coupon:total");
        if (null == couponAmount) {
            couponAmount = new BigDecimal(0);
        }
        BigDecimal totalAmount = originalTotalAmount.subtract(activityAmount).subtract(couponAmount);

        orderInfo.setOriginalTotalAmount(originalTotalAmount);
        orderInfo.setActivityAmount(activityAmount);
        orderInfo.setCouponAmount(couponAmount);
        orderInfo.setTotalAmount(totalAmount);

        // 计算团长佣金（目前固定为0）
        BigDecimal profitRate = new BigDecimal(0); // 可从配置服务中读取
        BigDecimal commissionAmount = orderInfo.getTotalAmount().multiply(profitRate);
        orderInfo.setCommissionAmount(commissionAmount);

        // 插入订单基本信息到数据库
        baseMapper.insert(orderInfo);

        // 批量插入订单明细项
        orderItemList.forEach(orderItem -> {
            orderItem.setOrderId(orderInfo.getId());
            orderItemMapper.insert(orderItem);
        });

        // 若使用了优惠券，则更新其使用状态
        if (orderInfo.getCouponId() != null) {
            activityFeignClient.updateCouponInfoUseStatus(orderInfo.getCouponId(),
                    userId, orderInfo.getId());
        }

        // 下单成功后，在Redis中记录用户购买商品的数量统计（以SKU维度累加）
        String orderSkuKey = RedisConst.ORDER_SKU_MAP + orderParamVo.getUserId();
        BoundHashOperations<String, String, Integer> hashOperations =
                redisTemplate.boundHashOps(orderSkuKey);
        cartInfoList.forEach(cartInfo -> {
            if (hashOperations.hasKey(cartInfo.getSkuId().toString())) {
                Integer orderSkuNum = hashOperations.get(cartInfo.getSkuId().toString()) + cartInfo.getSkuNum();
                hashOperations.put(cartInfo.getSkuId().toString(), orderSkuNum);
            }
        });

        redisTemplate.expire(orderSkuKey,
                DateUtil.getCurrentExpireTimes(),
                TimeUnit.SECONDS);

        // 返回生成的订单ID
        return orderInfo.getId();
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情对象
     */
    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);

        List<OrderItem> orderItemList = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderInfo.getId())
        );

        orderInfo.setOrderItemList(orderItemList);

        return orderInfo;
    }

    /**
     * 根据订单编号查询订单信息
     *
     * @param orderNo 订单编号
     * @return 订单信息对象，如果未找到则返回null
     */
    @Override
    public OrderInfo getOrderInfoByOrderNo(String orderNo) {
        OrderInfo orderInfo = baseMapper.selectOne(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getOrderNo, orderNo)
        );
        return orderInfo;
    }

    /**
     * 处理订单支付逻辑
     * 包括校验订单状态、更新订单状态以及发送扣减库存消息
     *
     * @param orderNo 订单编号
     */
    @Override
    public void orderPay(String orderNo) {
        // 查询订单状态是否已经修改完成了支付状态
        OrderInfo orderInfo = this.getOrderInfoByOrderNo(orderNo);
        if (orderInfo == null || orderInfo.getOrderStatus() != OrderStatus.UNPAID) {
            return;
        }
        // 更新状态
        this.updateOrderStatus(orderInfo.getId());

        // 扣减库存
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER_DIRECT,
                MqConst.ROUTING_MINUS_STOCK,
                orderNo);
    }

    /**
     * 分页查询用户订单信息，并封装订单项及订单状态名称
     *
     * @param pageParam        分页参数
     * @param orderUserQueryVo 用户订单查询条件（包括用户ID和订单状态）
     * @return 分页结果，包含订单列表及其详细信息
     */
    @Override
    public IPage<OrderInfo> getOrderInfoByUserIdPage(Page<OrderInfo> pageParam,
                                                     OrderUserQueryVo orderUserQueryVo) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, orderUserQueryVo.getUserId());
        wrapper.eq(OrderInfo::getOrderStatus, orderUserQueryVo.getOrderStatus());
        IPage<OrderInfo> pageModel = baseMapper.selectPage(pageParam, wrapper);

        // 获取每个订单，把每个订单里面订单项查询封装
        List<OrderInfo> orderInfoList = pageModel.getRecords();
        for (OrderInfo orderInfo : orderInfoList) {
            // 根据订单id查询里面所有订单项列表
            List<OrderItem> orderItemList = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, orderInfo.getId())
            );
            // 把订单项集合封装到每个订单里面
            orderInfo.setOrderItemList(orderItemList);
            // 封装订单状态名称
            orderInfo.getParam().put("orderStatusName", orderInfo.getOrderStatus().getComment());
        }
        return pageModel;
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     */
    @Override
    public void cancelOrder(String orderNo) {
        // 获取订单信息
        OrderInfo orderInfo = this.getOrderInfoByOrderNo(orderNo);
        // 验证订单是否存在且状态为取消状态
        if (orderInfo == null || orderInfo.getOrderStatus() != OrderStatus.CANCEL) {
            return;
        }
        // 更新订单状态
        this.updateOrderStatus(orderInfo.getId());

        // 发送消息到MQ，回滚库存
        rabbitService.sendMessage(MqConst.EXCHANGE_CANCEL_ORDER_DIRECT,
                MqConst.ROUTING_ROLLBACK_STOCK,
                orderNo);
    }


    /**
     * 更新订单状态为待发货状态
     *
     * @param id 订单主键ID
     */
    private void updateOrderStatus(Long id) {
        OrderInfo orderInfo = baseMapper.selectById(id);
        orderInfo.setOrderStatus(OrderStatus.WAITING_DELEVER);
        orderInfo.setProcessStatus(ProcessStatus.WAITING_DELEVER);
        baseMapper.updateById(orderInfo);
    }

    /**
     * 计算购物车商品总价
     *
     * @param cartInfoList 购物车商品列表
     * @return 商品总价
     */
    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal(0);
        for (CartInfo cartInfo : cartInfoList) {
            BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            total = total.add(itemTotal);
        }
        return total;
    }

    /**
     * 计算促销活动分摊金额
     *
     * @param cartInfoParamList 购物车商品列表
     * @return 每个SKU对应的活动优惠分摊金额映射表，key格式为"activity:{skuId}"，同时包含总优惠金额"activity:total"
     */
    private Map<String, BigDecimal> computeActivitySplitAmount(List<CartInfo> cartInfoParamList) {
        Map<String, BigDecimal> activitySplitAmountMap = new HashMap<>();

        // 促销活动相关信息
        List<CartInfoVo> cartInfoVoList = activityFeignClient.findCartActivityList(cartInfoParamList);

        // 活动总金额
        BigDecimal activityReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(cartInfoVoList)) {
            for (CartInfoVo cartInfoVo : cartInfoVoList) {
                ActivityRule activityRule = cartInfoVo.getActivityRule();
                List<CartInfo> cartInfoList = cartInfoVo.getCartInfoList();
                if (null != activityRule) {
                    // 优惠金额，按比例分摊
                    BigDecimal reduceAmount = activityRule.getReduceAmount();
                    activityReduceAmount = activityReduceAmount.add(reduceAmount);
                    if (cartInfoList.size() == 1) {
                        activitySplitAmountMap.put("activity:" + cartInfoList.get(0).getSkuId(), reduceAmount);
                    } else {
                        // 总金额
                        BigDecimal originalTotalAmount = new BigDecimal(0);
                        for (CartInfo cartInfo : cartInfoList) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                        }
                        // 记录除最后一项是所有分摊金额，最后一项=总的 - skuPartReduceAmount
                        BigDecimal skuPartReduceAmount = new BigDecimal(0);
                        if (activityRule.getActivityType() == ActivityType.FULL_REDUCTION) {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                                    // sku分摊金额
                                    BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        } else {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));

                                    // sku分摊金额
                                    BigDecimal skuDiscountTotalAmount = skuTotalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                                    BigDecimal skuReduceAmount = skuTotalAmount.subtract(skuDiscountTotalAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        }
                    }
                }
            }
        }
        activitySplitAmountMap.put("activity:total", activityReduceAmount);
        return activitySplitAmountMap;
    }

    /**
     * 计算优惠券分摊金额
     *
     * @param cartInfoList 购物车商品列表
     * @param couponId     优惠券ID
     * @return 每个SKU对应的优惠券分摊金额映射表，key格式为"coupon:{skuId}"，同时包含总优惠金额"coupon:total"
     */
    private Map<String, BigDecimal> computeCouponInfoSplitAmount(List<CartInfo> cartInfoList, Long couponId) {
        Map<String, BigDecimal> couponInfoSplitAmountMap = new HashMap<>();

        if (null == couponId) return couponInfoSplitAmountMap;
        CouponInfo couponInfo = activityFeignClient.findRangeSkuIdList(cartInfoList, couponId);

        if (null != couponInfo) {
            // sku对应的订单明细
            Map<Long, CartInfo> skuIdToCartInfoMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                skuIdToCartInfoMap.put(cartInfo.getSkuId(), cartInfo);
            }
            // 优惠券对应的skuId列表
            List<Long> skuIdList = couponInfo.getSkuIdList();
            if (CollectionUtils.isEmpty(skuIdList)) {
                return couponInfoSplitAmountMap;
            }
            // 优惠券优化总金额
            BigDecimal reduceAmount = couponInfo.getAmount();
            if (skuIdList.size() == 1) {
                // sku的优化金额
                couponInfoSplitAmountMap.put("coupon:" + skuIdToCartInfoMap.get(skuIdList.get(0)).getSkuId(), reduceAmount);
            } else {
                // 总金额
                BigDecimal originalTotalAmount = new BigDecimal(0);
                for (Long skuId : skuIdList) {
                    CartInfo cartInfo = skuIdToCartInfoMap.get(skuId);
                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                    originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                }
                // 记录除最后一项是所有分摊金额，最后一项=总的 - skuPartReduceAmount
                BigDecimal skuPartReduceAmount = new BigDecimal(0);
                if (couponInfo.getCouponType() == CouponType.CASH || couponInfo.getCouponType() == CouponType.FULL_REDUCTION) {
                    for (int i = 0, len = skuIdList.size(); i < len; i++) {
                        CartInfo cartInfo = skuIdToCartInfoMap.get(skuIdList.get(i));
                        if (i < len - 1) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            // sku分摊金额
                            BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);

                            skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                        } else {
                            BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);
                        }
                    }
                }
            }
            couponInfoSplitAmountMap.put("coupon:total", couponInfo.getAmount());
        }
        return couponInfoSplitAmountMap;
    }

}

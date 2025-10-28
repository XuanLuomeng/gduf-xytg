package cn.gduf.xytg.activity.service.impl;

import cn.gduf.xytg.activity.mapper.ActivityInfoMapper;
import cn.gduf.xytg.activity.mapper.ActivityRuleMapper;
import cn.gduf.xytg.activity.mapper.ActivitySkuMapper;
import cn.gduf.xytg.activity.service.ActivityInfoService;
import cn.gduf.xytg.activity.service.CouponInfoService;
import cn.gduf.xytg.client.product.ProductFeignClient;
import cn.gduf.xytg.enums.ActivityType;
import cn.gduf.xytg.model.activity.CouponInfo;
import cn.gduf.xytg.model.order.CartInfo;
import cn.gduf.xytg.vo.order.CartInfoVo;
import cn.gduf.xytg.vo.order.OrderConfirmVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.activity.ActivityInfo;
import cn.gduf.xytg.model.activity.ActivityRule;
import cn.gduf.xytg.model.activity.ActivitySku;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.vo.activity.ActivityRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 活动信息服务实现类
 * @date 2025/10/23 20:39
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {
    @Autowired
    private ActivityRuleMapper activityRuleMapper;

    @Autowired
    private ActivitySkuMapper activitySkuMapper;

    @Autowired
    private CouponInfoService couponInfoService;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 分页查询活动信息
     *
     * @param pageParam 分页参数，包含分页大小和当前页码等信息
     * @return 返回分页结果，包含活动信息列表和分页统计信息
     */
    @Override
    public Page<ActivityInfo> selectPageActivityInfo(Page<ActivityInfo> pageParam) {
        // 执行分页查询，获取活动信息分页数据
        Page<ActivityInfo> activityInfoPage = baseMapper.selectPage(pageParam, null);

        List<ActivityInfo> records = activityInfoPage.getRecords();

        // 遍历活动信息列表，设置活动类型描述字符串
        records.stream().forEach(item -> {
            item.setActivityTypeString(item.getActivityType().getComment());
        });

        return activityInfoPage;
    }

    /**
     * 查询活动规则列表
     *
     * @param id 活动id
     * @return 返回活动规则列表
     */
    @Override
    public Map<String, Object> findActivityRuleList(Long id) {
        Map<String, Object> result = new HashMap<>();
        // 根据活动id查询，查询规则列表 activity_rule表
        LambdaQueryWrapper<ActivityRule> wrapperActivityRule = new LambdaQueryWrapper<>();
        wrapperActivityRule.eq(ActivityRule::getActivityId, id);
        List<ActivityRule> activityRuleList = activityRuleMapper.selectList(wrapperActivityRule);
        result.put("activityRuleList", activityRuleList);

        // 根据活动id查询，查询使用规则商品skuid列表 activity_sku表
        List<ActivitySku> activitySkuList = activitySkuMapper.selectList(
                new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, id)
        );

        //获取所有skuId
        List<Long> skuIdList =
                activitySkuList.stream().map(ActivitySku::getSkuId).collect(Collectors.toList());

        // 通过远程调用 service-product模块接口，根据 skuid列表 得到商品信息
        List<SkuInfo> skuInfoList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(skuIdList)) {
            skuInfoList = productFeignClient.findSkuInfoList(skuIdList);
        }
        result.put("skuInfoList", skuInfoList);

        return result;
    }

    /**
     * 保存活动规则
     *
     * @param activityRuleVo 活动信息
     * @return 保存成功返回true，否则返回false
     */
    @Override
    @Transactional
    public boolean saveActivityRule(ActivityRuleVo activityRuleVo) {
        // 根据活动id删除之前规则数据
        Long activityId = activityRuleVo.getActivityId();

        // 删除原有的ActivityRule数据
        int deleteRule = activityRuleMapper.delete(
                new LambdaQueryWrapper<ActivityRule>()
                        .eq(ActivityRule::getActivityId, activityId)
        );

        // 删除原有的ActivitySku数据
        int deleteSku = activitySkuMapper.delete(
                new LambdaQueryWrapper<ActivitySku>()
                        .eq(ActivitySku::getActivityId, activityId)
        );

        // 获取规则列表数据
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        ActivityInfo activityInfo = baseMapper.selectById(activityId);
        activityRuleList.stream().forEach(activityRule -> {
            activityRule.setActivityId(activityId);
            activityRule.setActivityType(activityInfo.getActivityType());
            activityRuleMapper.insert(activityRule);
        });

        // 获取规则范围数据
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        activitySkuList.stream().forEach(activitySku -> {
            activitySku.setActivityId(activityId);
            activitySkuMapper.insert(activitySku);
        });

        return deleteRule > 0 && deleteSku > 0;
    }


    /**
     * 根据关键词查询商品信息
     *
     * @param keyword 关键词
     * @return 返回商品信息列表
     */
    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        // 根据关键字查询sku匹配内容列表
        List<SkuInfo> skuInfoList = productFeignClient
                .getSkuInfoByKeyword(keyword);

        // 如果查询结果为空，直接返回空列表
        if (!CollectionUtils.isEmpty(skuInfoList)) {
            return skuInfoList;
        }

        // 提取所有SKU的ID列表
        List<Long> skuIdList = skuInfoList.stream()
                .map(SkuInfo::getId)
                .collect(Collectors.toList());

        // 查询数据库中实际存在的SKU ID列表
        List<Long> existSkuIdList = baseMapper.selectSkuIdListExist(skuIdList);

        // 过滤出数据库中实际存在的SKU信息列表
        List<SkuInfo> findSkuInfoList = skuInfoList.stream()
                .filter(skuInfo -> existSkuIdList.contains(skuInfo.getId()))
                .collect(Collectors.toList());

        return findSkuInfoList;
    }

    /**
     * 查询活动信息
     *
     * @param skuIdList 商品ID列表
     * @return 返回活动信息，key为商品ID，value为该商品对应的活动规则描述列表
     */
    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> result = new HashMap<>();

        // 遍历商品ID列表，查询每个商品对应的活动规则
        skuIdList.forEach(skuId -> {
            List<ActivityRule> activityRuleList =
                    baseMapper.findActivityRule(skuId);

            // 如果查询到活动规则，则提取规则描述信息
            if (!CollectionUtils.isEmpty(activityRuleList)) {
                List<String> ruleList = new ArrayList<>();

                activityRuleList.stream().forEach(activityRule -> {
                    ruleList.add(this.getRuleDesc(activityRule));
                });

                result.put(skuId, ruleList);
            }
        });

        return result;
    }

    /**
     * 查询活动规则和优惠券信息
     *
     * @param skuId  商品ID
     * @param userId 用户ID
     * @return 返回活动规则和优惠券信息
     */
    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        // 查询商品相关的活动规则列表
        List<ActivityRule> activityRuleList = this.findActivityRuleBySkuId(skuId);

        // 查询用户可用的优惠券信息列表
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId, userId);

        // 构造返回结果，包含活动规则和优惠券信息
        Map<String, Object> result = new HashMap<>();
        result.put("couponInfoList", couponInfoList);
        result.put("activityRuleList", activityRuleList);

        return result;
    }

    /**
     * 根据skuId获取活动规则列表
     *
     * @param skuId 商品ID
     * @return 返回活动规则列表
     */
    @Override
    public List<ActivityRule> findActivityRuleBySkuId(Long skuId) {
        // 查询商品对应的活动规则
        List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);

        // 为每个活动规则设置描述信息
        activityRuleList.forEach(activityRule -> {
            activityRule.setRuleDesc(this.getRuleDesc(activityRule));
        });

        return activityRuleList;
    }

    /**
     * 获取购物车里满足条件优惠卷和活动的信息
     * 该方法用于在用户确认订单页面展示购物车中商品的活动优惠和优惠券信息，并计算各种金额
     *
     * @param cartInfoList 购物车数据列表，包含用户选购的所有商品信息
     * @param userId       用户ID，用于查询用户可用的优惠券
     * @return OrderConfirmVo 订单确认页面所需的数据对象，包含活动信息、优惠券信息和各种金额计算结果
     */
    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList,
                                                    Long userId) {
        // 调用findCartActivityList方法获取购物车中商品的活动分组信息
        // 将购物车中的商品按照可参与的活动进行分组，并计算每组的最优活动规则
        List<CartInfoVo> cartInfoVoList = this.findCartActivityList(cartInfoList);

        // 计算所有活动中获得的总优惠金额
        // 筛选出有活动规则的购物车分组，提取其优惠金额并求和
        BigDecimal activityReduceAmount = cartInfoVoList.stream()
                .filter(cartInfoVo -> cartInfoVo.getActivityRule() != null)  // 过滤出有活动规则的分组
                .map(cartInfoVo -> cartInfoVo.getActivityRule().getReduceAmount()) // 提取每个分组的优惠金额
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 累加所有优惠金额

        // 调用优惠券服务，查询购物车中可用的优惠券信息
        List<CouponInfo> couponInfoList =
                couponInfoService.findCartCouponInfo(cartInfoList, userId);

        // 计算优惠券的总优惠金额，默认为0
        BigDecimal couponReduceAmount = new BigDecimal(0);
        // 如果存在可用优惠券，则计算最优优惠券的优惠金额
        if(!CollectionUtils.isEmpty(couponInfoList)){
            couponReduceAmount = couponInfoList.stream()
                    .filter(couponInfo -> couponInfo.getIsOptimal().intValue() == 1) // 筛选标记为最优的优惠券
                    .map(couponInfo -> couponInfo.getAmount()) // 提取优惠券金额
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // 累加优惠券金额
        }

        // 计算购物车中选中商品的原始总金额（未扣除任何优惠）
        BigDecimal originalTotalAmount = cartInfoList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1) // 筛选被选中的商品
                .map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()))) // 计算每个商品的总价
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 累加得到原始总金额

        // 计算最终应付总金额 = 原始总金额 - 活动优惠金额 - 优惠券优惠金额
        BigDecimal totalAmount =
                originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);

        // 构造订单确认页面数据传输对象
        OrderConfirmVo orderTradeVo = new OrderConfirmVo();
        orderTradeVo.setCarInfoVoList(cartInfoVoList);        // 设置购物车活动分组信息
        orderTradeVo.setActivityReduceAmount(activityReduceAmount); // 设置活动总优惠金额
        orderTradeVo.setCouponInfoList(couponInfoList);       // 设置可用优惠券列表
        orderTradeVo.setCouponReduceAmount(couponReduceAmount);     // 设置优惠券总优惠金额
        orderTradeVo.setOriginalTotalAmount(originalTotalAmount);   // 设置原始总金额
        orderTradeVo.setTotalAmount(totalAmount);             // 设置最终应付金额
        return orderTradeVo;
    }


    /**
     * 获取购物车对应规则数据，用于计算购物车中商品的活动优惠信息
     *
     * @param cartInfoList 购物车数据列表，包含用户选购的商品信息
     * @return 购物车对应规则数据列表，每个元素包含一组可参与同一活动的商品及其适用的活动规则
     */
    @Override
    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        // 创建返回结果列表
        List<CartInfoVo> cartInfoVoList = new ArrayList<>();

        // 提取购物车中所有商品的SKU ID列表
        List<Long> skuIdList =
                cartInfoList.stream()
                        .map(CartInfo::getSkuId)
                        .collect(Collectors.toList());

        // 查询这些SKU对应的所有活动商品关联记录
        List<ActivitySku> activitySkuList = baseMapper.selectCartActivity(skuIdList);

        // 构建活动ID到SKU ID集合的映射关系，用于分组同一活动下的商品
        Map<Long, Set<Long>> activityIdToSkuIdListMap = activitySkuList.stream()
                .collect(
                        Collectors.groupingBy(
                                ActivitySku::getActivityId
                                , Collectors.mapping(
                                        ActivitySku::getSkuId,
                                        Collectors.toSet()
                                )
                        )
                );

        // 构建活动ID到活动规则列表的映射关系
        Map<Long, List<ActivityRule>> activityIdToActivityRuleListMap = new HashMap<>();

        // 提取所有涉及的活动ID集合
        Set<Long> activityIdSet = activitySkuList.stream().map(ActivitySku::getActivityId)
                .collect(Collectors.toSet());

        // 如果存在活动ID，则查询对应的活动规则列表
        if (!CollectionUtils.isEmpty(activityIdSet)) {
            // 构造查询条件，按金额条件和数量条件降序排列
            LambdaQueryWrapper<ActivityRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(ActivityRule::getConditionAmount, ActivityRule::getConditionNum);
            wrapper.in(ActivityRule::getActivityId, activityIdSet);

            // 查询活动规则列表
            List<ActivityRule> activityRuleList = activityRuleMapper.selectList(wrapper);

            // 构建活动ID到活动规则列表的映射
            activityIdToActivityRuleListMap = activityRuleList.stream().collect(
                    Collectors.groupingBy(activityRule -> activityRule.getActivityId())
            );
        }

        // 记录已处理的活动商品SKU ID集合
        Set<Long> activitySkuIdSet = new HashSet<>();

        // 如果存在活动商品分组映射
        if (!CollectionUtils.isEmpty(activityIdToSkuIdListMap)) {
            // 遍历每个活动分组
            Iterator<Map.Entry<Long, Set<Long>>> iterator = activityIdToSkuIdListMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iterator.next();
                Long activityId = entry.getKey();
                Set<Long> currentActivitySkuIdSet = entry.getValue();

                // 筛选出当前活动涉及的购物车商品
                List<CartInfo> currentActivityCartInfoList = cartInfoList
                        .stream().filter(cartInfo ->
                                currentActivitySkuIdSet.contains(cartInfo.getSkuId())
                        ).collect(Collectors.toList());

                // 计算当前活动商品的总金额
                BigDecimal activityTotalAmount =
                        this.computeTotalAmount(currentActivityCartInfoList);

                // 计算当前活动商品的总数量
                int activityTotalNum = this.computeCartNum(currentActivityCartInfoList);

                // 获取当前活动的规则列表
                List<ActivityRule> currentActivityRuleList =
                        activityIdToActivityRuleListMap.get(activityId);

                // 获取活动类型（满减或折扣）
                ActivityType activityType = currentActivityRuleList
                        .get(0).getActivityType();

                ActivityRule activityRule = null;
                // 根据活动类型计算最优活动规则
                if (activityType == ActivityType.FULL_REDUCTION) {
                    // 满减活动计算最优规则
                    activityRule = this.computeFullReduction(activityTotalAmount,
                            currentActivityRuleList);
                } else {
                    // 折扣活动计算最优规则
                    activityRule = this.computeFullDiscount(activityTotalNum,
                            activityTotalAmount,
                            currentActivityRuleList);
                }

                // 构造购物车信息VO对象
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(activityRule);
                cartInfoVo.setCartInfoList(currentActivityCartInfoList);
                cartInfoVoList.add(cartInfoVo);

                // 将已处理的商品SKU ID加入集合
                activitySkuIdSet.addAll(currentActivitySkuIdSet);
            }
        }

        // 移除已处理的SKU ID，剩余的是无活动的商品
        skuIdList.removeAll(activitySkuIdSet);

        // 处理无活动的商品
        if (!CollectionUtils.isEmpty(skuIdList)) {
            // 构建SKU ID到购物车信息的映射
            Map<Long, CartInfo> skuIdCartInfoMap = cartInfoList.stream()
                    .collect(Collectors.toMap(CartInfo::getSkuId, CartInfo -> CartInfo)
                    );

            // 为每个无活动商品创建独立的CartInfoVo对象
            for (Long skuId : skuIdList) {
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(null); // 无活动规则

                List<CartInfo> cartInfos = new ArrayList<>();

                cartInfos.add(skuIdCartInfoMap.get(skuId));
                cartInfoVo.setCartInfoList(cartInfos);

                cartInfoVoList.add(cartInfoVo);
            }
        }

        return cartInfoVoList;
    }

    /**
     * 计算最优的折扣活动规则
     * 根据购物车中商品的总数量和总金额，从活动规则列表中找出最适合的折扣优惠规则
     *
     * @param totalNum         购物车中参与活动商品的总数量
     * @param totalAmount      购物车中参与活动商品的总金额
     * @param activityRuleList 活动规则列表，已按优惠力度从大到小排序
     * @return 返回最优的活动规则，包含优惠金额和规则描述等信息
     */
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        // 遍历活动规则列表，寻找满足条件的最优折扣规则
        // 该活动规则列表数据已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            // 如果订单项购买个数大于等于折扣活动所需件数，则该规则可用
            if (totalNum.intValue() >= activityRule.getConditionNum()) {
                // 计算折扣后的总金额 = 原价 * 折扣率
                BigDecimal skuDiscountTotalAmount = totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                // 计算优惠金额 = 原价 - 折扣后金额
                BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break; // 找到第一个满足条件的就停止，因为列表已按优惠力度排序
            }
        }

        // 如果没有找到满足条件的活动规则
        if(null == optimalActivityRule) {
            // 取最小满足条件的一项作为参考规则（列表最后一项）
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0")); // 无优惠金额
            optimalActivityRule.setSelectType(1); // 标记为未满足条件

            // 构造未满足条件的规则描述，提示用户还差多少件可享受优惠
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，还差")
                    .append(totalNum-optimalActivityRule.getConditionNum())
                    .append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            // 构造满足条件的规则描述，显示已享受的优惠金额
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2); // 标记为已满足条件
        }
        return optimalActivityRule;
    }

    /**
     * 计算最优的满减活动规则
     * 根据购物车中商品的总金额，从活动规则列表中找出最适合的满减优惠规则
     *
     * @param totalAmount      购物车中参与活动商品的总金额
     * @param activityRuleList 活动规则列表，已按优惠力度从大到小排序
     * @return 返回最优的活动规则，包含优惠金额和规则描述等信息
     */
    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        // 遍历活动规则列表，寻找满足条件的最优满减规则
        // 该活动规则列表数据已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            // 如果订单项金额大于等于满减活动所需金额，则该规则可用
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                // 设置优惠后减少的金额（即满减金额）
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break; // 找到第一个满足条件的就停止，因为列表已按优惠力度排序
            }
        }

        // 如果没有找到满足条件的活动规则
        if(null == optimalActivityRule) {
            // 取最小满足条件的一项作为参考规则（列表最后一项）
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0")); // 无优惠金额
            optimalActivityRule.setSelectType(1); // 标记为未满足条件

            // 构造未满足条件的规则描述，提示用户还差多少钱可享受优惠
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount()))
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            // 构造满足条件的规则描述，显示已享受的优惠金额
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2); // 标记为已满足条件
        }
        return optimalActivityRule;
    }

    /**
     * 计算购物车中选中商品的总金额
     * 遍历购物车列表，累加所有被选中商品的价格*数量
     *
     * @param cartInfoList 购物车商品列表
     * @return 返回选中商品的总金额
     */
    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            // 判断商品是否被选中（isChecked=1表示选中）
            if(cartInfo.getIsChecked().intValue() == 1) {
                // 计算单个商品的总价 = 单价 * 数量
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal); // 累加到总金额
            }
        }
        return total;
    }

    /**
     * 计算购物车中选中商品的总数量
     * 遍历购物车列表，累加所有被选中商品的数量
     *
     * @param cartInfoList 购物车商品列表
     * @return 返回选中商品的总数量
     */
    private int computeCartNum(List<CartInfo> cartInfoList) {
        int total = 0;
        for (CartInfo cartInfo : cartInfoList) {
            // 判断商品是否被选中（isChecked=1表示选中）
            if(cartInfo.getIsChecked().intValue() == 1) {
                total += cartInfo.getSkuNum(); // 累加商品数量
            }
        }
        return total;
    }



    /**
     * 根据活动规则生成规则描述文本
     *
     * @param activityRule 活动规则对象
     * @return 返回格式化的活动规则描述字符串
     */
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        // 满减活动规则描述生成
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            // 折扣活动规则描述生成
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }
}

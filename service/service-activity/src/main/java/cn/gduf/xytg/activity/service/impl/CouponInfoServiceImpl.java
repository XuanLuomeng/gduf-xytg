package cn.gduf.xytg.activity.service.impl;

import cn.gduf.xytg.activity.mapper.CouponInfoMapper;
import cn.gduf.xytg.activity.mapper.CouponRangeMapper;
import cn.gduf.xytg.activity.mapper.CouponUseMapper;
import cn.gduf.xytg.activity.service.CouponInfoService;
import cn.gduf.xytg.client.product.ProductFeignClient;
import cn.gduf.xytg.enums.CouponStatus;
import cn.gduf.xytg.model.activity.CouponUse;
import cn.gduf.xytg.model.order.CartInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.enums.CouponRangeType;
import cn.gduf.xytg.model.activity.CouponInfo;
import cn.gduf.xytg.model.activity.CouponRange;
import cn.gduf.xytg.model.product.Category;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.vo.activity.CouponRuleVo;
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
 * @description 优惠卷信息服务实现类
 * @date 2025/10/23 20:37
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {
    @Autowired
    private CouponRangeMapper couponRangeMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CouponUseMapper couponUseMapper;

    /**
     * 分页查询优惠卷信息
     *
     * @param page  索引
     * @param limit 每页记录数
     * @return 优惠卷信息分页数据
     */
    @Override
    public IPage<CouponInfo> selectPageCouponInfo(Long page, Long limit) {
        // 创建分页对象
        IPage<CouponInfo> pageModel = new Page<>(page, limit);

        // 执行分页查询
        pageModel = baseMapper.selectPage(pageModel, null);

        // 处理优惠券类型和范围类型的显示文本
        pageModel.getRecords().forEach(item -> {
            item.setCouponTypeString(item.getCouponType().getComment());
            CouponRangeType rangeType = item.getRangeType();
            if (rangeType != null) {
                item.setRangeTypeString(rangeType.getComment());
            }
        });

        return pageModel;
    }

    /**
     * 根据id查询优惠卷信息
     *
     * @param id 优惠卷id
     * @return 优惠卷信息
     */
    @Override
    public CouponInfo getCouponInfo(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);

        // 处理优惠券类型和范围类型的显示文本
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType() != null) {
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }

        return couponInfo;
    }

    /**
     * 根据优惠卷id查询优惠卷规则
     *
     * @param id 优惠卷id
     * @return 优惠卷规则
     */
    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        // 查询优惠券基本信息
        CouponInfo couponInfo = baseMapper.selectById(id);

        // 查询优惠券使用范围列表
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id)
        );

        // 提取范围ID列表
        List<Long> randIdList =
                couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();

        // 根据优惠券范围类型查询对应的商品或分类信息
        if (!CollectionUtils.isEmpty(randIdList)) {
            if (couponInfo.getRangeType() == CouponRangeType.SKU) {
                List<SkuInfo> skuInfoList =
                        productFeignClient.findSkuInfoList(randIdList);

                result.put("skuInfoList", skuInfoList);
            } else if (couponInfo.getRangeType() == CouponRangeType.CATEGORY) {
                List<Category> categoryList =
                        productFeignClient.findCategoryList(randIdList);

                result.put("categoryList", categoryList);
            }
        }

        return result;
    }

    /**
     * 保存优惠卷规则
     *
     * @param couponRuleVo 优惠卷规则对象，包含优惠券ID、使用范围类型、满足条件金额、优惠金额、范围描述和优惠券范围列表
     * @return 保存结果，删除成功且更新成功时返回true，否则返回false
     */
    @Override
    @Transactional
    public boolean saveCouponRule(CouponRuleVo couponRuleVo) {
        // 删除原有的优惠券使用范围数据
        int deleteRange = couponRangeMapper.delete(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponRuleVo.getCouponId())
        );

        // 更新优惠券基本信息
        CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        int updateCouponInfo = baseMapper.updateById(couponInfo);

        // 保存新的优惠券使用范围数据
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();

        couponRangeList.stream().forEach(item -> {
            item.setCouponId(couponInfo.getId());
            couponRangeMapper.insert(item);
        });

        return deleteRange > 0 && updateCouponInfo > 0;
    }

    /**
     * 查询优惠券列表
     *
     * @param skuId  商品ID
     * @param userId 用户ID
     * @return 优惠券列表
     */
    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        List<CouponInfo> couponInfoList = baseMapper.selectCouponInfoList(skuInfo.getId(),
                skuInfo.getCategoryId(),
                userId);

        return couponInfoList;
    }

    /**
     * 获取购物车用户能用的优惠卷列表
     * 根据用户的购物车商品信息和用户ID，筛选出用户可用的优惠券，并标记最优优惠券
     *
     * @param cartInfoList 商品信息列表，包含用户购物车中的所有商品
     * @param userId       用户ID，用于查询该用户可用的优惠券
     * @return 优惠券列表，包含用户所有可用优惠券，其中最优优惠券会被特殊标记
     */
    @Override
    public List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId) {
        // 查询用户所有的优惠券信息
        List<CouponInfo> userAllCouponInfoList =
                baseMapper.selectCartCouponInfoList(userId);

        // 如果用户没有优惠券，直接返回空列表
        if (CollectionUtils.isEmpty(userAllCouponInfoList)) {
            return new ArrayList<CouponInfo>();
        }

        // 提取所有优惠券的ID列表，用于后续查询优惠券使用范围
        List<Long> couponIdList = userAllCouponInfoList.stream()
                .map(couponInfo -> couponInfo.getId())
                .collect(Collectors.toList());

        // 查询这些优惠券的使用范围信息
        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CouponRange::getCouponId, couponIdList);

        List<CouponRange> couponRangeList = couponRangeMapper.selectList(wrapper);

        // 构建优惠券ID到SKU ID列表的映射关系，用于确定每个优惠券适用的商品范围
        Map<Long, List<Long>> couponIdToSkuIdMap =
                this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);

        // 初始化最优优惠券的优惠金额和最优优惠券对象
        BigDecimal reduceAmount = new BigDecimal(0);
        CouponInfo optimalCouponInfo = null;

        // 遍历用户所有优惠券，筛选可用优惠券并找出最优的一个
        for (CouponInfo couponInfo : userAllCouponInfoList) {
            // 判断优惠券类型：全场通用优惠券
            if (CouponRangeType.ALL == couponInfo.getRangeType()) {
                // 计算购物车中所有选中商品的总金额
                BigDecimal totalAmount = this.computeTotalAmount(cartInfoList);
                // 如果总金额满足优惠券使用条件（大于等于门槛金额），则标记该优惠券为可选
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(1); // 标记为可选
                }
            } else {
                // 非全场通用优惠券（指定商品或指定分类）
                // 获取该优惠券适用的SKU ID列表
                List<Long> skuIdList = couponIdToSkuIdMap.get(couponInfo.getId());
                // 筛选出购物车中属于该优惠券适用范围的商品
                List<CartInfo> currentCartInfoList = cartInfoList.stream()
                        .filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId()))
                        .collect(Collectors.toList());

                // 计算适用范围内商品的总金额
                BigDecimal totalAmount = this.computeTotalAmount(currentCartInfoList);
                // 如果适用范围内商品总金额满足优惠券使用条件，标记该优惠券为可选
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(1); // 标记为可选
                }
            }

            // 如果该优惠券可选，且其优惠金额大于当前记录的最大优惠金额，则更新最优优惠券
            if (couponInfo.getIsSelect().intValue() == 1
                    && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount(); // 更新最大优惠金额
                optimalCouponInfo = couponInfo; // 更新最优优惠券
            }
        }

        // 如果找到了最优优惠券，则将其标记为最优选择
        if (optimalCouponInfo != null) {
            optimalCouponInfo.setIsOptimal(1); // 标记为最优优惠券
        }

        // 返回用户所有优惠券列表（包含可选状态和最优标记）
        return userAllCouponInfoList;
    }

    /**
     * 根据优惠券ID和购物车信息查询优惠券范围列表
     *
     * @param couponId     优惠券ID
     * @param cartInfoList 购物车信息列表
     * @return 优惠券ID到SKU ID列表的映射关系
     */
    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId) {
        CouponInfo couponInfo = baseMapper.selectById(couponId);
        if (couponInfo == null) {
            return null;
        }

        List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                new LambdaQueryWrapper<CouponRange>()
                        .eq(CouponRange::getCouponId, couponId)
        );

        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);

        List<Long> skuIdList = couponIdToSkuIdMap.entrySet().iterator().next().getValue();

        couponInfo.setSkuIdList(skuIdList);

        return couponInfo;
    }

    /**
     * 更新优惠券使用状态
     *
     * @param couponId 优惠券ID
     * @param userId   用户ID
     * @param orderId  订单ID
     */
    @Override
    public void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId) {
        // 查询优惠券使用记录
        CouponUse couponUse = couponUseMapper.selectOne(
                new LambdaQueryWrapper<CouponUse>()
                        .eq(CouponUse::getCouponId, couponId)
                        .eq(CouponUse::getUserId, userId)
                        .eq(CouponUse::getOrderId, orderId)
        );

        // 设置优惠券状态为已使用
        couponUse.setCouponStatus(CouponStatus.USED);

        // 更新优惠券使用状态到数据库
        couponUseMapper.updateById(couponUse);
    }


    /**
     * 计算购物车中选中商品的总金额
     *
     * @param cartInfoList 购物车信息列表
     * @return 选中商品的总金额
     */
    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        // 遍历购物车中的每个商品项
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }


    /**
     * 根据购物车信息查询优惠券范围列表
     *
     * @param cartInfoList    购物车信息列表
     * @param couponRangeList 优惠券范围列表
     * @return 优惠券ID到SKU ID列表的映射关系
     */
    private Map<Long, List<Long>> findCouponIdToSkuIdMap(List<CartInfo> cartInfoList,
                                                         List<CouponRange> couponRangeList) {
        Map<Long, List<Long>> couponIdToSkuIdMap = new HashMap<>();

        // 按优惠券ID分组优惠券范围列表
        Map<Long, List<CouponRange>> couponRangeToRangeListMap = couponRangeList.stream().collect(
                Collectors.groupingBy(couponRange -> couponRange.getCouponId())
        );

        Iterator<Map.Entry<Long, List<CouponRange>>> iterator =
                couponRangeToRangeListMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long, List<CouponRange>> entry = iterator.next();
            Long couponId = entry.getKey();
            List<CouponRange> rangeList = entry.getValue();

            // 遍历购物车信息，找出符合当前优惠券范围的SKU ID集合
            Set<Long> skuIdSet = new HashSet<>();
            for (CartInfo cartInfo : cartInfoList) {
                for (CouponRange couponRange : rangeList) {
                    if (couponRange.getRangeType() == CouponRangeType.SKU
                            && couponRange.getRangeId() == cartInfo.getSkuId().longValue()) {
                        skuIdSet.add(cartInfo.getSkuId());
                    } else if (couponRange.getRangeType() == CouponRangeType.CATEGORY
                            && couponRange.getRangeId().longValue() == cartInfo.getCategoryId().longValue()) {
                        skuIdSet.add(cartInfo.getSkuId());
                    }
                }
            }
            couponIdToSkuIdMap.put(couponId, new ArrayList<>(skuIdSet));
        }

        return couponIdToSkuIdMap;
    }

}

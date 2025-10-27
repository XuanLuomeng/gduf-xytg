package cn.gduf.xytg.activity.service.impl;

import cn.gduf.xytg.activity.mapper.ActivityInfoMapper;
import cn.gduf.xytg.activity.mapper.ActivityRuleMapper;
import cn.gduf.xytg.activity.mapper.ActivitySkuMapper;
import cn.gduf.xytg.activity.service.ActivityInfoService;
import cn.gduf.xytg.activity.service.CouponInfoService;
import cn.gduf.xytg.client.product.ProductFeignClient;
import cn.gduf.xytg.enums.ActivityType;
import cn.gduf.xytg.model.activity.CouponInfo;
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
     * @param skuId 商品ID
     * @param userId 用户ID
     * @return 返回活动规则和优惠券信息
     */
    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        List<ActivityRule> activityRuleList = this.findActivityRuleBySkuId(skuId);

        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId, userId);

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
     * 根据活动规则生成规则描述文本
     *
     * @param activityRule 活动规则对象
     * @return 返回格式化的活动规则描述字符串
     */
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
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

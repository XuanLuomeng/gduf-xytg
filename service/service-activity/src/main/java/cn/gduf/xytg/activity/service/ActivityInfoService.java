package cn.gduf.xytg.activity.service;

import cn.gduf.xytg.model.activity.ActivityRule;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.activity.ActivityInfo;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.vo.activity.ActivityRuleVo;

import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 活动信息服务接口
 * @date 2025/10/23 20:39
 */
public interface ActivityInfoService extends IService<ActivityInfo> {
    /**
     * 分页查询活动信息
     *
     * @param pageParam
     * @return
     */
    Page<ActivityInfo> selectPageActivityInfo(Page<ActivityInfo> pageParam);

    /**
     * 根据活动id获取活动规则
     *
     * @param id
     * @return
     */
    Map<String, Object> findActivityRuleList(Long id);

    /**
     * 保存活动规则
     *
     * @param activityRuleVo
     * @return
     */
    boolean saveActivityRule(ActivityRuleVo activityRuleVo);

    /**
     * 根据关键字查询商品
     *
     * @param keyword
     * @return
     */
    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    /**
     * 根据skuId列表获取促销信息
     *
     * @param skuIdList
     * @return
     */
    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    /**
     * 获取购物车里面满足优惠券和活动的信息
     *
     * @param skuId
     * @param userId
     * @return
     */
    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    /**
     * 根据skuId获取活动规则数据
     *
     * @param skuId
     * @return
     */
    List<ActivityRule> findActivityRuleBySkuId(Long skuId);
}

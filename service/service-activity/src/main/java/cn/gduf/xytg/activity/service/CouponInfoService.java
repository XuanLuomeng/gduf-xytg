package cn.gduf.xytg.activity.service;

import cn.gduf.xytg.model.order.CartInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.activity.CouponInfo;
import cn.gduf.xytg.vo.activity.CouponRuleVo;

import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 优惠卷信息服务接口
 * @date 2025/10/23 20:37
 */
public interface CouponInfoService extends IService<CouponInfo> {
    /**
     * 分页查询优惠卷信息
     *
     * @param page
     * @param limit
     * @return
     */
    IPage<CouponInfo> selectPageCouponInfo(Long page, Long limit);

    /**
     * 根据id查询优惠卷信息
     *
     * @param id
     * @return
     */
    CouponInfo getCouponInfo(Long id);

    /**
     * 添加优惠卷信息
     *
     * @param id
     * @return
     */
    Map<String, Object> findCouponRuleList(Long id);

    /**
     * 修改优惠卷信息
     *
     * @param couponRuleVo
     * @return
     */
    boolean saveCouponRule(CouponRuleVo couponRuleVo);

    /**
     * 根据skuId和userId查询优惠卷信息
     *
     * @param skuId
     * @param userId
     * @return
     */
    List<CouponInfo> findCouponInfoList(Long skuId, Long userId);

    /**
     * 获取购物车用户能用的优惠卷列表
     *
     * @param cartInfoList
     * @param userId
     * @return
     */
    List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId);

    /**
     * 获取优惠卷的skuId列表
     *
     * @param couponId
     * @return
     */
    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);

    /**
     * 更新优惠卷使用状态
     *
     * @param couponId
     * @param userId
     * @param orderId
     */
    void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId);
}

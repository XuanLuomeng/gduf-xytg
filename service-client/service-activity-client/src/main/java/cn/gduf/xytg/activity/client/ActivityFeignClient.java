package cn.gduf.xytg.activity.client;

import cn.gduf.xytg.model.order.CartInfo;
import cn.gduf.xytg.vo.order.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 活动信息 Feign客户端接口
 * @date 2025/10/26 21:51
 */
@FeignClient("service-activity")
public interface ActivityFeignClient {

    /**
     * 获取购物车里满足条件优惠卷和活动的信息
     *
     * @param userId
     * @param cartInfoList
     * @return
     */
    @PostMapping("inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@PathVariable("userId") Long userId,
                                                    @RequestBody List<CartInfo> cartInfoList);

    /**
     * 根据skuId列表获取促销信息
     *
     * @param skuIdList
     * @return
     */
    @PostMapping("/api/activity/inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList);

    /**
     * 根据skuId列表获取促销数据和优惠卷信息
     *
     * @param skuId
     * @param userId
     * @return
     */
    @PostMapping("/api/activity/inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable("skuId") Long skuId,
                                                     @PathVariable("userId") Long userId);
}

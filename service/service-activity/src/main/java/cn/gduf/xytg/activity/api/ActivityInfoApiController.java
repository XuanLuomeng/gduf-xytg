package cn.gduf.xytg.activity.api;

import cn.gduf.xytg.activity.service.ActivityInfoService;
import cn.gduf.xytg.model.order.CartInfo;
import cn.gduf.xytg.vo.order.OrderConfirmVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 活动信息Api控制器
 * @date 2025/10/26 21:27
 */
@Api(tags = "活动信息Api控制器")
@RestController
@RequestMapping("/api/activity")
public class ActivityInfoApiController {
    @Autowired
    private ActivityInfoService activityInfoService;

    /**
     * 获取购物车满足条件优惠卷和活动的信息
     *
     * @param cartInfoList 购物车列表
     * @param userId       用户Id
     * @return OrderConfirmVo
     */
    @ApiOperation("获取购物车里满足条件优惠卷和活动的信息")
    @PostMapping("inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@PathVariable("userId") Long userId,
                                                 @RequestBody List<CartInfo> cartInfoList) {
        return activityInfoService.findCartActivityAndCoupon(cartInfoList, userId);
    }

    /**
     * 根据skuId列表获取促销信息
     *
     * @param skuIdList skuId列表
     * @return Map<Long, List < String>>
     */
    @ApiOperation("根据skuId列表获取促销信息")
    @PostMapping("inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList) {
        return activityInfoService.findActivity(skuIdList);
    }

    /**
     * 根据skuId列表获取促销数据和优惠卷信息
     *
     * @param skuId  skuId
     * @param userId 用户Id
     * @return Map<String, Object>
     */
    @ApiOperation("根据skuId列表获取促销数据和优惠卷信息")
    @PostMapping("inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable Long skuId,
                                                     @PathVariable Long userId){
        return activityInfoService.findActivityAndCoupon(skuId, userId);
    }
}

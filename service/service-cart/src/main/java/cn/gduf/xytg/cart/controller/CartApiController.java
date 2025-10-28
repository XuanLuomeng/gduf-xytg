package cn.gduf.xytg.cart.controller;

import cn.gduf.xytg.activity.client.ActivityFeignClient;
import cn.gduf.xytg.cart.service.CartInfoService;
import cn.gduf.xytg.common.auth.AuthContextHolder;
import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.model.order.CartInfo;
import cn.gduf.xytg.vo.order.OrderConfirmVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 购物车 Api 控制器
 * @date 2025/10/27 21:57
 */
@Api(tags = "购物车接口")
@RestController
@RequestMapping("/api/cart")
public class CartApiController {
    @Autowired
    private CartInfoService cartInfoService;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    /**
     * 获取活动购物车列表
     *
     * @return 活动购物车列表
     */
    @ApiOperation("获取活动购物车列表")
    @GetMapping("activityCartList")
    public Result activityCartList() {
        Long userId = AuthContextHolder.getUserId();
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userId);

        OrderConfirmVo orderTradeVo = activityFeignClient.findCartActivityAndCoupon(userId, cartInfoList);
        return Result.ok(orderTradeVo);
    }

    /**
     * 获取购物车列表
     *
     * @return 购物车列表
     */
    @ApiOperation("获取购物车列表")
    @GetMapping("cartList")
    public Result cartList() {
        Long userId = AuthContextHolder.getUserId();
        List<CartInfo> cartInfoList = cartInfoService.cartList(userId);
        return Result.ok(cartInfoList);
    }

    /**
     * 添加购物车
     *
     * @param skuId  商品id
     * @param skuNum 商品数量
     * @return 添加结果
     */
    @ApiOperation("添加购物车")
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum) {
        Long userId = AuthContextHolder.getUserId();
        boolean isAdd = cartInfoService.addToCart(skuId, userId, skuNum);
        return isAdd ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 删除购物车
     *
     * @param skuId 商品id
     * @return 删除结果
     */
    @ApiOperation("删除购物车")
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId) {
        Long userId = AuthContextHolder.getUserId();
        boolean res = cartInfoService.deleteCart(skuId, userId);
        return res ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 删除所有购物车
     *
     * @return 删除结果
     */
    @ApiOperation("删除所有购物车")
    @DeleteMapping("deleteAllCart")
    public Result deleteAllCart() {
        Long userId = AuthContextHolder.getUserId();
        boolean res = cartInfoService.deleteAllCart(userId);
        return res ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 批量删除购物车
     *
     * @param skuIdList 商品id列表
     * @return 删除结果
     */
    @ApiOperation("批量删除购物车")
    @DeleteMapping("batchDeleteCart")
    public Result batchDeleteCart(@RequestBody List<Long> skuIdList) {
        Long userId = AuthContextHolder.getUserId();
        boolean res = cartInfoService.batchDeleteCart(skuIdList, userId);
        return res ? Result.ok(null) : Result.fail(null);
    }
}

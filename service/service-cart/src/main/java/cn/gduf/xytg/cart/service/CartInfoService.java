package cn.gduf.xytg.cart.service;

import cn.gduf.xytg.model.order.CartInfo;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 购物车信息服务接口
 * @date 2025/10/27 21:57
 */
public interface CartInfoService {
    /**
     * 添加购物车
     *
     * @param skuId  skuId
     * @param userId 用户Id
     * @param skuNum 商品数量
     * @return 是否添加成功
     */
    boolean addToCart(Long skuId, Long userId, Integer skuNum);

    /**
     * 删除购物车
     *
     * @param skuId  skuId
     * @param userId 用户Id
     * @return 是否删除成功
     */
    boolean deleteCart(Long skuId, Long userId);

    /**
     * 删除所有购物车
     *
     * @param userId 用户Id
     * @return 是否删除成功
     */
    boolean deleteAllCart(Long userId);

    /**
     * 批量删除购物车
     *
     * @param skuIdList skuId列表
     * @param userId    用户Id
     * @return 是否删除成功
     */
    boolean batchDeleteCart(List<Long> skuIdList, Long userId);

    /**
     * 购物车列表
     *
     * @return 购物车列表
     */
    List<CartInfo> cartList(Long userId);

    /**
     * 获取活动购物车列表
     *
     * @return 活动购物车列表
     */
    List<CartInfo> getCartList(Long userId);
}

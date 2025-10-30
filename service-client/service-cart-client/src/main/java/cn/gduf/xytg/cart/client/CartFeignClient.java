package cn.gduf.xytg.cart.client;

import cn.gduf.xytg.model.order.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 购物车Feign客户端
 * @date 2025/10/28 23:00
 */
@FeignClient("service-cart")
public interface CartFeignClient {
    /**
     * 获取购物车选中列表
     *
     * @param userId
     * @return
     */
    @GetMapping("/api/cart/inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId);
}

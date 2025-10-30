package cn.gduf.xytg.order.client;

import cn.gduf.xytg.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 订单服务Feign客户端接口
 * @date 2025/10/30 20:47
 */
@FeignClient(value = "service-order")
public interface OrderFeignClient {
    /**
     * 获取订单信息
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    @GetMapping("/api/order/inner/getOrderInfo/{orderNo}")
    public OrderInfo getOrderInfo(@PathVariable("orderNo") String orderNo);
}

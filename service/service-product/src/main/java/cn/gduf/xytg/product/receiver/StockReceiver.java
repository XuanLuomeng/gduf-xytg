package cn.gduf.xytg.product.receiver;

import cn.gduf.xytg.common.constant.MqConst;
import cn.gduf.xytg.product.service.SkuInfoService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品接收者
 * @date 2025/10/30 21:40
 */
@Component
public class StockReceiver {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 减库存
     *
     * @param orderNo 订单号
     * @param message 消息
     * @param channel 通道
     * @throws IOException IO异常
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MINUS_STOCK, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_DIRECT),
            key = {MqConst.ROUTING_MINUS_STOCK}
    ))
    public void minusStock(String orderNo,
                           Message message,
                           Channel channel) throws IOException {
        if (!StringUtils.isEmpty(orderNo)) {
            // 减库存
            skuInfoService.minusStock(orderNo);
        }
        // 手动确认消息处理完成，防止消息重复消费
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),
                false);
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单号
     * @param message 消息
     * @param channel 通道
     * @throws IOException IO异常
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.ROUTING_ROLLBACK_STOCK, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_CANCEL_ORDER_DIRECT),
            key = {MqConst.ROUTING_ROLLBACK_STOCK}
    ))
    public void cancelOrder(String orderNo,
                           Message message,
                           Channel channel) throws IOException {
        if (!StringUtils.isEmpty(orderNo)) {
            skuInfoService.rollbackStock(orderNo);
        }
        // 手动确认消息处理完成，防止消息重复消费
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),
                false);
    }
}

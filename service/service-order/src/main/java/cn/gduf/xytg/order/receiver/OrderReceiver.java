package cn.gduf.xytg.order.receiver;

import cn.gduf.xytg.common.constant.MqConst;
import cn.gduf.xytg.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 订单接收者
 * @date 2025/10/30 21:31
 */
@Component
public class OrderReceiver {
    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 处理订单支付成功的消息监听方法
     * 该方法监听RabbitMQ中指定队列的支付成功消息，处理订单支付逻辑并发送确认ACK
     *
     * @param orderNo 订单编号，用于标识需要处理支付的订单
     * @param message RabbitMQ消息对象，包含消息的元数据信息
     * @param channel RabbitMQ通道，用于手动确认消息处理完成
     * @throws IOException 当消息确认过程中发生IO异常时抛出
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER_PAY,durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_PAY_DIRECT),
            key = {MqConst.ROUTING_PAY_SUCCESS}
    ))
    public void orderPay(String orderNo,
                         Message message,
                         Channel channel) throws IOException {
        // 处理订单支付逻辑
        if (!StringUtils.isEmpty(orderNo)) {
            orderInfoService.orderPay(orderNo);
        }

        // 手动确认消息处理完成，防止消息重复消费
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),
                false);
    }

    /**
     * 处理订单取消消息监听方法
     * 该方法监听RabbitMQ中指定队列的订单取消消息，处理订单取消逻辑并发送确认ACK
     *
     * @param orderNo 订单编号，用于标识需要取消的订单
     * @param message RabbitMQ消息对象，包含消息的元数据信息
     * @param channel RabbitMQ通道，用于手动确认消息处理完成
     * @throws IOException 当消息确认过程中发生IO异常时抛出
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER_PAY,durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_CANCEL_ORDER_DIRECT),
            key = {MqConst.ROUTING_PAY_CANCEL}
    ))
    public void cancelOrder(String orderNo,
                         Message message,
                         Channel channel) throws IOException {
        // 处理订单取消逻辑
        if (!StringUtils.isEmpty(orderNo)) {
            orderInfoService.cancelOrder(orderNo);
        }

        // 手动确认消息处理完成，防止消息重复消费
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),
                false);
    }
}

package cn.gduf.xytg.cart.receiver;

import cn.gduf.xytg.cart.service.CartInfoService;
import cn.gduf.xytg.common.constant.MqConst;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 购物车接收者
 * @date 2025/10/29 23:47
 */
@Component
public class CartReceiver {
    @Autowired
    private CartInfoService cartInfoService;

    /**
     * 删除购物车
     *
     * @param userId  用户ID，用于标识需要删除购物车的用户
     * @param message RabbitMQ消息对象，包含消息的元数据信息
     * @param channel RabbitMQ通道，用于手动确认消息处理完成
     * @throws IOException 当通道操作失败时抛出
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_DELETE_CART, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_DIRECT),
            key = {MqConst.ROUTING_DELETE_CART}
    ))
    public void deleteCart(Long userId, Message message, Channel channel) throws IOException {
        // 如果用户ID不为空，则删除该用户已选中的购物车商品
        if (userId != null) {
            cartInfoService.deleteCartChecked(userId);
        }
        // 手动确认消息处理完成，确保消息不会重复消费
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}

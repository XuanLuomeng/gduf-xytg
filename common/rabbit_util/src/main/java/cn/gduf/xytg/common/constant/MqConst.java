package cn.gduf.xytg.common.constant;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description MQ常量类
 * @date 2025/10/23 20:01
 */
public class MqConst {
    /**
     * 消息补偿
     */
    public static final String MQ_KEY_PREFIX = "xytg.mq:list";
    public static final int RETRY_COUNT = 3;

    /**
     * 商品上下架
     */
    public static final String EXCHANGE_GOODS_DIRECT = "xytg.goods.direct";
    public static final String ROUTING_GOODS_UPPER = "xytg.goods.upper";
    public static final String ROUTING_GOODS_LOWER = "xytg.goods.lower";
    //队列
    public static final String QUEUE_GOODS_UPPER  = "xytg.goods.upper";
    public static final String QUEUE_GOODS_LOWER  = "xytg.goods.lower";

    /**
     * 团长上下线
     */
    public static final String EXCHANGE_LEADER_DIRECT = "xytg.leader.direct";
    public static final String ROUTING_LEADER_UPPER = "xytg.leader.upper";
    public static final String ROUTING_LEADER_LOWER = "xytg.leader.lower";
    //队列
    public static final String QUEUE_LEADER_UPPER  = "xytg.leader.upper";
    public static final String QUEUE_LEADER_LOWER  = "xytg.leader.lower";

    //订单
    public static final String EXCHANGE_ORDER_DIRECT = "xytg.order.direct";
    public static final String ROUTING_ROLLBACK_STOCK = "xytg.rollback.stock";
    public static final String ROUTING_MINUS_STOCK = "xytg.minus.stock";

    public static final String ROUTING_DELETE_CART = "xytg.delete.cart";
    //解锁普通商品库存
    public static final String QUEUE_ROLLBACK_STOCK = "xytg.rollback.stock";
    public static final String QUEUE_SECKILL_ROLLBACK_STOCK = "xytg.seckill.rollback.stock";
    public static final String QUEUE_MINUS_STOCK = "xytg.minus.stock";
    public static final String QUEUE_DELETE_CART = "xytg.delete.cart";

    //支付
    public static final String EXCHANGE_PAY_DIRECT = "xytg.pay.direct";
    public static final String ROUTING_PAY_SUCCESS = "xytg.pay.success";
    public static final String QUEUE_ORDER_PAY  = "xytg.order.pay";
    public static final String QUEUE_LEADER_BILL  = "xytg.leader.bill";

    //取消订单
    public static final String EXCHANGE_CANCEL_ORDER_DIRECT = "xytg.cancel.order.direct";
    public static final String ROUTING_CANCEL_ORDER = "xytg.cancel.order";
    //延迟取消订单队列
    public static final String QUEUE_CANCEL_ORDER  = "xytg.cancel.order";

    /**
     * 定时任务
     */
    public static final String EXCHANGE_DIRECT_TASK = "xytg.exchange.direct.task";
    public static final String ROUTING_TASK_23 = "xytg.task.23";

    //队列
    public static final String QUEUE_TASK_23  = "xytg.queue.task.23";
}

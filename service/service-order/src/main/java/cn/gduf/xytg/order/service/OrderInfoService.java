package cn.gduf.xytg.order.service;

import cn.gduf.xytg.model.order.OrderInfo;
import cn.gduf.xytg.vo.order.OrderConfirmVo;
import cn.gduf.xytg.vo.order.OrderSubmitVo;
import cn.gduf.xytg.vo.order.OrderUserQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 订单信息服务接口
 * @date 2025/10/29 10:31
 */
public interface OrderInfoService extends IService<OrderInfo> {
    /**
     * 获取订单确认信息
     *
     * @return
     */
    OrderConfirmVo confirmOrder();

    /**
     * 生成订单
     *
     * @param orderParamVo
     * @return
     */
    Long submitOrder(OrderSubmitVo orderParamVo);

    /**
     * 根据订单Id查询订单信息
     *
     * @param orderId
     * @return
     */
    OrderInfo getOrderInfoById(Long orderId);

    /**
     * 根据订单编号查询订单信息
     *
     * @param orderNo
     * @return
     */
    OrderInfo getOrderInfoByOrderNo(String orderNo);

    /**
     * 订单支付成功
     *
     * @param orderNo
     */
    void orderPay(String orderNo);

    /**
     * 获取订单分页列表
     *
     * @param pageParam
     * @param orderUserQueryVo
     * @return
     */
    public IPage<OrderInfo> getOrderInfoByUserIdPage(Page<OrderInfo> pageParam,
                                                     OrderUserQueryVo orderUserQueryVo);
}

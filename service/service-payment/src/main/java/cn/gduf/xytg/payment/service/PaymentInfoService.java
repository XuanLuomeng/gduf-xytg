package cn.gduf.xytg.payment.service;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.enums.PaymentType;
import cn.gduf.xytg.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 支付信息接口
 * @date 2025/10/30 20:22
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    /**
     * 根据订单编号获取支付信息
     *
     * @param orderNo
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfoByOrderNo(String orderNo, PaymentType paymentType);

    /**
     * 保存支付信息
     *
     * @param orderNo
     * @param paymentType
     * @return
     */
    PaymentInfo savePaymentInfo(String orderNo, PaymentType paymentType);

    /**
     * 支付成功
     *
     * @param outTradeNo
     * @param resultMap
     */
    void paySuccess(String outTradeNo, Map<String, String> resultMap);

    /**
     * 取消订单
     *
     * @param orderNo
     * @return
     */
    boolean cancelOrder(String orderNo);
}

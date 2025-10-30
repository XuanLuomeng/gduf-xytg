package cn.gduf.xytg.payment.service.impl;

import cn.gduf.xytg.common.exception.XytgException;
import cn.gduf.xytg.common.result.ResultCodeEnum;
import cn.gduf.xytg.enums.PaymentStatus;
import cn.gduf.xytg.enums.PaymentType;
import cn.gduf.xytg.model.order.OrderInfo;
import cn.gduf.xytg.model.order.PaymentInfo;
import cn.gduf.xytg.order.client.OrderFeignClient;
import cn.gduf.xytg.payment.mapper.PaymentInfoMapper;
import cn.gduf.xytg.payment.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 支付信息实现类
 * @date 2025/10/30 20:22
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * 根据订单编号和支付类型查询支付信息
     *
     * @param orderNo     订单编号
     * @param paymentType 支付类型
     * @return 支付信息对象
     */
    @Override
    public PaymentInfo getPaymentInfoByOrderNo(String orderNo, PaymentType paymentType) {
        PaymentInfo paymentInfo = baseMapper.selectOne(
                new LambdaQueryWrapper<PaymentInfo>()
                        .eq(PaymentInfo::getOrderNo, orderNo)
        );
        return paymentInfo;
    }

    /**
     * 保存支付信息
     *
     * @param orderNo     订单编号
     * @param paymentType 支付类型
     * @return 保存后的支付信息对象
     */
    @Override
    public PaymentInfo savePaymentInfo(String orderNo, PaymentType paymentType) {
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderNo);

        if (orderInfo == null) {
            throw new XytgException(ResultCodeEnum.DATA_ERROR);
        }

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(PaymentType.WEIXIN);
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOrderNo(orderInfo.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        String subject = "userID:"+orderInfo.getUserId()+"下订单";
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());

        baseMapper.insert(paymentInfo);
        return paymentInfo;
    }
}

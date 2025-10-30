package cn.gduf.xytg.payment.service.impl;

import cn.gduf.xytg.model.order.PaymentInfo;
import cn.gduf.xytg.payment.mapper.PaymentInfoMapper;
import cn.gduf.xytg.payment.service.PaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 支付服务实现类
 * @date 2025/10/30 20:22
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentService {
}

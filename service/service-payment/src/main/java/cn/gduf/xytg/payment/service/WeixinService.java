package cn.gduf.xytg.payment.service;

import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 微信服务接口
 * @date 2025/10/30 20:20
 */
public interface WeixinService {
    /**
     * 创建微信支付单
     *
     * @param orderNo
     * @return
     */
    Map<String, String> createJsapi(String orderNo);
}

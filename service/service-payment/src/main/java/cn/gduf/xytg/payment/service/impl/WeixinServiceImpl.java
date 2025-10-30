package cn.gduf.xytg.payment.service.impl;

import cn.gduf.xytg.common.constant.RedisConst;
import cn.gduf.xytg.enums.PaymentType;
import cn.gduf.xytg.model.order.PaymentInfo;
import cn.gduf.xytg.payment.service.PaymentInfoService;
import cn.gduf.xytg.payment.service.WeixinService;
import cn.gduf.xytg.payment.utils.HttpClient;
import cn.gduf.xytg.vo.user.UserLoginVo;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import cn.gduf.xytg.payment.utils.ConstantPropertiesUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 微信服务实现类
 * @date 2025/10/30 20:21
 */
@Service
public class WeixinServiceImpl implements WeixinService {
    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 创建微信支付订单 - JSAPI支付方式
     * 该方法实现了微信JSAPI支付的完整流程，包括参数组装、签名生成、统一下单接口调用、支付参数返回等步骤
     *
     * @param orderNo 订单编号，用于查询或创建支付信息
     * @return Map<String, String> 包含微信支付所需参数的Map，供前端调用微信支付接口使用
     * 返回参数包括：
     * - timeStamp: 时间戳
     * - nonceStr: 随机字符串
     * - signType: 签名类型(MD5)
     * - paySign: 支付签名
     * - package: 预支付交易会话标识
     */
    @Override
    public Map<String, String> createJsapi(String orderNo) {
        // 根据订单号获取微信支付信息，如果不存在则创建新的支付记录
        PaymentInfo paymentInfo =
                paymentInfoService.getPaymentInfoByOrderNo(orderNo, PaymentType.WEIXIN);

        // 如果支付信息不存在，则创建新的支付信息记录
        if (paymentInfo == null) {
            paymentInfo =
                    paymentInfoService.savePaymentInfo(orderNo, PaymentType.WEIXIN);
        }

        // 组装微信统一下单接口所需参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);           // 公众号ID
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);        // 商户号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());        // 随机字符串
        paramMap.put("body", paymentInfo.getSubject());                 // 商品描述
        paramMap.put("out_trade_no", paymentInfo.getOrderNo());         // 商户订单号
        // 金额转换为分，并转为字符串
        int totalFee = paymentInfo.getTotalAmount().multiply(new BigDecimal(100)).intValue();
        paramMap.put("total_fee", String.valueOf(totalFee));            // 总金额(单位:分)
        paramMap.put("spbill_create_ip", "127.0.0.1");                  // 终端IP
        paramMap.put("notify_url", ConstantPropertiesUtils.NOTIFYURL);  // 通知地址
        paramMap.put("trade_type", "JSAPI");                            // 交易类型(JSAPI支付)

        // 获取用户登录信息，用于获取用户的openid
        UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue()
                .get(RedisConst.USER_LOGIN_KEY_PREFIX + paymentInfo.getUserId());

        // 如果能获取到用户openid，则使用用户openid；否则使用默认的商户管理员openid
        if (null != userLoginVo && !StringUtils.isEmpty(userLoginVo.getOpenId())) {
            paramMap.put("openid", userLoginVo.getOpenId());
        } else {
            paramMap.put("openid", "--此为商户管理员的openId--");
        }

        // 创建HttpClient实例，用于调用微信统一下单接口
        HttpClient client =
                new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

        try {
            // 生成带有签名的XML请求参数
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));

            // 设置使用HTTPS协议
            client.setHttps(true);
            // 发送POST请求
            client.post();

            // 获取微信统一下单接口返回的XML响应
            String xml = client.getContent();
            // 将XML响应转换为Map格式
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            // 组装前端调用微信支付所需的参数
            Map<String, String> parameterMap = new HashMap<>();
            String prepayId = String.valueOf(resultMap.get("prepay_id"));  // 预支付交易会话标识
            String packages = "prepay_id=" + prepayId;                     // package参数值
            parameterMap.put("appId", ConstantPropertiesUtils.APPID);      // 应用ID
            parameterMap.put("nonceStr", resultMap.get("nonce_str"));      // 随机字符串
            parameterMap.put("package", packages);                         // package参数
            parameterMap.put("signType", "MD5");                           // 签名算法
            parameterMap.put("timeStamp", String.valueOf(new Date().getTime())); // 时间戳
            // 生成支付签名
            String sign = WXPayUtil.generateSignature(parameterMap, ConstantPropertiesUtils.PARTNERKEY);

            // 构建最终返回给前端的支付参数
            Map<String, String> result = new HashMap<>();
            result.put("timeStamp", parameterMap.get("timeStamp"));   // 时间戳
            result.put("nonceStr", parameterMap.get("nonceStr"));     // 随机字符串
            result.put("signType", "MD5");                            // 签名类型
            result.put("paySign", sign);                              // 支付签名
            result.put("package", packages);                          // 预支付交易会话标识

            return result;
        } catch (Exception e) {
            // 异常处理，抛出运行时异常
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询微信支付订单状态
     * 该方法通过调用微信支付订单查询接口，获取指定订单的支付状态信息
     * 主要用于检查订单是否已支付成功，支持前端轮询查询支付结果
     *
     * @param orderNo 订单编号，用于查询对应微信支付订单的状态
     * @return Map<String, String> 微信支付订单状态信息，包含以下关键字段：
     * - return_code: 返回状态码(SUCCESS/FAIL)
     * - return_msg: 返回信息
     * - result_code: 业务结果(SUCCESS/FAIL)
     * - trade_state: 交易状态
     * - trade_state_desc: 交易状态描述
     * - out_trade_no: 商户订单号
     * - transaction_id: 微信支付订单号
     * - total_fee: 订单总金额(分)
     * - time_end: 支付完成时间
     */
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        // 组装微信订单查询接口所需参数
        Map paramMap = new HashMap();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);        // 公众号ID
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);     // 商户号
        paramMap.put("out_trade_no", orderNo);                       // 商户订单号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());     // 随机字符串

        // 创建HttpClient实例，用于调用微信订单查询接口
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        try {
            // 生成带有签名的XML请求参数
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            // 设置使用HTTPS协议
            client.setHttps(true);
            // 发送POST请求
            client.post();

            // 获取微信订单查询接口返回的XML响应
            String xml = client.getContent();
            // 将XML响应转换为Map格式，便于处理返回结果
            Map<String, String> stringMap = WXPayUtil.xmlToMap(xml);
            // 返回订单状态信息
            return stringMap;
        } catch (Exception e) {
            // 异常处理，抛出运行时异常
            throw new RuntimeException(e);
        }
    }
}

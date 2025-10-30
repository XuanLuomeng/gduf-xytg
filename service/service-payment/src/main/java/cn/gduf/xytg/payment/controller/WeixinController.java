package cn.gduf.xytg.payment.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.common.result.ResultCodeEnum;
import cn.gduf.xytg.payment.service.PaymentInfoService;
import cn.gduf.xytg.payment.service.WeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 微信支付控制器
 * @date 2025/10/30 20:19
 */
@Api(tags = "微信支付控制器")
@RestController
@RequestMapping("/api/payment/weixin")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    /**
     * 创建微信支付单
     *
     * @param orderNo 订单编号
     * @return 微信支付单信息
     */
    @ApiOperation("创建微信支付单")
    @GetMapping("createJsapi/{orderNo}")
    public Result createJsapi(@PathVariable String orderNo) {
        Map<String, String> map = weixinService.createJsapi(orderNo);
        return Result.ok(map);
    }

    /**
     * 查询订单支付状态
     *
     * @param orderNo 订单编号
     * @return 订单支付状态
     */
    @ApiOperation("查询订单支付状态")
    @GetMapping("queryPayStatus/{orderNo}")
    public Result queryPayStatus(@PathVariable String orderNo) {
        Map<String, String> resultMap = weixinService.queryPayStatus(orderNo);

        if (resultMap == null) {
            return Result.build(null, ResultCodeEnum.PAYMENT_FAIL);
        }

        if ("SUCCESS".equals(resultMap.get("trade_state"))) {
            String out_trade_no = resultMap.get("out_trade_no");
            paymentInfoService.paySuccess(out_trade_no, resultMap);
            return Result.ok(null);
        }

        return Result.build(null, ResultCodeEnum.PAYMENT_WAITING);
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @return 取消结果
     */
    @ApiOperation("取消订单")
    @GetMapping("cancelOrder/{orderNo}")
    public Result cancelOrder(@PathVariable String orderNo) {
        boolean res = paymentInfoService.cancelOrder(orderNo);
        return Result.ok(res);
    }
}

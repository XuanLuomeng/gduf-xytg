package cn.gduf.xytg.payment.controller;

import cn.gduf.xytg.common.result.Result;
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

    /**
     * 创建微信支付单
     *
     * @param orderNo
     * @return
     */
    @ApiOperation("创建微信支付单")
    @GetMapping("createJsapi/{orderNo}")
    public Result createJsapi(@PathVariable String orderNo) {
        Map<String, String> map = weixinService.createJsapi(orderNo);
        return Result.ok(map);
    }
}

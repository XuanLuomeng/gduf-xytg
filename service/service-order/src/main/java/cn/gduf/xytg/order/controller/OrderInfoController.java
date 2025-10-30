package cn.gduf.xytg.order.controller;

import cn.gduf.xytg.common.auth.AuthContextHolder;
import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.model.order.OrderInfo;
import cn.gduf.xytg.order.service.OrderInfoService;
import cn.gduf.xytg.vo.order.OrderConfirmVo;
import cn.gduf.xytg.vo.order.OrderSubmitVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 订单信息控制器
 * @date 2025/10/29 10:33
 */
@Api(tags = "订单信息接口")
@RestController
@RequestMapping("/api/order")
public class OrderInfoController {
    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 确认订单
     * 该方法用于确认订单信息，调用订单服务获取确认订单的数据
     *
     * @return Result 包含订单确认信息的结果对象
     */
    @ApiOperation("确认订单")
    @GetMapping("auth/confirmOrder")
    public Result confirm() {
        OrderConfirmVo orderConfirmVo = orderInfoService.confirmOrder();
        return Result.ok(orderConfirmVo);
    }

    /**
     * 生成订单
     * 该方法用于提交订单信息，创建新的订单记录
     *
     * @param orderParamVo 订单提交参数对象，包含订单相关信息
     * @return Result 包含生成的订单ID的结果对象
     */
    @ApiOperation("生成订单")
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderSubmitVo orderParamVo) {
        // 调用服务层提交订单并获取订单ID
        Long orderId = orderInfoService.submitOrder(orderParamVo);
        return Result.ok(orderId);
    }

    /**
     * 获取订单详情
     * 该方法根据订单ID查询订单详细信息
     *
     * @param orderId 订单ID，用于标识要查询的订单
     * @return Result 包含订单详细信息的结果对象
     */
    @ApiOperation("获取订单详情")
    @GetMapping("auth/getOrderInfoById/{orderId}")
    public Result getOrderInfoById(@PathVariable("orderId") Long orderId) {
        // 根据订单ID查询订单信息
        OrderInfo orderInfo = orderInfoService.getOrderInfoById(orderId);
        return Result.ok(orderInfo);
    }

    /**
     * 内部接口：获取订单信息
     * 该方法用于获取订单信息，供内部服务调用
     *
     * @param orderNo 订单编号，用于标识要查询的订单
     * @return OrderInfo 订单信息对象
     */
    @ApiOperation("获取订单信息")
    @GetMapping("inner/getOrderInfo/{orderNo}")
    public OrderInfo getOrderInfo(@PathVariable("orderNo") String orderNo){
        OrderInfo orderInfo = orderInfoService.getOrderInfoByOrderNo(orderNo);

        return orderInfo;
    }
}

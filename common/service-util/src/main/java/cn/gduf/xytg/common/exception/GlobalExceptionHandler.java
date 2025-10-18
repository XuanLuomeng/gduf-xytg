package cn.gduf.xytg.common.exception;

import cn.gduf.xytg.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 全局异常处理类
 * @date 2025/10/17 21:59
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class) // 指定要处理的异常类型
    @ResponseBody // 返回 JSON 数据
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail(null);
    }

    @ExceptionHandler(XytgException.class)
    @ResponseBody
    public Result error(XytgException exception) {
        return Result.build(null, exception.getCode(), exception.getMessage());
    }
}

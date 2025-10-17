package cn.gduf.xytg.common.exception;

import cn.gduf.xytg.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description TODO
 * @date 2025/10/17 22:06
 */
@Data
public class XytgException extends RuntimeException {
    // 异常状态码
    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     *
     * @param code
     * @param message
     */
    public XytgException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 接受枚举类型创建异常对象
     *
     * @param resultCodeEnum
     */
    public XytgException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "XytgException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}

package cn.gduf.xytg.common.result;

import lombok.Data;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 统一返回结果类
 * @date 2025/10/17 22:00
 */
@Data
public class Result<T> {
    // 状态码
    private Integer code;
    // 信息
    private String message;
    // 数据（用泛型）
    private T data;

    private Result() {
    }

    /**
     * 构建返回结果
     *
     * @param data
     * @param resultCodeEnum
     * @param <T>
     * @return
     */
    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum) {
        // 创建Result对象
        Result<T> result = new Result<>();
        // 判断是否需要设置数据
        if (data != null) {
            result.setData(data);
        }
        // 设置其他数据
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());

        // 返回
        return result;
    }

    /**
     * 构建返回结果
     *
     * @param data
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Result<T> build(T data, Integer code, String message) {
        // 创建Result对象
        Result<T> result = new Result<>();
        // 判断是否需要设置数据
        if (data != null) {
            result.setData(data);
        }
        // 设置其他数据
        result.setCode(code);
        result.setMessage(message);

        // 返回
        return result;
    }

    /**
     * 成功返回的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = build(data, ResultCodeEnum.SUCCESS);
        return result;
    }

    /**
     * 失败返回的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(T data) {
        Result<T> result = build(data, ResultCodeEnum.FAIL);
        return result;
    }
}

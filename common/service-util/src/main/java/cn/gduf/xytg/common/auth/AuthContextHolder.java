package cn.gduf.xytg.common.auth;

import cn.gduf.xytg.vo.user.UserLoginVo;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 认证上下文处理器
 * @date 2025/10/25 20:41
 */
public class AuthContextHolder {
    /**
     * 用户ID线程本地变量，用于存储当前登录用户的ID
     */
    private static ThreadLocal<Long> userId = new ThreadLocal<>();

    /**
     * 仓库ID线程本地变量，用于存储当前用户关联的仓库ID
     */
    private static ThreadLocal<Long> wareId = new ThreadLocal<>();

    /**
     * 用户登录信息线程本地变量，用于存储当前登录用户的详细信息
     */
    private static ThreadLocal<UserLoginVo> userLoginVo = new ThreadLocal<>();

    /**
     * 获取用户ID的线程本地变量
     *
     * @return ThreadLocal<Long> 用户ID线程本地变量实例
     */
    public static Long getUserId() {
        return userId.get();
    }

    /**
     * 设置用户ID的线程本地变量
     *
     * @param userId 用户ID线程本地变量实例
     */
    public static void setUserId(Long userId) {
        AuthContextHolder.userId.set(userId);
    }

    /**
     * 获取仓库ID的线程本地变量
     *
     * @return ThreadLocal<Long> 仓库ID线程本地变量实例
     */
    public static Long getWareId() {
        return wareId.get();
    }

    /**
     * 设置仓库ID的线程本地变量
     *
     * @param wareId 仓库ID
     */
    public static void setWareId(Long wareId) {
        AuthContextHolder.wareId.set(wareId);
    }

    /**
     * 获取用户登录信息的线程本地变量
     *
     * @return UserLoginVo 用户登录信息
     */
    public static UserLoginVo getUserLoginVo() {
        return userLoginVo.get();
    }

    /**
     * 设置用户登录信息的线程本地变量
     *
     * @param userLoginVo 用户登录信息
     */
    public static void setUserLoginVo(UserLoginVo userLoginVo) {
        AuthContextHolder.userLoginVo.set(userLoginVo);
    }
}

package cn.gduf.xytg.common.auth;

import cn.gduf.xytg.common.constant.RedisConst;
import cn.gduf.xytg.common.utils.JwtHelper;
import cn.gduf.xytg.vo.user.UserLoginVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 用户登录拦截器
 * @date 2025/10/25 20:46
 */
public class UserLoginInterceptor implements HandlerInterceptor {
    /**
     * Redis模板
     */
    private RedisTemplate redisTemplate;

    /**
     * 构造函数
     *
     * @param redisTemplate Redis操作模板对象，用于访问Redis中的用户登录信息
     */
    public UserLoginInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 拦截器前置处理方法，在请求处理之前执行。
     * 主要功能是从请求头中获取token，并解析出用户信息存入上下文环境。
     *
     * @param request  HTTP请求对象，包含客户端发送的请求数据
     * @param response HTTP响应对象，用于向客户端返回数据
     * @param handler  被调用的处理器对象（Controller的方法）
     * @return boolean 返回true表示继续执行后续拦截器或目标方法；返回false则中断请求处理流程
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 解析并设置当前用户的登录信息到上下文中
        this.getUserLoginVo(request);
        return true;
    }

    /**
     * 从HTTP请求中提取token，并通过JWT工具类解析得到用户ID，
     * 再根据该用户ID从Redis缓存中获取完整的用户登录信息，
     * 并将这些信息保存至认证上下文持有者中供后续使用。
     *
     * @param request HTTP请求对象，用来获取请求头中的token字段
     */
    private void getUserLoginVo(HttpServletRequest request) {
        // 获取请求头中的token
        String token = request.getHeader("token");

        // 判断token是否为空
        if (!StringUtils.isEmpty(token)) {
            // 使用JwtHelper解析token获得用户ID
            Long userId = JwtHelper.getUserId(token);

            // 根据用户ID从Redis中查询对应的用户登录信息
            UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue()
                    .get(RedisConst.USER_LOGIN_KEY_PREFIX + userId);

            // 如果查找到有效的用户登录信息，则将其设置到认证上下文中
            if (userLoginVo != null) {
                AuthContextHolder.setUserLoginVo(userLoginVo);
                AuthContextHolder.setWareId(userLoginVo.getWareId());
                AuthContextHolder.setUserId(userLoginVo.getUserId());
            }
        }
    }
}

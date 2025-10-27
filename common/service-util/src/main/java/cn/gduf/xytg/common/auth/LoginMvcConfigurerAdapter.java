package cn.gduf.xytg.common.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 登录拦截器配置类
 * @date 2025/10/25 21:08
 */
@Configuration
public class LoginMvcConfigurerAdapter extends WebMvcConfigurationSupport {
    /**
     * Redis模板
     *
     * @Resource: 自动注入RedisTemplate对象(jdk自带且是通过byName注入)
     */
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 添加拦截器配置
     * 注册用户登录拦截器，对所有请求路径进行拦截，但排除登录和登出接口
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加用户登录拦截器，拦截所有路径，排除登录和登出接口
        registry.addInterceptor(new UserLoginInterceptor(redisTemplate))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/weixin/wxLogin/*");
        super.addInterceptors(registry);
    }
}


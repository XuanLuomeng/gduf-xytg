package cn.gduf.xytg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 跨越配置类
 * @date 2025/10/23 22:48
 */
@Configuration
public class CorsConfig {
    /**
     * 创建CorsWebFilter对象，用于处理跨域请求配置
     *
     * @return 配置好的CorsWebFilter实例
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        // 创建跨域配置对象并设置允许的源、头部和方法
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // 创建基于URL的跨域配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 注册全局跨域配置，对所有路径生效
        source.registerCorsConfiguration("/**", config);

        // 返回配置好的跨域过滤器
        return new CorsWebFilter(source);
    }
}

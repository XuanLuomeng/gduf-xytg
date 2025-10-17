package cn.gduf.xytg.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description MybatisPlus配置类
 * @date 2025/10/17 21:52
 */
@EnableTransactionManagement
@Configuration
@MapperScan("cn.gduf.xytg.*.mapper")
public class MybatisPlusConfig {

    /**
     * 配置 mp 插件
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor optimisticLockerInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 向 Mybatis 过滤器中添加分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

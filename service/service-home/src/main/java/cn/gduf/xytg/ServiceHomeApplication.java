package cn.gduf.xytg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 首页启动类
 * @date 2025/10/25 21:43
 */
// 本模块不需要数据源
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceHomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHomeApplication.class, args);
    }
}

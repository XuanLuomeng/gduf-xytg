package cn.gduf.xytg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品信息管理模块启动类
 * @date 2025/10/22 21:14
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class, args);
    }
}

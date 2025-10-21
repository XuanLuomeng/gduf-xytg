package cn.gduf.xytg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 区域管理模块启动类
 * @date 2025/10/21 22:39
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceSysApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSysApplication.class, args);
    }
}

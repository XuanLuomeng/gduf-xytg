package cn.gduf.xytg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 服务支付启动类
 * @date 2025/10/30 20:19
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServicePaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePaymentApplication.class, args);
    }
}

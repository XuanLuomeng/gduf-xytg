package cn.gduf.xytg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description ES 搜索服务启动类
 * @date 2025/10/23 19:20
 */
// 父类导入了数据库配置，这里需要取消掉连接数据源
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
// 启动服务发现功能
@EnableDiscoveryClient
// 启动feign功能
@EnableFeignClients
public class ServiceSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceSearchApplication.class, args);
    }

}
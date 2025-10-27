package cn.gduf.xytg.common.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description Mq配置类
 * @date 2025/10/23 19:55
 */
@Configuration
public class MQConfig {
    /**
     * 消息转换器
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}

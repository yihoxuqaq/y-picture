package top.yihoxu.ypicturebackend.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yihoxu
 * @date 2025/5/15  21:35
 * @description rabbitmq配置
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "cache.exchange";
    public static final String QUEUE_NAME = "cache.queue";
    public static final String ROUTING_KEY = "cache.key";

    @Bean
    public Queue myQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue myQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(myQueue).to(directExchange).with(ROUTING_KEY);
    }


}

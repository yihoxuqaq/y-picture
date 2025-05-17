package top.yihoxu.ypicturebackend.rabbitmq;

import cn.hutool.core.lang.UUID;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;

import static top.yihoxu.ypicturebackend.rabbitmq.RabbitMQConfig.*;

/**
 * @author yihoxu
 * @date 2025/5/15  22:20
 * @description 缓存消费者
 */
@Component
public class CacheConsumer {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = QUEUE_NAME),
            exchange= @Exchange(name = EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
    key = {ROUTING_KEY}))
    public void listener(String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        Set<String> keys = stringRedisTemplate.keys(msg);
        try {
            if (keys != null && !keys.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                //幂等性校验
                if (!checkAndMark(uuid)){
                    channel.basicAck(deliveryTag,false);
                    return;
                }
                stringRedisTemplate.delete(keys);
                channel.basicAck(deliveryTag,false);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkAndMark(String msgId) {
        return stringRedisTemplate.opsForValue().setIfAbsent("cache_msg:" + msgId, "1", Duration.ofMinutes(30));
    }

}

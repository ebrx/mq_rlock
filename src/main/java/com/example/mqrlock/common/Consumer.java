package com.example.mqrlock.common;

import com.example.mqrlock.config.Contants;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class Consumer {
    private final CountDownLatch latch = new CountDownLatch(1);

    @RabbitListener(id = Contants.QUEUE, queues = Contants.QUEUE)
    public void receive(String s, Message message, Channel channel) {
        try {
            long startTime = System.currentTimeMillis();
            log.info("delimiter: {}, {}", s, startTime);

            // 判断消息是否是被重新放回来的
            if (message.getMessageProperties().isRedelivered()) {
                log.info("消息是被重新放回来的");
            } else {
                log.info("消息是第一次投递");
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("{}确认完成, 耗时: {}", s, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            try {
                log.info("发生异常");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

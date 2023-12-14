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

    @RabbitListener(id = Contants.QUEUE,queues =Contants.QUEUE)
    public void receive(String s, Message message, Channel channel){
        try {
            long startTime = System.currentTimeMillis();
            log.info("delimiter :{},{}", s, startTime);
//            // 模拟异步处理
//            CompletableFuture.runAsync(() -> {
//                try {
//                    Thread.sleep(3000); // 模拟耗时操作
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                } finally {
//                    latch.countDown(); // 处理完成后释放 latch
//                }
//            });
//
//
//            log.info("等待异步处理完成...");
//            latch.await(); // 等待异步处理完成
//            log.info("确认完成, 耗时: {}", System.currentTimeMillis() - startTime);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

            log.info("{}确认完成,耗时:{}",s,System.currentTimeMillis() - startTime);
        }catch (Exception e) {
            try {
                log.info("发生异常");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

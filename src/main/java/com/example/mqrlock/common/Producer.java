package com.example.mqrlock.common;

import com.example.mqrlock.config.Contants;
import com.example.mqrlock.entity.TestEntity;
import com.example.mqrlock.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Producer {
    private final RabbitTemplate rabbitTemplate;
    private final RedissonClient redissonClient;

    @Autowired
    private TestService testService;

    @Autowired
    public Producer(RabbitTemplate rabbitTemplate, RedissonClient redissonClient) {
        this.rabbitTemplate = rabbitTemplate;
        this.redissonClient = redissonClient;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 3000)
    public void producerMsg() {
        // 获取分布式锁
        RLock lock = redissonClient.getLock("lock");
        try {
            // 阻塞等待获取锁
            lock.lock();
            log.info("成功获取锁，开始生产");

            List<TestEntity> list = this.testService.findTop5Entities();
            log.info("获取生产数据：{}条", list.size());
            // 更新状态为01
            List<TestEntity> testEntityList = list.stream().peek(testEntity -> testEntity.setStatus("01")).collect(Collectors.toList());

            // 获取成功后去用户表里面修改
            this.testService.saveAll(testEntityList);

            list.forEach((e) -> {
                log.info("发送消息：{}", e.getId());

                // 发送消息时，可以通过 CorrelationData 传递一些相关信息，例如消息的唯一标识
                CorrelationData correlationData = new CorrelationData(e.getId().toString());
                this.rabbitTemplate.convertAndSend(Contants.QUEUE, e.getId(), correlationData);
            });
        } catch (Exception e) {
            log.error("生产消息时发生异常", e);
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("释放锁");
            }
        }
    }

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(((correlationData, ack, cause) -> {
            if (ack) {
                String sId = correlationData.getId();
                log.info("生产消息成功：{}", sId);
            } else {
                String fId = correlationData.getId();
                log.error("生产消息失败：{}", fId);
                // 可以在这里进行失败的处理，例如重新发送或记录日志
            }
        }));
    }
}

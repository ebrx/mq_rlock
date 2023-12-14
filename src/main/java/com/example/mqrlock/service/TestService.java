package com.example.mqrlock.service;

import com.example.mqrlock.entity.TestEntity;
import com.example.mqrlock.repositroy.TestEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TestService {

    private final TestEntityRepository entityRepository;

    private final RedisTemplate redisTemplate;

    @Autowired
    public TestService(TestEntityRepository entityRepository, RedisTemplate redisTemplate) {
        this.entityRepository = entityRepository;
        this.redisTemplate = redisTemplate;
    }

//    @Scheduled(initialDelay = 3000, fixedDelay = 5000)
    public List<TestEntity> query(){
        List<TestEntity> l = entityRepository.findAll();
        redisTemplate.opsForValue().set("test_key" , "haha");
        log.info("postgres database mq_test count: {}",l.size());
        log.info("redis key1 resut: {}", redisTemplate.opsForValue().get("test_key"));
       return l;
    }
    public List<TestEntity> findTop5Entities(){
        Pageable pageable = new PageRequest(0, 5);
        List<TestEntity> l = entityRepository.findTop5Entities(pageable);
        log.info("postgres database mq_test count: {}",l.size());
        return l;
    }

    public List<TestEntity> saveAll(List<TestEntity> entityList){
        List<TestEntity> l = entityRepository.save(entityList);
        log.info("postgres database mq_test count: {}",l.size());
        return l;
    }
}

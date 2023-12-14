package com.example.mqrlock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalConfig {
    private final RedissonConfig redissonConfig;

    public LocalConfig(RedissonConfig redissonConfig) {
        this.redissonConfig = redissonConfig;
    }

    @Bean(Contants.QUEUE)
    public Queue queue() {
        return QueueBuilder.durable(Contants.QUEUE).build();
    }

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        if(redissonConfig.getClusterFlag()){
            config.useClusterServers().setPassword(redissonConfig.getPassword())
                    .setNodeAddresses(redissonConfig.getProcessNodes());
        }else{
            config.useSingleServer().setDatabase(redissonConfig.getDatabase())
                    .setPassword(redissonConfig.getPassword())
                    .setAddress(redissonConfig.getAddress());
        }
        return Redisson.create(config);
    }
}

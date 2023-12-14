package com.example.mqrlock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties("spring.redis")
public class RedissonConfig {
    private Integer database;
    private String host;
    private Integer port;

    private String password;

    public String getAddress(){
        return "redis://"+host+":"+port;
    }

    public List<String> getProcessNodes(){
        List<String> NODES = this.getClusterProperties().getNodes();
        return NODES.stream().map(node -> "redis://"+node).collect(Collectors.toList());
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ClusterProperties getClusterProperties() {
        return clusterProperties;
    }

    public void setClusterProperties(ClusterProperties clusterProperties) {
        this.clusterProperties = clusterProperties;
    }

    private ClusterProperties clusterProperties;
    public static class ClusterProperties{
        private List<String> nodes;
        public List<String> getNodes() {
            return nodes;
        }
        public void setNodes(List<String>nodes) {
            this.nodes = nodes;
        }
    }

    public Boolean getClusterFlag(){
        boolean clusterFlag = false;
        if(null != this.getClusterProperties()){
            clusterFlag = this.getClusterProperties().getNodes().size()>0;
        }
        return clusterFlag;
    }
}

package com.cloud.common.zookeeper.config;

import com.cloud.common.zookeeper.config.props.ZkProps;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Aijm
 * @Description Zookeeper配置类
 * @Date 2019/8/19
 */
@Configuration
@EnableConfigurationProperties(ZkProps.class)
public class ZkConfig {
    private final ZkProps zkProps;

    @Autowired
    public ZkConfig(ZkProps zkProps) {
        this.zkProps = zkProps;
    }

    @Bean
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkProps.getTimeout(), zkProps.getRetry());
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString(zkProps.getUrl())
                .namespace(zkProps.getNamespace())
                .authorization(zkProps.getScheme(), zkProps.getAuth().getBytes())
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        return client;
    }
}

package com.cloud.common.zookeeper.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author Aijm
 * @Description Zookeeper 配置项
 * @Date 2019/8/19
 */
@Data
@ConfigurationProperties(prefix = "zk")
public class ZkProps {
    /**
     * 连接地址
     */
    private String url = "127.0.0.1:2181";

    /**
     * 工作空间
     */
    private String namespace = "cloud";

    /**
     *
     */
    private String scheme = "digest";

    /**
     *
     */
    private String auth = "cloud:admin";

    /**
     * 超时时间(毫秒)，默认1000
     */
    private int timeout = 1000;

    /**
     * 重试次数，默认3
     */
    private int retry = 3;
}

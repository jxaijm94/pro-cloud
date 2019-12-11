package com.cloud.common.zookeeper.annotation;

import com.cloud.common.zookeeper.config.AutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启zk分布式锁
 * @author Aijm
 * @since 2019/11/3
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({AutoConfiguration.class})
public @interface EnableProZk {
}

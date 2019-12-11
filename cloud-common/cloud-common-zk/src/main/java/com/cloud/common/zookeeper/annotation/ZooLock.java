package com.cloud.common.zookeeper.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * @Author Aijm
 * @Description   基于Zookeeper的分布式锁注解
 * @Date 2019/8/19
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ZooLock {
    /**
     * 分布式锁的键
     */
    String key();

    /**
     * 锁释放时间，默认五秒
     */
    long timeout() default 5 * 1000;

    /**
     * 时间格式，默认：毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}

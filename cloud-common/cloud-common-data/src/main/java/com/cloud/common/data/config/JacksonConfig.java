package com.cloud.common.data.config;

import com.cloud.common.util.util.DateUtils;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * jackson序列化
 * @author Aijm
 * @since 2019/11/18
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer builderCustomizer() {

        return builder -> {
            // 所有Long类型转换成String到前台
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(DateUtils.DATE_FORMATTER));
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateUtils.DATETIME_FORMATTER));
        };
    }
}

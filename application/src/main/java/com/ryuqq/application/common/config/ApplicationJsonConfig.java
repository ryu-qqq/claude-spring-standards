package com.ryuqq.application.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * ApplicationJsonConfig - Application 레이어 JSON 설정
 *
 * <p>Application 레이어에서 사용하는 ObjectMapper 빈을 등록합니다.
 *
 * <p>주로 FeedbackQueue 페이로드 파싱에 사용됩니다.
 *
 * @author ryu-qqq
 */
@Configuration
public class ApplicationJsonConfig {

    @Bean
    @Primary
    public ObjectMapper applicationObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}

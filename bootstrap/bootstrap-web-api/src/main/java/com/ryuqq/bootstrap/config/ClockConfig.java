package com.ryuqq.bootstrap.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clock Bean 설정
 *
 * <p>Bootstrap Layer에서 Clock Bean을 등록합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>UseCase/Service에서 Clock.instant()로 현재 시간 획득
 *   <li>Aggregate에는 Instant 파라미터로 전달
 *   <li>테스트 환경에서는 Clock.fixed()로 교체 가능
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-21
 */
@Configuration
public class ClockConfig {

    /**
     * System Clock Bean 등록
     *
     * @return System Default Zone Clock
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}

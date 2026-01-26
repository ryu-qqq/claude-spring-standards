package com.ryuqq.adapter.out.persistence;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Persistence MySQL 모듈 테스트용 Spring Boot Application
 *
 * <p>persistence-mysql 모듈은 라이브러리 모듈이므로 main에 @SpringBootApplication이 없습니다. 테스트 시 이 클래스를 참조하여 Spring
 * Context를 구성합니다.
 *
 * <p><strong>설정 분리:</strong>
 *
 * <ul>
 *   <li>@EntityScan: Entity 클래스 스캔 (DDL 생성용)
 *   <li>@EnableJpaRepositories: JpaConfig에서 정의 (중복 방지)
 * </ul>
 *
 * <p>@DataJpaTest는 Auto-Configuration으로 Repository를 스캔하고, @SpringBootTest는
 * JpaConfig의 @EnableJpaRepositories를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@SpringBootApplication
@EntityScan(basePackages = "com.ryuqq.adapter.out.persistence")
public class TestPersistenceApplication {}

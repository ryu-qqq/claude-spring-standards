package com.ryuqq.adapter.in.rest;

import com.ryuqq.adapter.in.rest.config.UseCaseMockConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * REST API 모듈 테스트용 Spring Boot Application
 *
 * <p>rest-api 모듈은 라이브러리 모듈이므로 main에 @SpringBootApplication이 없습니다. @WebMvcTest 사용 시 이 클래스를 참조하여
 * Spring Context를 구성합니다.
 *
 * <p>UseCaseMockConfiguration을 import하여 모든 UseCase를 Mock으로 제공합니다.
 * SecurityConfig는 @SpringBootApplication의 컴포넌트 스캔으로 자동 로드되어 테스트에서 인증 없이 엔드포인트에 접근할 수 있습니다. 이를 통해
 * Controller 테스트에서 실제 비즈니스 로직이 아닌 HTTP 요청/응답 매핑만 테스트할 수 있습니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@SpringBootApplication
@Import(UseCaseMockConfiguration.class)
public class TestRestApiApplication {}

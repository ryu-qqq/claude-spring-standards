package com.ryuqq.application.architecture;

import com.ryuqq.testing.archunit.LombokProhibitionRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Application Layer 아키텍처 규칙 검증 테스트
 *
 * <p>Application Layer의 코딩 규칙을 자동으로 검증합니다:</p>
 * <ul>
 *   <li>Lombok 금지 (권장)</li>
 *   <li>UseCase Single Responsibility</li>
 *   <li>Transaction 경계 관리</li>
 *   <li>Command/Query 분리 (CQRS)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Application Layer 아키텍처 규칙 검증")
class ApplicationLayerArchitectureTest {

    private static JavaClasses applicationClasses;

    @BeforeAll
    static void setUp() {
        applicationClasses = new ClassFileImporter()
            .importPackages("com.ryuqq.application");
    }

    /**
     * Lombok 금지 규칙 검증
     *
     * <p>Application Layer는 다음 Lombok 어노테이션을 사용하지 않는 것을 권장합니다:</p>
     * <ul>
     *   <li>@Data - Plain Java getter/setter 사용</li>
     *   <li>@Builder - Static Factory Method 사용</li>
     *   <li>@Getter - 명시적 getter 메서드 작성</li>
     *   <li>@Setter - 불변성 유지 (Setter 금지)</li>
     *   <li>@AllArgsConstructor - 명시적 생성자 작성</li>
     *   <li>@NoArgsConstructor - 명시적 기본 생성자 작성</li>
     * </ul>
     *
     * @return 동적 테스트 스트림 (6개 테스트)
     */
    @TestFactory
    @DisplayName("Lombok 금지 규칙 (권장)")
    Stream<DynamicTest> lombokProhibitionTests() {
        return LombokProhibitionRules.generate("application", applicationClasses);
    }

    // TODO: 향후 추가 검증 규칙
    // - UseCase 네이밍 규칙: *UseCase로 끝나야 함
    // - Port 인터페이스 네이밍: *Port로 끝나야 함
    // - @Transactional 사용 검증 (UseCase 클래스에만 허용)
    // - Command/Query 분리 검증
}

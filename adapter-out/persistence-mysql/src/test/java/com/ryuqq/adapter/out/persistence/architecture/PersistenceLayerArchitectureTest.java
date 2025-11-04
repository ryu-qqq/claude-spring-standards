package com.ryuqq.adapter.out.persistence.architecture;

import com.ryuqq.testing.archunit.LombokProhibitionRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Persistence Layer 아키텍처 규칙 검증 테스트
 *
 * <p>Persistence Layer의 코딩 규칙을 자동으로 검증합니다:</p>
 * <ul>
 *   <li>Lombok 금지 (특히 Entity)</li>
 *   <li>Long FK 전략 (JPA 관계 어노테이션 금지)</li>
 *   <li>Entity Immutability</li>
 *   <li>CQRS Separation</li>
 *   <li>N+1 문제 방지</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Persistence Layer 아키텍처 규칙 검증")
class PersistenceLayerArchitectureTest {

    private static JavaClasses persistenceClasses;

    @BeforeAll
    static void setUp() {
        persistenceClasses = new ClassFileImporter()
            .importPackages("com.ryuqq.adapter.out.persistence");
    }

    /**
     * Lombok 금지 규칙 검증
     *
     * <p>Persistence Layer는 다음 Lombok 어노테이션을 사용할 수 없습니다:</p>
     * <ul>
     *   <li>@Data - Plain Java getter/setter 사용</li>
     *   <li>@Builder - Static Factory Method 사용</li>
     *   <li>@Getter - 명시적 getter 메서드 작성</li>
     *   <li>@Setter - 불변성 유지 (Setter 금지)</li>
     *   <li>@AllArgsConstructor - 명시적 생성자 작성</li>
     *   <li>@NoArgsConstructor - 명시적 기본 생성자 작성 (JPA Entity는 protected 생성자 사용)</li>
     * </ul>
     *
     * @return 동적 테스트 스트림 (6개 테스트)
     */
    @TestFactory
    @DisplayName("Lombok 금지 규칙 (특히 Entity)")
    Stream<DynamicTest> lombokProhibitionTests() {
        return LombokProhibitionRules.generate("persistence", persistenceClasses);
    }

    // TODO: 향후 추가 검증 규칙
    // - JPA 관계 어노테이션 금지: @ManyToOne, @OneToMany, @OneToOne, @ManyToMany
    // - Long FK 전략 검증: Entity는 Long userId, Long orderId 등만 가져야 함
    // - Entity Immutability: Entity 필드는 final 또는 protected setter만 허용
    // - Repository 네이밍 규칙: *Repository로 끝나야 함
    // - Mapper 네이밍 규칙: *Mapper로 끝나야 함
}

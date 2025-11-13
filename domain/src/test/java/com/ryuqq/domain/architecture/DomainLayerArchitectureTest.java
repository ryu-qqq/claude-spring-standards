package com.ryuqq.domain.architecture;

import com.ryuqq.testing.archunit.FieldFinalCheckRules;
import com.ryuqq.testing.archunit.LombokProhibitionRules;
import com.ryuqq.testing.archunit.MethodExistenceCheckRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Domain Layer 아키텍처 규칙 검증 테스트
 *
 * <p>Domain Layer의 코딩 규칙을 자동으로 검증합니다:</p>
 * <ul>
 *   <li>Lombok 금지 (@Data, @Builder, @Getter, @Setter 등)</li>
 *   <li>ID 필드 final 강제</li>
 *   <li>reconstitute() 메서드 존재 검증</li>
 *   <li>getIdValue() 메서드 존재 검증 (Law of Demeter)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Domain Layer 아키텍처 규칙 검증")
class DomainLayerArchitectureTest {

    private static JavaClasses domainClasses;

    @BeforeAll
    static void setUp() {
        domainClasses = new ClassFileImporter()
            .importPackages("com.ryuqq.domain");
    }

    /**
     * Lombok 금지 규칙 검증
     *
     * <p>Domain Layer는 다음 Lombok 어노테이션을 사용할 수 없습니다:</p>
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
    @DisplayName("Lombok 금지 규칙")
    Stream<DynamicTest> lombokProhibitionTests() {
        return LombokProhibitionRules.generate("domain", domainClasses);
    }

    /**
     * ID 필드 Final 검증
     *
     * <p>Domain Layer의 모든 Entity는 ID 필드를 final로 선언해야 합니다.</p>
     * <p>이유: ID는 생성 후 변경되면 안 됩니다 (불변성 보장)</p>
     *
     * @return 동적 테스트 스트림
     */
    @TestFactory
    @DisplayName("ID 필드 Final 검증")
    Stream<DynamicTest> idFieldFinalTests() {
        return FieldFinalCheckRules.generateIdFieldCheck("domain", domainClasses);
    }

    /**
     * reconstitute() 메서드 존재 검증
     *
     * <p>Domain Entity는 DB에서 복원 시 사용하는 static reconstitute() 메서드를 반드시 가져야 합니다.</p>
     * <p>이유: JPA Entity → Domain Entity 변환 시 reconstitute()를 통해 복원</p>
     *
     * @return 동적 테스트 스트림
     */
    @TestFactory
    @DisplayName("reconstitute() 메서드 존재 검증")
    Stream<DynamicTest> reconstituteMethodTests() {
        return MethodExistenceCheckRules.generateReconstituteCheck("domain", domainClasses);
    }

    /**
     * getIdValue() 메서드 존재 검증
     *
     * <p>Domain Entity는 Law of Demeter 준수를 위해 getIdValue() 메서드를 반드시 가져야 합니다.</p>
     * <p>이유: Getter 체이닝 방지 (예: order.getId().getValue() → order.getIdValue())</p>
     *
     * @return 동적 테스트 스트림
     */
    @TestFactory
    @DisplayName("getIdValue() 메서드 존재 검증 (Law of Demeter)")
    Stream<DynamicTest> getIdValueMethodTests() {
        return MethodExistenceCheckRules.generateGetIdValueCheck("domain", domainClasses);
    }
}

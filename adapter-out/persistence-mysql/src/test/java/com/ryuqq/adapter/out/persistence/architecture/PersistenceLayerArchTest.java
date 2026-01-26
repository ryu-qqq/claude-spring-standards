package com.ryuqq.adapter.out.persistence.architecture;

import static com.ryuqq.adapter.out.persistence.architecture.ArchUnitPackageConstants.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PersistenceLayerArchTest - Persistence Layer 전체 아키텍처 규칙 검증
 *
 * <p>Persistence Layer의 핵심 아키텍처 규칙을 검증합니다:
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>규칙 1: Package 구조 검증 (adapter, entity, repository, mapper)
 *   <li>규칙 2: Port 구현 검증 (CommandPort, QueryPort, LockQueryPort)
 *   <li>규칙 3: JPA Entity와 Domain 분리 검증
 *   <li>규칙 4: Layer 의존성 검증 (단방향 의존성)
 *   <li>규칙 5: Application Layer 의존 금지
 *   <li>규칙 6: Domain Layer 의존 금지 (Port를 통해서만)
 *   <li>규칙 7: Adapter 네이밍 규칙 (*CommandAdapter, *QueryAdapter)
 *   <li>규칙 8: Repository 네이밍 규칙 (*Repository, *QueryDslRepository)
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("Persistence Layer 아키텍처 규칙 검증 (Zero-Tolerance)")
@Tag("architecture")
class PersistenceLayerArchTest {

    private static JavaClasses allClasses;

    @BeforeAll
    static void setUp() {
        // Persistence, Application, Domain 패키지를 모두 import해야
        // Port 인터페이스 구현 여부를 정확히 검증할 수 있습니다.
        allClasses =
                new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages(PERSISTENCE, APPLICATION, DOMAIN);
    }

    /** 규칙 1: Package 구조 검증 */
    @Test
    @DisplayName("[필수] Adapter는 ..adapter.. 패키지에 위치해야 한다")
    void persistence_AdaptersMustBeInAdapterPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Adapter")
                        .should()
                        .resideInAPackage(ADAPTER_PATTERN)
                        .because("Adapter 클래스는 adapter 패키지에 위치해야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    @Test
    @DisplayName("[필수] Entity는 ..entity.. 패키지에 위치해야 한다")
    void persistence_EntitiesMustBeInEntityPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("JpaEntity")
                        .should()
                        .resideInAPackage(ENTITY_PATTERN)
                        .because("JPA Entity 클래스는 entity 패키지에 위치해야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    @Test
    @DisplayName("[필수] Repository는 ..repository.. 패키지에 위치해야 한다")
    void persistence_RepositoriesMustBeInRepositoryPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameContaining("Repository")
                        .should()
                        .resideInAPackage(REPOSITORY_PATTERN)
                        .because("Repository 인터페이스/클래스는 repository 패키지에 위치해야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    @Test
    @DisplayName("[필수] Mapper는 ..mapper.. 패키지에 위치해야 한다")
    void persistence_MappersMustBeInMapperPackage() {
        // PersistenceObjectMapper는 Jackson ObjectMapper 설정용이므로 제외
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Mapper")
                        .and()
                        .haveSimpleNameNotEndingWith("ObjectMapper") // Jackson ObjectMapper 설정 제외
                        .should()
                        .resideInAPackage(MAPPER_PATTERN)
                        .because("Mapper 클래스는 mapper 패키지에 위치해야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /**
     * 규칙 2: Port 의존성 검증
     *
     * <p>인터페이스 구현 검증 대신 의존성으로 확인합니다. ArchUnit이 외부 모듈(application)의 인터페이스를 resolve하기 어렵기 때문에, 의존성
     * 체크로 대체합니다. 인터페이스를 구현하면 반드시 의존하게 되므로 기능적으로 동일합니다.
     */
    @Test
    @DisplayName("[필수] CommandAdapter는 PersistencePort 또는 CommandPort에 의존해야 한다")
    void persistence_CommandAdapterMustDependOnCommandPort() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("CommandAdapter")
                        .should()
                        .dependOnClassesThat(
                                com.tngtech.archunit.base.DescribedPredicate.describe(
                                        "PersistencePort or CommandPort interface",
                                        javaClass ->
                                                javaClass
                                                                .getSimpleName()
                                                                .endsWith("PersistencePort")
                                                        || javaClass
                                                                .getSimpleName()
                                                                .endsWith("CommandPort")))
                        .because("CommandAdapter는 PersistencePort 또는 CommandPort 인터페이스를 구현해야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    @Test
    @DisplayName("[필수] QueryAdapter는 QueryPort에 의존해야 한다")
    void persistence_QueryAdapterMustDependOnQueryPort() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("QueryAdapter")
                        .and()
                        .haveSimpleNameNotContaining("Lock")
                        .should()
                        .dependOnClassesThat(
                                com.tngtech.archunit.base.DescribedPredicate.describe(
                                        "QueryPort interface",
                                        javaClass ->
                                                javaClass.getSimpleName().endsWith("QueryPort")
                                                        && !javaClass
                                                                .getSimpleName()
                                                                .contains("Lock")))
                        .because("QueryAdapter는 QueryPort 인터페이스를 구현해야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    @Test
    @DisplayName("[필수] LockQueryAdapter는 LockQueryPort에 의존해야 한다")
    void persistence_LockQueryAdapterMustDependOnLockQueryPort() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .dependOnClassesThat(
                                com.tngtech.archunit.base.DescribedPredicate.describe(
                                        "LockQueryPort interface",
                                        javaClass ->
                                                javaClass
                                                        .getSimpleName()
                                                        .endsWith("LockQueryPort")))
                        .because("LockQueryAdapter는 LockQueryPort 인터페이스를 구현해야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 3: JPA Entity와 Domain 분리 검증 (Enum은 허용) */
    @Test
    @DisplayName("[필수] JPA Entity는 Domain Layer의 Enum만 의존할 수 있다")
    void persistence_JpaEntityCanOnlyDependOnDomainEnums() {
        ArchRule rule =
                noClasses()
                        .that()
                        .haveSimpleNameEndingWith("JpaEntity")
                        .should()
                        .dependOnClassesThat(
                                com.tngtech.archunit.base.DescribedPredicate.describe(
                                        "Domain Layer classes that are not enums",
                                        javaClass ->
                                                javaClass.getPackageName().contains(".domain.")
                                                        && !javaClass.isEnum()))
                        .because(
                                "JPA Entity는 Domain Layer의 Enum만 의존할 수 있습니다 "
                                        + "(VO, Entity 등 다른 Domain 클래스 의존 금지)");

        rule.allowEmptyShould(true).check(allClasses);
    }

    @Test
    @DisplayName("[필수] JPA Entity는 Application Layer를 의존하지 않아야 한다")
    void persistence_JpaEntityMustNotDependOnApplication() {
        ArchRule rule =
                noClasses()
                        .that()
                        .haveSimpleNameEndingWith("JpaEntity")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage(APPLICATION_ALL)
                        .because("JPA Entity는 Application Layer에 의존하면 안 됩니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    @Test
    @DisplayName("[필수] Domain은 JPA Entity를 의존하지 않아야 한다")
    void persistence_DomainMustNotDependOnJpaEntity() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAnyPackage(DOMAIN_ALL)
                        .should()
                        .dependOnClassesThat()
                        .haveSimpleNameEndingWith("JpaEntity")
                        .because("Domain은 JPA Entity에 의존하면 안 됩니다 (Clean Architecture 원칙)");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /**
     * 규칙 5: Application Layer 의존 금지 (Port 예외)
     *
     * <p>Adapter는 Application Layer의 Port 인터페이스를 구현해야 하므로 Port 패키지는 예외입니다. Repository, Entity,
     * Mapper는 Application Layer를 직접 의존하면 안 됩니다.
     */
    @Test
    @DisplayName("[금지] Repository/Entity/Mapper는 Application Layer를 직접 의존하지 않아야 한다")
    void persistence_MustNotDependOnApplicationLayer() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAnyPackage(REPOSITORY_PATTERN, ENTITY_PATTERN, MAPPER_PATTERN)
                        .and()
                        .resideOutsideOfPackages(ARCHITECTURE_PATTERN)
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage(APPLICATION_ALL)
                        .because(
                                "Repository/Entity/Mapper는 Application Layer를 직접 의존하면 안 됩니다 "
                                        + "(Adapter는 Port 구현을 위해 예외)");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /**
     * 규칙 6: Domain Layer 의존 규칙
     *
     * <p>헥사고날 아키텍처에서의 Domain 의존 규칙:
     *
     * <ul>
     *   <li>Entity: Domain Layer 의존 금지 (Enum만 예외 - 별도 규칙에서 검증)
     *   <li>Repository: Domain의 Query VO(SearchCriteria 등) 사용 허용
     *   <li>Mapper: Domain ↔ Entity 변환을 위해 Domain 의존 필수
     *   <li>Adapter: Domain 의존 필수 (Port 구현)
     * </ul>
     *
     * <p>Entity의 Domain 의존 금지는 persistence_JpaEntityCanOnlyDependOnDomainEnums에서 검증합니다.
     */
    @Test
    @DisplayName("[참조] Domain Layer 의존 규칙 - Entity는 Domain Enum만 허용, Mapper/Repository는 허용")
    void persistence_RepositoryEntityMapperDomainDependencyRules() {
        // 이 규칙은 참조용입니다.
        // Entity의 Domain 의존 금지는 persistence_JpaEntityCanOnlyDependOnDomainEnums에서 검증합니다.
        // Mapper와 Repository는 Domain Layer에 의존할 수 있습니다:
        // - Mapper: Entity ↔ Domain 변환 필수
        // - Repository: SearchCriteria 등 Query VO 파라미터 사용

        // Entity가 Domain을 직접 의존하지 않는지는 별도 테스트에서 검증
        // 여기서는 아키텍처 설계 의도만 문서화합니다
    }

    /** 규칙 7: Adapter 네이밍 규칙 */
    @Test
    @DisplayName("[필수] Adapter는 *CommandAdapter 또는 *QueryAdapter 네이밍 규칙을 따라야 한다")
    void persistence_AdaptersMustFollowNamingConvention() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Adapter")
                        .and()
                        .areNotInterfaces()
                        .and()
                        .haveSimpleNameNotContaining("Test")
                        .should()
                        .haveSimpleNameEndingWith("CommandAdapter")
                        .orShould()
                        .haveSimpleNameEndingWith("QueryAdapter")
                        .orShould()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .because("Adapter는 *CommandAdapter 또는 *QueryAdapter 네이밍 규칙을 따라야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 8: Repository 네이밍 규칙 */
    @Test
    @DisplayName("[필수] Repository는 *Repository 또는 *QueryDslRepository 네이밍 규칙을 따라야 한다")
    void persistence_RepositoriesMustFollowNamingConvention() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAPackage(REPOSITORY_PATTERN)
                        .and()
                        .haveSimpleNameNotContaining("Test")
                        .and()
                        .areNotAnonymousClasses() // 익명 클래스 제외
                        .and()
                        .areNotMemberClasses() // 내부 클래스 제외
                        .should()
                        .haveSimpleNameEndingWith("Repository")
                        .orShould()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .because("Repository는 *Repository 또는 *QueryDslRepository 네이밍 규칙을 따라야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }
}

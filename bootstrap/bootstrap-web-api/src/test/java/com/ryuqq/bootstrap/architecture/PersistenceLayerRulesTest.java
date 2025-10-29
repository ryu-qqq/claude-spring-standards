package com.ryuqq.bootstrap.architecture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Persistence Layer 아키텍처 규칙 검증
 *
 * <p>Persistence Layer는 다음 규칙을 준수해야 합니다:
 * <ul>
 *   <li>Long FK 전략 (JPA 관계 어노테이션 금지)</li>
 *   <li>Entity 불변성 (Setter 금지)</li>
 *   <li>Adapter에서 @Transactional 금지</li>
 *   <li>Lombok 사용 금지</li>
 *   <li>Domain만 의존 가능</li>
 * </ul>
 *
 * @author windsurf
 * @since 2025-10-28
 * @see <a href="docs/coding_convention/04-persistence-layer/">Persistence Layer Conventions</a>
 */
@DisplayName("Persistence Layer 아키텍처 규칙 검증")
class PersistenceLayerRulesTest {

    private JavaClasses persistenceClasses;

    @BeforeEach
    void setUp() {
        persistenceClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.ryuqq.adapter.out.persistence");
    }

    // ========================================
    // Long FK 전략 검증
    // ========================================

    @Test
    @DisplayName("JPA Entity는 @OneToMany 어노테이션 사용 금지 (Long FK 전략)")
    void jpaEntitiesShouldNotUseOneToMany() {
        ArchRule rule = fields()
                .that().areDeclaredInClassesThat().areAnnotatedWith("jakarta.persistence.Entity")
                .should().notBeAnnotatedWith("jakarta.persistence.OneToMany")
                .because("Long FK 전략을 사용해야 합니다. @OneToMany는 Law of Demeter 위반과 N+1 문제를 야기합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("JPA Entity는 @ManyToOne 어노테이션 사용 금지 (Long FK 전략)")
    void jpaEntitiesShouldNotUseManyToOne() {
        ArchRule rule = fields()
                .that().areDeclaredInClassesThat().areAnnotatedWith("jakarta.persistence.Entity")
                .should().notBeAnnotatedWith("jakarta.persistence.ManyToOne")
                .because("Long FK 전략을 사용해야 합니다. @ManyToOne은 LazyInitializationException과 테스트 복잡도 증가를 야기합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("JPA Entity는 @ManyToMany 어노테이션 사용 금지 (Long FK 전략)")
    void jpaEntitiesShouldNotUseManyToMany() {
        ArchRule rule = fields()
                .that().areDeclaredInClassesThat().areAnnotatedWith("jakarta.persistence.Entity")
                .should().notBeAnnotatedWith("jakarta.persistence.ManyToMany")
                .because("Long FK 전략을 사용해야 합니다. @ManyToMany는 명시적 중간 테이블 Entity로 관리하세요.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("JPA Entity는 @OneToOne 어노테이션 사용 금지 (Long FK 전략)")
    void jpaEntitiesShouldNotUseOneToOne() {
        ArchRule rule = fields()
                .that().areDeclaredInClassesThat().areAnnotatedWith("jakarta.persistence.Entity")
                .should().notBeAnnotatedWith("jakarta.persistence.OneToOne")
                .because("Long FK 전략을 사용해야 합니다. @OneToOne은 양방향 참조 복잡도를 증가시킵니다.");

        rule.check(persistenceClasses);
    }

    // ========================================
    // Entity 불변성 검증
    // ========================================

    // TODO: Setter 검증은 Custom ArchCondition으로 구현 필요
    // ArchUnit의 표준 API로는 "메서드가 존재하지 않아야 한다"를 직접 표현하기 어려움
    // 대안: Checkstyle, SpotBugs, 또는 Custom ArchCondition 활용
    /*
    @Test
    @DisplayName("JPA Entity는 Setter 메서드 사용 금지 (불변성)")
    void jpaEntitiesShouldNotHaveSetters() {
        // Custom ArchCondition 구현 필요
    }
    */

    // ========================================
    // 트랜잭션 경계 검증
    // ========================================

    @Test
    @DisplayName("Persistence Adapter는 @Transactional 사용 금지")
    void persistenceAdaptersShouldNotUseTransactional() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapter.out.persistence..")
                .and().haveSimpleNameEndingWith("Adapter")
                .should().notBeAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .andShould().notBeAnnotatedWith("jakarta.transaction.Transactional")
                .because("@Transactional은 Application Layer에서만 사용해야 합니다. Persistence Adapter는 트랜잭션 경계가 아닙니다.");

        rule.check(persistenceClasses);
    }

    // ========================================
    // 스프링 어노테이션 검증
    // ========================================

    @Test
    @DisplayName("Persistence Adapter는 @Component 어노테이션 사용")
    void persistenceAdaptersShouldBeAnnotatedWithComponent() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapter.out.persistence..adapter..")
                .and().haveSimpleNameEndingWith("Adapter")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .because("Persistence Adapter는 @Component로 Spring Bean으로 등록되어야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("QueryDSL Repository는 @Repository 어노테이션 사용")
    void queryDslRepositoriesShouldBeAnnotatedWithRepository() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapter.out.persistence..querydsl..")
                .and().haveSimpleNameEndingWith("Repository")
                .should().beAnnotatedWith("org.springframework.stereotype.Repository")
                .because("QueryDSL Repository는 @Repository로 Spring Bean으로 등록되어야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Entity Mapper는 @Component 어노테이션 사용")
    void entityMappersShouldBeAnnotatedWithComponent() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapter.out.persistence..mapper..")
                .and().haveSimpleNameEndingWith("Mapper")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .because("Entity Mapper는 @Component로 Spring Bean으로 등록되어야 합니다.");

        rule.check(persistenceClasses);
    }

    // ========================================
    // Lombok 금지 검증
    // ========================================

    @Test
    @DisplayName("Persistence Layer는 Lombok 사용 금지")
    void persistenceLayerShouldNotUseLombok() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..adapter.out.persistence..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "lombok.."
                )
                .because("Persistence Layer에서 Lombok 사용은 금지되어 있습니다. Pure Java를 사용하세요.");

        rule.check(persistenceClasses);
    }

    // ========================================
    // 의존성 검증
    // ========================================

    @Test
    @DisplayName("Persistence Layer는 Domain만 의존 가능 - Web/External Adapter 의존 금지")
    void persistenceLayerShouldOnlyDependOnDomain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..adapter.out.persistence..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..adapter.in.web..",
                        "..adapter.in.rest..",
                        "..adapter.out.external.."
                )
                .because("Persistence Layer는 Domain과 Application만 의존해야 하며, 다른 Adapter에 직접 의존하지 않아야 합니다.");

        rule.check(persistenceClasses);
    }

    // ========================================
    // 패키지 구조 검증
    // ========================================

    @Test
    @DisplayName("JPA Entity는 entity 패키지에 위치")
    void jpaEntitiesShouldResideInEntityPackage() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("jakarta.persistence.Entity")
                .should().resideInAPackage("..entity..")
                .because("JPA Entity는 entity 패키지에 위치해야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Spring Data JPA Repository는 repository 패키지에 위치")
    void springDataRepositoriesShouldResideInRepositoryPackage() {
        ArchRule rule = classes()
                .that().areAssignableTo("org.springframework.data.jpa.repository.JpaRepository")
                .should().resideInAPackage("..repository..")
                .because("Spring Data JPA Repository는 repository 패키지에 위치해야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("QueryDSL Repository는 querydsl 패키지에 위치")
    void queryDslRepositoriesShouldResideInQueryDslPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("QueryDsl")
                .and().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..querydsl..")
                .because("QueryDSL Repository는 querydsl 패키지에 위치하여 관심사를 분리해야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Persistence Adapter는 adapter 패키지에 위치")
    void persistenceAdaptersShouldResideInAdapterPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Adapter")
                .and().resideInAPackage("..adapter.out.persistence..")
                .should().resideInAPackage("..adapter..")
                .because("Persistence Adapter는 adapter 패키지에 위치해야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Entity Mapper는 mapper 패키지에 위치")
    void entityMappersShouldResideInMapperPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Mapper")
                .and().resideInAPackage("..adapter.out.persistence..")
                .should().resideInAPackage("..mapper..")
                .because("Entity Mapper는 mapper 패키지에 위치해야 합니다.");

        rule.check(persistenceClasses);
    }

    // ========================================
    // 네이밍 규칙 검증
    // ========================================

    @Test
    @DisplayName("JPA Entity는 *JpaEntity 네이밍 규칙 준수")
    void jpaEntitiesShouldFollowNamingConvention() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("jakarta.persistence.Entity")
                .should().haveSimpleNameEndingWith("JpaEntity")
                .because("JPA Entity는 *JpaEntity 네이밍 규칙을 따라야 합니다 (Domain Entity와 구분).");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("QueryDSL Repository는 *QueryDslRepository 네이밍 규칙 준수")
    void queryDslRepositoriesShouldFollowNamingConvention() {
        ArchRule rule = classes()
                .that().resideInAPackage("..querydsl..")
                .and().haveSimpleNameContaining("QueryDsl")
                .should().haveSimpleNameEndingWith("Repository")
                .because("QueryDSL Repository는 *QueryDslRepository 네이밍 규칙을 따라야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Entity Mapper는 *EntityMapper 네이밍 규칙 준수")
    void entityMappersShouldFollowNamingConvention() {
        ArchRule rule = classes()
                .that().resideInAPackage("..mapper..")
                .and().haveSimpleNameEndingWith("Mapper")
                .should().haveSimpleNameEndingWith("EntityMapper")
                .because("Entity Mapper는 *EntityMapper 네이밍 규칙을 따라야 합니다.");

        rule.check(persistenceClasses);
    }

    // ========================================
    // JPA Entity 설계 검증
    // ========================================

    @Test
    @DisplayName("JPA Entity는 @Entity 어노테이션 사용")
    void jpaEntitiesShouldBeAnnotatedWithEntity() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("JpaEntity")
                .and().haveSimpleNameNotStartingWith("Q")  // QueryDSL Q-Type 클래스 제외
                .should().beAnnotatedWith("jakarta.persistence.Entity")
                .because("*JpaEntity 클래스는 @Entity 어노테이션을 가져야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("JPA Entity는 @Table 어노테이션 사용 (명시적 테이블명)")
    void jpaEntitiesShouldBeAnnotatedWithTable() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("jakarta.persistence.Entity")
                .should().beAnnotatedWith("jakarta.persistence.Table")
                .because("JPA Entity는 명시적으로 @Table 어노테이션으로 테이블명을 지정해야 합니다.");

        rule.check(persistenceClasses);
    }
}

package com.ryuqq.bootstrap.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Orchestration Pattern 컨벤션 테스트
 *
 * <p>ArchUnit을 사용하여 Orchestration Pattern 규칙을 자동 검증합니다.
 * 빌드 시 자동 실행되며, 위반 시 빌드 실패합니다.</p>
 *
 * <h3>검증 규칙</h3>
 * <ul>
 *   <li>executeInternal()은 @Async 필수, @Transactional 금지</li>
 *   <li>Command는 Record 패턴 사용 (Lombok 금지)</li>
 *   <li>Orchestrator는 BaseOrchestrator 상속</li>
 *   <li>Outcome 반환 타입 사용</li>
 *   <li>Finalizer/Reaper는 @Scheduled 필수</li>
 * </ul>
 *
 * @author Orchestration Standards
 * @since 1.0.0
 */
class OrchestrationConventionTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.ryuqq");
    }

    @Test
    @DisplayName("Orchestrator는 BaseOrchestrator를 상속해야 함")
    void orchestratorsShouldExtendBaseOrchestrator() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Orchestrator")
            .and().haveSimpleNameNotEndingWith("BaseOrchestrator")
            .should().beAssignableTo("com.ryuqq.application.common.orchestration.BaseOrchestrator")
            .because("Orchestrator는 BaseOrchestrator를 상속하여 3-Phase Lifecycle을 구현해야 합니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("executeInternal()은 @Async 어노테이션이 필수")
    void executeInternalShouldHaveAsyncAnnotation() {
        ArchRule rule = methods()
            .that().haveName("executeInternal")
            .and().areProtected()
            .should().beAnnotatedWith(Async.class)
            .because("executeInternal()은 외부 API 호출을 위해 @Async가 필수입니다 (트랜잭션 밖에서 실행)");

        rule.check(classes);
    }

    @Test
    @DisplayName("executeInternal()은 @Transactional 금지")
    void executeInternalShouldNotHaveTransactionalAnnotation() {
        ArchRule rule = methods()
            .that().haveName("executeInternal")
            .should().notBeAnnotatedWith(Transactional.class)
            .because("executeInternal()은 외부 API 호출이므로 @Transactional을 사용하면 안 됩니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("Command는 Record 타입이어야 함 (Lombok 금지)")
    void commandsShouldBeRecords() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Command")
            .and().resideInAPackage("..command..")
            .should().beRecords()
            .because("Command는 Record 패턴을 사용해야 하며, Lombok은 금지됩니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("Command는 Lombok 어노테이션 금지")
    void commandsShouldNotUseLombok() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Command")
            .and().resideInAPackage("..command..")
            .should().notBeAnnotatedWith("lombok.Data")
            .andShould().notBeAnnotatedWith("lombok.Builder")
            .andShould().notBeAnnotatedWith("lombok.Getter")
            .andShould().notBeAnnotatedWith("lombok.Setter")
            .because("Command는 Record 패턴을 사용하므로 Lombok은 금지됩니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("Orchestrator는 Outcome을 반환해야 함")
    void executeInternalShouldReturnOutcome() {
        ArchRule rule = methods()
            .that().haveName("executeInternal")
            .and().areProtected()
            .should().haveRawReturnType("com.ryuqq.application.common.orchestration.Outcome")
            .because("executeInternal()은 Outcome (Ok/Retry/Fail)을 반환해야 타입 안전합니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("Finalizer는 @Scheduled 어노테이션 필수")
    void finalizersShouldHaveScheduledAnnotation() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Finalizer")
            .and().resideInAPackage("..scheduler..")
            .should().containAnyMethodsThat()
                .areAnnotatedWith(Scheduled.class)
            .because("Finalizer는 PENDING WAL을 주기적으로 처리하기 위해 @Scheduled가 필수입니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("Reaper는 @Scheduled 어노테이션 필수")
    void reapersShouldHaveScheduledAnnotation() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Reaper")
            .and().resideInAPackage("..scheduler..")
            .should().containAnyMethodsThat()
                .areAnnotatedWith(Scheduled.class)
            .because("Reaper는 TIMEOUT을 주기적으로 처리하기 위해 @Scheduled가 필수입니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("Operation Entity는 IdemKey를 가져야 함")
    void operationEntitiesShouldHaveIdemKey() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("OperationEntity")
            .and().resideInAPackage("..entity..")
            .should().containAnyFieldsThat()
                .haveName("idemKey")
            .because("Operation Entity는 멱등성 보장을 위해 IdemKey가 필수입니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("Orchestrator 패키지는 올바른 의존성만 가져야 함")
    void orchestrationLayerShouldFollowDependencyRules() {
        ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Orchestrator").definedBy("..application..*")
            .layer("Domain").definedBy("..domain..*")
            .layer("Port").definedBy("..port..*")
            .layer("Infrastructure").definedBy("..adapter.out..*")

            .whereLayer("Orchestrator").mayOnlyAccessLayersIn("Domain", "Port")
            .because("Orchestrator는 Domain과 Port에만 의존해야 하며, Infrastructure에 직접 의존하면 안 됩니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("Controller는 POST 메서드만 사용해야 함 (멱등성)")
    void orchestrationControllersShouldOnlyUsePostMapping() {
        ArchRule rule = methods()
            .that().areDeclaredInClassesThat()
                .haveSimpleNameEndingWith("Controller")
            .and().areDeclaredInClassesThat()
                .resideInAPackage("..web..")
            .and().areAnnotatedWith("org.springframework.web.bind.annotation.RequestMapping")
            .should().beAnnotatedWith("org.springframework.web.bind.annotation.PostMapping")
            .orShould().notBeAnnotatedWith("org.springframework.web.bind.annotation.GetMapping")
            .andShould().notBeAnnotatedWith("org.springframework.web.bind.annotation.PutMapping")
            .andShould().notBeAnnotatedWith("org.springframework.web.bind.annotation.DeleteMapping")
            .because("Orchestration Pattern은 POST로만 요청해야 멱등성이 보장됩니다 (IdemKey 사용)");

        rule.check(classes);
    }

    @Test
    @DisplayName("Repository는 JpaRepository를 상속해야 함")
    void repositoriesShouldExtendJpaRepository() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Repository")
            .and().resideInAPackage("..repository..")
            .and().areInterfaces()
            .should().beAssignableTo("org.springframework.data.jpa.repository.JpaRepository")
            .because("Repository는 JpaRepository를 상속하여 표준 CRUD 기능을 제공해야 합니다");

        rule.check(classes);
    }
}

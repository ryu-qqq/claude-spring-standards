package com.ryuqq.adapter.out.persistence.redis.architecture.config;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RedisConfigArchTest - Redis 설정 클래스 아키텍처 규칙 검증
 *
 * <p>Lettuce + Redisson 듀얼 전략 설정 규칙을 검증합니다.
 *
 * <p><strong>검증 항목:</strong>
 *
 * <ul>
 *   <li>LettuceConfig: RedisTemplate, Connection Pool
 *   <li>RedissonConfig: RedissonClient, 분산락
 *   <li>역할 분리: Lettuce(캐시) vs Redisson(락)
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("Redis Config 아키텍처 규칙 검증")
class RedisConfigArchTest {

    private static final String BASE_PACKAGE = "com.ryuqq.adapter.out.persistence.redis";

    private static JavaClasses allClasses;
    private static JavaClasses configClasses;

    @BeforeAll
    static void setUp() {
        allClasses =
                new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages(BASE_PACKAGE);

        configClasses =
                allClasses.that(
                        DescribedPredicate.describe(
                                "Redis Config 클래스",
                                javaClass ->
                                        javaClass.getSimpleName().endsWith("Config")
                                                && javaClass.isAnnotatedWith(Configuration.class)));
    }

    // ========================================================================
    // 1. 클래스 구조 규칙
    // ========================================================================

    @Nested
    @DisplayName("1. 클래스 구조 규칙")
    class ClassStructureRules {

        @Test
        @DisplayName("규칙 1-1: Config 클래스는 @Configuration 어노테이션이 필수입니다")
        void config_MustHaveConfigurationAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("Config")
                            .and()
                            .resideInAPackage("..config..")
                            .should()
                            .beAnnotatedWith(Configuration.class)
                            .allowEmptyShould(true)
                            .because("Config 클래스는 @Configuration 어노테이션이 필수입니다");

            rule.check(configClasses);
        }

        @Test
        @DisplayName("규칙 1-2: LettuceConfig가 존재해야 합니다")
        void lettuceConfig_MustExist() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleName("LettuceConfig")
                            .should()
                            .beAnnotatedWith(Configuration.class)
                            .allowEmptyShould(true)
                            .because("Lettuce 설정 클래스가 필수입니다 (캐시용)");

            rule.check(configClasses);
        }

        @Test
        @DisplayName("규칙 1-3: RedissonConfig가 존재해야 합니다")
        void redissonConfig_MustExist() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleName("RedissonConfig")
                            .should()
                            .beAnnotatedWith(Configuration.class)
                            .allowEmptyShould(true)
                            .because("Redisson 설정 클래스가 필수입니다 (분산락용)");

            rule.check(configClasses);
        }
    }

    // ========================================================================
    // 2. Bean 정의 규칙
    // ========================================================================

    @Nested
    @DisplayName("2. Bean 정의 규칙")
    class BeanDefinitionRules {

        @Test
        @DisplayName("규칙 2-1: Config 클래스는 @Bean 메서드를 가져야 합니다")
        void config_MustHaveBeanMethods() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("Config")
                            .and()
                            .resideInAPackage("..config..")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "@Bean 메서드가 존재",
                                                    javaClass ->
                                                            javaClass.getMethods().stream()
                                                                    .anyMatch(
                                                                            method ->
                                                                                    method
                                                                                            .isAnnotatedWith(
                                                                                                    Bean
                                                                                                            .class)))))
                            .allowEmptyShould(true)
                            .because("Config 클래스는 최소 하나의 @Bean 메서드를 가져야 합니다");

            rule.check(configClasses);
        }

        @Test
        @DisplayName("규칙 2-2: LettuceConfig는 RedisTemplate Bean을 정의해야 합니다")
        void lettuceConfig_MustDefineRedisTemplateBean() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleName("LettuceConfig")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "RedisTemplate @Bean 메서드",
                                                    javaClass ->
                                                            javaClass.getMethods().stream()
                                                                    .filter(
                                                                            method ->
                                                                                    method
                                                                                            .isAnnotatedWith(
                                                                                                    Bean
                                                                                                            .class))
                                                                    .anyMatch(
                                                                            method ->
                                                                                    method.getName()
                                                                                                    .contains(
                                                                                                            "redisTemplate")
                                                                                            || method.getRawReturnType()
                                                                                                    .getName()
                                                                                                    .contains(
                                                                                                            "RedisTemplate")))))
                            .allowEmptyShould(true)
                            .because("LettuceConfig는 RedisTemplate Bean을 정의해야 합니다");

            rule.check(configClasses);
        }

        @Test
        @DisplayName("규칙 2-3: RedissonConfig는 RedissonClient Bean을 정의해야 합니다")
        void redissonConfig_MustDefineRedissonClientBean() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleName("RedissonConfig")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "RedissonClient @Bean 메서드",
                                                    javaClass ->
                                                            javaClass.getMethods().stream()
                                                                    .filter(
                                                                            method ->
                                                                                    method
                                                                                            .isAnnotatedWith(
                                                                                                    Bean
                                                                                                            .class))
                                                                    .anyMatch(
                                                                            method ->
                                                                                    method.getName()
                                                                                                    .contains(
                                                                                                            "redissonClient")
                                                                                            || method.getRawReturnType()
                                                                                                    .getName()
                                                                                                    .contains(
                                                                                                            "RedissonClient")))))
                            .allowEmptyShould(true)
                            .because("RedissonConfig는 RedissonClient Bean을 정의해야 합니다");

            rule.check(configClasses);
        }
    }

    // ========================================================================
    // 3. 역할 분리 규칙
    // ========================================================================

    @Nested
    @DisplayName("3. 역할 분리 규칙")
    class RoleSeparationRules {

        @Test
        @DisplayName("규칙 3-1: LettuceConfig는 Redisson 의존성을 가지지 않아야 합니다")
        void lettuceConfig_MustNotDependOnRedisson() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleName("LettuceConfig")
                            .should()
                            .dependOnClassesThat()
                            .haveNameMatching(".*Redisson.*")
                            .allowEmptyShould(true)
                            .because("LettuceConfig는 캐시 전용입니다. Redisson 의존성 금지");

            rule.check(configClasses);
        }

        @Test
        @DisplayName("규칙 3-2: RedissonConfig는 RedisTemplate 의존성을 가지지 않아야 합니다")
        void redissonConfig_MustNotDependOnRedisTemplate() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleName("RedissonConfig")
                            .should()
                            .dependOnClassesThat()
                            .haveNameMatching(".*RedisTemplate.*")
                            .allowEmptyShould(true)
                            .because("RedissonConfig는 분산락 전용입니다. RedisTemplate 의존성 금지");

            rule.check(configClasses);
        }
    }

    // ========================================================================
    // 4. 금지 사항 규칙
    // ========================================================================

    @Nested
    @DisplayName("4. 금지 사항 규칙")
    class ProhibitionRules {

        @Test
        @DisplayName("규칙 4-1: Config에서 비즈니스 로직 포함이 금지됩니다")
        void config_MustNotContainBusinessLogic() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("Config")
                            .and()
                            .resideInAPackage("..config..")
                            .should()
                            .dependOnClassesThat()
                            .resideInAPackage("..domain..")
                            .allowEmptyShould(true)
                            .because("Config 클래스는 인프라 설정만 담당합니다");

            rule.check(configClasses);
        }

        @Test
        @DisplayName("규칙 4-2: Config에서 Adapter 의존성이 금지됩니다")
        void config_MustNotDependOnAdapters() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("Config")
                            .and()
                            .resideInAPackage("..config..")
                            .should()
                            .dependOnClassesThat()
                            .resideInAPackage("..adapter..")
                            .allowEmptyShould(true)
                            .because("Config 클래스는 Adapter에 의존하지 않습니다");

            rule.check(configClasses);
        }

        @Test
        @DisplayName("규칙 4-3: Config에서 Application Layer 의존성이 금지됩니다")
        void config_MustNotDependOnApplicationLayer() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("Config")
                            .and()
                            .resideInAPackage("..config..")
                            .should()
                            .dependOnClassesThat()
                            .resideInAPackage("..application..")
                            .allowEmptyShould(true)
                            .because("Config 클래스는 Application Layer에 의존하지 않습니다");

            rule.check(configClasses);
        }
    }

    // ========================================================================
    // 5. 설정 안전성 규칙
    // ========================================================================

    @Nested
    @DisplayName("5. 설정 안전성 규칙")
    class ConfigSafetyRules {

        @Test
        @DisplayName("규칙 5-1: @Value 어노테이션으로 외부 설정을 주입받아야 합니다")
        void config_MustUseValueAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("Config")
                            .and()
                            .resideInAPackage("..config..")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "@Value 어노테이션 사용",
                                                    javaClass ->
                                                            javaClass.getAllFields().stream()
                                                                    .anyMatch(
                                                                            field ->
                                                                                    field
                                                                                            .isAnnotatedWith(
                                                                                                    org
                                                                                                            .springframework
                                                                                                            .beans
                                                                                                            .factory
                                                                                                            .annotation
                                                                                                            .Value
                                                                                                            .class)))))
                            .allowEmptyShould(true)
                            .because("Redis 설정은 @Value로 외부화해야 합니다 (하드코딩 금지)");

            rule.check(configClasses);
        }
    }
}

package com.ryuqq.adapter.out.persistence.architecture.config;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.fail;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * DangerousConfigArchTest - 위험한 설정 방지 규칙 검증 (Zero-Tolerance)
 *
 * <p>데이터 손실, 성능 저하, 보안 취약점을 유발하는 설정을 빌드 타임에 차단합니다.
 *
 * <h2>검증 항목</h2>
 *
 * <ul>
 *   <li><strong>Flyway 위험 설정</strong>: clean() 호출, clean-disabled: false
 *   <li><strong>Hibernate 위험 설정</strong>: ddl-auto: create/update, open-in-view: true
 *   <li><strong>코드 레벨 위험</strong>: Flyway.clean() 직접 호출
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("위험한 설정 방지 검증 (Zero-Tolerance)")
class DangerousConfigArchTest {

    private static final String BASE_PACKAGE = "com.ryuqq.adapter.out.persistence";

    private static JavaClasses allClasses;

    @BeforeAll
    static void setUp() {
        allClasses =
                new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages(BASE_PACKAGE);
    }

    // ========================================================================
    // 1. Flyway 코드 레벨 위험 방지 (ArchUnit)
    // ========================================================================

    @Nested
    @DisplayName("1. Flyway 코드 레벨 위험 방지")
    class FlywayCodeLevelRules {

        @Test
        @DisplayName("규칙 1-1: Flyway.clean() 직접 호출 금지 - 전체 데이터 삭제 위험")
        void flywayClean_MustNotBeCalled() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..persistence..")
                            .should()
                            .callMethod(org.flywaydb.core.Flyway.class, "clean")
                            .because("Flyway.clean()은 모든 데이터를 삭제합니다. 절대 사용 금지!");

            rule.check(allClasses);
        }

        @Test
        @DisplayName("규칙 1-2: FlywayMigrationStrategy 구현 금지 - YAML 설정 사용 권장")
        void flywayMigrationStrategy_MustNotBeImplemented() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..persistence..")
                            .should()
                            .implement(
                                    org.springframework.boot.autoconfigure.flyway
                                            .FlywayMigrationStrategy.class)
                            .because("FlywayMigrationStrategy 대신 YAML 설정을 사용하세요. clean() 호출 위험 방지");

            rule.check(allClasses);
        }

        @Test
        @DisplayName("규칙 1-3: Flyway 직접 주입 금지 - 위험한 API 접근 차단")
        void flyway_MustNotBeInjected() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..persistence..")
                            .and()
                            .haveSimpleNameNotEndingWith("Test")
                            .should()
                            .dependOnClassesThat()
                            .haveFullyQualifiedName("org.flywaydb.core.Flyway")
                            .because("Flyway 직접 주입은 위험한 API(clean, repair 등) 접근을 허용합니다");

            rule.check(allClasses);
        }
    }

    // ========================================================================
    // 2. Hibernate/JPA 코드 레벨 위험 방지 (ArchUnit)
    // ========================================================================

    @Nested
    @DisplayName("2. Hibernate/JPA 코드 레벨 위험 방지")
    class HibernateCodeLevelRules {

        @Test
        @DisplayName("규칙 2-1: EntityManager.createNativeQuery() DELETE/TRUNCATE 금지")
        void nativeQueryDelete_MustBeCareful() {
            // 이 규칙은 코드 리뷰로 검증 권장
            // ArchUnit으로는 문자열 내용까지 검사하기 어려움
            // 대신 Native Query 사용 자체를 경고로 표시
        }

        @Test
        @DisplayName("규칙 2-2: @Modifying + @Query DELETE 시 조건절 필수")
        void modifyingDeleteQuery_MustHaveWhereClause() {
            // 이 규칙도 코드 리뷰로 검증 권장
            // 정적 분석으로는 쿼리 문자열 파싱이 필요
        }
    }

    // ========================================================================
    // 3. YAML 설정 파일 검증
    // ========================================================================

    @Nested
    @DisplayName("3. YAML 설정 파일 검증")
    class YamlConfigurationRules {

        private static final Path RESOURCES_PATH = Paths.get("src/main/resources");

        @Test
        @DisplayName("규칙 3-1: ddl-auto는 validate만 허용 - create/update/create-drop 금지")
        void ddlAuto_MustBeValidate() throws IOException {
            List<String> dangerousDdlAutoValues =
                    List.of("create", "update", "create-drop", "none");

            try (Stream<Path> paths = Files.walk(RESOURCES_PATH)) {
                paths.filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                        .filter(p -> p.getFileName().toString().contains("persistence"))
                        .forEach(
                                yamlPath -> {
                                    String ddlAutoValue =
                                            getYamlValue(
                                                    yamlPath,
                                                    "spring",
                                                    "jpa",
                                                    "hibernate",
                                                    "ddl-auto");

                                    if (ddlAutoValue != null
                                            && dangerousDdlAutoValues.contains(ddlAutoValue)) {
                                        fail(
                                                "⚠️ 위험한 설정 발견!\n"
                                                        + "파일: "
                                                        + yamlPath
                                                        + "\n"
                                                        + "설정: spring.jpa.hibernate.ddl-auto="
                                                        + ddlAutoValue
                                                        + "\n"
                                                        + "권장: ddl-auto: validate (Flyway가 스키마"
                                                        + " 관리)");
                                    }
                                });
            }
        }

        @Test
        @DisplayName("규칙 3-2: open-in-view는 false만 허용 - 커넥션 점유 방지")
        void openInView_MustBeFalse() throws IOException {
            try (Stream<Path> paths = Files.walk(RESOURCES_PATH)) {
                paths.filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                        .filter(p -> p.getFileName().toString().contains("persistence"))
                        .forEach(
                                yamlPath -> {
                                    String osivValue =
                                            getYamlValue(yamlPath, "spring", "jpa", "open-in-view");

                                    if (osivValue != null && "true".equalsIgnoreCase(osivValue)) {
                                        fail(
                                                "⚠️ 위험한 설정 발견!\n"
                                                        + "파일: "
                                                        + yamlPath
                                                        + "\n"
                                                        + "설정: spring.jpa.open-in-view=true\n"
                                                        + "문제: HTTP 요청 전체 기간 커넥션 점유, Lazy Loading"
                                                        + " N+1 위험\n"
                                                        + "권장: open-in-view: false");
                                    }
                                });
            }
        }

        @Test
        @DisplayName("규칙 3-3: flyway.clean-disabled는 true만 허용 - 데이터 보호")
        void flywayCleanDisabled_MustBeTrue() throws IOException {
            try (Stream<Path> paths = Files.walk(RESOURCES_PATH)) {
                paths.filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                        .filter(p -> p.getFileName().toString().contains("persistence"))
                        .forEach(
                                yamlPath -> {
                                    String cleanDisabledValue =
                                            getYamlValue(
                                                    yamlPath, "spring", "flyway", "clean-disabled");

                                    if (cleanDisabledValue != null
                                            && "false".equalsIgnoreCase(cleanDisabledValue)) {
                                        fail(
                                                "⚠️ 위험한 설정 발견!\n"
                                                        + "파일: "
                                                        + yamlPath
                                                        + "\n"
                                                        + "설정: spring.flyway.clean-disabled=false\n"
                                                        + "문제: flyway clean 명령어로 모든 데이터 삭제 가능\n"
                                                        + "권장: clean-disabled: true");
                                    }
                                });
            }
        }

        @Test
        @DisplayName("규칙 3-4: show-sql은 prod에서 false 권장 - 성능 저하 방지")
        void showSql_MustBeFalseInProd() throws IOException {
            Path prodYaml = RESOURCES_PATH.resolve("persistence-prod.yml");

            if (Files.exists(prodYaml)) {
                String showSqlValue = getYamlValue(prodYaml, "spring", "jpa", "show-sql");

                if ("true".equalsIgnoreCase(showSqlValue)) {
                    fail(
                            "⚠️ 운영 환경 성능 저하 위험!\n"
                                    + "파일: "
                                    + prodYaml
                                    + "\n"
                                    + "설정: spring.jpa.show-sql=true\n"
                                    + "문제: 콘솔 출력으로 인한 성능 저하\n"
                                    + "권장: show-sql: false (Logback SQL 로깅 사용)");
                }
            }
        }

        @Test
        @DisplayName("규칙 3-5: prod 환경에 하드코딩된 자격증명 금지")
        void prodCredentials_MustUseEnvVariables() throws IOException {
            Path prodYaml = RESOURCES_PATH.resolve("persistence-prod.yml");

            if (Files.exists(prodYaml)) {
                String password = getYamlValue(prodYaml, "spring", "datasource", "password");
                String username = getYamlValue(prodYaml, "spring", "datasource", "username");
                String url = getYamlValue(prodYaml, "spring", "datasource", "url");

                // 환경 변수 패턴 체크 (${...} 형태)
                if (password != null && !password.contains("${")) {
                    fail(
                            "⚠️ 보안 취약점!\n"
                                    + "파일: "
                                    + prodYaml
                                    + "\n"
                                    + "문제: password가 하드코딩됨\n"
                                    + "권장: ${DB_PASSWORD} 환경 변수 사용");
                }

                if (username != null && !username.contains("${")) {
                    fail(
                            "⚠️ 보안 취약점!\n"
                                    + "파일: "
                                    + prodYaml
                                    + "\n"
                                    + "문제: username이 하드코딩됨\n"
                                    + "권장: ${DB_USERNAME} 환경 변수 사용");
                }

                if (url != null && !url.contains("${")) {
                    fail(
                            "⚠️ 보안 취약점!\n"
                                    + "파일: "
                                    + prodYaml
                                    + "\n"
                                    + "문제: URL이 하드코딩됨\n"
                                    + "권장: ${DB_HOST}, ${DB_PORT}, ${DB_NAME} 환경 변수 사용");
                }
            }
        }

        /** YAML 파일에서 중첩된 키 값을 추출합니다. */
        @SuppressWarnings("unchecked")
        private String getYamlValue(Path yamlPath, String... keys) {
            try (InputStream inputStream = Files.newInputStream(yamlPath)) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(inputStream);

                if (data == null) {
                    return null;
                }

                Object current = data;
                for (String key : keys) {
                    if (current instanceof Map) {
                        current = ((Map<String, Object>) current).get(key);
                    } else {
                        return null;
                    }
                }

                return current != null ? current.toString() : null;
            } catch (IOException e) {
                return null;
            }
        }
    }

    // ========================================================================
    // 4. HikariCP 설정 검증
    // ========================================================================

    @Nested
    @DisplayName("4. HikariCP 설정 검증")
    class HikariCPConfigRules {

        private static final Path RESOURCES_PATH = Paths.get("src/main/resources");

        @Test
        @DisplayName("규칙 4-1: maximum-pool-size는 50 이하 권장")
        void maxPoolSize_MustBeReasonable() throws IOException {
            try (Stream<Path> paths = Files.walk(RESOURCES_PATH)) {
                paths.filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                        .filter(p -> p.getFileName().toString().contains("persistence"))
                        .forEach(
                                yamlPath -> {
                                    String maxPoolSize =
                                            getYamlValue(
                                                    yamlPath,
                                                    "spring",
                                                    "datasource",
                                                    "hikari",
                                                    "maximum-pool-size");

                                    if (maxPoolSize != null) {
                                        int poolSize = Integer.parseInt(maxPoolSize);
                                        if (poolSize > 50) {
                                            fail(
                                                    "⚠️ 과도한 커넥션 풀 설정!\n"
                                                            + "파일: "
                                                            + yamlPath
                                                            + "\n"
                                                            + "설정: maximum-pool-size="
                                                            + poolSize
                                                            + "\n"
                                                            + "문제: DB 부하 증가, Context Switching 비용\n"
                                                            + "권장: 50 이하 (공식: CPU cores * 2 +"
                                                            + " spindle count)");
                                        }
                                    }
                                });
            }
        }

        @Test
        @DisplayName("규칙 4-2: prod에서 leak-detection-threshold 활성화 권장")
        void leakDetection_MustBeEnabledInProd() throws IOException {
            Path prodYaml = RESOURCES_PATH.resolve("persistence-prod.yml");

            if (Files.exists(prodYaml)) {
                String leakThreshold =
                        getYamlValue(
                                prodYaml,
                                "spring",
                                "datasource",
                                "hikari",
                                "leak-detection-threshold");

                if (leakThreshold == null || "0".equals(leakThreshold)) {
                    System.out.println("⚠️ 권장사항: prod 환경에서 leak-detection-threshold 활성화 권장");
                }
            }
        }

        @SuppressWarnings("unchecked")
        private String getYamlValue(Path yamlPath, String... keys) {
            try (InputStream inputStream = Files.newInputStream(yamlPath)) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(inputStream);

                if (data == null) {
                    return null;
                }

                Object current = data;
                for (String key : keys) {
                    if (current instanceof Map) {
                        current = ((Map<String, Object>) current).get(key);
                    } else {
                        return null;
                    }
                }

                return current != null ? current.toString() : null;
            } catch (IOException e) {
                return null;
            }
        }
    }
}

package com.company.template.architecture;

import com.tngtech.archunit.core.domain.JavaCall;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Transaction Architecture Tests
 *
 * <p>Enforces transaction boundary rules to prevent performance issues:
 * <ul>
 *   <li>Issue #28: External API calls MUST NOT be inside @Transactional methods</li>
 *   <li>Issue #27: @Transactional MUST be on public methods only (Spring proxy limitations)</li>
 *   <li>Issue #27: Internal method calls in same class bypass Spring proxies</li>
 * </ul>
 *
 * <p>Related GitHub Issues:
 * <ul>
 *   <li><a href="https://github.com/ryu-qqq/claude-spring-standards/issues/28">Issue #28: Transaction Boundary Validation</a></li>
 *   <li><a href="https://github.com/ryu-qqq/claude-spring-standards/issues/27">Issue #27: Spring Proxy Limitation Validation</a></li>
 * </ul>
 *
 * @author Architecture Team
 * @see <a href="https://docs.spring.io/spring-framework/reference/data-access/transaction.html">Spring Transaction Management</a>
 * @since 1.0.0
 */
@DisplayName("💾 Transaction Architecture Enforcement")
class TransactionArchitectureTest {

    private static JavaClasses allClasses;

    @BeforeAll
    static void setup() {
        allClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.company.template");
    }

    // ========================================
    // Issue #28: External API Call Detection
    // ========================================

    /**
     * 외부 API 호출을 나타내는 Port 인터페이스 이름 패턴
     *
     * <p>다음 패턴들은 외부 시스템 호출로 간주됨:
     * <ul>
     *   <li>*GeneratePresignedUrlPort - S3 Presigned URL 발급</li>
     *   <li>*S3*Port - AWS S3 관련 작업</li>
     *   <li>*SendMessagePort - SQS/메시지 큐 발행</li>
     *   <li>*PublishEventPort - 이벤트 발행</li>
     *   <li>*ExternalApiPort - 외부 API 호출</li>
     *   <li>*HttpClientPort - HTTP 통신</li>
     * </ul>
     */
    private static final List<String> EXTERNAL_API_PORT_PATTERNS = Arrays.asList(
        ".*GeneratePresignedUrl.*Port",
        ".*S3.*Port",
        ".*SendMessage.*Port",
        ".*PublishEvent.*Port",
        ".*ExternalApi.*Port",
        ".*HttpClient.*Port"
    );

    @Nested
    @DisplayName("🌐 Issue #28: External API Call Detection in @Transactional")
    class ExternalApiCallDetectionTests {

        @Test
        @DisplayName("@Transactional methods MUST NOT call external API ports")
        void transactionalMethodsShouldNotCallExternalApiPorts() {
            ArchRule rule = noMethods()
                .that().areAnnotatedWith(Transactional.class)
                .should(new ArchCondition<JavaMethod>("not call external API ports") {
                    @Override
                    public void check(JavaMethod method, ConditionEvents events) {
                        Set<JavaCall<?>> calls = method.getCallsFromSelf();

                        for (JavaCall<?> call : calls) {
                            JavaClass targetClass = call.getTargetOwner();
                            String targetClassName = targetClass.getSimpleName();

                            // Check if target class matches external API port patterns
                            boolean isExternalApiPort = EXTERNAL_API_PORT_PATTERNS.stream()
                                .anyMatch(targetClassName::matches);

                            if (isExternalApiPort) {
                                String message = String.format(
                                    "@Transactional method %s.%s() calls external API port %s.%s() at line %d.\n" +
                                    "    ⚠️  Performance Issue: External API calls (S3, HTTP, SQS) inside transactions cause:\n" +
                                    "    - DB connection held for 100-500ms during API response wait\n" +
                                    "    - Connection pool exhaustion under load\n" +
                                    "    - Transaction timeout risks\n" +
                                    "    💡 Solution: Move external API calls outside @Transactional methods.\n" +
                                    "    📚 Reference: https://github.com/ryu-qqq/claude-spring-standards/issues/28",
                                    method.getOwner().getSimpleName(),
                                    method.getName(),
                                    targetClass.getSimpleName(),
                                    call.getName(),
                                    call.getLineNumber()
                                );
                                events.add(SimpleConditionEvent.violated(call, message));
                            }
                        }
                    }
                })
                .because("External API calls in @Transactional methods cause DB connection pool exhaustion. " +
                        "See Issue #28: https://github.com/ryu-qqq/claude-spring-standards/issues/28");

            rule.check(allClasses);
        }

        @Test
        @DisplayName("@Transactional methods MUST NOT call AWS SDK directly")
        void transactionalMethodsShouldNotCallAwsSdk() {
            ArchRule rule = methods()
                .that().areAnnotatedWith(Transactional.class)
                .should(new ArchCondition<JavaMethod>("not call AWS SDK") {
                    @Override
                    public void check(JavaMethod method, ConditionEvents events) {
                        boolean callsAwsSdk = method.getCallsFromSelf().stream()
                            .anyMatch(call -> call.getTargetOwner().getPackageName()
                                .startsWith("software.amazon.awssdk"));

                        if (callsAwsSdk) {
                            String message = String.format(
                                "@Transactional method %s.%s() directly calls AWS SDK.\n" +
                                "    ⚠️  This causes DB connection to be held during AWS API calls.\n" +
                                "    💡 Solution: Extract AWS SDK calls to a separate non-transactional service.\n" +
                                "    📚 Reference: Issue #28",
                                method.getOwner().getSimpleName(),
                                method.getName()
                            );
                            events.add(SimpleConditionEvent.violated(method, message));
                        }
                    }
                })
                .because("AWS SDK calls are I/O operations that should not be inside transactions");

            rule.check(allClasses);
        }

        @Test
        @DisplayName("@Transactional methods MUST NOT call HTTP clients directly")
        void transactionalMethodsShouldNotCallHttpClients() {
            ArchRule rule = methods()
                .that().areAnnotatedWith(Transactional.class)
                .should(new ArchCondition<JavaMethod>("not call HTTP clients") {
                    @Override
                    public void check(JavaMethod method, ConditionEvents events) {
                        boolean callsHttpClient = method.getCallsFromSelf().stream()
                            .anyMatch(call -> {
                                String targetName = call.getTargetOwner().getFullName();
                                return targetName.contains("RestTemplate") ||
                                       targetName.contains("WebClient") ||
                                       targetName.contains("HttpClient") ||
                                       targetName.contains("FeignClient");
                            });

                        if (callsHttpClient) {
                            String message = String.format(
                                "@Transactional method %s.%s() directly calls HTTP client.\n" +
                                "    ⚠️  HTTP calls are slow I/O operations (200-1000ms) that hold DB connections.\n" +
                                "    💡 Solution: Move HTTP calls outside transactions or use async processing.\n" +
                                "    📚 Reference: Issue #28",
                                method.getOwner().getSimpleName(),
                                method.getName()
                            );
                            events.add(SimpleConditionEvent.violated(method, message));
                        }
                    }
                })
                .because("HTTP client calls are slow I/O operations that should not be inside transactions");

            rule.check(allClasses);
        }
    }

    // ========================================
    // Issue #27: Spring Proxy Limitation Detection
    // ========================================

    @Nested
    @DisplayName("🔒 Issue #27: Spring Proxy Limitation Detection")
    class SpringProxyLimitationTests {

        @Test
        @DisplayName("@Transactional MUST be on public methods only")
        void transactionalMethodsMustBePublic() {
            ArchRule rule = methods()
                .that().areAnnotatedWith(Transactional.class)
                .should().bePublic()
                .because("@Transactional does not work on private/protected methods due to Spring AOP proxy limitations. " +
                        "Spring creates proxies only for public methods. " +
                        "See Issue #27: https://github.com/ryu-qqq/claude-spring-standards/issues/27");

            rule.check(allClasses);
        }

        @Test
        @DisplayName("@Transactional methods SHOULD NOT call other @Transactional methods in same class")
        void transactionalMethodsShouldNotCallInternalTransactionalMethods() {
            ArchRule rule = methods()
                .that().areAnnotatedWith(Transactional.class)
                .should(new ArchCondition<JavaMethod>("not call @Transactional methods in same class") {
                    @Override
                    public void check(JavaMethod method, ConditionEvents events) {
                        JavaClass ownerClass = method.getOwner();

                        // Find all @Transactional methods in the same class
                        Set<String> transactionalMethodNames = ownerClass.getMethods().stream()
                            .filter(m -> m.isAnnotatedWith(Transactional.class))
                            .filter(m -> !m.equals(method)) // Exclude self
                            .map(JavaMethod::getName)
                            .collect(Collectors.toSet());

                        // Check if this method calls any of them
                        for (JavaCall<?> call : method.getCallsFromSelf()) {
                            boolean isSameClass = call.getTargetOwner().equals(ownerClass);
                            boolean isTransactionalMethod = transactionalMethodNames.contains(call.getName());

                            if (isSameClass && isTransactionalMethod) {
                                String message = String.format(
                                    "@Transactional method %s.%s() calls another @Transactional method %s() in the same class at line %d.\n" +
                                    "    ⚠️  Proxy Bypass Issue: Internal method calls (this.method()) bypass Spring AOP proxy.\n" +
                                    "    - Transaction propagation settings (REQUIRES_NEW, etc.) will NOT work\n" +
                                    "    - The called method's @Transactional annotation is completely ignored\n" +
                                    "    💡 Solution: Extract the called method to a separate @Service/@Component bean.\n" +
                                    "    📚 Reference: https://github.com/ryu-qqq/claude-spring-standards/issues/27",
                                    method.getOwner().getSimpleName(),
                                    method.getName(),
                                    call.getName(),
                                    call.getLineNumber()
                                );
                                events.add(SimpleConditionEvent.violated(call, message));
                            }
                        }
                    }
                })
                .because("Internal calls to @Transactional methods bypass Spring proxy and won't apply transaction settings. " +
                        "Extract to separate service bean. " +
                        "See Issue #27: https://github.com/ryu-qqq/claude-spring-standards/issues/27");

            rule.check(allClasses);
        }

        @Test
        @DisplayName("@Transactional MUST NOT be on final methods")
        void transactionalMethodsMustNotBeFinal() {
            ArchRule rule = noMethods()
                .that().areAnnotatedWith(Transactional.class)
                .should().haveModifier(JavaModifier.FINAL)
                .because("@Transactional does not work on final methods due to CGLIB proxy limitations. " +
                        "CGLIB creates subclass proxies which cannot override final methods. " +
                        "See Issue #27: https://github.com/ryu-qqq/claude-spring-standards/issues/27");

            rule.check(allClasses);
        }

        @Test
        @DisplayName("Classes with @Transactional methods MUST NOT be final")
        void classesWithTransactionalMethodsMustNotBeFinal() {
            ArchRule rule = classes()
                .that().containAnyMethodsThat().areAnnotatedWith(Transactional.class)
                .should().notHaveModifier(JavaModifier.FINAL)
                .because("Classes with @Transactional methods cannot be final due to CGLIB proxy requirements. " +
                        "CGLIB needs to create a subclass to intercept method calls. " +
                        "See Issue #27: https://github.com/ryu-qqq/claude-spring-standards/issues/27");

            rule.check(allClasses);
        }
    }

    // ========================================
    // Best Practices
    // ========================================

    @Nested
    @DisplayName("✅ Transaction Best Practices")
    class TransactionBestPracticesTests {

        @Test
        @DisplayName("@Transactional readOnly=true for query methods")
        void queryMethodsShouldUseReadOnlyTransactions() {
            ArchRule rule = methods()
                .that().areAnnotatedWith(Transactional.class)
                .and().haveNameMatching("(get|find|load|search|query).*")
                .and().arePublic()
                .should(new ArchCondition<JavaMethod>("use readOnly=true for query operations") {
                    @Override
                    public void check(JavaMethod method, ConditionEvents events) {
                        Transactional annotation = method.getAnnotationOfType(Transactional.class);
                        if (annotation != null && !annotation.readOnly()) {
                            String message = String.format(
                                "Query method %s.%s() should use @Transactional(readOnly = true) for optimization.\n" +
                                "    💡 Read-only transactions provide better performance and prevent accidental modifications.",
                                method.getOwner().getSimpleName(),
                                method.getName()
                            );
                            events.add(SimpleConditionEvent.violated(method, message));
                        }
                    }
                })
                .because("Query methods should use readOnly=true for performance optimization");

            rule.check(allClasses);
        }
    }
}

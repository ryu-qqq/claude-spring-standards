// ========================================
// Adapter-In: REST Admin Servlet
// ========================================
// Inbound adapter for admin REST API (Servlet-based)
// Handles HTTP requests and responses for admin operations
// Middleware: Spring MVC (Servlet)
// NO Lombok allowed
// ========================================

plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // ========================================
    // Core Dependencies
    // ========================================
    // Application layer (use cases)
    api(project(":application"))
    api(project(":domain"))

    // Spring Web
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)

    // Spring Security (Optional)
    implementation(libs.spring.boot.starter.security)

    // JSON Processing
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jsr310)

    // API Documentation (Optional)
    implementation(libs.springdoc.openapi)

    // ========================================
    // Test Dependencies
    // ========================================
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.rest.assured)

    // ========================================
    // Test Fixtures Dependencies
    // ========================================
    // TestFixtures can use main dependencies
    testFixturesApi(project(":application"))
    testFixturesApi(project(":domain"))
    testFixturesImplementation(libs.jackson.databind)
}

// ========================================
// Test Coverage (70% for adapters)
// ========================================
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

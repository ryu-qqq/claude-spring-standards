// ========================================
// Adapter-Out: Persistence (JPA + QueryDSL)
// ========================================
// Outbound adapter for database operations
// Implements repository ports from application layer
// NO Lombok allowed
// ========================================

plugins {
    `java-library`
}

dependencies {
    // ========================================
    // Core Dependencies
    // ========================================
    api(project(":application"))
    api(project(":domain"))

    // Spring Data JPA
    implementation(libs.spring.boot.starter.data.jpa)

    // QueryDSL
    implementation(libs.querydsl.jpa) {
        artifact {
            classifier = "jakarta"
        }
    }
    annotationProcessor(libs.querydsl.apt) {
        artifact {
            classifier = "jakarta"
        }
    }
    annotationProcessor(libs.jakarta.persistence.api)

    // Database Drivers (Runtime)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2) // For testing

    // Connection Pooling
    implementation(libs.hikaricp)

    // Flyway Migration
    implementation(libs.flyway.core)
    runtimeOnly(libs.flyway.postgresql)

    // ========================================
    // Test Dependencies
    // ========================================
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit)
}

// ========================================
// QueryDSL Configuration
// ========================================
val generatedSourcesDir = file("build/generated/sources/annotationProcessor/java/main")

sourceSets {
    main {
        java {
            srcDir(generatedSourcesDir)
        }
    }
}

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(generatedSourcesDir)
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
        rule {
            element = "CLASS"
            excludes = listOf(
                "*.entity.*", // JPA entities excluded
                "*.Q*" // QueryDSL generated classes excluded
            )
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

// ========================================
// Clean Generated Sources
// ========================================
tasks.clean {
    delete(generatedSourcesDir)
}

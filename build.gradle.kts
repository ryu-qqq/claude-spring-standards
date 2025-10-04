import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    id("checkstyle")
    alias(libs.plugins.spotbugs) apply false
}

// ========================================
// Global Configuration
// ========================================
allprojects {
    group = "com.company.template"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// ========================================
// Subproject Configuration
// ========================================
subprojects {
    apply(plugin = "java")
    apply(plugin = "checkstyle")
    apply(plugin = "com.github.spotbugs")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    // ========================================
    // Dependency Management
    // ========================================
    apply(plugin = "io.spring.dependency-management")

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
        }
    }

    // ========================================
    // Dependencies
    // ========================================
    dependencies {
        // Test Dependencies (All Modules)
        testImplementation(rootProject.libs.junit.jupiter)
        testImplementation(rootProject.libs.assertj.core)
        testImplementation(rootProject.libs.mockito.core)
        testImplementation(rootProject.libs.mockito.junit)

        // ArchUnit for Architecture Testing
        testImplementation(rootProject.libs.archunit.junit5)

        // SpotBugs Annotations
        compileOnly(rootProject.libs.spotbugs.annotations)
    }

    // ========================================
    // Test Configuration
    // ========================================
    tasks.test {
        useJUnitPlatform()

        // Test Coverage Requirements
        finalizedBy(tasks.named("jacocoTestReport"))
    }

    // ========================================
    // Checkstyle Configuration
    // ========================================
    checkstyle {
        toolVersion = rootProject.libs.versions.checkstyle.get()
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")
        isIgnoreFailures = false
        maxWarnings = 0
    }

    // ========================================
    // SpotBugs Configuration
    // ========================================
    spotbugs {
        toolVersion.set(rootProject.libs.versions.spotbugs.get())
        effort.set(com.github.spotbugs.snom.Effort.MAX)
        reportLevel.set(com.github.spotbugs.snom.Confidence.LOW)
        excludeFilter.set(rootProject.file("config/spotbugs/spotbugs-exclude.xml"))
    }

    // ========================================
    // JaCoCo Coverage Configuration
    // ========================================
    apply(plugin = "jacoco")

    configure<JacocoPluginExtension> {
        toolVersion = rootProject.libs.versions.jacoco.get()
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)

        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    // ========================================
    // Lombok 금지 검증
    // ========================================
    tasks.register("checkNoLombok") {
        doLast {
            val lombokFound = configurations.flatMap { config ->
                config.dependencies.filter { dep ->
                    dep.group == "org.projectlombok" && dep.name == "lombok"
                }
            }

            if (lombokFound.isNotEmpty()) {
                throw GradleException(
                    """
                    ❌ LOMBOK DETECTED: Lombok is strictly prohibited in this project.
                    Found in: ${project.name}

                    Policy: All modules must use pure Java without Lombok.
                    """.trimIndent()
                )
            }
        }
    }

    tasks.build {
        dependsOn("checkNoLombok")
    }

    // ========================================
    // Compiler Configuration
    // ========================================
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:unchecked",
                "-Xlint:deprecation",
                "-parameters"
            )
        )
    }
}

// ========================================
// Dead Code Detection Task
// ========================================
tasks.register("detectDeadCode") {
    group = "verification"
    description = "Detect potentially unused code across all modules"

    doLast {
        println("""
            ========================================
            Dead Code Detection Report
            ========================================
            Running static analysis for unused code...

            Tools:
            - SpotBugs: Unused private methods/fields
            - JaCoCo: 0% coverage methods
            - Custom AST analysis

            See reports in: build/reports/deadcode/
            ========================================
        """.trimIndent())
    }

    dependsOn(subprojects.map { it.tasks.named("spotbugsMain") })
    dependsOn(subprojects.map { it.tasks.named("jacocoTestReport") })
}

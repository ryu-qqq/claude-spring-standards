# ========================================
# Spring Standards Template - Web API Dockerfile
# ========================================
# Multi-stage build for optimized image size
# Based on Eclipse Temurin JRE 21 Alpine
# ========================================

# ----------------------------------------
# Stage 1: Build
# ----------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY gradle/libs.versions.toml gradle/

# Copy all module build files (only existing ones)
COPY domain/build.gradle domain/
COPY application/build.gradle application/
COPY adapter-in/rest-api/build.gradle adapter-in/rest-api/
COPY adapter-out/persistence-mysql/build.gradle adapter-out/persistence-mysql/
COPY bootstrap/bootstrap-web-api/build.gradle bootstrap/bootstrap-web-api/

# Download dependencies (cache layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY domain/src domain/src
COPY application/src application/src
COPY adapter-in/rest-api/src adapter-in/rest-api/src
COPY adapter-out/persistence-mysql/src adapter-out/persistence-mysql/src
COPY bootstrap/bootstrap-web-api/src bootstrap/bootstrap-web-api/src

# Build the application (clean first to ensure fresh build, skip test and asciidoctor)
RUN ./gradlew clean :bootstrap:bootstrap-web-api:bootJar --no-daemon -x test -x asciidoctor

# ----------------------------------------
# Stage 2: Runtime
# ----------------------------------------
FROM eclipse-temurin:21-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# Copy the JAR file from builder
COPY --from=builder /workspace/bootstrap/bootstrap-web-api/build/libs/*.jar app.jar

# Set ownership
RUN chown -R app:app /app

# Switch to non-root user
USER app

# JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -Djava.security.egd=file:/dev/./urandom"

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

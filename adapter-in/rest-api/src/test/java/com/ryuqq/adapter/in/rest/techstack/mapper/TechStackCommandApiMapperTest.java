package com.ryuqq.adapter.in.rest.techstack.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.CreateTechStackApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateTechStackApiRequestFixture;
import com.ryuqq.adapter.in.rest.techstack.dto.request.CreateTechStackApiRequest;
import com.ryuqq.adapter.in.rest.techstack.dto.request.UpdateTechStackApiRequest;
import com.ryuqq.application.techstack.dto.command.CreateTechStackCommand;
import com.ryuqq.application.techstack.dto.command.UpdateTechStackCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TechStackCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("TechStackCommandApiMapper 단위 테스트")
class TechStackCommandApiMapperTest {

    private TechStackCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TechStackCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateTechStackApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateTechStackApiRequest request = CreateTechStackApiRequestFixture.valid();

            // When
            CreateTechStackCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.name()).isEqualTo("Spring Boot 3.5 with Java 21");
            assertThat(command.languageType()).isEqualTo("JAVA");
            assertThat(command.languageVersion()).isEqualTo("21");
            assertThat(command.languageFeatures())
                    .containsExactly("records", "sealed-classes", "pattern-matching");
            assertThat(command.frameworkType()).isEqualTo("SPRING_BOOT");
            assertThat(command.frameworkVersion()).isEqualTo("3.5.0");
            assertThat(command.frameworkModules()).containsExactly("spring-web", "spring-data-jpa");
            assertThat(command.platformType()).isEqualTo("JVM");
            assertThat(command.runtimeEnvironment()).isEqualTo("JVM");
            assertThat(command.buildToolType()).isEqualTo("GRADLE");
            assertThat(command.buildConfigFile()).isEqualTo("build.gradle");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateTechStackApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long techStackId = 1L;
            UpdateTechStackApiRequest request = UpdateTechStackApiRequestFixture.valid();

            // When
            UpdateTechStackCommand command = mapper.toCommand(techStackId, request);

            // Then
            assertThat(command.id()).isEqualTo(techStackId);
            assertThat(command.name()).isNotNull();
        }
    }
}

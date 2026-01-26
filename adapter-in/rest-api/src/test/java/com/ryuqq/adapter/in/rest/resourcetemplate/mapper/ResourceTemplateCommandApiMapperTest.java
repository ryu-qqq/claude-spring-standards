package com.ryuqq.adapter.in.rest.resourcetemplate.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.CreateResourceTemplateApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateResourceTemplateApiRequestFixture;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.CreateResourceTemplateApiRequest;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.UpdateResourceTemplateApiRequest;
import com.ryuqq.application.resourcetemplate.dto.command.CreateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.dto.command.UpdateResourceTemplateCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ResourceTemplateCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>null 처리 (required)
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("ResourceTemplateCommandApiMapper 단위 테스트")
class ResourceTemplateCommandApiMapperTest {

    private ResourceTemplateCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ResourceTemplateCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateResourceTemplateApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateResourceTemplateApiRequest request =
                    CreateResourceTemplateApiRequestFixture.valid();

            // When
            CreateResourceTemplateCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.moduleId()).isEqualTo(1L);
            assertThat(command.category()).isEqualTo("DOMAIN");
            assertThat(command.filePath()).isEqualTo("src/main/java/Order.java");
            assertThat(command.fileType()).isEqualTo("JAVA");
            assertThat(command.description()).isEqualTo("Order Aggregate");
            assertThat(command.templateContent()).isEqualTo("public class Order {}");
            assertThat(command.required()).isTrue();
        }

        @Test
        @DisplayName("null 필드 처리 - required가 null이면 true")
        void nullRequired_ShouldReturnTrue() {
            // Given
            CreateResourceTemplateApiRequest request =
                    CreateResourceTemplateApiRequestFixture.validMinimal();

            // When
            CreateResourceTemplateCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.required()).isTrue();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateResourceTemplateApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long resourceTemplateId = 1L;
            UpdateResourceTemplateApiRequest request =
                    UpdateResourceTemplateApiRequestFixture.valid();

            // When
            UpdateResourceTemplateCommand command = mapper.toCommand(resourceTemplateId, request);

            // Then
            assertThat(command.resourceTemplateId()).isEqualTo(resourceTemplateId);
            assertThat(command.category()).isEqualTo("DOMAIN");
            assertThat(command.filePath()).isEqualTo("src/main/java/Order.java");
            assertThat(command.fileType()).isEqualTo("JAVA");
            assertThat(command.description()).isEqualTo("Order Aggregate");
            assertThat(command.templateContent()).isEqualTo("public class Order {}");
            assertThat(command.required()).isTrue();
        }
    }
}

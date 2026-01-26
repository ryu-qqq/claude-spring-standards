package com.ryuqq.application.resourcetemplate.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.resourcetemplate.dto.command.CreateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.dto.command.UpdateResourceTemplateCommand;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplateUpdateData;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ResourceTemplateCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ResourceTemplateCommandFactory 단위 테스트")
class ResourceTemplateCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ResourceTemplateCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ResourceTemplateCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateResourceTemplateCommand로 ResourceTemplate 생성")
        void create_WithValidCommand_ShouldReturnResourceTemplate() {
            // given
            CreateResourceTemplateCommand command =
                    new CreateResourceTemplateCommand(
                            1L,
                            "CONFIG",
                            "src/main/resources/application.yml",
                            "YAML",
                            "애플리케이션 설정 파일",
                            "spring:\n  application:\n    name: test",
                            true);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ResourceTemplate result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleId().value()).isEqualTo(command.moduleId());
            assertThat(result.category().name()).isEqualTo(command.category());
            assertThat(result.filePath().value()).isEqualTo(command.filePath());
            assertThat(result.fileType().name()).isEqualTo(command.fileType());
            assertThat(result.description()).isEqualTo(command.description());
            assertThat(result.templateContent().value()).isEqualTo(command.templateContent());
            assertThat(result.isRequired()).isEqualTo(command.required());
        }

        @Test
        @DisplayName("성공 - 템플릿 내용 없이 ResourceTemplate 생성")
        void create_WithoutTemplateContent_ShouldReturnResourceTemplateWithEmptyContent() {
            // given
            CreateResourceTemplateCommand command =
                    new CreateResourceTemplateCommand(
                            1L,
                            "STATIC",
                            "src/main/resources/static/index.html",
                            "OTHER",
                            "정적 파일",
                            null,
                            false);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ResourceTemplate result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.templateContent().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("toUpdateData 메서드")
    class ToUpdateData {

        @Test
        @DisplayName("성공 - UpdateResourceTemplateCommand로 ResourceTemplateUpdateData 생성")
        void toUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateResourceTemplateCommand command =
                    new UpdateResourceTemplateCommand(
                            1L,
                            "I18N",
                            "src/main/resources/messages.properties",
                            "PROPERTIES",
                            "국제화 메시지",
                            "greeting=Hello",
                            true);

            // when
            ResourceTemplateUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.category()).isPresent();
            assertThat(result.category().get().name()).isEqualTo(command.category());
            assertThat(result.filePath()).isPresent();
            assertThat(result.filePath().get().value()).isEqualTo(command.filePath());
            assertThat(result.fileType()).isPresent();
            assertThat(result.fileType().get().name()).isEqualTo(command.fileType());
            assertThat(result.description()).isPresent();
            assertThat(result.description().get()).isEqualTo(command.description());
            assertThat(result.templateContent()).isPresent();
            assertThat(result.templateContent().get().value()).isEqualTo(command.templateContent());
            assertThat(result.required()).isPresent();
            assertThat(result.required().get()).isEqualTo(command.required());
        }

        @Test
        @DisplayName("성공 - 부분 업데이트 Command로 ResourceTemplateUpdateData 생성")
        void toUpdateData_WithPartialCommand_ShouldReturnPartialUpdateData() {
            // given
            UpdateResourceTemplateCommand command =
                    new UpdateResourceTemplateCommand(1L, null, null, null, "수정된 설명만", null, null);

            // when
            ResourceTemplateUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.category()).isEmpty();
            assertThat(result.filePath()).isEmpty();
            assertThat(result.fileType()).isEmpty();
            assertThat(result.description()).isPresent();
            assertThat(result.description().get()).isEqualTo("수정된 설명만");
            assertThat(result.templateContent()).isEmpty();
            assertThat(result.required()).isEmpty();
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateResourceTemplateCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateResourceTemplateCommand command =
                    new UpdateResourceTemplateCommand(
                            1L, "BUILD", "build.gradle", "GRADLE", "빌드 설정", "plugins {}", true);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ResourceTemplateId, ResourceTemplateUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.resourceTemplateId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}

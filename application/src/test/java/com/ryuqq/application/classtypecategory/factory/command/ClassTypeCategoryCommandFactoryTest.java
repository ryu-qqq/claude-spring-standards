package com.ryuqq.application.classtypecategory.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.classtypecategory.dto.command.CreateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.dto.command.UpdateClassTypeCategoryCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategoryUpdateData;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ClassTypeCategoryCommandFactory 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ClassTypeCategoryCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ClassTypeCategoryCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ClassTypeCategoryCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateCommand를 ClassTypeCategory 도메인으로 변환")
        void create_WithValidCommand_ShouldReturnCategory() {
            // given
            CreateClassTypeCategoryCommand command =
                    new CreateClassTypeCategoryCommand(1L, "DOMAIN", "도메인 레이어", "설명", 1);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ClassTypeCategory result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.architectureId().value()).isEqualTo(command.architectureId());
            assertThat(result.code().value()).isEqualTo(command.code());
            assertThat(result.name().value()).isEqualTo(command.name());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateCommand를 UpdateContext로 변환")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateClassTypeCategoryCommand command =
                    new UpdateClassTypeCategoryCommand(1L, "DOMAIN", "도메인 레이어", "설명", 1);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ClassTypeCategoryId, ClassTypeCategoryUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.id());
            assertThat(result.updateData().code().value()).isEqualTo(command.code());
            assertThat(result.updateData().name().value()).isEqualTo(command.name());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}

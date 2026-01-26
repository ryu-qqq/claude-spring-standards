package com.ryuqq.application.classtype.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.classtype.dto.command.CreateClassTypeCommand;
import com.ryuqq.application.classtype.dto.command.UpdateClassTypeCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.aggregate.ClassTypeUpdateData;
import com.ryuqq.domain.classtype.id.ClassTypeId;
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
@DisplayName("ClassTypeCommandFactory 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ClassTypeCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ClassTypeCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ClassTypeCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateCommand를 ClassType 도메인으로 변환")
        void create_WithValidCommand_ShouldReturnClassType() {
            // given
            CreateClassTypeCommand command =
                    new CreateClassTypeCommand(1L, "AGGREGATE", "Aggregate Root", "설명", 1);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ClassType result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.categoryId().value()).isEqualTo(command.categoryId());
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
            UpdateClassTypeCommand command =
                    new UpdateClassTypeCommand(1L, "AGGREGATE", "Aggregate Root", "설명", 1);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ClassTypeId, ClassTypeUpdateData> result =
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

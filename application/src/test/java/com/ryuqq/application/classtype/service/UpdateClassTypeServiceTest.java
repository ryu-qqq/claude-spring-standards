package com.ryuqq.application.classtype.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.classtype.dto.command.UpdateClassTypeCommand;
import com.ryuqq.application.classtype.factory.command.ClassTypeCommandFactory;
import com.ryuqq.application.classtype.manager.ClassTypePersistenceManager;
import com.ryuqq.application.classtype.validator.ClassTypeValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.aggregate.ClassTypeUpdateData;
import com.ryuqq.domain.classtype.exception.ClassTypeNotFoundException;
import com.ryuqq.domain.classtype.fixture.ClassTypeFixture;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtype.vo.ClassTypeName;
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
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateClassTypeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UpdateClassTypeServiceTest {

    @Mock private ClassTypeValidator classTypeValidator;

    @Mock private ClassTypeCommandFactory classTypeCommandFactory;

    @Mock private ClassTypePersistenceManager classTypePersistenceManager;

    private UpdateClassTypeService sut;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        sut =
                new UpdateClassTypeService(
                        classTypeValidator, classTypeCommandFactory, classTypePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - ClassType 수정")
        void execute_WithValidCommand_ShouldUpdateClassType() {
            // given
            UpdateClassTypeCommand command =
                    new UpdateClassTypeCommand(1L, "AGGREGATE", "Aggregate Root", "설명", 1);
            ClassType classType = ClassTypeFixture.defaultExistingClassType();
            ClassTypeUpdateData updateData =
                    ClassTypeUpdateData.of(
                            ClassTypeCode.of("AGGREGATE"),
                            ClassTypeName.of("Aggregate Root"),
                            "설명",
                            1);
            UpdateContext<ClassTypeId, ClassTypeUpdateData> context =
                    new UpdateContext<>(ClassTypeId.of(1L), updateData, FIXED_TIME);

            given(classTypeCommandFactory.createUpdateContext(command)).willReturn(context);
            given(classTypeValidator.findExistingOrThrow(context.id())).willReturn(classType);
            willDoNothing()
                    .given(classTypeValidator)
                    .validateCodeNotDuplicatedExcluding(any(), any(), any());
            given(classTypePersistenceManager.persist(any())).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(classTypeCommandFactory).should().createUpdateContext(command);
            then(classTypeValidator).should().findExistingOrThrow(context.id());
            then(classTypeValidator)
                    .should()
                    .validateCodeNotDuplicatedExcluding(any(), any(), any());
            then(classTypePersistenceManager).should().persist(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ClassType")
        void execute_WithNonExistingId_ShouldThrowException() {
            // given
            UpdateClassTypeCommand command =
                    new UpdateClassTypeCommand(999L, "AGGREGATE", "Aggregate Root", "설명", 1);
            ClassTypeUpdateData updateData =
                    ClassTypeUpdateData.of(
                            ClassTypeCode.of("AGGREGATE"),
                            ClassTypeName.of("Aggregate Root"),
                            "설명",
                            1);
            UpdateContext<ClassTypeId, ClassTypeUpdateData> context =
                    new UpdateContext<>(ClassTypeId.of(999L), updateData, FIXED_TIME);

            given(classTypeCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new ClassTypeNotFoundException(999L))
                    .given(classTypeValidator)
                    .findExistingOrThrow(context.id());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ClassTypeNotFoundException.class);
        }
    }
}

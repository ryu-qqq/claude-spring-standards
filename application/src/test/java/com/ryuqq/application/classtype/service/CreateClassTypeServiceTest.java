package com.ryuqq.application.classtype.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.classtype.dto.command.CreateClassTypeCommand;
import com.ryuqq.application.classtype.factory.command.ClassTypeCommandFactory;
import com.ryuqq.application.classtype.manager.ClassTypePersistenceManager;
import com.ryuqq.application.classtype.validator.ClassTypeValidator;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.exception.ClassTypeDuplicateCodeException;
import com.ryuqq.domain.classtype.fixture.ClassTypeFixture;
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
@DisplayName("CreateClassTypeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CreateClassTypeServiceTest {

    @Mock private ClassTypeValidator classTypeValidator;

    @Mock private ClassTypeCommandFactory classTypeCommandFactory;

    @Mock private ClassTypePersistenceManager classTypePersistenceManager;

    private CreateClassTypeService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateClassTypeService(
                        classTypeValidator, classTypeCommandFactory, classTypePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - ClassType 생성")
        void execute_WithValidCommand_ShouldReturnId() {
            // given
            CreateClassTypeCommand command =
                    new CreateClassTypeCommand(1L, "AGGREGATE", "Aggregate Root", "설명", 1);
            ClassType classType = ClassTypeFixture.defaultNewClassType();

            willDoNothing().given(classTypeValidator).validateCodeNotDuplicated(any(), any());
            given(classTypeCommandFactory.create(command)).willReturn(classType);
            given(classTypePersistenceManager.persist(classType)).willReturn(1L);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(1L);
            then(classTypeValidator).should().validateCodeNotDuplicated(any(), any());
            then(classTypeCommandFactory).should().create(command);
            then(classTypePersistenceManager).should().persist(classType);
        }

        @Test
        @DisplayName("실패 - 코드 중복")
        void execute_WithDuplicateCode_ShouldThrowException() {
            // given
            CreateClassTypeCommand command =
                    new CreateClassTypeCommand(1L, "DUPLICATE", "Aggregate Root", "설명", 1);

            willThrow(new ClassTypeDuplicateCodeException("DUPLICATE", 1L))
                    .given(classTypeValidator)
                    .validateCodeNotDuplicated(any(), any());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ClassTypeDuplicateCodeException.class);
        }
    }
}

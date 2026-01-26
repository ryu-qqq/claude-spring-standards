package com.ryuqq.application.classtypecategory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.classtypecategory.dto.command.CreateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.factory.command.ClassTypeCategoryCommandFactory;
import com.ryuqq.application.classtypecategory.manager.ClassTypeCategoryPersistenceManager;
import com.ryuqq.application.classtypecategory.validator.ClassTypeCategoryValidator;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.exception.ClassTypeCategoryDuplicateCodeException;
import com.ryuqq.domain.classtypecategory.fixture.ClassTypeCategoryFixture;
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
@DisplayName("CreateClassTypeCategoryService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CreateClassTypeCategoryServiceTest {

    @Mock private ClassTypeCategoryValidator classTypeCategoryValidator;

    @Mock private ClassTypeCategoryCommandFactory classTypeCategoryCommandFactory;

    @Mock private ClassTypeCategoryPersistenceManager classTypeCategoryPersistenceManager;

    private CreateClassTypeCategoryService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateClassTypeCategoryService(
                        classTypeCategoryValidator,
                        classTypeCategoryCommandFactory,
                        classTypeCategoryPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - Category 생성")
        void execute_WithValidCommand_ShouldReturnId() {
            // given
            CreateClassTypeCategoryCommand command =
                    new CreateClassTypeCategoryCommand(1L, "DOMAIN", "도메인 레이어", "설명", 1);
            ClassTypeCategory category = ClassTypeCategoryFixture.defaultNewCategory();

            willDoNothing()
                    .given(classTypeCategoryValidator)
                    .validateCodeNotDuplicated(any(), any());
            given(classTypeCategoryCommandFactory.create(command)).willReturn(category);
            given(classTypeCategoryPersistenceManager.persist(category)).willReturn(1L);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(1L);
            then(classTypeCategoryValidator).should().validateCodeNotDuplicated(any(), any());
            then(classTypeCategoryCommandFactory).should().create(command);
            then(classTypeCategoryPersistenceManager).should().persist(category);
        }

        @Test
        @DisplayName("실패 - 코드 중복")
        void execute_WithDuplicateCode_ShouldThrowException() {
            // given
            CreateClassTypeCategoryCommand command =
                    new CreateClassTypeCategoryCommand(1L, "DUPLICATE", "도메인 레이어", "설명", 1);

            willThrow(new ClassTypeCategoryDuplicateCodeException("DUPLICATE", 1L))
                    .given(classTypeCategoryValidator)
                    .validateCodeNotDuplicated(any(), any());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ClassTypeCategoryDuplicateCodeException.class);
        }
    }
}

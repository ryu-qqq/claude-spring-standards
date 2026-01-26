package com.ryuqq.application.classtypecategory.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.classtypecategory.dto.command.UpdateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.factory.command.ClassTypeCategoryCommandFactory;
import com.ryuqq.application.classtypecategory.manager.ClassTypeCategoryPersistenceManager;
import com.ryuqq.application.classtypecategory.validator.ClassTypeCategoryValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategoryUpdateData;
import com.ryuqq.domain.classtypecategory.exception.ClassTypeCategoryNotFoundException;
import com.ryuqq.domain.classtypecategory.fixture.ClassTypeCategoryFixture;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import com.ryuqq.domain.classtypecategory.vo.CategoryName;
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
@DisplayName("UpdateClassTypeCategoryService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UpdateClassTypeCategoryServiceTest {

    @Mock private ClassTypeCategoryValidator classTypeCategoryValidator;

    @Mock private ClassTypeCategoryCommandFactory classTypeCategoryCommandFactory;

    @Mock private ClassTypeCategoryPersistenceManager classTypeCategoryPersistenceManager;

    private UpdateClassTypeCategoryService sut;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        sut =
                new UpdateClassTypeCategoryService(
                        classTypeCategoryValidator,
                        classTypeCategoryCommandFactory,
                        classTypeCategoryPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - Category 수정")
        void execute_WithValidCommand_ShouldUpdateCategory() {
            // given
            UpdateClassTypeCategoryCommand command =
                    new UpdateClassTypeCategoryCommand(1L, "DOMAIN", "도메인 레이어", "설명", 1);
            ClassTypeCategory category = ClassTypeCategoryFixture.defaultExistingCategory();
            ClassTypeCategoryUpdateData updateData =
                    ClassTypeCategoryUpdateData.of(
                            CategoryCode.of("DOMAIN"), CategoryName.of("도메인 레이어"), "설명", 1);
            UpdateContext<ClassTypeCategoryId, ClassTypeCategoryUpdateData> context =
                    new UpdateContext<>(ClassTypeCategoryId.of(1L), updateData, FIXED_TIME);

            given(classTypeCategoryCommandFactory.createUpdateContext(command)).willReturn(context);
            given(classTypeCategoryValidator.findExistingOrThrow(context.id()))
                    .willReturn(category);
            willDoNothing()
                    .given(classTypeCategoryValidator)
                    .validateCodeNotDuplicatedExcluding(any(), any(), any());
            given(classTypeCategoryPersistenceManager.persist(any())).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(classTypeCategoryCommandFactory).should().createUpdateContext(command);
            then(classTypeCategoryValidator).should().findExistingOrThrow(context.id());
            then(classTypeCategoryValidator)
                    .should()
                    .validateCodeNotDuplicatedExcluding(any(), any(), any());
            then(classTypeCategoryPersistenceManager).should().persist(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Category")
        void execute_WithNonExistingId_ShouldThrowException() {
            // given
            UpdateClassTypeCategoryCommand command =
                    new UpdateClassTypeCategoryCommand(999L, "DOMAIN", "도메인 레이어", "설명", 1);
            ClassTypeCategoryUpdateData updateData =
                    ClassTypeCategoryUpdateData.of(
                            CategoryCode.of("DOMAIN"), CategoryName.of("도메인 레이어"), "설명", 1);
            UpdateContext<ClassTypeCategoryId, ClassTypeCategoryUpdateData> context =
                    new UpdateContext<>(ClassTypeCategoryId.of(999L), updateData, FIXED_TIME);

            given(classTypeCategoryCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new ClassTypeCategoryNotFoundException(999L))
                    .given(classTypeCategoryValidator)
                    .findExistingOrThrow(context.id());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ClassTypeCategoryNotFoundException.class);
        }
    }
}

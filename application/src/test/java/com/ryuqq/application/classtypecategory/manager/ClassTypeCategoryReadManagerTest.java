package com.ryuqq.application.classtypecategory.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.classtypecategory.port.out.ClassTypeCategoryQueryPort;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.fixture.ClassTypeCategoryFixture;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ClassTypeCategoryReadManager 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ClassTypeCategoryReadManagerTest {

    @Mock private ClassTypeCategoryQueryPort classTypeCategoryQueryPort;

    private ClassTypeCategoryReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypeCategoryReadManager(classTypeCategoryQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Category 조회")
        void findById_WithExistingId_ShouldReturnCategory() {
            // given
            ClassTypeCategoryId categoryId = ClassTypeCategoryId.of(1L);
            ClassTypeCategory category = ClassTypeCategoryFixture.defaultExistingCategory();
            given(classTypeCategoryQueryPort.findById(categoryId))
                    .willReturn(Optional.of(category));

            // when
            Optional<ClassTypeCategory> result = sut.findById(categoryId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().id().value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID")
        void findById_WithNonExistingId_ShouldReturnEmpty() {
            // given
            ClassTypeCategoryId categoryId = ClassTypeCategoryId.of(999L);
            given(classTypeCategoryQueryPort.findById(categoryId)).willReturn(Optional.empty());

            // when
            Optional<ClassTypeCategory> result = sut.findById(categoryId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - Criteria로 Category 목록 조회")
        void findBySliceCriteria_WithValidCriteria_ShouldReturnList() {
            // given
            ClassTypeCategory category1 = ClassTypeCategoryFixture.defaultExistingCategory();
            ClassTypeCategory category2 = ClassTypeCategoryFixture.applicationCategory();
            ClassTypeCategorySliceCriteria criteria =
                    ClassTypeCategorySliceCriteria.of(
                            CursorPageRequest.first(20), null, null, null, null);
            given(classTypeCategoryQueryPort.findBySliceCriteria(any()))
                    .willReturn(List.of(category1, category2));

            // when
            List<ClassTypeCategory> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }
    }
}

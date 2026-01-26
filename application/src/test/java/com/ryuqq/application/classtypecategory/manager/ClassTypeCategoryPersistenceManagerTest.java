package com.ryuqq.application.classtypecategory.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.classtypecategory.port.out.ClassTypeCategoryCommandPort;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
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
@Tag("manager")
@Tag("application-layer")
@DisplayName("ClassTypeCategoryPersistenceManager 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ClassTypeCategoryPersistenceManagerTest {

    @Mock private ClassTypeCategoryCommandPort classTypeCategoryCommandPort;

    private ClassTypeCategoryPersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypeCategoryPersistenceManager(classTypeCategoryCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - Category 저장")
        void persist_WithValidCategory_ShouldReturnId() {
            // given
            ClassTypeCategory category = ClassTypeCategoryFixture.defaultNewCategory();
            given(classTypeCategoryCommandPort.persist(any())).willReturn(1L);

            // when
            Long result = sut.persist(category);

            // then
            assertThat(result).isEqualTo(1L);
            then(classTypeCategoryCommandPort).should().persist(category);
        }
    }
}

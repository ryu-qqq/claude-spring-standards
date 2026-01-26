package com.ryuqq.application.classtype.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.classtype.dto.response.ClassTypeResult;
import com.ryuqq.application.classtype.dto.response.ClassTypeSliceResult;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.fixture.ClassTypeFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ClassTypeAssembler 단위 테스트")
class ClassTypeAssemblerTest {

    private ClassTypeAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypeAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - ClassType을 ClassTypeResult로 변환")
        void toResult_WithValidClassType_ShouldReturnResult() {
            // given
            ClassType classType = ClassTypeFixture.defaultExistingClassType();

            // when
            ClassTypeResult result = sut.toResult(classType);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(classType.id().value());
            assertThat(result.categoryId()).isEqualTo(classType.categoryId().value());
            assertThat(result.code()).isEqualTo(classType.code().value());
            assertThat(result.name()).isEqualTo(classType.name().value());
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenHasNext_ShouldReturnSliceWithHasNextTrue() {
            // given
            ClassType classType1 = ClassTypeFixture.defaultExistingClassType();
            ClassType classType2 = ClassTypeFixture.valueObjectClassType();
            ClassType classType3 = ClassTypeFixture.useCaseClassType();
            List<ClassType> classTypes = List.of(classType1, classType2, classType3);
            int size = 2;

            // when
            ClassTypeSliceResult result = sut.toSliceResult(classTypes, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isTrue();
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            ClassType classType1 = ClassTypeFixture.defaultExistingClassType();
            List<ClassType> classTypes = List.of(classType1);
            int size = 10;

            // when
            ClassTypeSliceResult result = sut.toSliceResult(classTypes, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isFalse();
            assertThat(result.content()).hasSize(1);
        }
    }
}

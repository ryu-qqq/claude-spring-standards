package com.ryuqq.application.classtype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.classtype.port.out.ClassTypeQueryPort;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.fixture.ClassTypeFixture;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
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
@DisplayName("ClassTypeReadManager 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ClassTypeReadManagerTest {

    @Mock private ClassTypeQueryPort classTypeQueryPort;

    private ClassTypeReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypeReadManager(classTypeQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ClassType 조회")
        void findById_WithExistingId_ShouldReturnClassType() {
            // given
            ClassTypeId classTypeId = ClassTypeId.of(1L);
            ClassType classType = ClassTypeFixture.defaultExistingClassType();
            given(classTypeQueryPort.findById(classTypeId)).willReturn(Optional.of(classType));

            // when
            Optional<ClassType> result = sut.findById(classTypeId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().id().value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID")
        void findById_WithNonExistingId_ShouldReturnEmpty() {
            // given
            ClassTypeId classTypeId = ClassTypeId.of(999L);
            given(classTypeQueryPort.findById(classTypeId)).willReturn(Optional.empty());

            // when
            Optional<ClassType> result = sut.findById(classTypeId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - Criteria로 ClassType 목록 조회")
        void findBySliceCriteria_WithValidCriteria_ShouldReturnList() {
            // given
            ClassType classType1 = ClassTypeFixture.defaultExistingClassType();
            ClassType classType2 = ClassTypeFixture.valueObjectClassType();
            ClassTypeSliceCriteria criteria =
                    ClassTypeSliceCriteria.of(
                            CursorPageRequest.first(20), null, null, null, null, null);
            given(classTypeQueryPort.findBySliceCriteria(any()))
                    .willReturn(List.of(classType1, classType2));

            // when
            List<ClassType> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }
    }
}

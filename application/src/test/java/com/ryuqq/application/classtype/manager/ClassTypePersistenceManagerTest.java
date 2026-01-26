package com.ryuqq.application.classtype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.classtype.port.out.ClassTypeCommandPort;
import com.ryuqq.domain.classtype.aggregate.ClassType;
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
@Tag("manager")
@Tag("application-layer")
@DisplayName("ClassTypePersistenceManager 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ClassTypePersistenceManagerTest {

    @Mock private ClassTypeCommandPort classTypeCommandPort;

    private ClassTypePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypePersistenceManager(classTypeCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - ClassType 저장")
        void persist_WithValidClassType_ShouldReturnId() {
            // given
            ClassType classType = ClassTypeFixture.defaultNewClassType();
            given(classTypeCommandPort.persist(any())).willReturn(1L);

            // when
            Long result = sut.persist(classType);

            // then
            assertThat(result).isEqualTo(1L);
            then(classTypeCommandPort).should().persist(classType);
        }
    }
}

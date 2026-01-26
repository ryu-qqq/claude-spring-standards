package com.ryuqq.application.classtemplate.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.classtemplate.port.out.ClassTemplateCommandPort;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ClassTemplatePersistenceManager 단위 테스트
 *
 * <p>ClassTemplate 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ClassTemplatePersistenceManager 단위 테스트")
class ClassTemplatePersistenceManagerTest {

    @Mock private ClassTemplateCommandPort classTemplateCommandPort;

    @Mock private ClassTemplate classTemplate;

    private ClassTemplatePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTemplatePersistenceManager(classTemplateCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - ClassTemplate 영속화")
        void persist_WithClassTemplate_ShouldReturnId() {
            // given
            ClassTemplateId expectedId = ClassTemplateId.of(1L);
            given(classTemplateCommandPort.persist(classTemplate)).willReturn(expectedId);

            // when
            ClassTemplateId result = sut.persist(classTemplate);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(classTemplateCommandPort).should().persist(classTemplate);
        }
    }
}

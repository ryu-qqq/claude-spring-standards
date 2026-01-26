package com.ryuqq.application.resourcetemplate.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.resourcetemplate.port.out.ResourceTemplateCommandPort;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ResourceTemplatePersistenceManager 단위 테스트
 *
 * <p>ResourceTemplate 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ResourceTemplatePersistenceManager 단위 테스트")
class ResourceTemplatePersistenceManagerTest {

    @Mock private ResourceTemplateCommandPort resourceTemplateCommandPort;

    @Mock private ResourceTemplate resourceTemplate;

    private ResourceTemplatePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ResourceTemplatePersistenceManager(resourceTemplateCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - ResourceTemplate 영속화")
        void persist_WithResourceTemplate_ShouldReturnId() {
            // given
            ResourceTemplateId expectedId = ResourceTemplateId.of(1L);
            given(resourceTemplateCommandPort.persist(resourceTemplate)).willReturn(expectedId);

            // when
            ResourceTemplateId result = sut.persist(resourceTemplate);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(resourceTemplateCommandPort).should().persist(resourceTemplate);
        }
    }
}

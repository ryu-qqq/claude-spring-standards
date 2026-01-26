package com.ryuqq.application.configfiletemplate.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.configfiletemplate.port.out.ConfigFileTemplateCommandPort;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ConfigFileTemplatePersistenceManager 단위 테스트
 *
 * <p>ConfigFileTemplate 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ConfigFileTemplatePersistenceManager 단위 테스트")
class ConfigFileTemplatePersistenceManagerTest {

    @Mock private ConfigFileTemplateCommandPort configFileTemplateCommandPort;

    @Mock private ConfigFileTemplate configFileTemplate;

    private ConfigFileTemplatePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ConfigFileTemplatePersistenceManager(configFileTemplateCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - ConfigFileTemplate 영속화")
        void persist_WithConfigFileTemplate_ShouldReturnId() {
            // given
            Long expectedId = 1L;
            given(configFileTemplateCommandPort.persist(configFileTemplate)).willReturn(expectedId);

            // when
            Long result = sut.persist(configFileTemplate);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(configFileTemplateCommandPort).should().persist(configFileTemplate);
        }
    }
}

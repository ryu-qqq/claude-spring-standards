package com.ryuqq.application.configfiletemplate.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.configfiletemplate.port.out.ConfigFileTemplateQueryPort;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
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

/**
 * ConfigFileTemplateReadManager 단위 테스트
 *
 * <p>ConfigFileTemplate 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ConfigFileTemplateReadManager 단위 테스트")
class ConfigFileTemplateReadManagerTest {

    @Mock private ConfigFileTemplateQueryPort configFileTemplateQueryPort;

    @Mock private ConfigFileTemplate configFileTemplate;

    @Mock private ConfigFileTemplateSliceCriteria criteria;

    private ConfigFileTemplateReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ConfigFileTemplateReadManager(configFileTemplateQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ConfigFileTemplate 조회")
        void findById_WithValidId_ShouldReturnConfigFileTemplate() {
            // given
            ConfigFileTemplateId id = ConfigFileTemplateId.of(1L);
            given(configFileTemplateQueryPort.findById(id))
                    .willReturn(Optional.of(configFileTemplate));

            // when
            Optional<ConfigFileTemplate> result = sut.findById(id);

            // then
            assertThat(result).isPresent().contains(configFileTemplate);
            then(configFileTemplateQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            // given
            ConfigFileTemplateId id = ConfigFileTemplateId.of(999L);
            given(configFileTemplateQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<ConfigFileTemplate> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
            then(configFileTemplateQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID 확인")
        void existsById_WhenExists_ShouldReturnTrue() {
            // given
            ConfigFileTemplateId id = ConfigFileTemplateId.of(1L);
            given(configFileTemplateQueryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
            then(configFileTemplateQueryPort).should().existsById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<ConfigFileTemplate> configFileTemplates = List.of(configFileTemplate);
            given(configFileTemplateQueryPort.findBySliceCriteria(criteria))
                    .willReturn(configFileTemplates);

            // when
            List<ConfigFileTemplate> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(configFileTemplate);
            then(configFileTemplateQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByTechStackId 메서드")
    class ExistsByTechStackId {

        @Test
        @DisplayName("성공 - TechStack에 속한 ConfigFileTemplate 존재 확인")
        void existsByTechStackId_WhenExists_ShouldReturnTrue() {
            // given
            TechStackId techStackId = TechStackId.of(1L);
            given(configFileTemplateQueryPort.existsByTechStackId(techStackId)).willReturn(true);

            // when
            boolean result = sut.existsByTechStackId(techStackId);

            // then
            assertThat(result).isTrue();
            then(configFileTemplateQueryPort).should().existsByTechStackId(techStackId);
        }
    }

    @Nested
    @DisplayName("findForMcp 메서드")
    class FindForMcp {

        @Test
        @DisplayName("성공 - MCP Tool용 조건 기반 조회")
        void findForMcp_WithConditions_ShouldReturnList() {
            // given
            TechStackId techStackId = TechStackId.of(1L);
            Long architectureId = 1L;
            List<ToolType> toolTypes = List.of(ToolType.CLAUDE);
            List<ConfigFileTemplate> configFileTemplates = List.of(configFileTemplate);
            given(configFileTemplateQueryPort.findForMcp(techStackId, architectureId, toolTypes))
                    .willReturn(configFileTemplates);

            // when
            List<ConfigFileTemplate> result =
                    sut.findForMcp(techStackId, architectureId, toolTypes);

            // then
            assertThat(result).hasSize(1).containsExactly(configFileTemplate);
            then(configFileTemplateQueryPort)
                    .should()
                    .findForMcp(techStackId, architectureId, toolTypes);
        }
    }
}

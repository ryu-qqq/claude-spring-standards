package com.ryuqq.application.resourcetemplate.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.resourcetemplate.port.out.ResourceTemplateQueryPort;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.exception.ResourceTemplateNotFoundException;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
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
 * ResourceTemplateReadManager 단위 테스트
 *
 * <p>ResourceTemplate 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ResourceTemplateReadManager 단위 테스트")
class ResourceTemplateReadManagerTest {

    @Mock private ResourceTemplateQueryPort resourceTemplateQueryPort;

    @Mock private ResourceTemplate resourceTemplate;

    @Mock private ResourceTemplateSliceCriteria criteria;

    private ResourceTemplateReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ResourceTemplateReadManager(resourceTemplateQueryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 ResourceTemplate 조회")
        void getById_WithValidId_ShouldReturnResourceTemplate() {
            // given
            ResourceTemplateId id = ResourceTemplateId.of(1L);
            given(resourceTemplateQueryPort.findById(id)).willReturn(Optional.of(resourceTemplate));

            // when
            ResourceTemplate result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(resourceTemplate);
            then(resourceTemplateQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            ResourceTemplateId id = ResourceTemplateId.of(999L);
            given(resourceTemplateQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(ResourceTemplateNotFoundException.class);
            then(resourceTemplateQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ResourceTemplate 조회")
        void findById_WithValidId_ShouldReturnResourceTemplate() {
            // given
            ResourceTemplateId id = ResourceTemplateId.of(1L);
            given(resourceTemplateQueryPort.findById(id)).willReturn(Optional.of(resourceTemplate));

            // when
            ResourceTemplate result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(resourceTemplate);
            then(resourceTemplateQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 null 반환")
        void findById_WithNonExistentId_ShouldReturnNull() {
            // given
            ResourceTemplateId id = ResourceTemplateId.of(999L);
            given(resourceTemplateQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            ResourceTemplate result = sut.findById(id);

            // then
            assertThat(result).isNull();
            then(resourceTemplateQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<ResourceTemplate> templates = List.of(resourceTemplate);
            given(resourceTemplateQueryPort.findBySliceCriteria(criteria)).willReturn(templates);

            // when
            List<ResourceTemplate> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(resourceTemplate);
            then(resourceTemplateQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findByModuleId 메서드")
    class FindByModuleId {

        @Test
        @DisplayName("성공 - 모듈 ID로 리소스 템플릿 목록 조회")
        void findByModuleId_WithModuleId_ShouldReturnList() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            List<ResourceTemplate> templates = List.of(resourceTemplate);
            given(resourceTemplateQueryPort.findByModuleId(moduleId)).willReturn(templates);

            // when
            List<ResourceTemplate> result = sut.findByModuleId(moduleId);

            // then
            assertThat(result).hasSize(1).containsExactly(resourceTemplate);
            then(resourceTemplateQueryPort).should().findByModuleId(moduleId);
        }
    }
}

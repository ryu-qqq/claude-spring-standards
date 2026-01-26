package com.ryuqq.application.classtemplate.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.classtemplate.port.out.ClassTemplateQueryPort;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateNotFoundException;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
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
 * ClassTemplateReadManager 단위 테스트
 *
 * <p>ClassTemplate 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ClassTemplateReadManager 단위 테스트")
class ClassTemplateReadManagerTest {

    @Mock private ClassTemplateQueryPort classTemplateQueryPort;

    @Mock private ClassTemplate classTemplate;

    @Mock private ClassTemplateSliceCriteria criteria;

    private ClassTemplateReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTemplateReadManager(classTemplateQueryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 ClassTemplate 조회")
        void getById_WithValidId_ShouldReturnClassTemplate() {
            // given
            ClassTemplateId id = ClassTemplateId.of(1L);
            given(classTemplateQueryPort.findById(id)).willReturn(Optional.of(classTemplate));

            // when
            ClassTemplate result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(classTemplate);
            then(classTemplateQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            ClassTemplateId id = ClassTemplateId.of(999L);
            given(classTemplateQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(ClassTemplateNotFoundException.class);
            then(classTemplateQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ClassTemplate 조회")
        void findById_WithValidId_ShouldReturnClassTemplate() {
            // given
            ClassTemplateId id = ClassTemplateId.of(1L);
            given(classTemplateQueryPort.findById(id)).willReturn(Optional.of(classTemplate));

            // when
            ClassTemplate result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(classTemplate);
            then(classTemplateQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 null 반환")
        void findById_WithNonExistentId_ShouldReturnNull() {
            // given
            ClassTemplateId id = ClassTemplateId.of(999L);
            given(classTemplateQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            ClassTemplate result = sut.findById(id);

            // then
            assertThat(result).isNull();
            then(classTemplateQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<ClassTemplate> templates = List.of(classTemplate);
            given(classTemplateQueryPort.findBySliceCriteria(criteria)).willReturn(templates);

            // when
            List<ClassTemplate> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(classTemplate);
            then(classTemplateQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByStructureIdAndTemplateCode 메서드")
    class ExistsByStructureIdAndTemplateCode {

        @Test
        @DisplayName("성공 - 패키지 구조 내 템플릿 코드 존재 확인")
        void existsByStructureIdAndTemplateCode_WhenExists_ShouldReturnTrue() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            TemplateCode templateCode = TemplateCode.of("AGG-001");
            given(
                            classTemplateQueryPort.existsByStructureIdAndTemplateCode(
                                    structureId, templateCode))
                    .willReturn(true);

            // when
            boolean result = sut.existsByStructureIdAndTemplateCode(structureId, templateCode);

            // then
            assertThat(result).isTrue();
            then(classTemplateQueryPort)
                    .should()
                    .existsByStructureIdAndTemplateCode(structureId, templateCode);
        }
    }

    @Nested
    @DisplayName("existsByStructureIdAndTemplateCodeExcluding 메서드")
    class ExistsByStructureIdAndTemplateCodeExcluding {

        @Test
        @DisplayName("성공 - 특정 템플릿 제외하고 존재 확인")
        void existsByStructureIdAndTemplateCodeExcluding_WhenExists_ShouldReturnTrue() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            TemplateCode templateCode = TemplateCode.of("AGG-001");
            ClassTemplateId excludeId = ClassTemplateId.of(1L);
            given(
                            classTemplateQueryPort.existsByStructureIdAndTemplateCodeExcluding(
                                    structureId, templateCode, excludeId))
                    .willReturn(true);

            // when
            boolean result =
                    sut.existsByStructureIdAndTemplateCodeExcluding(
                            structureId, templateCode, excludeId);

            // then
            assertThat(result).isTrue();
            then(classTemplateQueryPort)
                    .should()
                    .existsByStructureIdAndTemplateCodeExcluding(
                            structureId, templateCode, excludeId);
        }
    }

    @Nested
    @DisplayName("findByStructureId 메서드")
    class FindByStructureId {

        @Test
        @DisplayName("성공 - 패키지 구조 ID로 클래스 템플릿 목록 조회")
        void findByStructureId_WithStructureId_ShouldReturnList() {
            // given
            Long structureId = 1L;
            List<ClassTemplate> templates = List.of(classTemplate);
            given(classTemplateQueryPort.findByStructureId(structureId)).willReturn(templates);

            // when
            List<ClassTemplate> result = sut.findByStructureId(structureId);

            // then
            assertThat(result).hasSize(1).containsExactly(classTemplate);
            then(classTemplateQueryPort).should().findByStructureId(structureId);
        }
    }

    @Nested
    @DisplayName("searchByKeyword 메서드")
    class SearchByKeyword {

        @Test
        @DisplayName("성공 - 키워드로 클래스 템플릿 검색")
        void searchByKeyword_WithKeyword_ShouldReturnList() {
            // given
            String keyword = "Aggregate";
            Long structureId = 1L;
            List<ClassTemplate> templates = List.of(classTemplate);
            given(classTemplateQueryPort.searchByKeyword(keyword, structureId))
                    .willReturn(templates);

            // when
            List<ClassTemplate> result = sut.searchByKeyword(keyword, structureId);

            // then
            assertThat(result).hasSize(1).containsExactly(classTemplate);
            then(classTemplateQueryPort).should().searchByKeyword(keyword, structureId);
        }
    }
}

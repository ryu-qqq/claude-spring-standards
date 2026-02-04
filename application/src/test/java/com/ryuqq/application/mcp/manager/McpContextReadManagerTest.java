package com.ryuqq.application.mcp.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.mcp.dto.context.CodingRuleWithDetailsDto;
import com.ryuqq.application.mcp.dto.context.ModuleWithLayerAndConventionDto;
import com.ryuqq.application.mcp.dto.context.PackageStructureWithPurposesDto;
import com.ryuqq.application.mcp.dto.context.PlanningLayerModuleStructureDto;
import com.ryuqq.application.mcp.dto.context.PlanningTechStackArchitectureDto;
import com.ryuqq.application.mcp.dto.context.TemplateAndTestDto;
import com.ryuqq.application.mcp.dto.context.ValidationChecklistDto;
import com.ryuqq.application.mcp.dto.context.ValidationZeroToleranceDto;
import com.ryuqq.application.mcp.port.out.McpContextQueryPort;
import com.ryuqq.domain.module.exception.ModuleNotFoundException;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
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
 * McpContextReadManager 단위 테스트
 *
 * <p>MCP Context 조회 매니저의 동작을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("McpContextReadManager 단위 테스트")
class McpContextReadManagerTest {

    @Mock private McpContextQueryPort mcpContextQueryPort;

    private McpContextReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new McpContextReadManager(mcpContextQueryPort);
    }

    @Nested
    @DisplayName("getModuleWithLayerAndConvention 메서드")
    class GetModuleWithLayerAndConvention {

        @Test
        @DisplayName("성공 - 모듈 정보 반환")
        void getModuleWithLayerAndConvention_WhenExists_ShouldReturnDto() {
            // given
            Long moduleId = 1L;
            ModuleWithLayerAndConventionDto expected =
                    new ModuleWithLayerAndConventionDto(
                            moduleId,
                            "TestModule",
                            "Description",
                            1L,
                            "DOMAIN",
                            "Domain",
                            1L,
                            "1.0.0",
                            "Convention Description");

            given(mcpContextQueryPort.findModuleWithLayerAndConvention(moduleId))
                    .willReturn(Optional.of(expected));

            // when
            ModuleWithLayerAndConventionDto result = sut.getModuleWithLayerAndConvention(moduleId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 - 모듈이 없으면 예외 발생")
        void getModuleWithLayerAndConvention_WhenNotExists_ShouldThrowException() {
            // given
            Long moduleId = 999L;

            given(mcpContextQueryPort.findModuleWithLayerAndConvention(moduleId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getModuleWithLayerAndConvention(moduleId))
                    .isInstanceOf(ModuleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findModuleWithLayerAndConvention 메서드")
    class FindModuleWithLayerAndConvention {

        @Test
        @DisplayName("성공 - Optional 반환")
        void findModuleWithLayerAndConvention_ShouldReturnOptional() {
            // given
            Long moduleId = 1L;
            ModuleWithLayerAndConventionDto expected =
                    new ModuleWithLayerAndConventionDto(
                            moduleId,
                            "TestModule",
                            "Description",
                            1L,
                            "DOMAIN",
                            "Domain",
                            1L,
                            "1.0.0",
                            "Convention Description");

            given(mcpContextQueryPort.findModuleWithLayerAndConvention(moduleId))
                    .willReturn(Optional.of(expected));

            // when
            Optional<ModuleWithLayerAndConventionDto> result =
                    sut.findModuleWithLayerAndConvention(moduleId);

            // then
            assertThat(result).isPresent().contains(expected);
        }
    }

    @Nested
    @DisplayName("findPackageStructuresWithPurposes 메서드")
    class FindPackageStructuresWithPurposes {

        @Test
        @DisplayName("성공 - 패키지 구조 목록 반환")
        void findPackageStructuresWithPurposes_ShouldReturnList() {
            // given
            Long moduleId = 1L;
            List<PackageStructureWithPurposesDto> expected = List.of();

            given(mcpContextQueryPort.findPackageStructuresWithPurposes(moduleId))
                    .willReturn(expected);

            // when
            List<PackageStructureWithPurposesDto> result =
                    sut.findPackageStructuresWithPurposes(moduleId);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("findTemplatesAndTests 메서드")
    class FindTemplatesAndTests {

        @Test
        @DisplayName("성공 - 빈 구조 ID 목록이면 빈 리스트 반환")
        void findTemplatesAndTests_WhenEmptyIds_ShouldReturnEmptyList() {
            // given
            List<Long> structureIds = List.of();

            // when
            List<TemplateAndTestDto> result = sut.findTemplatesAndTests(structureIds, null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공 - 템플릿 및 테스트 목록 반환")
        void findTemplatesAndTests_WhenHasIds_ShouldReturnList() {
            // given
            List<Long> structureIds = List.of(1L, 2L);
            Long classTypeId = 1L;
            List<TemplateAndTestDto> expected = List.of();

            given(mcpContextQueryPort.findTemplatesAndTests(structureIds, classTypeId))
                    .willReturn(expected);

            // when
            List<TemplateAndTestDto> result = sut.findTemplatesAndTests(structureIds, classTypeId);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("findCodingRulesWithDetails 메서드")
    class FindCodingRulesWithDetails {

        @Test
        @DisplayName("성공 - null 컨벤션 ID이면 빈 리스트 반환")
        void findCodingRulesWithDetails_WhenNullConventionId_ShouldReturnEmptyList() {
            // given
            Long conventionId = null;
            Long classTypeId = 1L;

            // when
            List<CodingRuleWithDetailsDto> result =
                    sut.findCodingRulesWithDetails(conventionId, classTypeId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공 - 코딩 규칙 목록 반환")
        void findCodingRulesWithDetails_WhenHasConventionId_ShouldReturnList() {
            // given
            Long conventionId = 1L;
            Long classTypeId = 1L;
            List<CodingRuleWithDetailsDto> expected = List.of();

            given(mcpContextQueryPort.findCodingRulesWithDetails(conventionId, classTypeId))
                    .willReturn(expected);

            // when
            List<CodingRuleWithDetailsDto> result =
                    sut.findCodingRulesWithDetails(conventionId, classTypeId);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("getTechStackWithArchitecture 메서드")
    class GetTechStackWithArchitecture {

        @Test
        @DisplayName("성공 - 기술 스택 정보 반환")
        void getTechStackWithArchitecture_WhenExists_ShouldReturnDto() {
            // given
            Long techStackId = 1L;
            PlanningTechStackArchitectureDto expected =
                    new PlanningTechStackArchitectureDto(
                            techStackId,
                            "Spring Boot",
                            "Java",
                            "21",
                            "Spring Boot",
                            "3.5.x",
                            1L,
                            "Hexagonal",
                            "Description");

            given(mcpContextQueryPort.findTechStackWithArchitecture(techStackId))
                    .willReturn(Optional.of(expected));

            // when
            PlanningTechStackArchitectureDto result = sut.getTechStackWithArchitecture(techStackId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 - 기술 스택이 없으면 예외 발생")
        void getTechStackWithArchitecture_WhenNotExists_ShouldThrowException() {
            // given
            Long techStackId = 999L;

            given(mcpContextQueryPort.findTechStackWithArchitecture(techStackId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getTechStackWithArchitecture(techStackId))
                    .isInstanceOf(TechStackNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findTechStackWithArchitecture 메서드")
    class FindTechStackWithArchitecture {

        @Test
        @DisplayName("성공 - Optional 반환")
        void findTechStackWithArchitecture_ShouldReturnOptional() {
            // given
            Long techStackId = 1L;
            PlanningTechStackArchitectureDto expected =
                    new PlanningTechStackArchitectureDto(
                            techStackId,
                            "Spring Boot",
                            "Java",
                            "21",
                            "Spring Boot",
                            "3.5.x",
                            1L,
                            "Hexagonal",
                            "Description");

            given(mcpContextQueryPort.findTechStackWithArchitecture(techStackId))
                    .willReturn(Optional.of(expected));

            // when
            Optional<PlanningTechStackArchitectureDto> result =
                    sut.findTechStackWithArchitecture(techStackId);

            // then
            assertThat(result).isPresent().contains(expected);
        }
    }

    @Nested
    @DisplayName("findLayerModuleStructures 메서드")
    class FindLayerModuleStructures {

        @Test
        @DisplayName("성공 - null 아키텍처 ID이면 빈 리스트 반환")
        void findLayerModuleStructures_WhenNullArchitectureId_ShouldReturnEmptyList() {
            // given
            Long architectureId = null;
            List<String> layerCodes = List.of("DOMAIN");

            // when
            List<PlanningLayerModuleStructureDto> result =
                    sut.findLayerModuleStructures(architectureId, layerCodes);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공 - 레이어 모듈 구조 목록 반환")
        void findLayerModuleStructures_WhenHasArchitectureId_ShouldReturnList() {
            // given
            Long architectureId = 1L;
            List<String> layerCodes = List.of("DOMAIN");
            List<PlanningLayerModuleStructureDto> expected = List.of();

            given(mcpContextQueryPort.findLayerModuleStructures(architectureId, layerCodes))
                    .willReturn(expected);

            // when
            List<PlanningLayerModuleStructureDto> result =
                    sut.findLayerModuleStructures(architectureId, layerCodes);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("findZeroToleranceRulesForValidation 메서드")
    class FindZeroToleranceRulesForValidation {

        @Test
        @DisplayName("성공 - null 아키텍처 ID이면 빈 리스트 반환")
        void findZeroToleranceRulesForValidation_WhenNullArchitectureId_ShouldReturnEmptyList() {
            // given
            Long architectureId = null;
            List<String> layerCodes = List.of();
            List<String> classTypes = List.of();

            // when
            List<ValidationZeroToleranceDto> result =
                    sut.findZeroToleranceRulesForValidation(architectureId, layerCodes, classTypes);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공 - 제로 톨러런스 규칙 목록 반환")
        void findZeroToleranceRulesForValidation_WhenHasArchitectureId_ShouldReturnList() {
            // given
            Long architectureId = 1L;
            List<String> layerCodes = List.of("DOMAIN");
            List<String> classTypes = List.of("Aggregate");
            List<ValidationZeroToleranceDto> expected = List.of();

            given(
                            mcpContextQueryPort.findZeroToleranceRulesForValidation(
                                    architectureId, layerCodes, classTypes))
                    .willReturn(expected);

            // when
            List<ValidationZeroToleranceDto> result =
                    sut.findZeroToleranceRulesForValidation(architectureId, layerCodes, classTypes);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("findChecklistItemsForValidation 메서드")
    class FindChecklistItemsForValidation {

        @Test
        @DisplayName("성공 - null 아키텍처 ID이면 빈 리스트 반환")
        void findChecklistItemsForValidation_WhenNullArchitectureId_ShouldReturnEmptyList() {
            // given
            Long architectureId = null;
            List<String> layerCodes = List.of();
            List<String> classTypes = List.of();

            // when
            List<ValidationChecklistDto> result =
                    sut.findChecklistItemsForValidation(architectureId, layerCodes, classTypes);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공 - 체크리스트 항목 목록 반환")
        void findChecklistItemsForValidation_WhenHasArchitectureId_ShouldReturnList() {
            // given
            Long architectureId = 1L;
            List<String> layerCodes = List.of("DOMAIN");
            List<String> classTypes = List.of("Aggregate");
            List<ValidationChecklistDto> expected = List.of();

            given(
                            mcpContextQueryPort.findChecklistItemsForValidation(
                                    architectureId, layerCodes, classTypes))
                    .willReturn(expected);

            // when
            List<ValidationChecklistDto> result =
                    sut.findChecklistItemsForValidation(architectureId, layerCodes, classTypes);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}

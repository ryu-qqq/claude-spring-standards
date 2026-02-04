package com.ryuqq.application.mcp.manager;

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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * McpContextReadManager - MCP Context 조회 전용 ReadManager
 *
 * <p>MCP 서비스에서 사용하는 Module Context 관련 데이터를 조회합니다.
 *
 * <p>N+1 문제 해결을 위해 최적화된 쿼리를 사용합니다 (20+ → 4-5 쿼리).
 *
 * <p>MGR-001: Manager 명명 규칙 준수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class McpContextReadManager {

    private final McpContextQueryPort mcpContextQueryPort;

    public McpContextReadManager(McpContextQueryPort mcpContextQueryPort) {
        this.mcpContextQueryPort = mcpContextQueryPort;
    }

    /**
     * Module + Layer + Convention(Active) 조회
     *
     * <p>Module을 기준으로 Layer와 활성 Convention을 한 번의 쿼리로 조회합니다.
     *
     * @param moduleId 모듈 ID
     * @return Module 기본 정보 (Layer, Convention 포함)
     * @throws ModuleNotFoundException 모듈이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public ModuleWithLayerAndConventionDto getModuleWithLayerAndConvention(Long moduleId) {
        return mcpContextQueryPort
                .findModuleWithLayerAndConvention(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException(moduleId));
    }

    /**
     * Module + Layer + Convention(Active) 조회 (Optional)
     *
     * @param moduleId 모듈 ID
     * @return Module 기본 정보 (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<ModuleWithLayerAndConventionDto> findModuleWithLayerAndConvention(
            Long moduleId) {
        return mcpContextQueryPort.findModuleWithLayerAndConvention(moduleId);
    }

    /**
     * PackageStructure + PackagePurpose 조회
     *
     * @param moduleId 모듈 ID
     * @return PackageStructure 목록 (각각 PackagePurpose 포함)
     */
    @Transactional(readOnly = true)
    public List<PackageStructureWithPurposesDto> findPackageStructuresWithPurposes(Long moduleId) {
        return mcpContextQueryPort.findPackageStructuresWithPurposes(moduleId);
    }

    /**
     * ClassTemplate + ArchUnitTest 조회
     *
     * @param structureIds 패키지 구조 ID 목록
     * @param classTypeId 클래스 타입 ID 필터 (nullable)
     * @return ClassTemplate 및 ArchUnitTest 목록
     */
    @Transactional(readOnly = true)
    public List<TemplateAndTestDto> findTemplatesAndTests(
            List<Long> structureIds, Long classTypeId) {
        if (structureIds.isEmpty()) {
            return List.of();
        }
        return mcpContextQueryPort.findTemplatesAndTests(structureIds, classTypeId);
    }

    /**
     * CodingRule + 상세 정보 조회
     *
     * @param conventionId 컨벤션 ID
     * @param classTypeId 클래스 타입 ID (appliesTo 필터링용)
     * @return CodingRule 목록 (각각 RuleExample, ZeroTolerance, ChecklistItem 포함)
     */
    @Transactional(readOnly = true)
    public List<CodingRuleWithDetailsDto> findCodingRulesWithDetails(
            Long conventionId, Long classTypeId) {
        if (conventionId == null) {
            return List.of();
        }
        return mcpContextQueryPort.findCodingRulesWithDetails(conventionId, classTypeId);
    }

    // ========== Planning Context 조회 메서드 ==========

    /**
     * TechStack + Architecture 조회 (Planning Context용)
     *
     * @param techStackId 기술 스택 ID (nullable - null이면 활성 스택 조회)
     * @return TechStack + Architecture 정보
     * @throws TechStackNotFoundException 기술 스택이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public PlanningTechStackArchitectureDto getTechStackWithArchitecture(Long techStackId) {
        return mcpContextQueryPort
                .findTechStackWithArchitecture(techStackId)
                .orElseThrow(() -> new TechStackNotFoundException(techStackId));
    }

    /**
     * TechStack + Architecture 조회 (Optional)
     *
     * @param techStackId 기술 스택 ID (nullable)
     * @return TechStack + Architecture 정보 (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<PlanningTechStackArchitectureDto> findTechStackWithArchitecture(
            Long techStackId) {
        return mcpContextQueryPort.findTechStackWithArchitecture(techStackId);
    }

    /**
     * Layer + Module + PackageStructure + 통계 조회 (Planning Context용)
     *
     * @param architectureId 아키텍처 ID
     * @param layerCodes 레이어 코드 필터 목록
     * @return Flat 구조의 조회 결과
     */
    @Transactional(readOnly = true)
    public List<PlanningLayerModuleStructureDto> findLayerModuleStructures(
            Long architectureId, List<String> layerCodes) {
        if (architectureId == null) {
            return List.of();
        }
        return mcpContextQueryPort.findLayerModuleStructures(architectureId, layerCodes);
    }

    // ========== Validation Context 조회 메서드 ==========

    /**
     * ZeroToleranceRule 조회 (Validation Context용)
     *
     * <p>Layer → Module → Convention(Active) → CodingRule → ZeroToleranceRule JOIN.
     *
     * @param architectureId 아키텍처 ID
     * @param layerCodes 레이어 코드 필터 목록 (empty면 전체 조회)
     * @param classTypes 클래스 타입 필터 목록 (empty면 전체 조회)
     * @return ZeroTolerance 규칙 목록
     */
    @Transactional(readOnly = true)
    public List<ValidationZeroToleranceDto> findZeroToleranceRulesForValidation(
            Long architectureId, List<String> layerCodes, List<String> classTypes) {
        if (architectureId == null) {
            return List.of();
        }
        return mcpContextQueryPort.findZeroToleranceRulesForValidation(
                architectureId, layerCodes, classTypes);
    }

    /**
     * ChecklistItem 조회 (Validation Context용)
     *
     * <p>Layer → Module → Convention(Active) → CodingRule → ChecklistItem JOIN.
     *
     * @param architectureId 아키텍처 ID
     * @param layerCodes 레이어 코드 필터 목록 (empty면 전체 조회)
     * @param classTypes 클래스 타입 필터 목록 (empty면 전체 조회)
     * @return 체크리스트 항목 목록
     */
    @Transactional(readOnly = true)
    public List<ValidationChecklistDto> findChecklistItemsForValidation(
            Long architectureId, List<String> layerCodes, List<String> classTypes) {
        if (architectureId == null) {
            return List.of();
        }
        return mcpContextQueryPort.findChecklistItemsForValidation(
                architectureId, layerCodes, classTypes);
    }
}

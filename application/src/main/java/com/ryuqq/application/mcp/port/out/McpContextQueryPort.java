package com.ryuqq.application.mcp.port.out;

import com.ryuqq.application.mcp.dto.context.CodingRuleWithDetailsDto;
import com.ryuqq.application.mcp.dto.context.ModuleWithLayerAndConventionDto;
import com.ryuqq.application.mcp.dto.context.PackageStructureWithPurposesDto;
import com.ryuqq.application.mcp.dto.context.PlanningLayerModuleStructureDto;
import com.ryuqq.application.mcp.dto.context.PlanningTechStackArchitectureDto;
import com.ryuqq.application.mcp.dto.context.TemplateAndTestDto;
import com.ryuqq.application.mcp.dto.context.ValidationChecklistDto;
import com.ryuqq.application.mcp.dto.context.ValidationZeroToleranceDto;
import java.util.List;
import java.util.Optional;

/**
 * McpContextQueryPort - MCP Context 조회 전용 Port
 *
 * <p>MCP 서비스에서 사용하는 Module Context 관련 데이터를 조회합니다.
 *
 * <p>성능 최적화를 위해 JOIN 쿼리를 사용하여 N+1 문제를 해결합니다.
 *
 * <p>PORT-001: Port 인터페이스 명명 규칙 준수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface McpContextQueryPort {

    /**
     * Module + Layer + Convention(Active) 조회
     *
     * <p>Module을 기준으로 Layer와 활성 Convention을 JOIN하여 조회합니다.
     *
     * @param moduleId 모듈 ID
     * @return Module 기본 정보 + Layer 정보 + Active Convention 정보
     */
    Optional<ModuleWithLayerAndConventionDto> findModuleWithLayerAndConvention(Long moduleId);

    /**
     * Convention 기준 CodingRule + RuleExample + ZeroTolerance + ChecklistItem 조회
     *
     * <p>Convention의 CodingRule 중 classTypeId에 해당하는 규칙만 조회합니다.
     *
     * @param conventionId 컨벤션 ID
     * @param classTypeId 클래스 타입 ID (appliesTo 필터링용)
     * @return CodingRule 목록 (각각 RuleExample, ZeroTolerance, ChecklistItem 포함)
     */
    List<CodingRuleWithDetailsDto> findCodingRulesWithDetails(Long conventionId, Long classTypeId);

    /**
     * Module 기준 PackageStructure + PackagePurpose 조회
     *
     * <p>Module의 모든 PackageStructure와 PackagePurpose를 JOIN하여 조회합니다.
     *
     * @param moduleId 모듈 ID
     * @return PackageStructure 목록 (각각 PackagePurpose 포함)
     */
    List<PackageStructureWithPurposesDto> findPackageStructuresWithPurposes(Long moduleId);

    /**
     * PackageStructure ID 목록 기준 ClassTemplate + ArchUnitTest 조회
     *
     * <p>PackageStructure의 ClassTemplate과 ArchUnitTest를 조회합니다.
     *
     * @param structureIds 패키지 구조 ID 목록
     * @param classTypeId 클래스 타입 ID 필터 (nullable)
     * @return ClassTemplate 및 ArchUnitTest 목록
     */
    List<TemplateAndTestDto> findTemplatesAndTests(List<Long> structureIds, Long classTypeId);

    // ========== Planning Context 조회 메서드 ==========

    /**
     * TechStack + Architecture 조회 (Planning Context용)
     *
     * <p>TechStack과 연결된 Architecture를 JOIN하여 조회합니다.
     *
     * @param techStackId 기술 스택 ID (nullable - null이면 활성 스택 조회)
     * @return TechStack + Architecture 정보
     */
    Optional<PlanningTechStackArchitectureDto> findTechStackWithArchitecture(Long techStackId);

    /**
     * Layer + Module + PackageStructure + 통계 조회 (Planning Context용)
     *
     * <p>Architecture의 모든 Layer, Module, PackageStructure를 JOIN하여 조회합니다.
     *
     * <p>각 PackageStructure별 템플릿 개수, 규칙 개수를 서브쿼리로 계산합니다.
     *
     * @param architectureId 아키텍처 ID
     * @param layerCodes 레이어 코드 필터 목록 (empty면 전체 조회)
     * @return Flat 구조의 조회 결과 (메모리에서 그룹핑 필요)
     */
    List<PlanningLayerModuleStructureDto> findLayerModuleStructures(
            Long architectureId, List<String> layerCodes);

    // ========== Validation Context 조회 메서드 ==========

    /**
     * ZeroToleranceRule 조회 (Validation Context용)
     *
     * <p>Layer → Module → Convention(Active) → CodingRule → ZeroToleranceRule JOIN.
     *
     * <p>layerCodes, classTypes 필터를 지원합니다.
     *
     * @param architectureId 아키텍처 ID
     * @param layerCodes 레이어 코드 필터 목록 (empty면 전체 조회)
     * @param classTypes 클래스 타입 필터 목록 (empty면 전체 조회)
     * @return ZeroTolerance 규칙 목록
     */
    List<ValidationZeroToleranceDto> findZeroToleranceRulesForValidation(
            Long architectureId, List<String> layerCodes, List<String> classTypes);

    /**
     * ChecklistItem 조회 (Validation Context용)
     *
     * <p>Layer → Module → Convention(Active) → CodingRule → ChecklistItem JOIN.
     *
     * <p>layerCodes, classTypes 필터를 지원합니다.
     *
     * @param architectureId 아키텍처 ID
     * @param layerCodes 레이어 코드 필터 목록 (empty면 전체 조회)
     * @param classTypes 클래스 타입 필터 목록 (empty면 전체 조회)
     * @return 체크리스트 항목 목록
     */
    List<ValidationChecklistDto> findChecklistItemsForValidation(
            Long architectureId, List<String> layerCodes, List<String> classTypes);
}

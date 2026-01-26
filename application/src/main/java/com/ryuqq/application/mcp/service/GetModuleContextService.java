package com.ryuqq.application.mcp.service;

import com.ryuqq.application.mcp.assembler.McpContextAssembler;
import com.ryuqq.application.mcp.dto.context.CodingRuleWithDetailsDto;
import com.ryuqq.application.mcp.dto.context.ModuleWithLayerAndConventionDto;
import com.ryuqq.application.mcp.dto.context.PackageStructureWithPurposesDto;
import com.ryuqq.application.mcp.dto.context.TemplateAndTestDto;
import com.ryuqq.application.mcp.dto.query.ModuleContextQuery;
import com.ryuqq.application.mcp.dto.response.ExecutionContextResult;
import com.ryuqq.application.mcp.dto.response.ModuleContextResult;
import com.ryuqq.application.mcp.dto.response.RuleContextResult;
import com.ryuqq.application.mcp.manager.McpContextReadManager;
import com.ryuqq.application.mcp.port.in.GetModuleContextUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetModuleContextService - Module Context 조회 서비스
 *
 * <p>GetModuleContextUseCase를 구현합니다.
 *
 * <p>N+1 문제 해결을 위해 McpContextReadManager를 사용하여 최적화된 쿼리(4-5개)로 조회합니다.
 *
 * <p>Assembler 패턴을 사용하여 조회된 DTO를 응답 Result로 변환합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class GetModuleContextService implements GetModuleContextUseCase {

    private final McpContextReadManager mcpContextReadManager;
    private final McpContextAssembler mcpContextAssembler;

    public GetModuleContextService(
            McpContextReadManager mcpContextReadManager, McpContextAssembler mcpContextAssembler) {
        this.mcpContextReadManager = mcpContextReadManager;
        this.mcpContextAssembler = mcpContextAssembler;
    }

    @Override
    public ModuleContextResult execute(ModuleContextQuery query) {
        // Query 1: Module + Layer + Convention (Active) 조회
        ModuleWithLayerAndConventionDto moduleDto =
                mcpContextReadManager.getModuleWithLayerAndConvention(query.moduleId());

        // Query 2: PackageStructure + Purpose 조회
        List<PackageStructureWithPurposesDto> structures =
                mcpContextReadManager.findPackageStructuresWithPurposes(query.moduleId());

        // Query 3: ClassTemplate + ArchUnitTest 조회
        List<Long> structureIds =
                structures.stream().map(PackageStructureWithPurposesDto::structureId).toList();
        List<TemplateAndTestDto> templatesAndTests =
                mcpContextReadManager.findTemplatesAndTests(structureIds, query.classTypeId());

        // Query 4: CodingRule + RuleExample + ZeroTolerance + ChecklistItem 조회
        List<CodingRuleWithDetailsDto> codingRules =
                mcpContextReadManager.findCodingRulesWithDetails(moduleDto.conventionId());

        // Assembler를 사용하여 결과 조립
        ExecutionContextResult executionContext =
                mcpContextAssembler.toExecutionContextResult(structures, templatesAndTests);
        RuleContextResult ruleContext =
                mcpContextAssembler.toRuleContextResult(moduleDto, codingRules);

        return mcpContextAssembler.assemble(moduleDto, executionContext, ruleContext);
    }
}

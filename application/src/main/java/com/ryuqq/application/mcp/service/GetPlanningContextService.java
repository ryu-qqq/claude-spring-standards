package com.ryuqq.application.mcp.service;

import com.ryuqq.application.mcp.assembler.McpContextAssembler;
import com.ryuqq.application.mcp.dto.context.PlanningLayerModuleStructureDto;
import com.ryuqq.application.mcp.dto.context.PlanningTechStackArchitectureDto;
import com.ryuqq.application.mcp.dto.query.PlanningContextQuery;
import com.ryuqq.application.mcp.dto.response.PlanningContextResult;
import com.ryuqq.application.mcp.manager.McpContextReadManager;
import com.ryuqq.application.mcp.port.in.GetPlanningContextUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetPlanningContextService - Planning Context 조회 서비스
 *
 * <p>GetPlanningContextUseCase를 구현합니다.
 *
 * <p>N+1 문제 해결: 60+ 쿼리 → 2 쿼리로 최적화.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class GetPlanningContextService implements GetPlanningContextUseCase {

    private final McpContextReadManager readManager;
    private final McpContextAssembler assembler;

    public GetPlanningContextService(
            McpContextReadManager readManager, McpContextAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public PlanningContextResult execute(PlanningContextQuery query) {
        // Query 1: TechStack + Architecture 조회 (1 쿼리)
        PlanningTechStackArchitectureDto techStackDto =
                readManager.getTechStackWithArchitecture(query.techStackId());

        // Query 2: Layer + Module + PackageStructure + 통계 조회 (1 쿼리)
        List<PlanningLayerModuleStructureDto> layerStructures =
                readManager.findLayerModuleStructures(
                        techStackDto.architectureId(), query.layers());

        // Assemble: Flat 구조 → 계층적 Result 변환 (메모리에서 처리)
        return assembler.assemblePlanningContext(techStackDto, layerStructures);
    }
}

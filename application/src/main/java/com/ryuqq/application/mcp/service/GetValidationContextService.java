package com.ryuqq.application.mcp.service;

import com.ryuqq.application.mcp.assembler.ValidationContextAssembler;
import com.ryuqq.application.mcp.dto.context.ValidationChecklistDto;
import com.ryuqq.application.mcp.dto.context.ValidationZeroToleranceDto;
import com.ryuqq.application.mcp.dto.query.ValidationContextQuery;
import com.ryuqq.application.mcp.dto.response.ValidationContextResult;
import com.ryuqq.application.mcp.manager.McpContextReadManager;
import com.ryuqq.application.mcp.port.in.GetValidationContextUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetValidationContextService - Validation Context 조회 서비스
 *
 * <p>GetValidationContextUseCase를 구현합니다.
 *
 * <p>N+1 문제 해결을 위해 McpContextReadManager의 최적화된 쿼리를 사용합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class GetValidationContextService implements GetValidationContextUseCase {

    private final McpContextReadManager mcpContextReadManager;
    private final ValidationContextAssembler validationContextAssembler;

    public GetValidationContextService(
            McpContextReadManager mcpContextReadManager,
            ValidationContextAssembler validationContextAssembler) {
        this.mcpContextReadManager = mcpContextReadManager;
        this.validationContextAssembler = validationContextAssembler;
    }

    @Override
    public ValidationContextResult execute(ValidationContextQuery query) {
        Long techStackId = query.techStackId();
        Long architectureId = query.architectureId();

        // 1. TechStack + Architecture 유효성 검증
        mcpContextReadManager.getTechStackWithArchitecture(techStackId);

        // 2. ZeroToleranceRules 조회 (최적화된 JOIN 쿼리)
        List<ValidationZeroToleranceDto> zeroToleranceRules =
                mcpContextReadManager.findZeroToleranceRulesForValidation(
                        architectureId, query.layers(), query.classTypes());

        // 3. ChecklistItems 조회 (최적화된 JOIN 쿼리)
        List<ValidationChecklistDto> checklistItems =
                mcpContextReadManager.findChecklistItemsForValidation(
                        architectureId, query.layers(), query.classTypes());

        // 4. 결과 조립 (Assembler 위임)
        return validationContextAssembler.assemble(zeroToleranceRules, checklistItems);
    }
}

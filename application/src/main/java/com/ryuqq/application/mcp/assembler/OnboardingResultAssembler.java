package com.ryuqq.application.mcp.assembler;

import com.ryuqq.application.mcp.dto.response.OnboardingContextsResult;
import com.ryuqq.application.mcp.dto.response.OnboardingResult;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OnboardingResultAssembler - OnboardingContext Domain → MCP Result DTO 변환
 *
 * <p>Domain 객체를 MCP Tool용 Response DTO로 변환합니다.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 → Assembler를 통해 변환.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지 → Assembler에서 값 추출.
 *
 * <p>C-002: 변환기에서 null 체크 금지.
 *
 * <p>C-003: 변환기에서 기본값 할당 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class OnboardingResultAssembler {

    /**
     * OnboardingContext Domain을 OnboardingResult로 변환
     *
     * @param context OnboardingContext 도메인 객체
     * @return OnboardingResult
     */
    public OnboardingResult toResult(OnboardingContext context) {
        return new OnboardingResult(
                context.idValue(),
                context.contextTypeName(),
                context.titleValue(),
                context.contentValue(),
                context.priorityValue());
    }

    /**
     * OnboardingContext Domain 목록을 OnboardingResult 목록으로 변환
     *
     * @param contexts OnboardingContext 도메인 객체 목록
     * @return OnboardingResult 목록
     */
    public List<OnboardingResult> toResults(List<OnboardingContext> contexts) {
        return contexts.stream().map(this::toResult).toList();
    }

    /**
     * OnboardingContext Domain 목록을 OnboardingContextsResult로 변환
     *
     * @param contexts OnboardingContext 도메인 객체 목록
     * @return OnboardingContextsResult (목록 + 총 개수)
     */
    public OnboardingContextsResult toOnboardingContextsResult(List<OnboardingContext> contexts) {
        List<OnboardingResult> results = toResults(contexts);
        return new OnboardingContextsResult(results, results.size());
    }
}

package com.ryuqq.application.onboardingcontext.assembler;

import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextResult;
import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextSliceResult;
import com.ryuqq.domain.common.vo.SliceMeta;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OnboardingContextAssembler - OnboardingContext Domain → Response DTO 변환
 *
 * <p>Domain 객체를 Response DTO로 변환합니다.
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
public class OnboardingContextAssembler {

    /**
     * OnboardingContext Domain을 OnboardingContextResult로 변환
     *
     * @param onboardingContext OnboardingContext 도메인 객체
     * @return OnboardingContextResult
     */
    public OnboardingContextResult toResult(OnboardingContext onboardingContext) {
        return new OnboardingContextResult(
                onboardingContext.idValue(),
                onboardingContext.techStackIdValue(),
                onboardingContext.architectureIdValue(),
                onboardingContext.contextTypeName(),
                onboardingContext.titleValue(),
                onboardingContext.contentValue(),
                onboardingContext.priorityValue(),
                onboardingContext.isDeleted(),
                onboardingContext.createdAt(),
                onboardingContext.updatedAt());
    }

    /**
     * OnboardingContext Domain 목록을 OnboardingContextResult 목록으로 변환
     *
     * @param onboardingContexts OnboardingContext 도메인 객체 목록
     * @return OnboardingContextResult 목록
     */
    public List<OnboardingContextResult> toResults(List<OnboardingContext> onboardingContexts) {
        return onboardingContexts.stream().map(this::toResult).toList();
    }

    /**
     * OnboardingContext Domain 목록을 OnboardingContextSliceResult로 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 → SliceMeta와 함께 반환합니다.
     *
     * @param onboardingContexts OnboardingContext 도메인 객체 목록
     * @param size 페이지 크기
     * @return OnboardingContextSliceResult
     */
    public OnboardingContextSliceResult toSliceResult(
            List<OnboardingContext> onboardingContexts, int size) {
        List<OnboardingContextResult> content = toResults(onboardingContexts);
        boolean hasNext = content.size() > size;

        if (hasNext) {
            content = content.subList(0, size);
        }

        return new OnboardingContextSliceResult(content, SliceMeta.of(size, hasNext));
    }
}

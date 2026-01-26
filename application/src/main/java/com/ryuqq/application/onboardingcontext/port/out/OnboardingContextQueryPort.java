package com.ryuqq.application.onboardingcontext.port.out;

import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.onboardingcontext.query.OnboardingContextSliceCriteria;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;

/**
 * OnboardingContextQueryPort - OnboardingContext 조회 Port
 *
 * <p>영속성 계층으로의 OnboardingContext 조회 아웃바운드 포트입니다.
 *
 * <p>QPRT-002: 표준 메서드를 제공합니다.
 *
 * <p>QPRT-004: 원시타입 대신 VO를 파라미터로 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface OnboardingContextQueryPort {

    /**
     * ID로 OnboardingContext 조회
     *
     * @param id OnboardingContext ID (VO)
     * @return OnboardingContext (Optional)
     */
    Optional<OnboardingContext> findById(OnboardingContextId id);

    /**
     * ID로 존재 여부 확인
     *
     * @param id OnboardingContext ID (VO)
     * @return 존재 여부
     */
    boolean existsById(OnboardingContextId id);

    /**
     * 커서 기반 슬라이스 조건으로 OnboardingContext 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return OnboardingContext 목록
     */
    List<OnboardingContext> findBySliceCriteria(OnboardingContextSliceCriteria criteria);

    /**
     * TechStack에 속한 OnboardingContext 존재 여부 확인
     *
     * @param techStackId TechStack ID (VO)
     * @return 자식 존재 여부
     */
    boolean existsByTechStackId(TechStackId techStackId);

    /**
     * MCP Tool용 조건 기반 OnboardingContext 목록 조회
     *
     * <p>get_onboarding_context Tool에서 사용. 페이지네이션 없이 조건에 맞는 전체 목록 반환.
     *
     * @param techStackId TechStack ID (필수)
     * @param architectureId Architecture ID (nullable)
     * @param contextTypes Context Type 목록 (nullable)
     * @return OnboardingContext 목록 (priority 오름차순 정렬)
     */
    List<OnboardingContext> findForMcp(
            TechStackId techStackId, Long architectureId, List<ContextType> contextTypes);
}

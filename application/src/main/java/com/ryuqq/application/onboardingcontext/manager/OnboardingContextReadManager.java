package com.ryuqq.application.onboardingcontext.manager;

import com.ryuqq.application.onboardingcontext.port.out.OnboardingContextQueryPort;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.onboardingcontext.query.OnboardingContextSliceCriteria;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OnboardingContextReadManager - OnboardingContext 조회 관리자
 *
 * <p>QueryPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional(readOnly=true)은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class OnboardingContextReadManager {

    private final OnboardingContextQueryPort onboardingContextQueryPort;

    public OnboardingContextReadManager(OnboardingContextQueryPort onboardingContextQueryPort) {
        this.onboardingContextQueryPort = onboardingContextQueryPort;
    }

    /**
     * ID로 OnboardingContext 조회
     *
     * @param id OnboardingContext ID (VO)
     * @return OnboardingContext (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<OnboardingContext> findById(OnboardingContextId id) {
        return onboardingContextQueryPort.findById(id);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id OnboardingContext ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(OnboardingContextId id) {
        return onboardingContextQueryPort.existsById(id);
    }

    /**
     * 커서 기반 슬라이스 조건으로 OnboardingContext 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return OnboardingContext 목록
     */
    @Transactional(readOnly = true)
    public List<OnboardingContext> findBySliceCriteria(OnboardingContextSliceCriteria criteria) {
        return onboardingContextQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * TechStack에 속한 OnboardingContext 존재 여부 확인
     *
     * @param techStackId TechStack ID (VO)
     * @return 자식 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByTechStackId(TechStackId techStackId) {
        return onboardingContextQueryPort.existsByTechStackId(techStackId);
    }

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
    @Transactional(readOnly = true)
    public List<OnboardingContext> findForMcp(
            TechStackId techStackId,
            Long architectureId,
            List<com.ryuqq.domain.onboardingcontext.vo.ContextType> contextTypes) {
        return onboardingContextQueryPort.findForMcp(techStackId, architectureId, contextTypes);
    }
}

package com.ryuqq.adapter.out.persistence.onboardingcontext.adapter;

import com.ryuqq.adapter.out.persistence.onboardingcontext.entity.OnboardingContextJpaEntity;
import com.ryuqq.adapter.out.persistence.onboardingcontext.mapper.OnboardingContextEntityMapper;
import com.ryuqq.adapter.out.persistence.onboardingcontext.repository.OnboardingContextQueryDslRepository;
import com.ryuqq.application.onboardingcontext.port.out.OnboardingContextQueryPort;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.onboardingcontext.query.OnboardingContextSliceCriteria;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * OnboardingContextQueryAdapter - OnboardingContext 조회 어댑터
 *
 * <p>OnboardingContextQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QPRT-002: 표준 메서드를 구현합니다.
 *
 * <p><strong>주의사항:</strong>
 *
 * <ul>
 *   <li>QueryAdapter는 QueryDslRepository에만 의존합니다 (JpaRepository 금지)
 *   <li>JpaRepository는 Command 작업을 수행할 수 있어 QueryAdapter에서 사용하면 안 됩니다
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class OnboardingContextQueryAdapter implements OnboardingContextQueryPort {

    private final OnboardingContextQueryDslRepository queryDslRepository;
    private final OnboardingContextEntityMapper mapper;

    public OnboardingContextQueryAdapter(
            OnboardingContextQueryDslRepository queryDslRepository,
            OnboardingContextEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 OnboardingContext 조회
     *
     * @param id OnboardingContext ID (VO)
     * @return OnboardingContext (Optional)
     */
    @Override
    public Optional<OnboardingContext> findById(OnboardingContextId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id OnboardingContext ID (VO)
     * @return 존재 여부
     */
    @Override
    public boolean existsById(OnboardingContextId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 커서 기반 슬라이스 조건으로 OnboardingContext 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return OnboardingContext 목록
     */
    @Override
    public List<OnboardingContext> findBySliceCriteria(OnboardingContextSliceCriteria criteria) {
        List<OnboardingContextJpaEntity> entities =
                queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * TechStack에 속한 OnboardingContext 존재 여부 확인
     *
     * <p>TechStack 삭제 시 자식 확인을 위해 사용합니다.
     *
     * @param techStackId TechStack ID (VO)
     * @return 자식 존재 여부
     */
    @Override
    public boolean existsByTechStackId(TechStackId techStackId) {
        return queryDslRepository.existsByTechStackId(techStackId.value());
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
    @Override
    public List<OnboardingContext> findForMcp(
            TechStackId techStackId, Long architectureId, List<ContextType> contextTypes) {
        List<String> contextTypeStrings =
                contextTypes != null ? contextTypes.stream().map(ContextType::name).toList() : null;

        List<OnboardingContextJpaEntity> entities =
                queryDslRepository.findForMcp(
                        techStackId.value(), architectureId, contextTypeStrings);
        return entities.stream().map(mapper::toDomain).toList();
    }
}

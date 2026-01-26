package com.ryuqq.application.convention.service;

import com.ryuqq.application.convention.assembler.ConventionAssembler;
import com.ryuqq.application.convention.dto.query.ConventionSearchParams;
import com.ryuqq.application.convention.dto.response.ConventionSliceResult;
import com.ryuqq.application.convention.factory.query.ConventionQueryFactory;
import com.ryuqq.application.convention.manager.ConventionReadManager;
import com.ryuqq.application.convention.port.in.SearchConventionsByCursorUseCase;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchConventionsByCursorService - Convention 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchConventionsByCursorUseCase를 구현합니다.
 *
 * <p>Convention 목록을 커서 기반으로 복합 조건(모듈 ID 필터)으로 조회합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 → Assembler 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
 *
 * @author ryu-qqq
 */
@Service
public class SearchConventionsByCursorService implements SearchConventionsByCursorUseCase {

    private final ConventionReadManager conventionReadManager;
    private final ConventionQueryFactory conventionQueryFactory;
    private final ConventionAssembler conventionAssembler;

    /**
     * 생성자 주입
     *
     * @param conventionReadManager 컨벤션 조회 매니저
     * @param conventionQueryFactory 컨벤션 쿼리 팩토리
     * @param conventionAssembler 컨벤션 어셈블러
     */
    public SearchConventionsByCursorService(
            ConventionReadManager conventionReadManager,
            ConventionQueryFactory conventionQueryFactory,
            ConventionAssembler conventionAssembler) {
        this.conventionReadManager = conventionReadManager;
        this.conventionQueryFactory = conventionQueryFactory;
        this.conventionAssembler = conventionAssembler;
    }

    @Override
    public ConventionSliceResult execute(ConventionSearchParams searchParams) {
        // Factory에서 Criteria 생성 (moduleIds 필터 포함)
        ConventionSliceCriteria criteria = conventionQueryFactory.createSliceCriteria(searchParams);
        List<Convention> conventions = conventionReadManager.findBySliceCriteria(criteria);
        return conventionAssembler.toSliceResult(conventions, searchParams.size());
    }
}

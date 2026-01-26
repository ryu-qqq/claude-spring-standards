package com.ryuqq.application.techstack.service;

import com.ryuqq.application.techstack.assembler.TechStackAssembler;
import com.ryuqq.application.techstack.dto.query.TechStackSearchParams;
import com.ryuqq.application.techstack.dto.response.TechStackSliceResult;
import com.ryuqq.application.techstack.factory.query.TechStackQueryFactory;
import com.ryuqq.application.techstack.manager.TechStackReadManager;
import com.ryuqq.application.techstack.port.in.SearchTechStacksByCursorUseCase;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchTechStacksByCursorService - TechStack 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchTechStacksByCursorUseCase를 구현합니다.
 *
 * <p>TechStack 목록을 커서 기반으로 복합 조건(상태, 플랫폼 타입)으로 조회합니다.
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
public class SearchTechStacksByCursorService implements SearchTechStacksByCursorUseCase {

    private final TechStackReadManager techStackReadManager;
    private final TechStackQueryFactory techStackQueryFactory;
    private final TechStackAssembler techStackAssembler;

    public SearchTechStacksByCursorService(
            TechStackReadManager techStackReadManager,
            TechStackQueryFactory techStackQueryFactory,
            TechStackAssembler techStackAssembler) {
        this.techStackReadManager = techStackReadManager;
        this.techStackQueryFactory = techStackQueryFactory;
        this.techStackAssembler = techStackAssembler;
    }

    @Override
    public TechStackSliceResult execute(TechStackSearchParams searchParams) {
        TechStackSliceCriteria criteria = techStackQueryFactory.createSliceCriteria(searchParams);

        List<TechStack> techStacks = techStackReadManager.findBySliceCriteria(criteria);
        return techStackAssembler.toSliceResult(techStacks, searchParams.size());
    }
}

package com.ryuqq.application.architecture.service;

import com.ryuqq.application.architecture.assembler.ArchitectureAssembler;
import com.ryuqq.application.architecture.dto.query.ArchitectureSearchParams;
import com.ryuqq.application.architecture.dto.response.ArchitectureSliceResult;
import com.ryuqq.application.architecture.factory.query.ArchitectureQueryFactory;
import com.ryuqq.application.architecture.manager.ArchitectureReadManager;
import com.ryuqq.application.architecture.port.in.SearchArchitecturesByCursorUseCase;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchArchitecturesByCursorService - Architecture 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchArchitecturesByCursorUseCase를 구현합니다.
 *
 * <p>TechStack ID 필터를 지원하여 Architecture 목록을 커서 기반으로 조회합니다.
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
public class SearchArchitecturesByCursorService implements SearchArchitecturesByCursorUseCase {

    private final ArchitectureReadManager architectureReadManager;
    private final ArchitectureQueryFactory architectureQueryFactory;
    private final ArchitectureAssembler architectureAssembler;

    public SearchArchitecturesByCursorService(
            ArchitectureReadManager architectureReadManager,
            ArchitectureQueryFactory architectureQueryFactory,
            ArchitectureAssembler architectureAssembler) {
        this.architectureReadManager = architectureReadManager;
        this.architectureQueryFactory = architectureQueryFactory;
        this.architectureAssembler = architectureAssembler;
    }

    @Override
    public ArchitectureSliceResult execute(ArchitectureSearchParams searchParams) {
        ArchitectureSliceCriteria criteria =
                architectureQueryFactory.createSliceCriteria(searchParams);

        List<Architecture> architectures = architectureReadManager.findBySliceCriteria(criteria);
        return architectureAssembler.toSliceResult(architectures, searchParams.size());
    }
}

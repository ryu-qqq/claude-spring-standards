package com.ryuqq.application.packagestructure.service;

import com.ryuqq.application.packagestructure.assembler.PackageStructureAssembler;
import com.ryuqq.application.packagestructure.dto.query.PackageStructureSearchParams;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureSliceResult;
import com.ryuqq.application.packagestructure.factory.query.PackageStructureQueryFactory;
import com.ryuqq.application.packagestructure.manager.PackageStructureReadManager;
import com.ryuqq.application.packagestructure.port.in.SearchPackageStructuresByCursorUseCase;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchPackageStructuresByCursorService - PackageStructure 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchPackageStructuresByCursorUseCase를 구현합니다.
 *
 * <p>PackageStructure 목록을 커서 기반으로 복합 조건(모듈 ID)으로 조회합니다.
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
public class SearchPackageStructuresByCursorService
        implements SearchPackageStructuresByCursorUseCase {

    private final PackageStructureQueryFactory packageStructureQueryFactory;
    private final PackageStructureReadManager packageStructureReadManager;
    private final PackageStructureAssembler packageStructureAssembler;

    public SearchPackageStructuresByCursorService(
            PackageStructureQueryFactory packageStructureQueryFactory,
            PackageStructureReadManager packageStructureReadManager,
            PackageStructureAssembler packageStructureAssembler) {
        this.packageStructureQueryFactory = packageStructureQueryFactory;
        this.packageStructureReadManager = packageStructureReadManager;
        this.packageStructureAssembler = packageStructureAssembler;
    }

    @Override
    public PackageStructureSliceResult execute(PackageStructureSearchParams searchParams) {
        // Factory에서 Criteria 생성 (필터 포함)
        PackageStructureSliceCriteria criteria =
                packageStructureQueryFactory.createSliceCriteria(searchParams);
        List<PackageStructure> packageStructures =
                packageStructureReadManager.findBySliceCriteria(criteria);
        return packageStructureAssembler.toSliceResult(packageStructures, searchParams.size());
    }
}

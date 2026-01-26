package com.ryuqq.application.module.service;

import com.ryuqq.application.module.assembler.ModuleAssembler;
import com.ryuqq.application.module.dto.query.ModuleSearchParams;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;
import com.ryuqq.application.module.factory.query.ModuleQueryFactory;
import com.ryuqq.application.module.manager.ModuleReadManager;
import com.ryuqq.application.module.port.in.SearchModulesByCursorUseCase;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchModulesByCursorService - Module 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchModulesByCursorUseCase를 구현합니다.
 *
 * <p>Module 목록을 커서 기반으로 복합 조건(layerId)으로 조회합니다.
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
 * @since 1.0.0
 */
@Service
public class SearchModulesByCursorService implements SearchModulesByCursorUseCase {

    private final ModuleReadManager moduleReadManager;
    private final ModuleQueryFactory moduleQueryFactory;
    private final ModuleAssembler moduleAssembler;

    public SearchModulesByCursorService(
            ModuleReadManager moduleReadManager,
            ModuleQueryFactory moduleQueryFactory,
            ModuleAssembler moduleAssembler) {
        this.moduleReadManager = moduleReadManager;
        this.moduleQueryFactory = moduleQueryFactory;
        this.moduleAssembler = moduleAssembler;
    }

    @Override
    public ModuleSliceResult execute(ModuleSearchParams searchParams) {
        ModuleSliceCriteria criteria = moduleQueryFactory.createSliceCriteria(searchParams);

        List<Module> modules = moduleReadManager.findBySliceCriteria(criteria);
        return moduleAssembler.toSliceResult(modules, searchParams.size());
    }
}

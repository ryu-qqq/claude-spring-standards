package com.ryuqq.application.module.service;

import com.ryuqq.application.module.assembler.ModuleAssembler;
import com.ryuqq.application.module.dto.response.ModuleTreeResult;
import com.ryuqq.application.module.manager.ModuleReadManager;
import com.ryuqq.application.module.port.in.GetModuleTreeUseCase;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetModuleTreeService - Module 트리 조회 서비스
 *
 * <p>GetModuleTreeUseCase를 구현합니다.
 *
 * <p>레이어 내 전체 모듈을 트리 구조로 조회합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 → Assembler 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class GetModuleTreeService implements GetModuleTreeUseCase {

    private final ModuleAssembler moduleAssembler;
    private final ModuleReadManager moduleReadManager;

    public GetModuleTreeService(
            ModuleAssembler moduleAssembler, ModuleReadManager moduleReadManager) {
        this.moduleAssembler = moduleAssembler;
        this.moduleReadManager = moduleReadManager;
    }

    @Override
    public List<ModuleTreeResult> execute(Long layerId) {
        List<Module> modules = moduleReadManager.findAllByLayerId(LayerId.of(layerId));
        return moduleAssembler.toTreeResults(modules);
    }
}

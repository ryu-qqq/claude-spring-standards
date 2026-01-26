package com.ryuqq.application.module.port.in;

import com.ryuqq.application.module.dto.response.ModuleTreeResult;
import java.util.List;

/**
 * GetModuleTreeUseCase - Module 트리 구조 조회 UseCase
 *
 * <p>특정 레이어의 모듈들을 트리 구조로 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface GetModuleTreeUseCase {

    /**
     * 레이어별 Module 트리 조회
     *
     * @param layerId 레이어 ID
     * @return 해당 레이어의 Module 트리 구조 (루트 노드들, children 포함)
     */
    List<ModuleTreeResult> execute(Long layerId);
}

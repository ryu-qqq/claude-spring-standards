package com.ryuqq.application.module.port.out;

import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
import com.ryuqq.domain.module.vo.ModuleName;
import java.util.List;
import java.util.Optional;

/**
 * ModuleQueryPort - 모듈 조회 포트
 *
 * <p>모듈 조회를 위한 아웃바운드 포트입니다.
 *
 * @author ryu-qqq
 */
public interface ModuleQueryPort {

    /**
     * ID로 모듈 조회
     *
     * @param moduleId 모듈 ID
     * @return 모듈 (없으면 Optional.empty())
     */
    Optional<Module> findById(ModuleId moduleId);

    /**
     * 슬라이스 조건으로 모듈 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 모듈 목록
     */
    List<Module> findBySliceCriteria(ModuleSliceCriteria criteria);

    /**
     * 레이어 ID로 전체 모듈 목록 조회 (트리 구성용)
     *
     * @param layerId 레이어 ID
     * @return 해당 레이어의 모든 모듈
     */
    List<Module> findAllByLayerId(LayerId layerId);

    /**
     * 레이어 내 모듈 이름 존재 여부 확인
     *
     * @param layerId 레이어 ID
     * @param name 모듈 이름
     * @return 존재하면 true
     */
    boolean existsByLayerIdAndName(LayerId layerId, ModuleName name);

    /**
     * 레이어 내 모듈 이름 존재 여부 확인 (특정 모듈 제외)
     *
     * @param layerId 레이어 ID
     * @param name 모듈 이름
     * @param excludeModuleId 제외할 모듈 ID
     * @return 존재하면 true
     */
    boolean existsByLayerIdAndNameExcluding(
            LayerId layerId, ModuleName name, ModuleId excludeModuleId);

    /**
     * 부모 모듈 ID로 자식 모듈 존재 여부 확인
     *
     * @param parentModuleId 부모 모듈 ID
     * @return 자식 모듈이 존재하면 true
     */
    boolean existsByParentModuleId(ModuleId parentModuleId);

    /**
     * 키워드 검색
     *
     * <p>name, description 필드에서 키워드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param layerId 레이어 ID (nullable)
     * @return 검색된 모듈 목록
     */
    List<Module> searchByKeyword(String keyword, Long layerId);
}

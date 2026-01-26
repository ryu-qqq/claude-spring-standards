package com.ryuqq.application.module.manager;

import com.ryuqq.application.module.port.out.ModuleQueryPort;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.exception.ModuleNotFoundException;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
import com.ryuqq.domain.module.vo.ModuleName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ModuleReadManager - 모듈 조회 관리자
 *
 * <p>모듈 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class ModuleReadManager {

    private final ModuleQueryPort moduleQueryPort;

    public ModuleReadManager(ModuleQueryPort moduleQueryPort) {
        this.moduleQueryPort = moduleQueryPort;
    }

    /**
     * ID로 Module 조회
     *
     * @param moduleId Module ID (VO)
     * @return Module (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Module> findById(ModuleId moduleId) {
        return moduleQueryPort.findById(moduleId);
    }

    /**
     * ID로 Module 조회 (없으면 예외)
     *
     * @param moduleId Module ID (VO)
     * @return Module 도메인 객체
     * @throws ModuleNotFoundException Module이 없는 경우
     */
    @Transactional(readOnly = true)
    public Module getById(ModuleId moduleId) {
        return moduleQueryPort
                .findById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException(moduleId.value()));
    }

    /**
     * 슬라이스 조건으로 모듈 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 모듈 목록
     */
    @Transactional(readOnly = true)
    public List<Module> findBySliceCriteria(ModuleSliceCriteria criteria) {
        return moduleQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 레이어 ID로 전체 모듈 목록 조회
     *
     * @param layerId 레이어 ID
     * @return 모듈 목록
     */
    @Transactional(readOnly = true)
    public List<Module> findAllByLayerId(LayerId layerId) {
        return moduleQueryPort.findAllByLayerId(layerId);
    }

    /**
     * 레이어 내 모듈 이름 존재 여부 확인
     *
     * @param layerId 레이어 ID
     * @param name 모듈 이름
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByLayerIdAndName(LayerId layerId, ModuleName name) {
        return moduleQueryPort.existsByLayerIdAndName(layerId, name);
    }

    /**
     * 레이어 내 모듈 이름 존재 여부 확인 (특정 모듈 제외)
     *
     * @param layerId 레이어 ID
     * @param name 모듈 이름
     * @param excludeModuleId 제외할 모듈 ID
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByLayerIdAndNameExcluding(
            LayerId layerId, ModuleName name, ModuleId excludeModuleId) {
        return moduleQueryPort.existsByLayerIdAndNameExcluding(layerId, name, excludeModuleId);
    }

    /**
     * 부모 모듈 ID로 자식 모듈 존재 여부 확인
     *
     * @param parentModuleId 부모 모듈 ID
     * @return 자식 모듈이 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean hasChildren(ModuleId parentModuleId) {
        return moduleQueryPort.existsByParentModuleId(parentModuleId);
    }

    /**
     * 키워드로 모듈 검색
     *
     * <p>name, description 필드에서 키워드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param layerId 레이어 ID (nullable)
     * @return 검색된 모듈 목록
     */
    @Transactional(readOnly = true)
    public List<Module> searchByKeyword(String keyword, Long layerId) {
        return moduleQueryPort.searchByKeyword(keyword, layerId);
    }
}

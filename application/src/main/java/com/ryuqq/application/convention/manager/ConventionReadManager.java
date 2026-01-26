package com.ryuqq.application.convention.manager;

import com.ryuqq.application.convention.port.out.ConventionQueryPort;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ConventionReadManager - Convention 조회 관리자
 *
 * <p>QueryPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional(readOnly=true)은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ConventionReadManager {

    private final ConventionQueryPort conventionQueryPort;

    public ConventionReadManager(ConventionQueryPort conventionQueryPort) {
        this.conventionQueryPort = conventionQueryPort;
    }

    /**
     * ID로 Convention 조회
     *
     * @param id Convention ID (VO)
     * @return Convention (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Convention> findById(ConventionId id) {
        return conventionQueryPort.findById(id);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id Convention ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(ConventionId id) {
        return conventionQueryPort.existsById(id);
    }

    /**
     * 전체 활성화된 컨벤션 조회
     *
     * @return 활성화된 컨벤션 목록
     */
    @Transactional(readOnly = true)
    public List<Convention> findAllActive() {
        return conventionQueryPort.findAllActive();
    }

    /**
     * 모듈별 활성화된 컨벤션 조회
     *
     * @param moduleId 모듈 ID
     * @return 해당 모듈의 활성화된 컨벤션 (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Convention> findActiveByModuleId(ModuleId moduleId) {
        return conventionQueryPort.findActiveByModuleId(moduleId);
    }

    /**
     * 모듈+버전 중복 체크
     *
     * @param moduleId 모듈 ID
     * @param version 컨벤션 버전
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByModuleIdAndVersion(ModuleId moduleId, String version) {
        return conventionQueryPort.existsByModuleIdAndVersion(moduleId, version);
    }

    /**
     * ID를 제외한 모듈+버전 중복 체크 (수정 시 사용)
     *
     * @param moduleId 모듈 ID
     * @param version 컨벤션 버전
     * @param excludeId 제외할 ID
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByModuleIdAndVersionAndIdNot(
            ModuleId moduleId, String version, ConventionId excludeId) {
        return conventionQueryPort.existsByModuleIdAndVersionAndIdNot(moduleId, version, excludeId);
    }

    /**
     * 슬라이스 조건으로 Convention 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return Convention 목록
     */
    @Transactional(readOnly = true)
    public List<Convention> findBySliceCriteria(ConventionSliceCriteria criteria) {
        return conventionQueryPort.findBySliceCriteria(criteria);
    }
}

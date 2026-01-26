package com.ryuqq.application.architecture.manager;

import com.ryuqq.application.architecture.port.out.ArchitectureQueryPort;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ArchitectureReadManager - Architecture 조회 관리자
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
public class ArchitectureReadManager {

    private final ArchitectureQueryPort architectureQueryPort;

    public ArchitectureReadManager(ArchitectureQueryPort architectureQueryPort) {
        this.architectureQueryPort = architectureQueryPort;
    }

    /**
     * ID로 Architecture 조회
     *
     * @param id Architecture ID (VO)
     * @return Architecture (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Architecture> findById(ArchitectureId id) {
        return architectureQueryPort.findById(id);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id Architecture ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(ArchitectureId id) {
        return architectureQueryPort.existsById(id);
    }

    /**
     * 커서 기반 슬라이스 조건으로 Architecture 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return Architecture 목록
     */
    @Transactional(readOnly = true)
    public List<Architecture> findBySliceCriteria(ArchitectureSliceCriteria criteria) {
        return architectureQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 이름 중복 체크
     *
     * @param name 체크할 이름 (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByName(ArchitectureName name) {
        return architectureQueryPort.existsByName(name);
    }

    /**
     * ID를 제외한 이름 중복 체크 (수정 시 사용)
     *
     * @param name 체크할 이름 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(ArchitectureName name, ArchitectureId excludeId) {
        return architectureQueryPort.existsByNameAndIdNot(name, excludeId);
    }

    /**
     * TechStack에 속한 Architecture 존재 여부 확인
     *
     * @param techStackId TechStack ID (VO)
     * @return 자식 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByTechStackId(TechStackId techStackId) {
        return architectureQueryPort.existsByTechStackId(techStackId);
    }
}

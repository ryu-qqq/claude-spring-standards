package com.ryuqq.application.layer.manager;

import com.ryuqq.application.layer.port.out.LayerQueryPort;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.exception.LayerNotFoundException;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
import com.ryuqq.domain.layer.vo.LayerCode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * LayerReadManager - Layer 조회 관리자
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
public class LayerReadManager {

    private final LayerQueryPort layerQueryPort;

    public LayerReadManager(LayerQueryPort layerQueryPort) {
        this.layerQueryPort = layerQueryPort;
    }

    /**
     * ID로 Layer 조회
     *
     * @param id Layer ID (VO)
     * @return Layer (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Layer> findById(LayerId id) {
        return layerQueryPort.findById(id);
    }

    /**
     * ID로 Layer 조회 (없으면 예외)
     *
     * @param id Layer ID (VO)
     * @return Layer 도메인 객체
     * @throws LayerNotFoundException Layer가 없는 경우
     */
    @Transactional(readOnly = true)
    public Layer getById(LayerId id) {
        return layerQueryPort
                .findById(id)
                .orElseThrow(() -> new LayerNotFoundException(id.value()));
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id Layer ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(LayerId id) {
        return layerQueryPort.existsById(id);
    }

    /**
     * 슬라이스 조건으로 Layer 목록 조회
     *
     * @param criteria 슬라이스 조건
     * @return Layer 목록
     */
    @Transactional(readOnly = true)
    public List<Layer> findBySliceCriteria(LayerSliceCriteria criteria) {
        return layerQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 아키텍처 내 코드 중복 체크
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 체크할 코드 (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByArchitectureIdAndCode(ArchitectureId architectureId, LayerCode code) {
        return layerQueryPort.existsByArchitectureIdAndCode(architectureId, code);
    }

    /**
     * ID를 제외한 아키텍처 내 코드 중복 체크 (수정 시 사용)
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 체크할 코드 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByArchitectureIdAndCodeAndIdNot(
            ArchitectureId architectureId, LayerCode code, LayerId excludeId) {
        return layerQueryPort.existsByArchitectureIdAndCodeAndIdNot(
                architectureId, code, excludeId);
    }

    /**
     * Architecture에 속한 Layer 존재 여부 확인
     *
     * @param architectureId Architecture ID (VO)
     * @return 자식 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByArchitectureId(ArchitectureId architectureId) {
        return layerQueryPort.existsByArchitectureId(architectureId);
    }
}

package com.ryuqq.application.techstack.manager;

import com.ryuqq.application.techstack.port.out.TechStackQueryPort;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
import com.ryuqq.domain.techstack.vo.TechStackName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TechStackReadManager - TechStack 조회 관리자
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
public class TechStackReadManager {

    private final TechStackQueryPort techStackQueryPort;

    public TechStackReadManager(TechStackQueryPort techStackQueryPort) {
        this.techStackQueryPort = techStackQueryPort;
    }

    /**
     * ID로 TechStack 조회
     *
     * @param id TechStack ID (VO)
     * @return TechStack (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<TechStack> findById(TechStackId id) {
        return techStackQueryPort.findById(id);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id TechStack ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(TechStackId id) {
        return techStackQueryPort.existsById(id);
    }

    /**
     * 슬라이스 조건으로 TechStack 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조건
     * @return TechStack 목록
     */
    @Transactional(readOnly = true)
    public List<TechStack> findBySliceCriteria(TechStackSliceCriteria criteria) {
        return techStackQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 이름 중복 체크
     *
     * @param name 체크할 이름 (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByName(TechStackName name) {
        return techStackQueryPort.existsByName(name);
    }

    /**
     * ID를 제외한 이름 중복 체크 (수정 시 사용)
     *
     * @param name 체크할 이름 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(TechStackName name, TechStackId excludeId) {
        return techStackQueryPort.existsByNameAndIdNot(name, excludeId);
    }
}

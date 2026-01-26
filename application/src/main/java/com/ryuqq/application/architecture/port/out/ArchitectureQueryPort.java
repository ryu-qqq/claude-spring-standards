package com.ryuqq.application.architecture.port.out;

import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;

/**
 * ArchitectureQueryPort - Architecture 조회 Port
 *
 * <p>영속성 계층으로의 Architecture 조회 아웃바운드 포트입니다.
 *
 * <p>QPRT-002: 표준 4개 메서드를 제공합니다.
 *
 * <p>QPRT-003: Unique 필드(name) 조회 메서드를 추가 제공합니다.
 *
 * <p>QPRT-004: 원시타입 대신 VO를 파라미터로 사용합니다.
 *
 * @author ryu-qqq
 */
public interface ArchitectureQueryPort {

    /**
     * ID로 Architecture 조회
     *
     * @param id Architecture ID (VO)
     * @return Architecture (Optional)
     */
    Optional<Architecture> findById(ArchitectureId id);

    /**
     * ID로 존재 여부 확인
     *
     * @param id Architecture ID (VO)
     * @return 존재 여부
     */
    boolean existsById(ArchitectureId id);

    /**
     * 커서 기반 슬라이스 조건으로 Architecture 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return Architecture 목록
     */
    List<Architecture> findBySliceCriteria(ArchitectureSliceCriteria criteria);

    /**
     * 이름 중복 체크 (QPRT-003: Unique 필드)
     *
     * @param name 체크할 이름 (VO)
     * @return 중복 여부
     */
    boolean existsByName(ArchitectureName name);

    /**
     * ID를 제외한 이름 중복 체크 (수정 시 사용)
     *
     * @param name 체크할 이름 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    boolean existsByNameAndIdNot(ArchitectureName name, ArchitectureId excludeId);

    /**
     * TechStack에 속한 Architecture 존재 여부 확인
     *
     * <p>TechStack 삭제 시 자식 확인을 위해 사용합니다.
     *
     * @param techStackId TechStack ID (VO)
     * @return 자식 존재 여부
     */
    boolean existsByTechStackId(TechStackId techStackId);
}

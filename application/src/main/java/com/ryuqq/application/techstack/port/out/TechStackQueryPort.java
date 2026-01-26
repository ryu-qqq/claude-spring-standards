package com.ryuqq.application.techstack.port.out;

import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
import com.ryuqq.domain.techstack.vo.TechStackName;
import java.util.List;
import java.util.Optional;

/**
 * TechStackQueryPort - TechStack 조회 Port
 *
 * <p>영속성 계층으로의 TechStack 조회 아웃바운드 포트입니다.
 *
 * <p>QPRT-002: 표준 4개 메서드를 제공합니다.
 *
 * <p>QPRT-003: Unique 필드(name) 조회 메서드를 추가 제공합니다.
 *
 * <p>QPRT-004: 원시타입 대신 VO를 파라미터로 사용합니다.
 *
 * @author ryu-qqq
 */
public interface TechStackQueryPort {

    /**
     * ID로 TechStack 조회
     *
     * @param id TechStack ID (VO)
     * @return TechStack (Optional)
     */
    Optional<TechStack> findById(TechStackId id);

    /**
     * ID로 존재 여부 확인
     *
     * @param id TechStack ID (VO)
     * @return 존재 여부
     */
    boolean existsById(TechStackId id);

    /**
     * 슬라이스 조건으로 TechStack 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조건
     * @return TechStack 목록
     */
    List<TechStack> findBySliceCriteria(TechStackSliceCriteria criteria);

    /**
     * 이름 중복 체크 (QPRT-003: Unique 필드)
     *
     * @param name 체크할 이름 (VO)
     * @return 중복 여부
     */
    boolean existsByName(TechStackName name);

    /**
     * ID를 제외한 이름 중복 체크 (수정 시 사용)
     *
     * @param name 체크할 이름 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    boolean existsByNameAndIdNot(TechStackName name, TechStackId excludeId);
}

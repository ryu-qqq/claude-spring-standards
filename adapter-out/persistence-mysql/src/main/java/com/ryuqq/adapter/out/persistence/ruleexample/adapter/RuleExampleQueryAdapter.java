package com.ryuqq.adapter.out.persistence.ruleexample.adapter;

import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import com.ryuqq.adapter.out.persistence.ruleexample.mapper.RuleExampleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.ruleexample.repository.RuleExampleQueryDslRepository;
import com.ryuqq.application.ruleexample.port.out.RuleExampleQueryPort;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * RuleExampleQueryAdapter - 규칙 예시 조회 어댑터
 *
 * <p>RuleExampleQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QADP-001: QueryDslRepository 위임만
 *
 * <p>QADP-002: QueryAdapter에서 @Transactional 금지
 *
 * <p>QADP-006: Domain 반환 (DTO 반환 금지)
 *
 * <p>QADP-007: Entity -> Domain 변환 (Mapper 사용)
 *
 * <p>QADP-008: QueryAdapter에 비즈니스 로직 금지
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleQueryAdapter implements RuleExampleQueryPort {

    private final RuleExampleQueryDslRepository queryDslRepository;
    private final RuleExampleJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public RuleExampleQueryAdapter(
            RuleExampleQueryDslRepository queryDslRepository, RuleExampleJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 규칙 예시 조회
     *
     * @param id 규칙 예시 ID
     * @return 규칙 예시 Optional
     */
    @Override
    public Optional<RuleExample> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * RuleExampleId로 규칙 예시 조회
     *
     * @param ruleExampleId 규칙 예시 ID
     * @return 규칙 예시 Optional
     */
    @Override
    public Optional<RuleExample> findById(RuleExampleId ruleExampleId) {
        return queryDslRepository.findById(ruleExampleId.value()).map(mapper::toDomain);
    }

    /**
     * 코딩 규칙 ID로 규칙 예시 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 규칙 예시 목록
     */
    @Override
    public List<RuleExample> findByRuleId(Long ruleId) {
        List<RuleExampleJpaEntity> entities = queryDslRepository.findByRuleId(ruleId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * CodingRuleId 값 객체로 규칙 예시 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 규칙 예시 목록
     */
    @Override
    public List<RuleExample> findByRuleId(CodingRuleId ruleId) {
        return findByRuleId(ruleId.value());
    }

    /**
     * 슬라이스 조건으로 규칙 예시 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 규칙 예시 목록
     */
    @Override
    public List<RuleExample> findBySliceCriteria(RuleExampleSliceCriteria criteria) {
        List<RuleExampleJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 전체 규칙 예시 목록 조회
     *
     * @return 규칙 예시 목록
     */
    @Override
    public List<RuleExample> findAll() {
        List<RuleExampleJpaEntity> entities = queryDslRepository.searchAll();
        return entities.stream().map(mapper::toDomain).toList();
    }
}

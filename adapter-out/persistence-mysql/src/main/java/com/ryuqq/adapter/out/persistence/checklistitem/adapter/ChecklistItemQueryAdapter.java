package com.ryuqq.adapter.out.persistence.checklistitem.adapter;

import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
import com.ryuqq.adapter.out.persistence.checklistitem.mapper.ChecklistItemJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.checklistitem.repository.ChecklistItemQueryDslRepository;
import com.ryuqq.application.checklistitem.port.out.ChecklistItemQueryPort;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemQueryAdapter - 체크리스트 항목 조회 어댑터
 *
 * <p>ChecklistItemQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class ChecklistItemQueryAdapter implements ChecklistItemQueryPort {

    private final ChecklistItemQueryDslRepository queryDslRepository;
    private final ChecklistItemJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ChecklistItemQueryAdapter(
            ChecklistItemQueryDslRepository queryDslRepository,
            ChecklistItemJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 체크리스트 항목 조회
     *
     * @param id 체크리스트 항목 ID
     * @return 체크리스트 항목 Optional
     */
    @Override
    public Optional<ChecklistItem> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * ChecklistItemId로 체크리스트 항목 조회
     *
     * @param checklistItemId 체크리스트 항목 ID
     * @return 체크리스트 항목 Optional
     */
    @Override
    public Optional<ChecklistItem> findById(ChecklistItemId checklistItemId) {
        return queryDslRepository.findById(checklistItemId.value()).map(mapper::toDomain);
    }

    /**
     * 코딩 규칙 ID로 체크리스트 항목 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 체크리스트 항목 목록
     */
    @Override
    public List<ChecklistItem> findByRuleId(Long ruleId) {
        List<ChecklistItemJpaEntity> entities = queryDslRepository.findByRuleId(ruleId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * CodingRuleId 값 객체로 체크리스트 항목 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 체크리스트 항목 목록
     */
    @Override
    public List<ChecklistItem> findByRuleId(CodingRuleId ruleId) {
        return findByRuleId(ruleId.value());
    }

    /**
     * 슬라이스 조건으로 체크리스트 항목 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 체크리스트 항목 목록
     */
    @Override
    public List<ChecklistItem> findBySliceCriteria(ChecklistItemSliceCriteria criteria) {
        List<ChecklistItemJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 전체 체크리스트 항목 목록 조회
     *
     * @return 체크리스트 항목 목록
     */
    @Override
    public List<ChecklistItem> findAll() {
        List<ChecklistItemJpaEntity> entities = queryDslRepository.searchAll();
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 코딩 규칙 ID와 순서로 중복 확인
     *
     * @param ruleId 코딩 규칙 ID
     * @param sequenceOrder 순서
     * @return 존재하면 true
     */
    @Override
    public boolean existsByRuleIdAndSequenceOrder(Long ruleId, int sequenceOrder) {
        return queryDslRepository.existsByRuleIdAndSequenceOrder(ruleId, sequenceOrder);
    }
}

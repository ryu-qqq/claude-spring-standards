package com.ryuqq.adapter.out.persistence.zerotolerance.adapter;

import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
import com.ryuqq.adapter.out.persistence.checklistitem.mapper.ChecklistItemJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.codingrule.mapper.CodingRuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import com.ryuqq.adapter.out.persistence.ruleexample.mapper.RuleExampleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.zerotolerance.mapper.ZeroToleranceRuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.zerotolerance.repository.ZeroToleranceRuleJpaRepository;
import com.ryuqq.adapter.out.persistence.zerotolerance.repository.ZeroToleranceRuleQueryDslRepository;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleDetailResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.application.zerotolerance.port.out.ZeroToleranceRuleQueryPort;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleQueryAdapter - Zero-Tolerance 규칙 조회 어댑터
 *
 * <p>ZeroToleranceRuleQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QADP-001: QueryDslRepository 위임만
 *
 * <p>QADP-002: QueryAdapter에서 @Transactional 금지
 *
 * <p>QADP-006: Domain 반환 (DTO 반환 금지) - 단, 이 포트는 Result DTO 반환
 *
 * <p>QADP-007: Entity → Domain 변환 (Mapper 사용)
 *
 * <p>QADP-008: QueryAdapter에 비즈니스 로직 금지
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ZeroToleranceRuleQueryAdapter implements ZeroToleranceRuleQueryPort {

    private final ZeroToleranceRuleJpaRepository jpaRepository;
    private final ZeroToleranceRuleQueryDslRepository queryDslRepository;
    private final ZeroToleranceRuleJpaEntityMapper zeroToleranceRuleMapper;
    private final CodingRuleJpaEntityMapper codingRuleMapper;
    private final RuleExampleJpaEntityMapper ruleExampleMapper;
    private final ChecklistItemJpaEntityMapper checklistItemMapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param jpaRepository JPA 레포지토리
     * @param queryDslRepository QueryDSL 레포지토리
     * @param zeroToleranceRuleMapper ZeroToleranceRule Entity-Domain 매퍼
     * @param codingRuleMapper CodingRule Entity-Domain 매퍼
     * @param ruleExampleMapper RuleExample Entity-Domain 매퍼
     * @param checklistItemMapper ChecklistItem Entity-Domain 매퍼
     */
    public ZeroToleranceRuleQueryAdapter(
            ZeroToleranceRuleJpaRepository jpaRepository,
            ZeroToleranceRuleQueryDslRepository queryDslRepository,
            ZeroToleranceRuleJpaEntityMapper zeroToleranceRuleMapper,
            CodingRuleJpaEntityMapper codingRuleMapper,
            RuleExampleJpaEntityMapper ruleExampleMapper,
            ChecklistItemJpaEntityMapper checklistItemMapper) {
        this.jpaRepository = jpaRepository;
        this.queryDslRepository = queryDslRepository;
        this.zeroToleranceRuleMapper = zeroToleranceRuleMapper;
        this.codingRuleMapper = codingRuleMapper;
        this.ruleExampleMapper = ruleExampleMapper;
        this.checklistItemMapper = checklistItemMapper;
    }

    /**
     * ID로 Zero-Tolerance 규칙 조회
     *
     * @param zeroToleranceRuleId Zero-Tolerance 규칙 ID
     * @return ZeroToleranceRule Optional
     */
    @Override
    public Optional<ZeroToleranceRule> findById(ZeroToleranceRuleId zeroToleranceRuleId) {
        return jpaRepository
                .findById(zeroToleranceRuleId.value())
                .map(zeroToleranceRuleMapper::toDomain);
    }

    /**
     * CodingRuleId로 Zero-Tolerance 규칙 존재 여부 확인
     *
     * @param ruleId 코딩 규칙 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByRuleId(Long ruleId) {
        return jpaRepository.existsByRuleId(ruleId);
    }

    /**
     * ID로 Zero-Tolerance 규칙 상세 조회
     *
     * <p>CodingRule과 관련된 RuleExample, ChecklistItem을 함께 조회합니다.
     *
     * @param ruleId 코딩 규칙 ID
     * @return Zero-Tolerance 규칙 상세 결과 Optional
     */
    @Override
    public Optional<ZeroToleranceRuleDetailResult> findDetailById(Long ruleId) {
        Optional<CodingRuleJpaEntity> codingRuleOpt =
                queryDslRepository.findZeroToleranceRuleById(ruleId);

        if (codingRuleOpt.isEmpty()) {
            return Optional.empty();
        }

        CodingRuleJpaEntity codingRuleEntity = codingRuleOpt.get();
        List<RuleExampleJpaEntity> exampleEntities =
                queryDslRepository.findRuleExamplesByRuleId(ruleId);
        List<ChecklistItemJpaEntity> checklistEntities =
                queryDslRepository.findChecklistItemsByRuleId(ruleId);

        ZeroToleranceRuleDetailResult result =
                assembleDetailResult(codingRuleEntity, exampleEntities, checklistEntities);

        return Optional.of(result);
    }

    /**
     * 슬라이스 조건으로 Zero-Tolerance 규칙 상세 목록 조회
     *
     * <p>커서 기반 페이징으로 조회하며, 각 규칙에 대해 관련 RuleExample, ChecklistItem을 함께 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return Zero-Tolerance 규칙 슬라이스 결과
     */
    @Override
    public ZeroToleranceRuleSliceResult findAllDetails(ZeroToleranceRuleSliceCriteria criteria) {
        List<CodingRuleJpaEntity> codingRuleEntities =
                queryDslRepository.findBySliceCriteria(criteria);

        if (codingRuleEntities.isEmpty()) {
            return ZeroToleranceRuleSliceResult.empty();
        }

        boolean hasNext = codingRuleEntities.size() > criteria.size();
        List<CodingRuleJpaEntity> resultEntities =
                hasNext ? codingRuleEntities.subList(0, criteria.size()) : codingRuleEntities;

        List<Long> ruleIds = resultEntities.stream().map(CodingRuleJpaEntity::getId).toList();

        List<RuleExampleJpaEntity> allExamples =
                queryDslRepository.findRuleExamplesByRuleIds(ruleIds);
        List<ChecklistItemJpaEntity> allChecklistItems =
                queryDslRepository.findChecklistItemsByRuleIds(ruleIds);

        Map<Long, List<RuleExampleJpaEntity>> examplesByRuleId =
                allExamples.stream()
                        .collect(Collectors.groupingBy(RuleExampleJpaEntity::getRuleId));
        Map<Long, List<ChecklistItemJpaEntity>> checklistItemsByRuleId =
                allChecklistItems.stream()
                        .collect(Collectors.groupingBy(ChecklistItemJpaEntity::getRuleId));

        List<ZeroToleranceRuleDetailResult> detailResults = new ArrayList<>();
        for (CodingRuleJpaEntity codingRuleEntity : resultEntities) {
            Long ruleId = codingRuleEntity.getId();
            List<RuleExampleJpaEntity> exampleEntities =
                    examplesByRuleId.getOrDefault(ruleId, List.of());
            List<ChecklistItemJpaEntity> checklistEntities =
                    checklistItemsByRuleId.getOrDefault(ruleId, List.of());

            ZeroToleranceRuleDetailResult detailResult =
                    assembleDetailResult(codingRuleEntity, exampleEntities, checklistEntities);
            detailResults.add(detailResult);
        }

        return ZeroToleranceRuleSliceResult.of(detailResults, hasNext);
    }

    /**
     * Entity들을 ZeroToleranceRuleDetailResult로 조립
     *
     * @param codingRuleEntity CodingRule 엔티티
     * @param exampleEntities RuleExample 엔티티 목록
     * @param checklistEntities ChecklistItem 엔티티 목록
     * @return ZeroToleranceRuleDetailResult
     */
    private ZeroToleranceRuleDetailResult assembleDetailResult(
            CodingRuleJpaEntity codingRuleEntity,
            List<RuleExampleJpaEntity> exampleEntities,
            List<ChecklistItemJpaEntity> checklistEntities) {

        CodingRule codingRule = codingRuleMapper.toDomain(codingRuleEntity);
        List<RuleExample> examples =
                exampleEntities.stream().map(ruleExampleMapper::toDomain).toList();
        List<ChecklistItem> checklistItems =
                checklistEntities.stream().map(checklistItemMapper::toDomain).toList();

        CodingRuleResult codingRuleResult = CodingRuleResult.from(codingRule);
        List<RuleExampleResult> exampleResults =
                examples.stream().map(RuleExampleResult::from).toList();
        List<ChecklistItemResult> checklistItemResults =
                checklistItems.stream().map(ChecklistItemResult::from).toList();

        return ZeroToleranceRuleDetailResult.of(
                codingRuleResult, exampleResults, checklistItemResults);
    }
}

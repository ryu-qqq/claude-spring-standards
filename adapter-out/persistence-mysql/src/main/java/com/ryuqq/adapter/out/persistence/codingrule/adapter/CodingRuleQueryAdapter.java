package com.ryuqq.adapter.out.persistence.codingrule.adapter;

import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.codingrule.mapper.CodingRuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.codingrule.repository.CodingRuleQueryDslRepository;
import com.ryuqq.application.codingrule.dto.response.CodingRuleIndexItem;
import com.ryuqq.application.codingrule.port.out.CodingRuleQueryPort;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.query.CodingRuleIndexCriteria;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
import com.ryuqq.domain.codingrule.vo.CodingRuleIndexData;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * CodingRuleQueryAdapter - 코딩 규칙 조회 어댑터
 *
 * <p>CodingRuleQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QADP-001: QueryDslRepository 위임만
 *
 * <p>QADP-002: QueryAdapter에서 @Transactional 금지
 *
 * <p>QADP-006: Domain 반환 (DTO 반환 금지)
 *
 * <p>QADP-007: Entity → Domain 변환 (Mapper 사용)
 *
 * <p>QADP-008: QueryAdapter에 비즈니스 로직 금지
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleQueryAdapter implements CodingRuleQueryPort {

    private final CodingRuleQueryDslRepository queryDslRepository;
    private final CodingRuleJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public CodingRuleQueryAdapter(
            CodingRuleQueryDslRepository queryDslRepository, CodingRuleJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * CodingRuleId로 규칙 조회
     *
     * @param codingRuleId 코딩 규칙 ID
     * @return 규칙 Optional
     */
    @Override
    public Optional<CodingRule> findById(CodingRuleId codingRuleId) {
        return queryDslRepository.findById(codingRuleId.value()).map(mapper::toDomain);
    }

    /**
     * ID로 코딩 규칙 존재 여부 확인
     *
     * @param id CodingRule ID (VO)
     * @return 존재하면 true
     */
    @Override
    public boolean existsById(CodingRuleId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 슬라이스 조건으로 코딩 규칙 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 코딩 규칙 목록
     */
    @Override
    public List<CodingRule> findBySliceCriteria(CodingRuleSliceCriteria criteria) {
        List<CodingRuleJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 컨벤션 내 규칙 코드 존재 여부 확인
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @return 존재하면 true
     */
    @Override
    public boolean existsByConventionIdAndCode(ConventionId conventionId, RuleCode code) {
        return queryDslRepository.existsByConventionIdAndCode(conventionId.value(), code.value());
    }

    /**
     * 컨벤션 내 규칙 코드 존재 여부 확인 (특정 규칙 제외)
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @param excludeCodingRuleId 제외할 코딩 규칙 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByConventionIdAndCodeExcluding(
            ConventionId conventionId, RuleCode code, CodingRuleId excludeCodingRuleId) {
        return queryDslRepository.existsByConventionIdAndCodeExcluding(
                conventionId.value(), code.value(), excludeCodingRuleId.value());
    }

    /**
     * 컨벤션 ID로 코딩 규칙 목록 조회
     *
     * @param conventionId 컨벤션 ID
     * @return 코딩 규칙 목록
     */
    @Override
    public List<CodingRule> findByConventionId(ConventionId conventionId) {
        List<CodingRuleJpaEntity> entities =
                queryDslRepository.findByConventionId(conventionId.value());
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 키워드로 코딩 규칙 검색 (컨벤션 ID 필터 옵션)
     *
     * @param keyword 검색 키워드
     * @param conventionId 컨벤션 ID (null이면 전체 검색)
     * @return 코딩 규칙 목록
     */
    @Override
    public List<CodingRule> searchByKeyword(String keyword, ConventionId conventionId) {
        Long conventionIdValue = conventionId != null ? conventionId.value() : null;
        List<CodingRuleJpaEntity> entities =
                queryDslRepository.searchByKeyword(keyword, conventionIdValue);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 규칙 인덱스 조회 (code, name, severity, category만)
     *
     * <p>QADP-001: QueryDslRepository 위임
     *
     * <p>QADP-007: Domain VO → Application DTO 변환
     *
     * @param criteria 인덱스 조회 조건
     * @return 규칙 인덱스 목록
     */
    @Override
    public List<CodingRuleIndexItem> findRuleIndex(CodingRuleIndexCriteria criteria) {
        List<CodingRuleIndexData> indexDataList = queryDslRepository.findRuleIndex(criteria);
        return indexDataList.stream()
                .map(
                        data ->
                                CodingRuleIndexItem.of(
                                        data.code(), data.name(), data.severity(), data.category()))
                .toList();
    }
}

package com.ryuqq.application.codingrule.port.out;

import com.ryuqq.application.codingrule.dto.response.CodingRuleIndexItem;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.query.CodingRuleIndexCriteria;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.List;
import java.util.Optional;

/**
 * CodingRuleQueryPort - 코딩 규칙 조회 아웃바운드 포트
 *
 * <p>영속성 계층에서 구현합니다.
 *
 * @author ryu-qqq
 */
public interface CodingRuleQueryPort {

    /**
     * CodingRuleId로 규칙 조회
     *
     * @param codingRuleId 코딩 규칙 ID
     * @return 규칙 Optional
     */
    Optional<CodingRule> findById(CodingRuleId codingRuleId);

    /**
     * ID로 코딩 규칙 존재 여부 확인
     *
     * @param id CodingRule ID (VO)
     * @return 존재하면 true
     */
    boolean existsById(CodingRuleId id);

    /**
     * 슬라이스 조건으로 코딩 규칙 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 코딩 규칙 목록
     */
    List<CodingRule> findBySliceCriteria(CodingRuleSliceCriteria criteria);

    /**
     * 컨벤션 내 규칙 코드 존재 여부 확인
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @return 존재하면 true
     */
    boolean existsByConventionIdAndCode(ConventionId conventionId, RuleCode code);

    /**
     * 컨벤션 내 규칙 코드 존재 여부 확인 (특정 규칙 제외)
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @param excludeCodingRuleId 제외할 코딩 규칙 ID
     * @return 존재하면 true
     */
    boolean existsByConventionIdAndCodeExcluding(
            ConventionId conventionId, RuleCode code, CodingRuleId excludeCodingRuleId);

    /**
     * 컨벤션 ID로 코딩 규칙 목록 조회
     *
     * @param conventionId 컨벤션 ID
     * @return 코딩 규칙 목록
     */
    List<CodingRule> findByConventionId(ConventionId conventionId);

    /**
     * 키워드로 코딩 규칙 검색 (컨벤션 ID 필터 옵션)
     *
     * @param keyword 검색 키워드
     * @param conventionId 컨벤션 ID (null이면 전체 검색)
     * @return 코딩 규칙 목록
     */
    List<CodingRule> searchByKeyword(String keyword, ConventionId conventionId);

    /**
     * 규칙 인덱스 조회 (code, name, severity, category만)
     *
     * <p>규칙 상세 대신 인덱스만 조회하여 캐싱 효율성을 높입니다.
     *
     * @param criteria 인덱스 조회 조건
     * @return 규칙 인덱스 목록
     */
    List<CodingRuleIndexItem> findRuleIndex(CodingRuleIndexCriteria criteria);
}

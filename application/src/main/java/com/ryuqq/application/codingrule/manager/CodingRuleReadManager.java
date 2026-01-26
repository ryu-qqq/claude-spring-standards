package com.ryuqq.application.codingrule.manager;

import com.ryuqq.application.codingrule.dto.response.CodingRuleIndexItem;
import com.ryuqq.application.codingrule.port.out.CodingRuleQueryPort;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.query.CodingRuleIndexCriteria;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * CodingRuleReadManager - 코딩 규칙 조회 관리자
 *
 * <p>코딩 규칙 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleReadManager {

    private final CodingRuleQueryPort codingRuleQueryPort;

    public CodingRuleReadManager(CodingRuleQueryPort codingRuleQueryPort) {
        this.codingRuleQueryPort = codingRuleQueryPort;
    }

    /**
     * ID로 코딩 규칙 조회 (Optional 반환)
     *
     * @param id CodingRule ID (VO)
     * @return Optional<CodingRule>
     */
    @Transactional(readOnly = true)
    public Optional<CodingRule> findById(CodingRuleId id) {
        return codingRuleQueryPort.findById(id);
    }

    /**
     * ID로 코딩 규칙 존재 여부 확인
     *
     * @param id CodingRule ID (VO)
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsById(CodingRuleId id) {
        return codingRuleQueryPort.existsById(id);
    }

    /**
     * 슬라이스 조건으로 코딩 규칙 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 코딩 규칙 목록
     */
    @Transactional(readOnly = true)
    public List<CodingRule> findBySliceCriteria(CodingRuleSliceCriteria criteria) {
        return codingRuleQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 컨벤션 내 규칙 코드 존재 여부 확인
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByConventionIdAndCode(ConventionId conventionId, RuleCode code) {
        return codingRuleQueryPort.existsByConventionIdAndCode(conventionId, code);
    }

    /**
     * 컨벤션 내 규칙 코드 존재 여부 확인 (특정 규칙 제외)
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @param excludeCodingRuleId 제외할 코딩 규칙 ID
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByConventionIdAndCodeExcluding(
            ConventionId conventionId, RuleCode code, CodingRuleId excludeCodingRuleId) {
        return codingRuleQueryPort.existsByConventionIdAndCodeExcluding(
                conventionId, code, excludeCodingRuleId);
    }

    /**
     * 컨벤션 ID로 코딩 규칙 목록 조회
     *
     * @param conventionId 컨벤션 ID
     * @return 코딩 규칙 목록
     */
    @Transactional(readOnly = true)
    public List<CodingRule> findByConventionId(ConventionId conventionId) {
        return codingRuleQueryPort.findByConventionId(conventionId);
    }

    /**
     * 키워드로 코딩 규칙 검색 (컨벤션 ID 필터 옵션)
     *
     * @param keyword 검색 키워드
     * @param conventionId 컨벤션 ID (null이면 전체 검색)
     * @return 코딩 규칙 목록
     */
    @Transactional(readOnly = true)
    public List<CodingRule> searchByKeyword(String keyword, ConventionId conventionId) {
        return codingRuleQueryPort.searchByKeyword(keyword, conventionId);
    }

    /**
     * 규칙 인덱스 조회 (code, name, severity, category만)
     *
     * <p>규칙 상세 대신 인덱스만 조회하여 캐싱 효율성을 높입니다.
     *
     * @param criteria 인덱스 조회 조건
     * @return 규칙 인덱스 목록
     */
    @Transactional(readOnly = true)
    public List<CodingRuleIndexItem> findRuleIndex(CodingRuleIndexCriteria criteria) {
        return codingRuleQueryPort.findRuleIndex(criteria);
    }
}

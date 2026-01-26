package com.ryuqq.adapter.out.persistence.ruleexample.condition;

import static com.ryuqq.adapter.out.persistence.ruleexample.entity.QRuleExampleJpaEntity.ruleExampleJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RuleExampleConditionBuilder - 규칙 예시 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class RuleExampleConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return ruleExampleJpaEntity.deletedAt.isNull();
    }

    /**
     * ID 일치 조건
     *
     * @param id 규칙 예시 ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? ruleExampleJpaEntity.id.eq(id) : null;
    }

    /**
     * 코딩 규칙 ID 일치 조건
     *
     * @param ruleId 코딩 규칙 ID
     * @return ruleId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression ruleIdEq(Long ruleId) {
        return ruleId != null ? ruleExampleJpaEntity.ruleId.eq(ruleId) : null;
    }

    /**
     * 코딩 규칙 ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 ruleIds가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ruleIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression ruleIdsIn(RuleExampleSliceCriteria criteria) {
        if (!criteria.hasRuleIds()) {
            return null;
        }
        List<Long> ruleIdValues = criteria.ruleIds().stream().map(id -> id.value()).toList();
        return ruleExampleJpaEntity.ruleId.in(ruleIdValues);
    }

    /**
     * 예시 타입 일치 조건
     *
     * @param exampleType 예시 타입 문자열
     * @return exampleType 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression exampleTypeEq(String exampleType) {
        return exampleType != null ? ruleExampleJpaEntity.exampleType.eq(exampleType) : null;
    }

    /**
     * 예시 타입 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 exampleTypes가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return exampleTypes IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression exampleTypesIn(RuleExampleSliceCriteria criteria) {
        if (!criteria.hasExampleTypes()) {
            return null;
        }
        List<String> exampleTypeNames =
                criteria.exampleTypes().stream().map(ExampleType::name).toList();
        return ruleExampleJpaEntity.exampleType.in(exampleTypeNames);
    }

    /**
     * 언어 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 languages가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return languages IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression languagesIn(RuleExampleSliceCriteria criteria) {
        if (!criteria.hasLanguages()) {
            return null;
        }
        List<String> languageNames =
                criteria.languages().stream().map(ExampleLanguage::name).toList();
        return ruleExampleJpaEntity.language.in(languageNames);
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(RuleExampleSliceCriteria criteria) {
        return criteria.hasCursor()
                ? ruleExampleJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * 커서 기반 페이징 조건 (레거시 - 호환성 유지)
     *
     * @param cursor 커서 (마지막 ID)
     * @return ID < cursor 조건 (nullable이면 null 반환)
     */
    public BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? ruleExampleJpaEntity.id.lt(cursor) : null;
    }
}

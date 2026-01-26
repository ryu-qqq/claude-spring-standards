package com.ryuqq.adapter.out.persistence.checklistitem.condition;

import static com.ryuqq.adapter.out.persistence.checklistitem.entity.QChecklistItemJpaEntity.checklistItemJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemConditionBuilder - 체크리스트 항목 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ChecklistItemConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return checklistItemJpaEntity.deletedAt.isNull();
    }

    /**
     * ID 일치 조건
     *
     * @param id 체크리스트 항목 ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? checklistItemJpaEntity.id.eq(id) : null;
    }

    /**
     * 코딩 규칙 ID 일치 조건
     *
     * @param ruleId 코딩 규칙 ID
     * @return ruleId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression ruleIdEq(Long ruleId) {
        return ruleId != null ? checklistItemJpaEntity.ruleId.eq(ruleId) : null;
    }

    /**
     * 체크 타입 일치 조건
     *
     * @param checkType 체크 타입 문자열
     * @return checkType 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression checkTypeEq(String checkType) {
        return checkType != null ? checklistItemJpaEntity.checkType.eq(checkType) : null;
    }

    /**
     * 자동화 도구 일치 조건
     *
     * @param automationTool 자동화 도구 문자열
     * @return automationTool 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression automationToolEq(String automationTool) {
        return automationTool != null
                ? checklistItemJpaEntity.automationTool.eq(automationTool)
                : null;
    }

    /**
     * 필수 여부 일치 조건
     *
     * @param isCritical 필수 여부
     * @return isCritical 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression isCriticalEq(Boolean isCritical) {
        return isCritical != null ? checklistItemJpaEntity.isCritical.eq(isCritical) : null;
    }

    /**
     * 코딩 규칙 ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 ruleIds가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ruleIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression ruleIdsIn(ChecklistItemSliceCriteria criteria) {
        if (!criteria.hasRuleIds()) {
            return null;
        }
        List<Long> ruleIdValues = criteria.ruleIds().stream().map(id -> id.value()).toList();
        return checklistItemJpaEntity.ruleId.in(ruleIdValues);
    }

    /**
     * 체크 타입 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 checkTypes가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return checkTypes IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression checkTypesIn(ChecklistItemSliceCriteria criteria) {
        if (!criteria.hasCheckTypes()) {
            return null;
        }
        List<String> checkTypeNames = criteria.checkTypes().stream().map(CheckType::name).toList();
        return checklistItemJpaEntity.checkType.in(checkTypeNames);
    }

    /**
     * 자동화 도구 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 automationTools가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return automationTools IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression automationToolsIn(ChecklistItemSliceCriteria criteria) {
        if (!criteria.hasAutomationTools()) {
            return null;
        }
        List<String> automationToolNames =
                criteria.automationTools().stream().map(AutomationTool::name).toList();
        return checklistItemJpaEntity.automationTool.in(automationToolNames);
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(ChecklistItemSliceCriteria criteria) {
        return criteria.hasCursor()
                ? checklistItemJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * 커서 기반 페이징 조건 (레거시 - 호환성 유지)
     *
     * @param cursor 커서 (마지막 ID)
     * @return ID < cursor 조건 (nullable이면 null 반환)
     */
    public BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? checklistItemJpaEntity.id.lt(cursor) : null;
    }

    /**
     * 순서 일치 조건
     *
     * @param sequenceOrder 순서
     * @return sequenceOrder 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression sequenceOrderEq(Integer sequenceOrder) {
        return sequenceOrder != null
                ? checklistItemJpaEntity.sequenceOrder.eq(sequenceOrder)
                : null;
    }
}

package com.ryuqq.domain.checklistitem.fixture;

import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.vo.AutomationRuleId;
import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckDescription;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ChecklistItem VO 테스트 Fixture
 *
 * <p>ChecklistItem의 Value Object들을 위한 테스트 데이터 생성 유틸리티입니다.
 *
 * @author ryu-qqq
 */
public final class ChecklistItemVoFixtures {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1L);

    private ChecklistItemVoFixtures() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ==================== ID Fixtures ====================

    /**
     * 다음 ChecklistItemId 생성 (시퀀스 증가)
     *
     * @return 새로운 ChecklistItemId
     */
    public static ChecklistItemId nextChecklistItemId() {
        return ChecklistItemId.of(ID_SEQUENCE.getAndIncrement());
    }

    /**
     * 고정 CodingRule ID
     *
     * @return CodingRuleId
     */
    public static CodingRuleId fixedCodingRuleId() {
        return CodingRuleId.of(100L);
    }

    // ==================== CheckDescription Fixtures ====================

    /**
     * 기본 체크 설명
     *
     * @return CheckDescription
     */
    public static CheckDescription defaultCheckDescription() {
        return CheckDescription.of("기본 체크 항목 설명");
    }

    /**
     * 지정된 값의 체크 설명
     *
     * @param description 설명 문자열
     * @return CheckDescription
     */
    public static CheckDescription checkDescriptionOf(String description) {
        return CheckDescription.of(description);
    }

    // ==================== CheckType Fixtures ====================

    /**
     * 기본 체크 타입 (AUTOMATED)
     *
     * @return CheckType
     */
    public static CheckType defaultCheckType() {
        return CheckType.AUTOMATED;
    }

    // ==================== AutomationTool Fixtures ====================

    /**
     * 기본 자동화 도구 (ARCHUNIT)
     *
     * @return AutomationTool
     */
    public static AutomationTool defaultAutomationTool() {
        return AutomationTool.ARCHUNIT;
    }

    // ==================== AutomationRuleId Fixtures ====================

    /**
     * 기본 자동화 규칙 ID
     *
     * @return AutomationRuleId
     */
    public static AutomationRuleId defaultAutomationRuleId() {
        return AutomationRuleId.of("ARCHUNIT-001");
    }

    /**
     * 빈 자동화 규칙 ID
     *
     * @return AutomationRuleId
     */
    public static AutomationRuleId emptyAutomationRuleId() {
        return AutomationRuleId.empty();
    }
}

package com.ryuqq.domain.codingrule.aggregate;

import com.ryuqq.domain.codingrule.vo.AppliesTo;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.codingrule.vo.RuleName;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import com.ryuqq.domain.codingrule.vo.SdkConstraint;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;

/**
 * CodingRuleUpdateData - 코딩 규칙 수정 데이터
 *
 * <p>Zero-Tolerance 여부는 ZeroToleranceRule 엔티티의 존재 여부로 판단하므로 이 record에서 제외됩니다.
 *
 * @author ryu-qqq
 */
public record CodingRuleUpdateData(
        PackageStructureId structureId,
        RuleCode code,
        RuleName name,
        RuleSeverity severity,
        RuleCategory category,
        String description,
        String rationale,
        boolean autoFixable,
        AppliesTo appliesTo,
        SdkConstraint sdkConstraint) {

    public CodingRuleUpdateData {
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (severity == null) {
            throw new IllegalArgumentException("severity must not be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }
        if (appliesTo == null) {
            appliesTo = AppliesTo.empty();
        }
        if (sdkConstraint == null) {
            sdkConstraint = SdkConstraint.empty();
        }
        // structureId는 nullable
        // description, rationale은 nullable
    }
}

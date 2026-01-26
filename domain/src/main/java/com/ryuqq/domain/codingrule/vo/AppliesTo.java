package com.ryuqq.domain.codingrule.vo;

import java.util.Collections;
import java.util.List;

/**
 * AppliesTo - 규칙 적용 대상 Value Object
 *
 * <p>예: ["CLASS", "METHOD", "FIELD"]
 *
 * @author ryu-qqq
 */
public record AppliesTo(List<String> targets) {

    public AppliesTo {
        targets = targets != null ? Collections.unmodifiableList(targets) : Collections.emptyList();
    }

    public static AppliesTo of(List<String> targets) {
        return new AppliesTo(targets);
    }

    public static AppliesTo empty() {
        return new AppliesTo(Collections.emptyList());
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }

    public boolean appliesTo(String target) {
        return targets.contains(target);
    }
}

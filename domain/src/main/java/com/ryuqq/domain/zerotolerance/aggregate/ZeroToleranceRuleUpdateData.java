package com.ryuqq.domain.zerotolerance.aggregate;

import com.ryuqq.domain.zerotolerance.vo.DetectionPattern;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import com.ryuqq.domain.zerotolerance.vo.ErrorMessage;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceType;

/**
 * ZeroToleranceRuleUpdateData - Zero Tolerance 규칙 수정 데이터
 *
 * @author ryu-qqq
 */
public record ZeroToleranceRuleUpdateData(
        ZeroToleranceType type,
        DetectionPattern detectionPattern,
        DetectionType detectionType,
        boolean autoRejectPr,
        ErrorMessage errorMessage) {

    public ZeroToleranceRuleUpdateData {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (detectionPattern == null) {
            throw new IllegalArgumentException("detectionPattern must not be null");
        }
        if (detectionType == null) {
            throw new IllegalArgumentException("detectionType must not be null");
        }
        if (errorMessage == null) {
            throw new IllegalArgumentException("errorMessage must not be null");
        }
    }
}

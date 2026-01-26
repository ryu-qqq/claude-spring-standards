package com.ryuqq.application.zerotolerance.dto.command;

import com.ryuqq.domain.zerotolerance.vo.DetectionType;

/**
 * CreateZeroToleranceRuleCommand - Zero-Tolerance 규칙 생성 커맨드
 *
 * <p>Zero-Tolerance 규칙 생성에 필요한 데이터를 담습니다.
 *
 * <p>CMD-001: Command는 Record로 정의.
 *
 * @author ryu-qqq
 */
public record CreateZeroToleranceRuleCommand(
        Long ruleId,
        String type,
        String detectionPattern,
        DetectionType detectionType,
        boolean autoRejectPr,
        String errorMessage) {}

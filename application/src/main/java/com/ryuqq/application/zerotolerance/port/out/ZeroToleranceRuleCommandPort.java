package com.ryuqq.application.zerotolerance.port.out;

import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;

/**
 * ZeroToleranceRuleCommandPort - Zero-Tolerance 규칙 명령 아웃바운드 포트
 *
 * <p>Zero-Tolerance 규칙 저장/수정을 위한 아웃바운드 포트입니다.
 *
 * <p>PORT-002: CommandPort는 persist, delete 메서드 정의.
 *
 * @author ryu-qqq
 */
public interface ZeroToleranceRuleCommandPort {

    /**
     * Zero-Tolerance 규칙 저장 (생성 및 수정)
     *
     * @param zeroToleranceRule 저장할 Zero-Tolerance 규칙
     * @return 저장된 Zero-Tolerance 규칙 ID
     */
    ZeroToleranceRuleId persist(ZeroToleranceRule zeroToleranceRule);
}

package com.ryuqq.adapter.in.rest.zerotolerance.mapper;

import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.CreateZeroToleranceRuleApiRequest;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.UpdateZeroToleranceRuleApiRequest;
import com.ryuqq.application.zerotolerance.dto.command.CreateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.dto.command.UpdateZeroToleranceRuleCommand;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleCommandApiMapper - Zero-Tolerance 규칙 Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAP-001: Mapper는 @Component로 등록.
 *
 * <p>MAP-002: Mapper에서 Static 메서드 금지.
 *
 * <p>MAP-004: Mapper는 필드 매핑만 수행.
 *
 * <p>MAP-006: Mapper에서 Domain 객체 직접 사용 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ZeroToleranceRuleCommandApiMapper {

    /**
     * CreateZeroToleranceRuleApiRequest -> CreateZeroToleranceRuleCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateZeroToleranceRuleCommand toCommand(CreateZeroToleranceRuleApiRequest request) {
        return new CreateZeroToleranceRuleCommand(
                request.ruleId(),
                request.type(),
                request.detectionPattern(),
                request.detectionType(),
                request.autoRejectPr(),
                request.errorMessage());
    }

    /**
     * UpdateZeroToleranceRuleApiRequest + PathVariable ID -> UpdateZeroToleranceRuleCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param zeroToleranceRuleId Zero-Tolerance 규칙 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateZeroToleranceRuleCommand toCommand(
            Long zeroToleranceRuleId, UpdateZeroToleranceRuleApiRequest request) {
        return new UpdateZeroToleranceRuleCommand(
                zeroToleranceRuleId,
                request.type(),
                request.detectionPattern(),
                request.detectionType(),
                request.autoRejectPr(),
                request.errorMessage());
    }
}

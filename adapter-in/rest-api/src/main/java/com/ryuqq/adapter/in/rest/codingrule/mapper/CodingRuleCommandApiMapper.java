package com.ryuqq.adapter.in.rest.codingrule.mapper;

import com.ryuqq.adapter.in.rest.codingrule.dto.request.CreateCodingRuleApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.request.UpdateCodingRuleApiRequest;
import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CodingRuleCommandApiMapper - CodingRule Command API 변환 매퍼
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
public class CodingRuleCommandApiMapper {

    /**
     * CreateCodingRuleApiRequest -> CreateCodingRuleCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateCodingRuleCommand toCommand(CreateCodingRuleApiRequest request) {
        List<String> appliesTo =
                request.appliesTo() != null ? request.appliesTo() : Collections.emptyList();

        String sdkArtifact = null;
        String sdkMinVersion = null;
        String sdkMaxVersion = null;

        if (request.sdkConstraint() != null) {
            sdkArtifact = request.sdkConstraint().artifact();
            sdkMinVersion = request.sdkConstraint().minVersion();
            sdkMaxVersion = request.sdkConstraint().maxVersion();
        }

        return new CreateCodingRuleCommand(
                request.conventionId(),
                request.structureId(),
                request.code(),
                request.name(),
                request.severity(),
                request.category(),
                request.description(),
                request.rationale(),
                request.autoFixable(),
                appliesTo,
                sdkArtifact,
                sdkMinVersion,
                sdkMaxVersion);
    }

    /**
     * UpdateCodingRuleApiRequest + PathVariable ID -> UpdateCodingRuleCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param codingRuleId CodingRule ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateCodingRuleCommand toCommand(
            Long codingRuleId, UpdateCodingRuleApiRequest request) {
        List<String> appliesTo =
                request.appliesTo() != null ? request.appliesTo() : Collections.emptyList();

        String sdkArtifact = null;
        String sdkMinVersion = null;
        String sdkMaxVersion = null;

        if (request.sdkConstraint() != null) {
            sdkArtifact = request.sdkConstraint().artifact();
            sdkMinVersion = request.sdkConstraint().minVersion();
            sdkMaxVersion = request.sdkConstraint().maxVersion();
        }

        return new UpdateCodingRuleCommand(
                codingRuleId,
                request.structureId(),
                request.code(),
                request.name(),
                request.severity(),
                request.category(),
                request.description(),
                request.rationale(),
                request.autoFixable(),
                appliesTo,
                sdkArtifact,
                sdkMinVersion,
                sdkMaxVersion);
    }
}

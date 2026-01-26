package com.ryuqq.application.codingrule.factory.command;

import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.aggregate.CodingRuleUpdateData;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.AppliesTo;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.codingrule.vo.RuleName;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import com.ryuqq.domain.codingrule.vo.SdkConstraint;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Component;

/**
 * CodingRuleCommandFactory - 코딩 규칙 커맨드 팩토리
 *
 * <p>코딩 규칙 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleCommandFactory {

    private final TimeProvider timeProvider;

    public CodingRuleCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateCodingRuleCommand로부터 CodingRule 도메인 객체 생성
     *
     * <p>FCT-002: Factory에서 TimeProvider 사용하여 시간 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 CodingRule 인스턴스
     */
    public CodingRule create(CreateCodingRuleCommand command) {
        return CodingRule.forNew(
                ConventionId.of(command.conventionId()),
                command.structureId() != null ? PackageStructureId.of(command.structureId()) : null,
                RuleCode.of(command.code()),
                RuleName.of(command.name()),
                RuleSeverity.valueOf(command.severity()),
                RuleCategory.valueOf(command.category()),
                command.description(),
                command.rationale(),
                command.autoFixable(),
                command.appliesTo() != null ? AppliesTo.of(command.appliesTo()) : AppliesTo.empty(),
                createSdkConstraint(
                        command.sdkArtifact(), command.sdkMinVersion(), command.sdkMaxVersion()),
                timeProvider.now());
    }

    private SdkConstraint createSdkConstraint(
            String artifact, String minVersion, String maxVersion) {
        if (artifact == null || artifact.isBlank()) {
            return SdkConstraint.empty();
        }
        return SdkConstraint.of(artifact, minVersion, maxVersion);
    }

    /**
     * UpdateCodingRuleCommand로부터 CodingRuleUpdateData 생성
     *
     * @param command 수정 커맨드
     * @return CodingRuleUpdateData
     */
    public CodingRuleUpdateData createUpdateData(UpdateCodingRuleCommand command) {
        return new CodingRuleUpdateData(
                command.structureId() != null ? PackageStructureId.of(command.structureId()) : null,
                RuleCode.of(command.code()),
                RuleName.of(command.name()),
                RuleSeverity.valueOf(command.severity()),
                RuleCategory.valueOf(command.category()),
                command.description(),
                command.rationale(),
                command.autoFixable(),
                command.appliesTo() != null ? AppliesTo.of(command.appliesTo()) : AppliesTo.empty(),
                createSdkConstraint(
                        command.sdkArtifact(), command.sdkMinVersion(), command.sdkMaxVersion()));
    }

    /**
     * UpdateCodingRuleCommand로부터 CodingRuleId와 CodingRuleUpdateData 생성
     *
     * <p>업데이트에 필요한 ID와 UpdateData를 한 번에 생성합니다.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData)
     */
    public UpdateContext<CodingRuleId, CodingRuleUpdateData> createUpdateContext(
            UpdateCodingRuleCommand command) {
        CodingRuleId id = CodingRuleId.of(command.codingRuleId());
        CodingRuleUpdateData updateData = createUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}

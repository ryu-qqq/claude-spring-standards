package com.ryuqq.application.zerotolerance.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.zerotolerance.dto.command.CreateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.dto.command.UpdateZeroToleranceRuleCommand;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRuleUpdateData;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.vo.DetectionPattern;
import com.ryuqq.domain.zerotolerance.vo.ErrorMessage;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceType;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleCommandFactory - Zero-Tolerance 규칙 커맨드 팩토리
 *
 * <p>Zero-Tolerance 규칙 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
 *
 * @author ryu-qqq
 */
@Component
public class ZeroToleranceRuleCommandFactory {

    private final TimeProvider timeProvider;

    public ZeroToleranceRuleCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateZeroToleranceRuleCommand로부터 ZeroToleranceRule 도메인 객체 생성
     *
     * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 ZeroToleranceRule 인스턴스
     */
    public ZeroToleranceRule create(CreateZeroToleranceRuleCommand command) {
        return create(command, timeProvider.now());
    }

    /**
     * CreateZeroToleranceRuleCommand로부터 ZeroToleranceRule 도메인 객체 생성
     *
     * @param command 생성 커맨드
     * @param now 현재 시각
     * @return 새로운 ZeroToleranceRule 인스턴스
     */
    public ZeroToleranceRule create(CreateZeroToleranceRuleCommand command, Instant now) {
        return ZeroToleranceRule.forNew(
                CodingRuleId.of(command.ruleId()),
                ZeroToleranceType.of(command.type()),
                DetectionPattern.of(command.detectionPattern()),
                command.detectionType(),
                command.autoRejectPr(),
                ErrorMessage.of(command.errorMessage()),
                now);
    }

    /**
     * UpdateZeroToleranceRuleCommand로부터 ZeroToleranceRuleUpdateData 생성
     *
     * @param command 수정 커맨드
     * @return ZeroToleranceRuleUpdateData
     */
    public ZeroToleranceRuleUpdateData toUpdateData(UpdateZeroToleranceRuleCommand command) {
        return new ZeroToleranceRuleUpdateData(
                ZeroToleranceType.of(command.type()),
                DetectionPattern.of(command.detectionPattern()),
                command.detectionType(),
                command.autoRejectPr(),
                ErrorMessage.of(command.errorMessage()));
    }

    /**
     * UpdateZeroToleranceRuleCommand로부터 UpdateContext 생성
     *
     * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<ZeroToleranceRuleId, ZeroToleranceRuleUpdateData> createUpdateContext(
            UpdateZeroToleranceRuleCommand command) {
        ZeroToleranceRuleId id = ZeroToleranceRuleId.of(command.zeroToleranceRuleId());
        ZeroToleranceRuleUpdateData updateData = toUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}

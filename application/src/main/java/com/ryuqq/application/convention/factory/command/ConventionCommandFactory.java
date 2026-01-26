package com.ryuqq.application.convention.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.convention.dto.command.CreateConventionCommand;
import com.ryuqq.application.convention.dto.command.UpdateConventionCommand;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.aggregate.ConventionUpdateData;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ConventionCommandFactory - Convention Command -> Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 -> Factory에 위임.
 *
 * <p>APP-FAC-001: Factory는 복잡한 객체 생성과 TimeProvider가 필요한 작업에만 사용합니다. 단순 VO 변환(XxxId.of() 등)은
 * Service에서 직접 호출합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ConventionCommandFactory {

    private final TimeProvider timeProvider;

    public ConventionCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateConventionCommand로부터 Convention 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return Convention 도메인 객체
     */
    public Convention create(CreateConventionCommand command) {
        Instant now = timeProvider.now();

        return Convention.forNew(
                ModuleId.of(command.moduleId()),
                ConventionVersion.of(command.version()),
                command.description(),
                now);
    }

    // ==================== UpdateContext 생성 ====================

    /**
     * UpdateConventionCommand로부터 UpdateContext 생성
     *
     * <p>ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<ConventionId, ConventionUpdateData> createUpdateContext(
            UpdateConventionCommand command) {
        ConventionId id = ConventionId.of(command.id());
        ConventionUpdateData updateData =
                new ConventionUpdateData(
                        ModuleId.of(command.moduleId()),
                        ConventionVersion.of(command.version()),
                        command.description(),
                        command.active());
        Instant changedAt = timeProvider.now();
        return new UpdateContext<>(id, updateData, changedAt);
    }
}

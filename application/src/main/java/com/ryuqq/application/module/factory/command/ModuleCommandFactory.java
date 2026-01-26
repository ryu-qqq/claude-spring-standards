package com.ryuqq.application.module.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.module.dto.command.CreateModuleCommand;
import com.ryuqq.application.module.dto.command.UpdateModuleCommand;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.aggregate.ModuleUpdateData;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.BuildIdentifier;
import com.ryuqq.domain.module.vo.ModuleDescription;
import com.ryuqq.domain.module.vo.ModuleName;
import com.ryuqq.domain.module.vo.ModulePath;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ModuleCommandFactory - Module Command → Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ModuleCommandFactory {

    private final TimeProvider timeProvider;

    public ModuleCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateModuleCommand로부터 Module 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return Module 도메인 객체
     */
    public Module create(CreateModuleCommand command) {
        Instant now = timeProvider.now();

        return Module.forNew(
                LayerId.of(command.layerId()),
                command.parentModuleId() != null ? ModuleId.of(command.parentModuleId()) : null,
                ModuleName.of(command.name()),
                command.description() != null
                        ? ModuleDescription.of(command.description())
                        : ModuleDescription.empty(),
                ModulePath.of(command.modulePath()),
                command.buildIdentifier() != null
                        ? BuildIdentifier.of(command.buildIdentifier())
                        : BuildIdentifier.empty(),
                now);
    }

    /**
     * UpdateModuleCommand로부터 ModuleUpdateData 생성
     *
     * <p>내부에서만 사용되므로 private으로 선언합니다.
     *
     * @param command 수정 Command
     * @return ModuleUpdateData
     */
    private ModuleUpdateData createUpdateData(UpdateModuleCommand command) {
        return new ModuleUpdateData(
                command.parentModuleId() != null ? ModuleId.of(command.parentModuleId()) : null,
                ModuleName.of(command.name()),
                command.description() != null
                        ? ModuleDescription.of(command.description())
                        : ModuleDescription.empty(),
                ModulePath.of(command.modulePath()),
                command.buildIdentifier() != null
                        ? BuildIdentifier.of(command.buildIdentifier())
                        : BuildIdentifier.empty());
    }

    /**
     * UpdateModuleCommand로부터 UpdateContext 생성
     *
     * <p>업데이트에 필요한 ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<ModuleId, ModuleUpdateData> createUpdateContext(
            UpdateModuleCommand command) {
        ModuleId id = ModuleId.of(command.moduleId());
        ModuleUpdateData updateData = createUpdateData(command);
        Instant changedAt = timeProvider.now();
        return new UpdateContext<>(id, updateData, changedAt);
    }
}

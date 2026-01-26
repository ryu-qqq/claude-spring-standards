package com.ryuqq.application.layer.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.layer.dto.command.CreateLayerCommand;
import com.ryuqq.application.layer.dto.command.UpdateLayerCommand;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.aggregate.LayerUpdateData;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
import com.ryuqq.domain.layer.vo.LayerName;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * LayerCommandFactory - Layer Command → Domain 변환 Factory
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
public class LayerCommandFactory {

    private final TimeProvider timeProvider;

    public LayerCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateLayerCommand로부터 Layer 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return Layer 도메인 객체
     */
    public Layer create(CreateLayerCommand command) {
        Instant now = timeProvider.now();

        return Layer.forNew(
                ArchitectureId.of(command.architectureId()),
                LayerCode.of(command.code()),
                LayerName.of(command.name()),
                command.description(),
                command.orderIndex(),
                now);
    }

    /**
     * UpdateLayerCommand로부터 LayerUpdateData 생성
     *
     * <p>내부에서만 사용되므로 private으로 선언합니다.
     *
     * @param command 수정 Command
     * @return LayerUpdateData
     */
    private LayerUpdateData createUpdateData(UpdateLayerCommand command) {
        return new LayerUpdateData(
                LayerCode.of(command.code()),
                LayerName.of(command.name()),
                command.description(),
                command.orderIndex());
    }

    /**
     * UpdateLayerCommand로부터 UpdateContext 생성
     *
     * <p>업데이트에 필요한 ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<LayerId, LayerUpdateData> createUpdateContext(UpdateLayerCommand command) {
        LayerId id = LayerId.of(command.id());
        LayerUpdateData updateData = createUpdateData(command);
        Instant changedAt = timeProvider.now();
        return new UpdateContext<>(id, updateData, changedAt);
    }
}

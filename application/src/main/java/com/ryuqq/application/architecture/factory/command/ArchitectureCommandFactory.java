package com.ryuqq.application.architecture.factory.command;

import com.ryuqq.application.architecture.dto.command.CreateArchitectureCommand;
import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.aggregate.ArchitectureUpdateData;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.architecture.vo.PatternDescription;
import com.ryuqq.domain.architecture.vo.PatternPrinciples;
import com.ryuqq.domain.architecture.vo.PatternType;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ArchitectureCommandFactory - Architecture Command → Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author ryu-qqq
 */
@Component
public class ArchitectureCommandFactory {

    private final TimeProvider timeProvider;

    public ArchitectureCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateArchitectureCommand로부터 Architecture 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return Architecture 도메인 객체
     */
    public Architecture create(CreateArchitectureCommand command) {
        Instant now = timeProvider.now();

        return Architecture.forNew(
                TechStackId.of(command.techStackId()),
                ArchitectureName.of(command.name()),
                PatternType.valueOf(command.patternType()),
                PatternDescription.of(command.patternDescription()),
                PatternPrinciples.of(command.patternPrinciples()),
                ReferenceLinks.of(command.referenceLinks()),
                now);
    }

    /**
     * UpdateArchitectureCommand로부터 ArchitectureUpdateData 생성
     *
     * @param command 수정 Command
     * @return ArchitectureUpdateData
     */
    public ArchitectureUpdateData createUpdateData(UpdateArchitectureCommand command) {
        return new ArchitectureUpdateData(
                ArchitectureName.of(command.name()),
                PatternType.valueOf(command.patternType()),
                PatternDescription.of(command.patternDescription()),
                PatternPrinciples.of(command.patternPrinciples()),
                ReferenceLinks.of(command.referenceLinks()));
    }

    /**
     * UpdateArchitectureCommand로부터 ArchitectureId와 ArchitectureUpdateData 생성
     *
     * <p>업데이트에 필요한 ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<ArchitectureId, ArchitectureUpdateData> createUpdateContext(
            UpdateArchitectureCommand command) {
        ArchitectureId id = ArchitectureId.of(command.id());
        ArchitectureUpdateData updateData = createUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}

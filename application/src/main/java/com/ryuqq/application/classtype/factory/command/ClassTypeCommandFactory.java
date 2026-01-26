package com.ryuqq.application.classtype.factory.command;

import com.ryuqq.application.classtype.dto.command.CreateClassTypeCommand;
import com.ryuqq.application.classtype.dto.command.UpdateClassTypeCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.aggregate.ClassTypeUpdateData;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtype.vo.ClassTypeName;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCommandFactory - ClassType Command → Domain 변환 Factory
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
public class ClassTypeCommandFactory {

    private final TimeProvider timeProvider;

    public ClassTypeCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateClassTypeCommand로부터 ClassType 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return ClassType 도메인 객체
     */
    public ClassType create(CreateClassTypeCommand command) {
        Instant now = timeProvider.now();

        return ClassType.forNew(
                ClassTypeCategoryId.of(command.categoryId()),
                ClassTypeCode.of(command.code()),
                ClassTypeName.of(command.name()),
                command.description(),
                command.orderIndex(),
                now);
    }

    /**
     * UpdateClassTypeCommand로부터 ClassTypeUpdateData 생성
     *
     * @param command 수정 Command
     * @return ClassTypeUpdateData
     */
    private ClassTypeUpdateData createUpdateData(UpdateClassTypeCommand command) {
        return ClassTypeUpdateData.of(
                ClassTypeCode.of(command.code()),
                ClassTypeName.of(command.name()),
                command.description(),
                command.orderIndex());
    }

    /**
     * UpdateClassTypeCommand로부터 UpdateContext 생성
     *
     * <p>업데이트에 필요한 ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<ClassTypeId, ClassTypeUpdateData> createUpdateContext(
            UpdateClassTypeCommand command) {
        ClassTypeId id = ClassTypeId.of(command.id());
        ClassTypeUpdateData updateData = createUpdateData(command);
        Instant changedAt = timeProvider.now();
        return new UpdateContext<>(id, updateData, changedAt);
    }
}

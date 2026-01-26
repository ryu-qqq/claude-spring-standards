package com.ryuqq.application.classtypecategory.factory.command;

import com.ryuqq.application.classtypecategory.dto.command.CreateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.dto.command.UpdateClassTypeCategoryCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategoryUpdateData;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import com.ryuqq.domain.classtypecategory.vo.CategoryName;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryCommandFactory - ClassTypeCategory Command → Domain 변환 Factory
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
public class ClassTypeCategoryCommandFactory {

    private final TimeProvider timeProvider;

    public ClassTypeCategoryCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateClassTypeCategoryCommand로부터 ClassTypeCategory 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return ClassTypeCategory 도메인 객체
     */
    public ClassTypeCategory create(CreateClassTypeCategoryCommand command) {
        Instant now = timeProvider.now();

        return ClassTypeCategory.forNew(
                ArchitectureId.of(command.architectureId()),
                CategoryCode.of(command.code()),
                CategoryName.of(command.name()),
                command.description(),
                command.orderIndex(),
                now);
    }

    /**
     * UpdateClassTypeCategoryCommand로부터 ClassTypeCategoryUpdateData 생성
     *
     * @param command 수정 Command
     * @return ClassTypeCategoryUpdateData
     */
    private ClassTypeCategoryUpdateData createUpdateData(UpdateClassTypeCategoryCommand command) {
        return ClassTypeCategoryUpdateData.of(
                CategoryCode.of(command.code()),
                CategoryName.of(command.name()),
                command.description(),
                command.orderIndex());
    }

    /**
     * UpdateClassTypeCategoryCommand로부터 UpdateContext 생성
     *
     * <p>업데이트에 필요한 ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<ClassTypeCategoryId, ClassTypeCategoryUpdateData> createUpdateContext(
            UpdateClassTypeCategoryCommand command) {
        ClassTypeCategoryId id = ClassTypeCategoryId.of(command.id());
        ClassTypeCategoryUpdateData updateData = createUpdateData(command);
        Instant changedAt = timeProvider.now();
        return new UpdateContext<>(id, updateData, changedAt);
    }
}

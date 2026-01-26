package com.ryuqq.application.resourcetemplate.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.resourcetemplate.dto.command.CreateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.dto.command.UpdateResourceTemplateCommand;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplateUpdateData;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import com.ryuqq.domain.resourcetemplate.vo.TemplateContent;
import com.ryuqq.domain.resourcetemplate.vo.TemplatePath;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateCommandFactory - 리소스 템플릿 커맨드 팩토리
 *
 * <p>리소스 템플릿 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplateCommandFactory {

    private final TimeProvider timeProvider;

    public ResourceTemplateCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateResourceTemplateCommand로부터 ResourceTemplate 도메인 객체 생성
     *
     * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 ResourceTemplate 인스턴스
     */
    public ResourceTemplate create(CreateResourceTemplateCommand command) {
        return ResourceTemplate.forNew(
                ModuleId.of(command.moduleId()),
                TemplateCategory.valueOf(command.category()),
                TemplatePath.of(command.filePath()),
                FileType.valueOf(command.fileType()),
                command.description(),
                command.templateContent() != null
                        ? TemplateContent.of(command.templateContent())
                        : TemplateContent.empty(),
                command.required(),
                timeProvider.now());
    }

    /**
     * UpdateResourceTemplateCommand로부터 ResourceTemplateUpdateData 생성
     *
     * <p>요청으로 들어온 데이터를 기반으로 객체를 만들고, JPA의 더티체킹을 활용하여 변경사항을 처리합니다.
     *
     * @param command 수정 커맨드
     * @return ResourceTemplateUpdateData
     */
    public ResourceTemplateUpdateData toUpdateData(UpdateResourceTemplateCommand command) {
        return ResourceTemplateUpdateData.builder()
                .category(
                        command.category() != null
                                ? TemplateCategory.valueOf(command.category())
                                : null)
                .filePath(command.filePath() != null ? TemplatePath.of(command.filePath()) : null)
                .fileType(command.fileType() != null ? FileType.valueOf(command.fileType()) : null)
                .description(command.description())
                .templateContent(
                        command.templateContent() != null
                                ? TemplateContent.of(command.templateContent())
                                : null)
                .required(command.required())
                .build();
    }

    /**
     * UpdateResourceTemplateCommand로부터 ResourceTemplateId와 ResourceTemplateUpdateData 생성
     *
     * <p>업데이트에 필요한 ID와 UpdateData를 한 번에 생성합니다.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData)
     */
    public UpdateContext<ResourceTemplateId, ResourceTemplateUpdateData> createUpdateContext(
            UpdateResourceTemplateCommand command) {
        ResourceTemplateId id = ResourceTemplateId.of(command.resourceTemplateId());
        ResourceTemplateUpdateData updateData = toUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}

package com.ryuqq.application.configfiletemplate.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.configfiletemplate.dto.command.CreateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.dto.command.UpdateConfigFileTemplateCommand;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplateUpdateData;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.configfiletemplate.vo.DisplayOrder;
import com.ryuqq.domain.configfiletemplate.vo.FileName;
import com.ryuqq.domain.configfiletemplate.vo.FilePath;
import com.ryuqq.domain.configfiletemplate.vo.TemplateCategory;
import com.ryuqq.domain.configfiletemplate.vo.TemplateContent;
import com.ryuqq.domain.configfiletemplate.vo.TemplateDescription;
import com.ryuqq.domain.configfiletemplate.vo.TemplateVariables;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateCommandFactory - ConfigFileTemplate Command → Domain 변환 Factory
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
public class ConfigFileTemplateCommandFactory {

    private final TimeProvider timeProvider;

    public ConfigFileTemplateCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateConfigFileTemplateCommand로부터 ConfigFileTemplate 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return ConfigFileTemplate 도메인 객체
     */
    public ConfigFileTemplate create(CreateConfigFileTemplateCommand command) {
        Instant now = timeProvider.now();

        return ConfigFileTemplate.forNew(
                TechStackId.of(command.techStackId()),
                toArchitectureId(command.architectureId()),
                ToolType.valueOf(command.toolType()),
                FilePath.of(command.filePath()),
                FileName.of(command.fileName()),
                TemplateContent.of(command.content()),
                toCategory(command.category()),
                TemplateDescription.of(command.description()),
                TemplateVariables.of(command.variables()),
                DisplayOrder.of(command.displayOrder()),
                command.isRequired(),
                now);
    }

    /**
     * UpdateConfigFileTemplateCommand로부터 ConfigFileTemplateUpdateData 생성
     *
     * @param command 수정 Command
     * @return ConfigFileTemplateUpdateData
     */
    public ConfigFileTemplateUpdateData createUpdateData(UpdateConfigFileTemplateCommand command) {
        return new ConfigFileTemplateUpdateData(
                ToolType.valueOf(command.toolType()),
                FilePath.of(command.filePath()),
                FileName.of(command.fileName()),
                TemplateContent.of(command.content()),
                toCategory(command.category()),
                TemplateDescription.of(command.description()),
                TemplateVariables.of(command.variables()),
                DisplayOrder.of(command.displayOrder()),
                command.isRequired());
    }

    /**
     * UpdateConfigFileTemplateCommand로부터 UpdateContext 생성
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<ConfigFileTemplateId, ConfigFileTemplateUpdateData> createUpdateContext(
            UpdateConfigFileTemplateCommand command) {
        ConfigFileTemplateId id = ConfigFileTemplateId.of(command.id());
        ConfigFileTemplateUpdateData updateData = createUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }

    // ==================== Private Helper Methods ====================

    private ArchitectureId toArchitectureId(Long id) {
        return id != null ? ArchitectureId.of(id) : null;
    }

    private TemplateCategory toCategory(String category) {
        return category != null ? TemplateCategory.valueOf(category) : null;
    }
}

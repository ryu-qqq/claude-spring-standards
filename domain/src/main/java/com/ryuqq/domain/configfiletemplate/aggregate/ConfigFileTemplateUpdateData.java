package com.ryuqq.domain.configfiletemplate.aggregate;

import com.ryuqq.domain.configfiletemplate.vo.DisplayOrder;
import com.ryuqq.domain.configfiletemplate.vo.FileName;
import com.ryuqq.domain.configfiletemplate.vo.FilePath;
import com.ryuqq.domain.configfiletemplate.vo.TemplateCategory;
import com.ryuqq.domain.configfiletemplate.vo.TemplateContent;
import com.ryuqq.domain.configfiletemplate.vo.TemplateDescription;
import com.ryuqq.domain.configfiletemplate.vo.TemplateVariables;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;

/**
 * ConfigFileTemplateUpdateData - 설정 파일 템플릿 수정 데이터
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ConfigFileTemplateUpdateData(
        ToolType toolType,
        FilePath filePath,
        FileName fileName,
        TemplateContent content,
        TemplateCategory category,
        TemplateDescription description,
        TemplateVariables variables,
        DisplayOrder displayOrder,
        Boolean isRequired) {

    public ConfigFileTemplateUpdateData {
        if (toolType == null) {
            throw new IllegalArgumentException("toolType must not be null");
        }
        if (filePath == null) {
            throw new IllegalArgumentException("filePath must not be null");
        }
        if (fileName == null) {
            throw new IllegalArgumentException("fileName must not be null");
        }
        if (content == null) {
            content = TemplateContent.empty();
        }
        if (description == null) {
            description = TemplateDescription.empty();
        }
        if (variables == null) {
            variables = TemplateVariables.empty();
        }
        if (displayOrder == null) {
            displayOrder = DisplayOrder.defaultOrder();
        }
        if (isRequired == null) {
            isRequired = true;
        }
    }
}

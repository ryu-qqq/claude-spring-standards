package com.ryuqq.application.feedbackqueue.internal.validator.merge.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import com.ryuqq.application.classtemplate.manager.ClassTemplateReadManager;
import com.ryuqq.application.feedbackqueue.internal.validator.merge.FeedbackMergeValidator;
import com.ryuqq.application.packagestructure.manager.PackageStructureReadManager;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.FeedbackMergeValidationException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateMergeValidator - 클래스 템플릿 병합 시점 검증기
 *
 * <p>CLASS_TEMPLATE 타입의 피드백을 병합 시점에 재검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: PackageStructure(부모) 존재 여부 (필수)
 *   <li>MODIFY/DELETE 시: ClassTemplate(대상) 존재 여부 (필수)
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplateMergeValidator implements FeedbackMergeValidator {

    private final ObjectMapper objectMapper;
    private final PackageStructureReadManager packageStructureReadManager;
    private final ClassTemplateReadManager classTemplateReadManager;

    public ClassTemplateMergeValidator(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            PackageStructureReadManager packageStructureReadManager,
            ClassTemplateReadManager classTemplateReadManager) {
        this.objectMapper = objectMapper;
        this.packageStructureReadManager = packageStructureReadManager;
        this.classTemplateReadManager = classTemplateReadManager;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.CLASS_TEMPLATE;
    }

    @Override
    public void validate(FeedbackQueue feedbackQueue) {
        FeedbackType feedbackType = feedbackQueue.feedbackType();
        String payload = feedbackQueue.payloadValue();

        if (feedbackType.isAdd()) {
            validateAdd(feedbackType, payload);
        } else if (feedbackType.isModify()) {
            validateModify(feedbackType, payload);
        } else if (feedbackType.isDelete()) {
            validateDelete(feedbackType, feedbackQueue.targetId());
        } else {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "Unsupported feedback type");
        }
    }

    private void validateAdd(FeedbackType feedbackType, String payload) {
        CreateClassTemplateCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateClassTemplateCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for CreateClassTemplateCommand: " + e.getMessage());
        }

        PackageStructureId structureId = PackageStructureId.of(createCommand.structureId());
        boolean parentExists = packageStructureReadManager.findById(structureId) != null;

        if (!parentExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "PackageStructure not found: " + structureId.value());
        }
    }

    private void validateModify(FeedbackType feedbackType, String payload) {
        UpdateClassTemplateCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateClassTemplateCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for UpdateClassTemplateCommand: " + e.getMessage());
        }

        ClassTemplateId classTemplateId = ClassTemplateId.of(updateCommand.classTemplateId());
        boolean targetExists = classTemplateReadManager.findById(classTemplateId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "ClassTemplate not found: " + classTemplateId.value());
        }
    }

    private void validateDelete(FeedbackType feedbackType, Long targetId) {
        if (targetId == null) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "Target ID is required for delete operation");
        }

        ClassTemplateId classTemplateId = ClassTemplateId.of(targetId);
        boolean targetExists = classTemplateReadManager.findById(classTemplateId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "ClassTemplate not found: " + targetId);
        }
    }
}

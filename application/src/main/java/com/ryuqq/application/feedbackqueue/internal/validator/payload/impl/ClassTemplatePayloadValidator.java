package com.ryuqq.application.feedbackqueue.internal.validator.payload.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import com.ryuqq.application.classtemplate.manager.ClassTemplateReadManager;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.internal.validator.payload.FeedbackPayloadValidator;
import com.ryuqq.application.packagestructure.manager.PackageStructureReadManager;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackPayloadException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ClassTemplatePayloadValidator - 클래스 템플릿 페이로드 검증기
 *
 * <p>CLASS_TEMPLATE 타입의 피드백 페이로드를 검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: PackageStructure(부모) 존재 여부 → 없으면 예외
 *   <li>MODIFY 시: ClassTemplate 존재 여부 → 없으면 예외
 *   <li>DELETE 시: ClassTemplate 존재 여부 → 없으면 예외
 * </ol>
 *
 * <p>검증 성공 시 아무것도 반환하지 않고, 실패 시 {@link InvalidFeedbackPayloadException}을 던집니다.
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplatePayloadValidator implements FeedbackPayloadValidator {

    private final ObjectMapper objectMapper;
    private final PackageStructureReadManager packageStructureReadManager;
    private final ClassTemplateReadManager classTemplateReadManager;

    public ClassTemplatePayloadValidator(
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
    public void validate(CreateFeedbackCommand command) {
        FeedbackTargetType targetType = supportedType();
        String feedbackTypeString = command.feedbackType();
        FeedbackType feedbackType = FeedbackType.valueOf(feedbackTypeString);

        if (feedbackType.isAdd()) {
            validateAdd(targetType, feedbackTypeString, command.payload());
        } else if (feedbackType.isModify()) {
            validateModify(targetType, feedbackTypeString, command.payload());
        } else if (feedbackType.isDelete()) {
            validateDelete(targetType, feedbackTypeString, command.targetId());
        } else {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackTypeString, "Unsupported feedback type");
        }
    }

    private void validateAdd(FeedbackTargetType targetType, String feedbackType, String payload) {
        CreateClassTemplateCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateClassTemplateCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for CreateClassTemplateCommand: " + e.getMessage());
        }

        PackageStructureId structureId = PackageStructureId.of(createCommand.structureId());
        boolean parentExists = packageStructureReadManager.findById(structureId) != null;

        if (!parentExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "PackageStructure not found: " + structureId.value());
        }
    }

    private void validateModify(
            FeedbackTargetType targetType, String feedbackType, String payload) {
        UpdateClassTemplateCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateClassTemplateCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for UpdateClassTemplateCommand: " + e.getMessage());
        }

        ClassTemplateId classTemplateId = ClassTemplateId.of(updateCommand.classTemplateId());
        boolean targetExists = classTemplateReadManager.findById(classTemplateId) != null;

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "ClassTemplate not found for modification: " + classTemplateId.value());
        }
    }

    private void validateDelete(FeedbackTargetType targetType, String feedbackType, Long targetId) {
        if (targetId == null) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "Target ID is required for delete operation");
        }

        ClassTemplateId classTemplateId = ClassTemplateId.of(targetId);
        boolean targetExists = classTemplateReadManager.findById(classTemplateId) != null;

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "ClassTemplate not found for deletion: " + targetId);
        }
    }
}

package com.ryuqq.application.feedbackqueue.internal.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import com.ryuqq.application.classtemplate.factory.command.ClassTemplateCommandFactory;
import com.ryuqq.application.classtemplate.manager.ClassTemplatePersistenceManager;
import com.ryuqq.application.classtemplate.validator.ClassTemplateValidator;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.feedbackqueue.internal.strategy.FeedbackMergeStrategy;
import com.ryuqq.application.packagestructure.validator.PackageStructureValidator;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateMergeStrategy - 클래스 템플릿 머지 전략
 *
 * <p>FeedbackQueue의 CLASS_TEMPLATE 타입을 처리합니다.
 *
 * <p>처리 흐름:
 *
 * <ol>
 *   <li>Payload JSON 파싱
 *   <li>PackageStructure 존재 여부 검증 (ADD 시)
 *   <li>ClassTemplate 존재 여부 검증 (MODIFY/DELETE 시)
 *   <li>도메인 객체 생성/수정/삭제
 *   <li>영속화
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplateMergeStrategy implements FeedbackMergeStrategy {

    private final ObjectMapper objectMapper;
    private final PackageStructureValidator packageStructureValidator;
    private final ClassTemplateValidator classTemplateValidator;
    private final ClassTemplateCommandFactory classTemplateCommandFactory;
    private final ClassTemplatePersistenceManager classTemplatePersistenceManager;
    private final TimeProvider timeProvider;

    public ClassTemplateMergeStrategy(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            PackageStructureValidator packageStructureValidator,
            ClassTemplateValidator classTemplateValidator,
            ClassTemplateCommandFactory classTemplateCommandFactory,
            ClassTemplatePersistenceManager classTemplatePersistenceManager,
            TimeProvider timeProvider) {
        this.objectMapper = objectMapper;
        this.packageStructureValidator = packageStructureValidator;
        this.classTemplateValidator = classTemplateValidator;
        this.classTemplateCommandFactory = classTemplateCommandFactory;
        this.classTemplatePersistenceManager = classTemplatePersistenceManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.CLASS_TEMPLATE;
    }

    @Override
    public Long merge(FeedbackQueue feedbackQueue) {
        FeedbackType feedbackType = feedbackQueue.feedbackType();

        if (feedbackType.isAdd()) {
            return handleAdd(feedbackQueue);
        } else if (feedbackType.isModify()) {
            return handleModify(feedbackQueue);
        } else if (feedbackType.isDelete()) {
            return handleDelete(feedbackQueue);
        }

        throw new IllegalArgumentException("Unsupported feedback type: " + feedbackType);
    }

    private Long handleAdd(FeedbackQueue feedbackQueue) {
        CreateClassTemplateCommand command = parseCreateCommand(feedbackQueue.payloadValue());

        // PackageStructure 존재 검증
        PackageStructureId structureId = PackageStructureId.of(command.structureId());
        packageStructureValidator.validateExists(structureId);

        // 도메인 객체 생성 및 영속화
        ClassTemplate classTemplate = classTemplateCommandFactory.create(command);
        ClassTemplateId savedId = classTemplatePersistenceManager.persist(classTemplate);

        return savedId.value();
    }

    private Long handleModify(FeedbackQueue feedbackQueue) {
        UpdateClassTemplateCommand command = parseUpdateCommand(feedbackQueue.payloadValue());

        // ClassTemplate 존재 검증 및 조회
        ClassTemplateId classTemplateId = ClassTemplateId.of(command.classTemplateId());
        ClassTemplate classTemplate = classTemplateValidator.findExistingOrThrow(classTemplateId);

        // 업데이트 및 영속화
        classTemplate.update(classTemplateCommandFactory.toUpdateData(command), timeProvider.now());
        ClassTemplateId savedId = classTemplatePersistenceManager.persist(classTemplate);

        return savedId.value();
    }

    private Long handleDelete(FeedbackQueue feedbackQueue) {
        Long targetId = feedbackQueue.targetId();
        ClassTemplateId classTemplateId = ClassTemplateId.of(targetId);

        // ClassTemplate 존재 검증 및 조회
        ClassTemplate classTemplate = classTemplateValidator.findExistingOrThrow(classTemplateId);

        // 삭제 처리 (soft delete)
        classTemplate.delete(timeProvider.now());
        classTemplatePersistenceManager.persist(classTemplate);

        return targetId;
    }

    private CreateClassTemplateCommand parseCreateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, CreateClassTemplateCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse CreateClassTemplateCommand from payload", e);
        }
    }

    private UpdateClassTemplateCommand parseUpdateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, UpdateClassTemplateCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse UpdateClassTemplateCommand from payload", e);
        }
    }
}

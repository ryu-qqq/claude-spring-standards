package com.ryuqq.application.feedbackqueue.internal.validator.payload.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import com.ryuqq.application.archunittest.manager.ArchUnitTestReadManager;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.internal.validator.payload.FeedbackPayloadValidator;
import com.ryuqq.application.packagestructure.manager.PackageStructureReadManager;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackPayloadException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestPayloadValidator - ArchUnit 테스트 페이로드 검증기
 *
 * <p>ARCH_UNIT_TEST 타입의 피드백 페이로드를 검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: PackageStructure(부모) 존재 여부 → 없으면 예외
 *   <li>MODIFY 시: ArchUnitTest 존재 여부 → 없으면 예외
 *   <li>DELETE 시: ArchUnitTest 존재 여부 → 없으면 예외
 * </ol>
 *
 * <p>검증 성공 시 아무것도 반환하지 않고, 실패 시 {@link InvalidFeedbackPayloadException}을 던집니다.
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestPayloadValidator implements FeedbackPayloadValidator {

    private final ObjectMapper objectMapper;
    private final PackageStructureReadManager packageStructureReadManager;
    private final ArchUnitTestReadManager archUnitTestReadManager;

    public ArchUnitTestPayloadValidator(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            PackageStructureReadManager packageStructureReadManager,
            ArchUnitTestReadManager archUnitTestReadManager) {
        this.objectMapper = objectMapper;
        this.packageStructureReadManager = packageStructureReadManager;
        this.archUnitTestReadManager = archUnitTestReadManager;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.ARCH_UNIT_TEST;
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
        CreateArchUnitTestCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateArchUnitTestCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for CreateArchUnitTestCommand: " + e.getMessage());
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
        UpdateArchUnitTestCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateArchUnitTestCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for UpdateArchUnitTestCommand: " + e.getMessage());
        }

        ArchUnitTestId archUnitTestId = ArchUnitTestId.of(updateCommand.archUnitTestId());
        boolean targetExists = archUnitTestReadManager.findById(archUnitTestId) != null;

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "ArchUnitTest not found for modification: " + archUnitTestId.value());
        }
    }

    private void validateDelete(FeedbackTargetType targetType, String feedbackType, Long targetId) {
        if (targetId == null) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "Target ID is required for delete operation");
        }

        ArchUnitTestId archUnitTestId = ArchUnitTestId.of(targetId);
        boolean targetExists = archUnitTestReadManager.findById(archUnitTestId) != null;

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "ArchUnitTest not found for deletion: " + targetId);
        }
    }
}

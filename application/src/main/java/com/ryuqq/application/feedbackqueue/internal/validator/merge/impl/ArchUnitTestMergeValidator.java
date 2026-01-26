package com.ryuqq.application.feedbackqueue.internal.validator.merge.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import com.ryuqq.application.archunittest.manager.ArchUnitTestReadManager;
import com.ryuqq.application.feedbackqueue.internal.validator.merge.FeedbackMergeValidator;
import com.ryuqq.application.packagestructure.manager.PackageStructureReadManager;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.FeedbackMergeValidationException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestMergeValidator - ArchUnit 테스트 병합 시점 검증기
 *
 * <p>ARCH_UNIT_TEST 타입의 피드백을 병합 시점에 재검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: PackageStructure(부모) 존재 여부 (필수)
 *   <li>MODIFY/DELETE 시: ArchUnitTest(대상) 존재 여부 (필수)
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestMergeValidator implements FeedbackMergeValidator {

    private final ObjectMapper objectMapper;
    private final PackageStructureReadManager packageStructureReadManager;
    private final ArchUnitTestReadManager archUnitTestReadManager;

    public ArchUnitTestMergeValidator(
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
        CreateArchUnitTestCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateArchUnitTestCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for CreateArchUnitTestCommand: " + e.getMessage());
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
        UpdateArchUnitTestCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateArchUnitTestCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for UpdateArchUnitTestCommand: " + e.getMessage());
        }

        ArchUnitTestId archUnitTestId = ArchUnitTestId.of(updateCommand.archUnitTestId());
        boolean targetExists = archUnitTestReadManager.findById(archUnitTestId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "ArchUnitTest not found: " + archUnitTestId.value());
        }
    }

    private void validateDelete(FeedbackType feedbackType, Long targetId) {
        if (targetId == null) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "Target ID is required for delete operation");
        }

        ArchUnitTestId archUnitTestId = ArchUnitTestId.of(targetId);
        boolean targetExists = archUnitTestReadManager.findById(archUnitTestId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "ArchUnitTest not found: " + targetId);
        }
    }
}

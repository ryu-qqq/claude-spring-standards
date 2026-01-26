package com.ryuqq.application.feedbackqueue.internal.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import com.ryuqq.application.archunittest.factory.command.ArchUnitTestCommandFactory;
import com.ryuqq.application.archunittest.manager.ArchUnitTestPersistenceManager;
import com.ryuqq.application.archunittest.manager.ArchUnitTestReadManager;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.feedbackqueue.internal.strategy.FeedbackMergeStrategy;
import com.ryuqq.application.packagestructure.manager.PackageStructureReadManager;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestMergeStrategy - ArchUnit 테스트 머지 전략
 *
 * <p>FeedbackQueue의 ARCH_UNIT_TEST 타입을 처리합니다.
 *
 * <p>처리 흐름:
 *
 * <ol>
 *   <li>Payload JSON 파싱
 *   <li>PackageStructure 존재 여부 검증 (ADD 시)
 *   <li>ArchUnitTest 존재 여부 검증 (MODIFY/DELETE 시)
 *   <li>도메인 객체 생성/수정/삭제
 *   <li>영속화
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestMergeStrategy implements FeedbackMergeStrategy {

    private final ObjectMapper objectMapper;
    private final PackageStructureReadManager packageStructureReadManager;
    private final ArchUnitTestReadManager archUnitTestReadManager;
    private final ArchUnitTestCommandFactory archUnitTestCommandFactory;
    private final ArchUnitTestPersistenceManager archUnitTestPersistenceManager;
    private final TimeProvider timeProvider;

    public ArchUnitTestMergeStrategy(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            PackageStructureReadManager packageStructureReadManager,
            ArchUnitTestReadManager archUnitTestReadManager,
            ArchUnitTestCommandFactory archUnitTestCommandFactory,
            ArchUnitTestPersistenceManager archUnitTestPersistenceManager,
            TimeProvider timeProvider) {
        this.objectMapper = objectMapper;
        this.packageStructureReadManager = packageStructureReadManager;
        this.archUnitTestReadManager = archUnitTestReadManager;
        this.archUnitTestCommandFactory = archUnitTestCommandFactory;
        this.archUnitTestPersistenceManager = archUnitTestPersistenceManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.ARCH_UNIT_TEST;
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
        CreateArchUnitTestCommand command = parseCreateCommand(feedbackQueue.payloadValue());

        // PackageStructure 존재 검증
        PackageStructureId structureId = archUnitTestCommandFactory.toStructureId(command);
        packageStructureReadManager.getById(structureId);

        // 도메인 객체 생성 및 영속화
        ArchUnitTest archUnitTest = archUnitTestCommandFactory.create(command, timeProvider.now());
        ArchUnitTestId savedId = archUnitTestPersistenceManager.persist(archUnitTest);

        return savedId.value();
    }

    private Long handleModify(FeedbackQueue feedbackQueue) {
        UpdateArchUnitTestCommand command = parseUpdateCommand(feedbackQueue.payloadValue());

        // ArchUnitTest 존재 검증 및 조회
        ArchUnitTestId archUnitTestId = archUnitTestCommandFactory.toArchUnitTestId(command);
        ArchUnitTest archUnitTest = archUnitTestReadManager.getById(archUnitTestId);

        // 업데이트 및 영속화
        archUnitTest.update(archUnitTestCommandFactory.toUpdateData(command), timeProvider.now());
        ArchUnitTestId savedId = archUnitTestPersistenceManager.persist(archUnitTest);

        return savedId.value();
    }

    private Long handleDelete(FeedbackQueue feedbackQueue) {
        Long targetId = feedbackQueue.targetId();
        ArchUnitTestId archUnitTestId = ArchUnitTestId.of(targetId);

        // ArchUnitTest 존재 검증 및 조회
        ArchUnitTest archUnitTest = archUnitTestReadManager.getById(archUnitTestId);

        // 삭제 처리 (soft delete)
        archUnitTest.delete(timeProvider.now());
        archUnitTestPersistenceManager.persist(archUnitTest);

        return targetId;
    }

    private CreateArchUnitTestCommand parseCreateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, CreateArchUnitTestCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse CreateArchUnitTestCommand from payload", e);
        }
    }

    private UpdateArchUnitTestCommand parseUpdateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, UpdateArchUnitTestCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse UpdateArchUnitTestCommand from payload", e);
        }
    }
}

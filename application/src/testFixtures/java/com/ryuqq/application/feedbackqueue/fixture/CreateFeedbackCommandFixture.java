package com.ryuqq.application.feedbackqueue.fixture;

import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;

/**
 * CreateFeedbackCommand Test Fixture
 *
 * @author development-team
 */
public final class CreateFeedbackCommandFixture {

    private CreateFeedbackCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 기본 CreateFeedbackCommand 생성 (RULE_EXAMPLE ADD)
     *
     * @return CreateFeedbackCommand
     */
    public static CreateFeedbackCommand defaultCommand() {
        return new CreateFeedbackCommand(
                "RULE_EXAMPLE",
                null,
                "ADD",
                "{\"code\": \"example\", \"description\": \"test example\"}");
    }

    /**
     * RULE_EXAMPLE 대상 ADD 피드백 생성
     *
     * @return CreateFeedbackCommand
     */
    public static CreateFeedbackCommand ruleExampleAddCommand() {
        return new CreateFeedbackCommand(
                "RULE_EXAMPLE",
                null,
                "ADD",
                "{\"code\": \"NEW-001\", \"description\": \"New rule example\"}");
    }

    /**
     * CLASS_TEMPLATE 대상 ADD 피드백 생성
     *
     * @return CreateFeedbackCommand
     */
    public static CreateFeedbackCommand classTemplateAddCommand() {
        return new CreateFeedbackCommand(
                "CLASS_TEMPLATE",
                null,
                "ADD",
                "{\"name\": \"NewTemplate\", \"content\": \"template content\"}");
    }

    /**
     * CODING_RULE 대상 ADD 피드백 생성
     *
     * @return CreateFeedbackCommand
     */
    public static CreateFeedbackCommand codingRuleAddCommand() {
        return new CreateFeedbackCommand(
                "CODING_RULE", null, "ADD", "{\"code\": \"DOM-999\", \"name\": \"New Rule\"}");
    }

    /**
     * CHECKLIST_ITEM 대상 ADD 피드백 생성
     *
     * @return CreateFeedbackCommand
     */
    public static CreateFeedbackCommand checklistItemAddCommand() {
        return new CreateFeedbackCommand(
                "CHECKLIST_ITEM", null, "ADD", "{\"item\": \"New checklist item\"}");
    }

    /**
     * ARCH_UNIT_TEST 대상 ADD 피드백 생성
     *
     * @return CreateFeedbackCommand
     */
    public static CreateFeedbackCommand archUnitTestAddCommand() {
        return new CreateFeedbackCommand(
                "ARCH_UNIT_TEST",
                null,
                "ADD",
                "{\"testName\": \"newArchTest\", \"testCode\": \"...\"}");
    }

    /**
     * MODIFY 타입 피드백 생성
     *
     * @param targetId 대상 ID
     * @return CreateFeedbackCommand
     */
    public static CreateFeedbackCommand modifyCommand(Long targetId) {
        return new CreateFeedbackCommand(
                "CLASS_TEMPLATE",
                targetId,
                "MODIFY",
                "{\"name\": \"UpdatedTemplate\", \"content\": \"modified content\"}");
    }

    /**
     * DELETE 타입 피드백 생성
     *
     * @param targetId 대상 ID
     * @return CreateFeedbackCommand
     */
    public static CreateFeedbackCommand deleteCommand(Long targetId) {
        return new CreateFeedbackCommand(
                "CHECKLIST_ITEM", targetId, "DELETE", "{\"reason\": \"obsolete item\"}");
    }

    /**
     * 특정 대상 타입과 피드백 타입으로 커맨드 생성
     *
     * @param targetType 대상 타입
     * @param targetId 대상 ID (nullable)
     * @param feedbackType 피드백 타입
     * @param payload JSON payload
     * @return CreateFeedbackCommand
     */
    public static CreateFeedbackCommand withParams(
            String targetType, Long targetId, String feedbackType, String payload) {
        return new CreateFeedbackCommand(targetType, targetId, feedbackType, payload);
    }
}

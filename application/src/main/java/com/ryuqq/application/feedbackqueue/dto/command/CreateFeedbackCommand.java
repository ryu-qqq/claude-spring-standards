package com.ryuqq.application.feedbackqueue.dto.command;

/**
 * CreateFeedbackCommand - 피드백 생성 커맨드
 *
 * <p>피드백 큐에 새로운 피드백을 생성하기 위한 커맨드입니다.
 *
 * @param targetType 피드백 대상 타입 (RULE_EXAMPLE, CLASS_TEMPLATE, CODING_RULE, CHECKLIST_ITEM,
 *     ARCH_UNIT_TEST)
 * @param targetId 대상 ID (수정/삭제 시 기존 ID, 신규 시 null)
 * @param feedbackType 피드백 유형 (ADD, MODIFY, DELETE)
 * @param payload JSON 형태의 피드백 내용
 * @author ryu-qqq
 */
public record CreateFeedbackCommand(
        String targetType, Long targetId, String feedbackType, String payload) {}

package com.ryuqq.application.feedbackqueue.internal.validator.payload;

import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;

/**
 * FeedbackPayloadValidator - 피드백 페이로드 검증 인터페이스
 *
 * <p>피드백 생성 시 입력 시점(Stage 1) 검증을 수행합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 구조가 타겟 타입에 맞는지
 *   <li>필수 필드가 존재하는지
 *   <li>부모 엔티티가 존재하는지 (ADD 시)
 *   <li>대상 엔티티가 존재하는지 (MODIFY/DELETE 시)
 * </ol>
 *
 * <p>검증 실패 시 {@link com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackPayloadException}을
 * 던집니다.
 *
 * @author ryu-qqq
 */
public interface FeedbackPayloadValidator {

    /**
     * 지원하는 타겟 타입 반환
     *
     * @return FeedbackTargetType
     */
    FeedbackTargetType supportedType();

    /**
     * 페이로드 검증 수행
     *
     * <p>검증 성공 시 아무 처리 없이 반환하고, 실패 시 예외를 던집니다.
     *
     * @param command 피드백 생성 커맨드
     * @throws com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackPayloadException 검증 실패 시
     */
    void validate(CreateFeedbackCommand command);
}

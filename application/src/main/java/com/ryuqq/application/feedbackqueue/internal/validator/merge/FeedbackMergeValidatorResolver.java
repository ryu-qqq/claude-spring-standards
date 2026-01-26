package com.ryuqq.application.feedbackqueue.internal.validator.merge;

import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * FeedbackMergeValidatorResolver - 피드백 병합 검증기 해결자
 *
 * <p>FeedbackTargetType에 맞는 FeedbackMergeValidator 구현체를 반환합니다.
 *
 * <p>Strategy Pattern + Map 기반 라우팅으로 O(1) 조회를 보장합니다.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackMergeValidatorResolver {

    private final Map<FeedbackTargetType, FeedbackMergeValidator> validatorMap;

    public FeedbackMergeValidatorResolver(List<FeedbackMergeValidator> validators) {
        this.validatorMap =
                validators.stream()
                        .collect(
                                Collectors.toMap(
                                        FeedbackMergeValidator::supportedType,
                                        Function.identity()));
    }

    /**
     * FeedbackTargetType에 맞는 검증기 반환
     *
     * @param targetType 피드백 타겟 타입
     * @return 해당 타입의 검증기
     * @throws IllegalArgumentException 지원하지 않는 타입인 경우
     */
    public FeedbackMergeValidator resolve(FeedbackTargetType targetType) {
        FeedbackMergeValidator validator = validatorMap.get(targetType);
        if (validator == null) {
            throw new IllegalArgumentException(
                    "No FeedbackMergeValidator found for target type: " + targetType);
        }
        return validator;
    }
}

package com.ryuqq.application.feedbackqueue.internal.validator.payload;

import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * FeedbackPayloadValidatorResolver - 피드백 페이로드 검증기 리졸버
 *
 * <p>FeedbackTargetType에 따라 적절한 검증기를 찾아 반환합니다.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackPayloadValidatorResolver {

    private final Map<FeedbackTargetType, FeedbackPayloadValidator> validatorMap;

    public FeedbackPayloadValidatorResolver(List<FeedbackPayloadValidator> validators) {
        this.validatorMap =
                validators.stream()
                        .collect(
                                Collectors.toMap(
                                        FeedbackPayloadValidator::supportedType,
                                        Function.identity()));
    }

    /**
     * 타겟 타입에 맞는 검증기 조회
     *
     * @param targetType 피드백 타겟 타입
     * @return 해당 타입의 검증기
     * @throws IllegalArgumentException 지원하지 않는 타입인 경우
     */
    public FeedbackPayloadValidator resolve(FeedbackTargetType targetType) {
        FeedbackPayloadValidator validator = validatorMap.get(targetType);
        if (validator == null) {
            throw new IllegalArgumentException(
                    "No FeedbackPayloadValidator found for target type: " + targetType);
        }
        return validator;
    }
}

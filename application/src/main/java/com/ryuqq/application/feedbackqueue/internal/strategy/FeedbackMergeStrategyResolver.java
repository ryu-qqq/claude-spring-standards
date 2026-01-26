package com.ryuqq.application.feedbackqueue.internal.strategy;

import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * FeedbackMergeStrategyResolver - 피드백 머지 전략 해결기
 *
 * <p>FeedbackTargetType에 맞는 FeedbackMergeStrategy를 찾아 반환합니다.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackMergeStrategyResolver {

    private final Map<FeedbackTargetType, FeedbackMergeStrategy> strategyMap;

    public FeedbackMergeStrategyResolver(List<FeedbackMergeStrategy> strategies) {
        this.strategyMap = new EnumMap<>(FeedbackTargetType.class);
        for (FeedbackMergeStrategy strategy : strategies) {
            this.strategyMap.put(strategy.supportedType(), strategy);
        }
    }

    /**
     * 타겟 타입에 맞는 전략 반환
     *
     * @param targetType 피드백 타겟 타입
     * @return 해당 타입의 머지 전략
     * @throws IllegalArgumentException 지원하지 않는 타입인 경우
     */
    public FeedbackMergeStrategy resolve(FeedbackTargetType targetType) {
        FeedbackMergeStrategy strategy = strategyMap.get(targetType);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No merge strategy found for target type: " + targetType);
        }
        return strategy;
    }
}

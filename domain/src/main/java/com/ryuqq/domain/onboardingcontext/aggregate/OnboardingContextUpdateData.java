package com.ryuqq.domain.onboardingcontext.aggregate;

import com.ryuqq.domain.onboardingcontext.vo.ContextContent;
import com.ryuqq.domain.onboardingcontext.vo.ContextTitle;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.onboardingcontext.vo.Priority;

/**
 * OnboardingContextUpdateData - 온보딩 컨텍스트 수정 데이터
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record OnboardingContextUpdateData(
        ContextType contextType, ContextTitle title, ContextContent content, Priority priority) {

    public OnboardingContextUpdateData {
        if (contextType == null) {
            throw new IllegalArgumentException("contextType must not be null");
        }
        if (title == null) {
            throw new IllegalArgumentException("title must not be null");
        }
        if (content == null) {
            content = ContextContent.empty();
        }
        if (priority == null) {
            priority = Priority.defaultPriority();
        }
    }
}

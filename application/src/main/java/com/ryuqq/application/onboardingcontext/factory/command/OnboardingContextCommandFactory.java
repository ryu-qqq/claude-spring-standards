package com.ryuqq.application.onboardingcontext.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.onboardingcontext.dto.command.CreateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.dto.command.UpdateOnboardingContextCommand;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContextUpdateData;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.onboardingcontext.vo.ContextContent;
import com.ryuqq.domain.onboardingcontext.vo.ContextTitle;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.onboardingcontext.vo.Priority;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * OnboardingContextCommandFactory - OnboardingContext Command → Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class OnboardingContextCommandFactory {

    private final TimeProvider timeProvider;

    public OnboardingContextCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateOnboardingContextCommand로부터 OnboardingContext 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return OnboardingContext 도메인 객체
     */
    public OnboardingContext create(CreateOnboardingContextCommand command) {
        Instant now = timeProvider.now();

        return OnboardingContext.forNew(
                TechStackId.of(command.techStackId()),
                ContextType.valueOf(command.contextType()),
                ContextTitle.of(command.title()),
                ContextContent.of(command.content()),
                Priority.of(command.priority()),
                now);
    }

    /**
     * UpdateOnboardingContextCommand로부터 OnboardingContextUpdateData 생성
     *
     * @param command 수정 Command
     * @return OnboardingContextUpdateData
     */
    public OnboardingContextUpdateData createUpdateData(UpdateOnboardingContextCommand command) {
        return new OnboardingContextUpdateData(
                ContextType.valueOf(command.contextType()),
                ContextTitle.of(command.title()),
                ContextContent.of(command.content()),
                Priority.of(command.priority()));
    }

    /**
     * UpdateOnboardingContextCommand로부터 OnboardingContextId와 OnboardingContextUpdateData 생성
     *
     * <p>업데이트에 필요한 ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<OnboardingContextId, OnboardingContextUpdateData> createUpdateContext(
            UpdateOnboardingContextCommand command) {
        OnboardingContextId id = OnboardingContextId.of(command.id());
        OnboardingContextUpdateData updateData = createUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}

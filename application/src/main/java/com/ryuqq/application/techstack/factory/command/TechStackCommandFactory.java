package com.ryuqq.application.techstack.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.techstack.dto.command.CreateTechStackCommand;
import com.ryuqq.application.techstack.dto.command.UpdateTechStackCommand;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.aggregate.TechStackUpdateData;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.vo.BuildConfigFile;
import com.ryuqq.domain.techstack.vo.BuildToolType;
import com.ryuqq.domain.techstack.vo.FrameworkModules;
import com.ryuqq.domain.techstack.vo.FrameworkType;
import com.ryuqq.domain.techstack.vo.FrameworkVersion;
import com.ryuqq.domain.techstack.vo.LanguageFeatures;
import com.ryuqq.domain.techstack.vo.LanguageType;
import com.ryuqq.domain.techstack.vo.LanguageVersion;
import com.ryuqq.domain.techstack.vo.PlatformType;
import com.ryuqq.domain.techstack.vo.RuntimeEnvironment;
import com.ryuqq.domain.techstack.vo.TechStackName;
import com.ryuqq.domain.techstack.vo.TechStackStatus;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * TechStackCommandFactory - TechStack Command → Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author ryu-qqq
 */
@Component
public class TechStackCommandFactory {

    private final TimeProvider timeProvider;

    public TechStackCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateTechStackCommand로부터 TechStack 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return TechStack 도메인 객체
     */
    public TechStack create(CreateTechStackCommand command) {
        Instant now = timeProvider.now();

        return TechStack.forNew(
                TechStackName.of(command.name()),
                LanguageType.valueOf(command.languageType()),
                LanguageVersion.of(command.languageVersion()),
                LanguageFeatures.of(command.languageFeatures()),
                FrameworkType.valueOf(command.frameworkType()),
                FrameworkVersion.of(command.frameworkVersion()),
                FrameworkModules.of(command.frameworkModules()),
                PlatformType.valueOf(command.platformType()),
                RuntimeEnvironment.valueOf(command.runtimeEnvironment()),
                BuildToolType.valueOf(command.buildToolType()),
                BuildConfigFile.of(command.buildConfigFile()),
                ReferenceLinks.of(command.referenceLinks()),
                now);
    }

    /**
     * UpdateTechStackCommand로부터 TechStackUpdateData 생성
     *
     * <p>내부에서만 사용되므로 private으로 선언합니다.
     *
     * @param command 수정 Command
     * @return TechStackUpdateData
     */
    private TechStackUpdateData createUpdateData(UpdateTechStackCommand command) {
        return new TechStackUpdateData(
                TechStackName.of(command.name()),
                TechStackStatus.valueOf(command.status()),
                LanguageType.valueOf(command.languageType()),
                LanguageVersion.of(command.languageVersion()),
                LanguageFeatures.of(command.languageFeatures()),
                FrameworkType.valueOf(command.frameworkType()),
                FrameworkVersion.of(command.frameworkVersion()),
                FrameworkModules.of(command.frameworkModules()),
                PlatformType.valueOf(command.platformType()),
                RuntimeEnvironment.valueOf(command.runtimeEnvironment()),
                BuildToolType.valueOf(command.buildToolType()),
                BuildConfigFile.of(command.buildConfigFile()),
                ReferenceLinks.of(command.referenceLinks()));
    }

    /**
     * UpdateTechStackCommand로부터 UpdateContext 생성
     *
     * <p>업데이트에 필요한 ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<TechStackId, TechStackUpdateData> createUpdateContext(
            UpdateTechStackCommand command) {
        TechStackId id = TechStackId.of(command.id());
        TechStackUpdateData updateData = createUpdateData(command);
        Instant changedAt = timeProvider.now();
        return new UpdateContext<>(id, updateData, changedAt);
    }
}

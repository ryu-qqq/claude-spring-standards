package com.ryuqq.application.classtemplate.factory.command;

import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplateUpdateData;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.NamingPattern;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.classtemplate.vo.TemplateDescription;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateCommandFactory - 클래스 템플릿 커맨드 팩토리
 *
 * <p>클래스 템플릿 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplateCommandFactory {

    private final TimeProvider timeProvider;

    public ClassTemplateCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateClassTemplateCommand로부터 ClassTemplate 도메인 객체 생성
     *
     * <p>FCT-002: Factory에서 TimeProvider 사용하여 시간 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 ClassTemplate 인스턴스
     */
    public ClassTemplate create(CreateClassTemplateCommand command) {
        return ClassTemplate.forNew(
                PackageStructureId.of(command.structureId()),
                ClassTypeId.of(command.classTypeId()),
                TemplateCode.of(command.templateCode()),
                NamingPattern.of(command.namingPattern()),
                command.description() != null
                        ? TemplateDescription.of(command.description())
                        : null,
                command.requiredAnnotations(),
                command.forbiddenAnnotations(),
                command.requiredInterfaces(),
                command.forbiddenInheritance(),
                command.requiredMethods(),
                timeProvider.now());
    }

    /**
     * UpdateClassTemplateCommand로부터 ClassTemplateUpdateData 생성
     *
     * @param command 수정 커맨드
     * @return ClassTemplateUpdateData
     */
    public ClassTemplateUpdateData toUpdateData(UpdateClassTemplateCommand command) {
        return new ClassTemplateUpdateData(
                command.classTypeId() != null ? ClassTypeId.of(command.classTypeId()) : null,
                command.templateCode() != null ? TemplateCode.of(command.templateCode()) : null,
                NamingPattern.of(command.namingPattern()),
                command.description() != null
                        ? TemplateDescription.of(command.description())
                        : null,
                command.requiredAnnotations(),
                command.forbiddenAnnotations(),
                command.requiredInterfaces(),
                command.forbiddenInheritance(),
                command.requiredMethods());
    }

    /**
     * UpdateClassTemplateCommand로부터 ClassTemplateId와 ClassTemplateUpdateData 생성
     *
     * <p>업데이트에 필요한 ID와 UpdateData를 한 번에 생성합니다.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<ClassTemplateId, ClassTemplateUpdateData> createUpdateContext(
            UpdateClassTemplateCommand command) {
        ClassTemplateId id = ClassTemplateId.of(command.classTemplateId());
        ClassTemplateUpdateData updateData = toUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}

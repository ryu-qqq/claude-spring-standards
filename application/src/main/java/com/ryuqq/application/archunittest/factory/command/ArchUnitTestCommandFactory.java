package com.ryuqq.application.archunittest.factory.command;

import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTestUpdateData;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestDescription;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestName;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSeverity;
import com.ryuqq.domain.archunittest.vo.TestCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestCommandFactory - ArchUnit 테스트 커맨드 팩토리
 *
 * <p>ArchUnit 테스트 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestCommandFactory {

    private final TimeProvider timeProvider;

    public ArchUnitTestCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateArchUnitTestCommand로부터 ArchUnitTest 도메인 객체 생성
     *
     * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 ArchUnitTest 인스턴스
     */
    public ArchUnitTest create(CreateArchUnitTestCommand command) {
        return create(command, timeProvider.now());
    }

    /**
     * CreateArchUnitTestCommand로부터 ArchUnitTest 도메인 객체 생성
     *
     * @param command 생성 커맨드
     * @param now 현재 시각
     * @return 새로운 ArchUnitTest 인스턴스
     */
    public ArchUnitTest create(CreateArchUnitTestCommand command, Instant now) {
        return ArchUnitTest.forNew(
                PackageStructureId.of(command.structureId()),
                command.code(),
                ArchUnitTestName.of(command.name()),
                command.description() != null
                        ? ArchUnitTestDescription.of(command.description())
                        : null,
                command.testClassName(),
                command.testMethodName(),
                TestCode.of(command.testCode()),
                command.severity() != null
                        ? ArchUnitTestSeverity.valueOf(command.severity())
                        : null,
                now);
    }

    /**
     * UpdateArchUnitTestCommand로부터 ArchUnitTestUpdateData 생성
     *
     * @param command 수정 커맨드
     * @return ArchUnitTestUpdateData
     */
    public ArchUnitTestUpdateData toUpdateData(UpdateArchUnitTestCommand command) {
        return ArchUnitTestUpdateData.builder()
                .code(command.code())
                .name(command.name() != null ? ArchUnitTestName.of(command.name()) : null)
                .description(
                        command.description() != null
                                ? ArchUnitTestDescription.of(command.description())
                                : null)
                .testClassName(command.testClassName())
                .testMethodName(command.testMethodName())
                .testCode(command.testCode() != null ? TestCode.of(command.testCode()) : null)
                .severity(
                        command.severity() != null
                                ? ArchUnitTestSeverity.valueOf(command.severity())
                                : null)
                .build();
    }

    /**
     * UpdateArchUnitTestCommand로부터 ArchUnitTestId와 ArchUnitTestUpdateData 생성
     *
     * <p>업데이트에 필요한 ID와 UpdateData를 한 번에 생성합니다.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData)
     */
    public UpdateContext<ArchUnitTestId, ArchUnitTestUpdateData> createUpdateContext(
            UpdateArchUnitTestCommand command) {
        ArchUnitTestId id = ArchUnitTestId.of(command.archUnitTestId());
        ArchUnitTestUpdateData updateData = toUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }

    /**
     * PackageStructureId 변환 (생성 커맨드용)
     *
     * @param command 생성 커맨드
     * @return PackageStructureId
     */
    public PackageStructureId toStructureId(CreateArchUnitTestCommand command) {
        return PackageStructureId.of(command.structureId());
    }

    /**
     * 테스트 코드 반환 (생성 커맨드용)
     *
     * @param command 생성 커맨드
     * @return 테스트 코드
     */
    public String toCode(CreateArchUnitTestCommand command) {
        return command.code();
    }

    /**
     * ArchUnitTestId 변환 (수정 커맨드용)
     *
     * @param command 수정 커맨드
     * @return ArchUnitTestId
     */
    public ArchUnitTestId toArchUnitTestId(UpdateArchUnitTestCommand command) {
        return ArchUnitTestId.of(command.archUnitTestId());
    }

    /**
     * 테스트 코드 반환 (수정 커맨드용)
     *
     * @param command 수정 커맨드
     * @return 테스트 코드 (nullable)
     */
    public String toCode(UpdateArchUnitTestCommand command) {
        return command.code();
    }
}

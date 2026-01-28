package com.ryuqq.application.packagestructure.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.packagestructure.dto.command.CreatePackageStructureCommand;
import com.ryuqq.application.packagestructure.dto.command.UpdatePackageStructureCommand;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructureUpdateData;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import org.springframework.stereotype.Component;

/**
 * PackageStructureCommandFactory - 패키지 구조 커맨드 팩토리
 *
 * <p>패키지 구조 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class PackageStructureCommandFactory {

    private final TimeProvider timeProvider;

    public PackageStructureCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreatePackageStructureCommand로부터 PackageStructure 도메인 객체 생성
     *
     * <p>FCT-002: Factory에서 TimeProvider 사용하여 시간 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 PackageStructure 인스턴스
     */
    public PackageStructure create(CreatePackageStructureCommand command) {
        return PackageStructure.forNew(
                ModuleId.of(command.moduleId()),
                PathPattern.of(command.pathPattern()),
                command.description(),
                timeProvider.now());
    }

    /**
     * UpdatePackageStructureCommand로부터 PackageStructureUpdateData 생성
     *
     * <p>요청으로 들어온 데이터를 기반으로 객체를 만들고, JPA의 더티체킹을 활용하여 변경사항을 처리합니다.
     *
     * @param command 수정 커맨드 (모든 필드 필수)
     * @return PackageStructureUpdateData
     */
    public PackageStructureUpdateData toUpdateData(UpdatePackageStructureCommand command) {
        return new PackageStructureUpdateData(
                PathPattern.of(command.pathPattern()), command.description());
    }

    /**
     * UpdatePackageStructureCommand로부터 PackageStructureId와 PackageStructureUpdateData 생성
     *
     * <p>업데이트에 필요한 ID와 UpdateData를 한 번에 생성합니다.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData)
     */
    public UpdateContext<PackageStructureId, PackageStructureUpdateData> createUpdateContext(
            UpdatePackageStructureCommand command) {
        PackageStructureId id = PackageStructureId.of(command.packageStructureId());
        PackageStructureUpdateData updateData = toUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}

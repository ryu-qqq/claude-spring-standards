package com.ryuqq.application.packagepurpose.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurposeUpdateData;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagepurpose.vo.PurposeName;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeCommandFactory - 패키지 목적 커맨드 팩토리
 *
 * <p>패키지 목적 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class PackagePurposeCommandFactory {

    private final TimeProvider timeProvider;

    public PackagePurposeCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreatePackagePurposeCommand로부터 PackagePurpose 도메인 객체 생성
     *
     * <p>FCT-002: Factory에서 TimeProvider 사용하여 시간 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 PackagePurpose 인스턴스
     */
    public PackagePurpose create(CreatePackagePurposeCommand command) {
        return PackagePurpose.forNew(
                PackageStructureId.of(command.structureId()),
                PurposeCode.of(command.code()),
                PurposeName.of(command.name()),
                command.description(),
                timeProvider.now());
    }

    /**
     * UpdatePackagePurposeCommand로부터 PackagePurposeUpdateData 생성
     *
     * <p>요청으로 들어온 데이터를 기반으로 객체를 만들고, JPA의 더티체킹을 활용하여 변경사항을 처리합니다.
     *
     * @param command 수정 커맨드 (모든 필드 필수)
     * @return PackagePurposeUpdateData
     */
    public PackagePurposeUpdateData toUpdateData(UpdatePackagePurposeCommand command) {
        return new PackagePurposeUpdateData(
                PurposeCode.of(command.code()),
                PurposeName.of(command.name()),
                command.description());
    }

    /**
     * UpdatePackagePurposeCommand로부터 PackagePurposeId와 PackagePurposeUpdateData 생성
     *
     * <p>업데이트에 필요한 ID와 UpdateData를 한 번에 생성합니다.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData)
     */
    public UpdateContext<PackagePurposeId, PackagePurposeUpdateData> createUpdateContext(
            UpdatePackagePurposeCommand command) {
        PackagePurposeId id = PackagePurposeId.of(command.packagePurposeId());
        PackagePurposeUpdateData updateData = toUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}

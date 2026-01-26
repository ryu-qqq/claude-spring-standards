package com.ryuqq.adapter.in.rest.packagestructure.mapper;

import com.ryuqq.adapter.in.rest.packagestructure.dto.request.CreatePackageStructureApiRequest;
import com.ryuqq.adapter.in.rest.packagestructure.dto.request.UpdatePackageStructureApiRequest;
import com.ryuqq.application.packagestructure.dto.command.CreatePackageStructureCommand;
import com.ryuqq.application.packagestructure.dto.command.UpdatePackageStructureCommand;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PackageStructureCommandApiMapper - PackageStructure Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAP-001: Mapper는 @Component로 등록.
 *
 * <p>MAP-002: Mapper에서 Static 메서드 금지.
 *
 * <p>MAP-004: Mapper는 필드 매핑만 수행.
 *
 * <p>MAP-006: Mapper에서 Domain 객체 직접 사용 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class PackageStructureCommandApiMapper {

    /**
     * CreatePackageStructureApiRequest -> CreatePackageStructureCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreatePackageStructureCommand toCommand(CreatePackageStructureApiRequest request) {
        return new CreatePackageStructureCommand(
                request.moduleId(),
                request.pathPattern(),
                nullSafeList(request.allowedClassTypes()),
                request.namingPattern(),
                request.namingSuffix(),
                request.description());
    }

    /**
     * UpdatePackageStructureApiRequest + PathVariable ID -> UpdatePackageStructureCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param packageStructureId PackageStructure ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdatePackageStructureCommand toCommand(
            Long packageStructureId, UpdatePackageStructureApiRequest request) {
        return new UpdatePackageStructureCommand(
                packageStructureId,
                request.pathPattern(),
                nullSafeList(request.allowedClassTypes()),
                request.namingPattern(),
                request.namingSuffix(),
                request.description());
    }

    private List<String> nullSafeList(List<String> list) {
        return list != null ? list : Collections.emptyList();
    }
}

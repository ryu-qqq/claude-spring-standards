package com.ryuqq.adapter.in.rest.packagepurpose.mapper;

import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.CreatePackagePurposeApiRequest;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.UpdatePackagePurposeApiRequest;
import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeCommandApiMapper - PackagePurpose Command API 변환 매퍼
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
public class PackagePurposeCommandApiMapper {

    /**
     * CreatePackagePurposeApiRequest -> CreatePackagePurposeCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreatePackagePurposeCommand toCommand(CreatePackagePurposeApiRequest request) {
        List<String> allowedClassTypes =
                Objects.requireNonNullElse(request.defaultAllowedClassTypes(), List.of());

        return new CreatePackagePurposeCommand(
                request.structureId(),
                request.code(),
                request.name(),
                request.description(),
                allowedClassTypes,
                request.defaultNamingPattern(),
                request.defaultNamingSuffix());
    }

    /**
     * UpdatePackagePurposeApiRequest + PathVariable ID -> UpdatePackagePurposeCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param packagePurposeId PackagePurpose ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdatePackagePurposeCommand toCommand(
            Long packagePurposeId, UpdatePackagePurposeApiRequest request) {
        List<String> allowedClassTypes =
                Objects.requireNonNullElse(request.defaultAllowedClassTypes(), List.of());

        return new UpdatePackagePurposeCommand(
                packagePurposeId,
                request.code(),
                request.name(),
                request.description(),
                allowedClassTypes,
                request.defaultNamingPattern(),
                request.defaultNamingSuffix());
    }
}

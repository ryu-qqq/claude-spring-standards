package com.ryuqq.adapter.in.rest.classtypecategory.mapper;

import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.CreateClassTypeCategoryApiRequest;
import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.UpdateClassTypeCategoryApiRequest;
import com.ryuqq.application.classtypecategory.dto.command.CreateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.dto.command.UpdateClassTypeCategoryCommand;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryCommandApiMapper - ClassTypeCategory Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-002: API Request -> Application Command 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeCategoryCommandApiMapper {

    /**
     * CreateClassTypeCategoryApiRequest -> CreateClassTypeCategoryCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateClassTypeCategoryCommand toCommand(CreateClassTypeCategoryApiRequest request) {
        return new CreateClassTypeCategoryCommand(
                request.architectureId(),
                request.code(),
                request.name(),
                request.description(),
                request.orderIndex());
    }

    /**
     * UpdateClassTypeCategoryApiRequest + PathVariable ID -> UpdateClassTypeCategoryCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param id ClassTypeCategory ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateClassTypeCategoryCommand toCommand(
            Long id, UpdateClassTypeCategoryApiRequest request) {
        return new UpdateClassTypeCategoryCommand(
                id, request.code(), request.name(), request.description(), request.orderIndex());
    }
}

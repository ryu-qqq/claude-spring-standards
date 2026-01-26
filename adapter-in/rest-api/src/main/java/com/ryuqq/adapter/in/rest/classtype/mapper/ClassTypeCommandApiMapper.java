package com.ryuqq.adapter.in.rest.classtype.mapper;

import com.ryuqq.adapter.in.rest.classtype.dto.request.CreateClassTypeApiRequest;
import com.ryuqq.adapter.in.rest.classtype.dto.request.UpdateClassTypeApiRequest;
import com.ryuqq.application.classtype.dto.command.CreateClassTypeCommand;
import com.ryuqq.application.classtype.dto.command.UpdateClassTypeCommand;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCommandApiMapper - ClassType Command API 변환 매퍼
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
public class ClassTypeCommandApiMapper {

    /**
     * CreateClassTypeApiRequest -> CreateClassTypeCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateClassTypeCommand toCommand(CreateClassTypeApiRequest request) {
        return new CreateClassTypeCommand(
                request.categoryId(),
                request.code(),
                request.name(),
                request.description(),
                request.orderIndex());
    }

    /**
     * UpdateClassTypeApiRequest + PathVariable ID -> UpdateClassTypeCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param id ClassType ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateClassTypeCommand toCommand(Long id, UpdateClassTypeApiRequest request) {
        return new UpdateClassTypeCommand(
                id, request.code(), request.name(), request.description(), request.orderIndex());
    }
}

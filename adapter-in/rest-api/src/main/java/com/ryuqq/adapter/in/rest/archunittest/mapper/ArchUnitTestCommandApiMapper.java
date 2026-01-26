package com.ryuqq.adapter.in.rest.archunittest.mapper;

import com.ryuqq.adapter.in.rest.archunittest.dto.request.CreateArchUnitTestApiRequest;
import com.ryuqq.adapter.in.rest.archunittest.dto.request.UpdateArchUnitTestApiRequest;
import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestCommandApiMapper - ArchUnitTest Command API 변환 매퍼
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
public class ArchUnitTestCommandApiMapper {

    /**
     * CreateArchUnitTestApiRequest -> CreateArchUnitTestCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateArchUnitTestCommand toCommand(CreateArchUnitTestApiRequest request) {
        return new CreateArchUnitTestCommand(
                request.structureId(),
                request.code(),
                request.name(),
                request.description(),
                request.testClassName(),
                request.testMethodName(),
                request.testCode(),
                request.severity());
    }

    /**
     * UpdateArchUnitTestApiRequest + PathVariable ID -> UpdateArchUnitTestCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param archUnitTestId ArchUnitTest ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateArchUnitTestCommand toCommand(
            Long archUnitTestId, UpdateArchUnitTestApiRequest request) {
        return new UpdateArchUnitTestCommand(
                archUnitTestId,
                request.code(),
                request.name(),
                request.description(),
                request.testClassName(),
                request.testMethodName(),
                request.testCode(),
                request.severity());
    }
}

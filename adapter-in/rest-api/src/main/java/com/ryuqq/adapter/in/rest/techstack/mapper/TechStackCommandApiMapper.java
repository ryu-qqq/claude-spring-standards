package com.ryuqq.adapter.in.rest.techstack.mapper;

import com.ryuqq.adapter.in.rest.techstack.dto.request.CreateTechStackApiRequest;
import com.ryuqq.adapter.in.rest.techstack.dto.request.UpdateTechStackApiRequest;
import com.ryuqq.application.techstack.dto.command.CreateTechStackCommand;
import com.ryuqq.application.techstack.dto.command.UpdateTechStackCommand;
import org.springframework.stereotype.Component;

/**
 * TechStackCommandApiMapper - TechStack Command API 변환 매퍼
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
public class TechStackCommandApiMapper {

    /**
     * CreateTechStackApiRequest -> CreateTechStackCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateTechStackCommand toCommand(CreateTechStackApiRequest request) {
        return new CreateTechStackCommand(
                request.name(),
                request.languageType(),
                request.languageVersion(),
                request.languageFeatures(),
                request.frameworkType(),
                request.frameworkVersion(),
                request.frameworkModules(),
                request.platformType(),
                request.runtimeEnvironment(),
                request.buildToolType(),
                request.buildConfigFile(),
                request.referenceLinks());
    }

    /**
     * UpdateTechStackApiRequest + PathVariable ID -> UpdateTechStackCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param id TechStack ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateTechStackCommand toCommand(Long id, UpdateTechStackApiRequest request) {
        return new UpdateTechStackCommand(
                id,
                request.name(),
                request.status(),
                request.languageType(),
                request.languageVersion(),
                request.languageFeatures(),
                request.frameworkType(),
                request.frameworkVersion(),
                request.frameworkModules(),
                request.platformType(),
                request.runtimeEnvironment(),
                request.buildToolType(),
                request.buildConfigFile(),
                request.referenceLinks());
    }
}

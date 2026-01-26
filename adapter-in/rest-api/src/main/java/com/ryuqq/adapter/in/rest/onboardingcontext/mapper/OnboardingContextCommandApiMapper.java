package com.ryuqq.adapter.in.rest.onboardingcontext.mapper;

import com.ryuqq.adapter.in.rest.onboardingcontext.dto.request.CreateOnboardingContextApiRequest;
import com.ryuqq.adapter.in.rest.onboardingcontext.dto.request.UpdateOnboardingContextApiRequest;
import com.ryuqq.application.onboardingcontext.dto.command.CreateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.dto.command.UpdateOnboardingContextCommand;
import org.springframework.stereotype.Component;

/**
 * OnboardingContextCommandApiMapper - OnboardingContext Command API 변환 매퍼
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
public class OnboardingContextCommandApiMapper {

    /**
     * CreateOnboardingContextApiRequest -> CreateOnboardingContextCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateOnboardingContextCommand toCommand(CreateOnboardingContextApiRequest request) {
        return new CreateOnboardingContextCommand(
                request.techStackId(),
                request.architectureId(),
                request.contextType(),
                request.title(),
                request.content(),
                request.priority());
    }

    /**
     * UpdateOnboardingContextApiRequest + PathVariable ID -> UpdateOnboardingContextCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param id OnboardingContext ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateOnboardingContextCommand toCommand(
            Long id, UpdateOnboardingContextApiRequest request) {
        return new UpdateOnboardingContextCommand(
                id, request.contextType(), request.title(), request.content(), request.priority());
    }
}

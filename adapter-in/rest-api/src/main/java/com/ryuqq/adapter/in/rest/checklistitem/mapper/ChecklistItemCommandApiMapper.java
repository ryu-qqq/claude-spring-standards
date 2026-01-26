package com.ryuqq.adapter.in.rest.checklistitem.mapper;

import com.ryuqq.adapter.in.rest.checklistitem.dto.request.CreateChecklistItemApiRequest;
import com.ryuqq.adapter.in.rest.checklistitem.dto.request.UpdateChecklistItemApiRequest;
import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemCommandApiMapper - ChecklistItem Command API 변환 매퍼
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
 * <p>MAP-013: Mapper CQRS 분리 권장.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ChecklistItemCommandApiMapper {

    /**
     * CreateChecklistItemApiRequest -> CreateChecklistItemCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateChecklistItemCommand toCommand(CreateChecklistItemApiRequest request) {
        return new CreateChecklistItemCommand(
                request.ruleId(),
                request.sequenceOrder(),
                request.checkDescription(),
                request.checkType(),
                nullSafeString(request.automationTool()),
                nullSafeString(request.automationRuleId()),
                nullSafeBoolean(request.isCritical()));
    }

    /**
     * UpdateChecklistItemApiRequest + PathVariable ID -> UpdateChecklistItemCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param id ChecklistItem ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateChecklistItemCommand toCommand(Long id, UpdateChecklistItemApiRequest request) {
        return new UpdateChecklistItemCommand(
                id,
                request.sequenceOrder(),
                request.checkDescription(),
                request.checkType(),
                request.automationTool(),
                request.automationRuleId(),
                request.isCritical());
    }

    private String nullSafeString(String value) {
        return value != null ? value : "";
    }

    private boolean nullSafeBoolean(Boolean value) {
        return value != null && value;
    }
}

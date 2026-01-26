package com.ryuqq.adapter.in.rest.ruleexample.mapper;

import com.ryuqq.adapter.in.rest.ruleexample.dto.request.CreateRuleExampleApiRequest;
import com.ryuqq.adapter.in.rest.ruleexample.dto.request.UpdateRuleExampleApiRequest;
import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RuleExampleCommandApiMapper - RuleExample Command API 변환 매퍼
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
public class RuleExampleCommandApiMapper {

    /**
     * CreateRuleExampleApiRequest -> CreateRuleExampleCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateRuleExampleCommand toCommand(CreateRuleExampleApiRequest request) {
        return new CreateRuleExampleCommand(
                request.ruleId(),
                request.exampleType(),
                request.code(),
                request.language(),
                request.explanation(),
                nullSafeList(request.highlightLines()));
    }

    /**
     * UpdateRuleExampleApiRequest + PathVariable ID -> UpdateRuleExampleCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param ruleExampleId RuleExample ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateRuleExampleCommand toCommand(
            Long ruleExampleId, UpdateRuleExampleApiRequest request) {
        return new UpdateRuleExampleCommand(
                ruleExampleId,
                request.exampleType(),
                request.code(),
                request.language(),
                request.explanation(),
                nullSafeList(request.highlightLines()));
    }

    private List<Integer> nullSafeList(List<Integer> list) {
        return list != null ? list : Collections.emptyList();
    }
}

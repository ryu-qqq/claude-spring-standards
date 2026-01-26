package com.ryuqq.application.ruleexample.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.aggregate.RuleExampleUpdateData;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.vo.ExampleCode;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import com.ryuqq.domain.ruleexample.vo.HighlightLines;
import org.springframework.stereotype.Component;

/**
 * RuleExampleCommandFactory - 규칙 예시 커맨드 팩토리
 *
 * <p>규칙 예시 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleCommandFactory {

    private final TimeProvider timeProvider;

    public RuleExampleCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateRuleExampleCommand로부터 RuleExample 도메인 객체 생성
     *
     * <p>FCT-002: Factory에서 TimeProvider 사용하여 시간 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 RuleExample 인스턴스
     */
    public RuleExample create(CreateRuleExampleCommand command) {
        return RuleExample.forNew(
                CodingRuleId.of(command.ruleId()),
                ExampleType.valueOf(command.exampleType()),
                ExampleCode.of(command.code()),
                ExampleLanguage.valueOf(command.language()),
                command.explanation(),
                command.highlightLines() != null
                        ? HighlightLines.of(command.highlightLines())
                        : HighlightLines.empty(),
                timeProvider.now());
    }

    /**
     * UpdateRuleExampleCommand로부터 RuleExampleUpdateData 생성
     *
     * <p>요청으로 들어온 데이터를 기반으로 객체를 만들고, JPA의 더티체킹을 활용하여 변경사항을 처리합니다.
     *
     * @param command 수정 커맨드
     * @return RuleExampleUpdateData
     */
    public RuleExampleUpdateData toUpdateData(UpdateRuleExampleCommand command) {
        return RuleExampleUpdateData.builder()
                .exampleType(
                        command.exampleType() != null
                                ? ExampleType.valueOf(command.exampleType())
                                : null)
                .code(command.code() != null ? ExampleCode.of(command.code()) : null)
                .language(
                        command.language() != null
                                ? ExampleLanguage.valueOf(command.language())
                                : null)
                .explanation(command.explanation())
                .highlightLines(
                        command.highlightLines() != null
                                ? HighlightLines.of(command.highlightLines())
                                : null)
                .build();
    }

    /**
     * UpdateRuleExampleCommand로부터 RuleExampleId와 RuleExampleUpdateData 생성
     *
     * <p>업데이트에 필요한 ID와 UpdateData를 한 번에 생성합니다.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData)
     */
    public UpdateContext<RuleExampleId, RuleExampleUpdateData> createUpdateContext(
            UpdateRuleExampleCommand command) {
        RuleExampleId id = RuleExampleId.of(command.ruleExampleId());
        RuleExampleUpdateData updateData = toUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}

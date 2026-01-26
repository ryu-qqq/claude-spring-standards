package com.ryuqq.domain.ruleexample.aggregate;

import com.ryuqq.domain.ruleexample.vo.ExampleCode;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import com.ryuqq.domain.ruleexample.vo.HighlightLines;
import java.util.Optional;

/**
 * RuleExampleUpdateData - 규칙 예시 수정 데이터 Value Object
 *
 * <p>규칙 예시 수정에 필요한 데이터를 전달합니다.
 *
 * <p>요청으로 들어온 데이터를 기반으로 객체를 만들고, JPA의 더티체킹을 활용하여 변경사항을 처리합니다.
 *
 * <p>Optional 필드는 null인 경우 해당 필드를 업데이트하지 않음을 의미합니다.
 *
 * @param exampleType 예시 타입 (optional)
 * @param code 예시 코드 (optional)
 * @param language 언어 (optional)
 * @param explanation 설명 (optional)
 * @param highlightLines 하이라이트 라인 (optional)
 * @author ryu-qqq
 */
public record RuleExampleUpdateData(
        Optional<ExampleType> exampleType,
        Optional<ExampleCode> code,
        Optional<ExampleLanguage> language,
        Optional<String> explanation,
        Optional<HighlightLines> highlightLines) {

    public RuleExampleUpdateData {
        if (exampleType == null) {
            exampleType = Optional.empty();
        }
        if (code == null) {
            code = Optional.empty();
        }
        if (language == null) {
            language = Optional.empty();
        }
        if (explanation == null) {
            explanation = Optional.empty();
        }
        if (highlightLines == null) {
            highlightLines = Optional.empty();
        }
    }

    /**
     * 빈 업데이트 데이터 생성
     *
     * @return 빈 RuleExampleUpdateData
     */
    public static RuleExampleUpdateData empty() {
        return new RuleExampleUpdateData(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
    }

    /**
     * Builder 스타일 생성 메서드
     *
     * @return RuleExampleUpdateDataBuilder
     */
    public static RuleExampleUpdateDataBuilder builder() {
        return new RuleExampleUpdateDataBuilder();
    }

    /**
     * 업데이트할 내용이 있는지 확인
     *
     * @return 하나 이상의 필드가 present이면 true
     */
    public boolean hasUpdates() {
        return exampleType.isPresent()
                || code.isPresent()
                || language.isPresent()
                || explanation.isPresent()
                || highlightLines.isPresent();
    }

    /** RuleExampleUpdateData Builder */
    public static class RuleExampleUpdateDataBuilder {

        private Optional<ExampleType> exampleType = Optional.empty();
        private Optional<ExampleCode> code = Optional.empty();
        private Optional<ExampleLanguage> language = Optional.empty();
        private Optional<String> explanation = Optional.empty();
        private Optional<HighlightLines> highlightLines = Optional.empty();

        private RuleExampleUpdateDataBuilder() {}

        public RuleExampleUpdateDataBuilder exampleType(ExampleType exampleType) {
            this.exampleType = Optional.ofNullable(exampleType);
            return this;
        }

        public RuleExampleUpdateDataBuilder code(ExampleCode code) {
            this.code = Optional.ofNullable(code);
            return this;
        }

        public RuleExampleUpdateDataBuilder language(ExampleLanguage language) {
            this.language = Optional.ofNullable(language);
            return this;
        }

        public RuleExampleUpdateDataBuilder explanation(String explanation) {
            this.explanation = Optional.ofNullable(explanation);
            return this;
        }

        public RuleExampleUpdateDataBuilder highlightLines(HighlightLines highlightLines) {
            this.highlightLines = Optional.ofNullable(highlightLines);
            return this;
        }

        public RuleExampleUpdateData build() {
            return new RuleExampleUpdateData(
                    exampleType, code, language, explanation, highlightLines);
        }
    }
}

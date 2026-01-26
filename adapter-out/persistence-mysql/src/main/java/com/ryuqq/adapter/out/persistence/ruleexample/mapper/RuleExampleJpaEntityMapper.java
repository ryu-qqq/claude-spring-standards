package com.ryuqq.adapter.out.persistence.ruleexample.mapper;

import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.vo.ExampleCode;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleSource;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import com.ryuqq.domain.ruleexample.vo.HighlightLines;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RuleExampleJpaEntityMapper - RuleExample Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * <p>EMAP-002: Pure Java만 사용 (Lombok/MapStruct 금지)
 *
 * <p>EMAP-003: 시간 필드 생성 금지 (Instant.now() 금지)
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleJpaEntityMapper {

    private static final String LIST_DELIMITER = ",";

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public RuleExampleJpaEntityMapper() {}

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티 (null 허용)
     * @return RuleExample 도메인 객체, 입력이 null이면 null 반환
     */
    public RuleExample toDomain(RuleExampleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return RuleExample.reconstitute(
                RuleExampleId.of(entity.getId()),
                CodingRuleId.of(entity.getRuleId()),
                mapExampleType(entity.getExampleType()),
                ExampleCode.of(entity.getCode()),
                mapExampleLanguage(entity.getLanguage()),
                entity.getExplanation(),
                parseHighlightLines(entity.getHighlightLines()),
                mapExampleSource(entity.getSource()),
                entity.getFeedbackId(),
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain -> JPA Entity 변환
     *
     * <p>EMAP-004: toEntity(Domain) 메서드 필수
     *
     * <p>EMAP-006: Entity.of() 호출
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain RuleExample 도메인 객체 (null 허용)
     * @return JPA 엔티티, 입력이 null이면 null 반환
     */
    public RuleExampleJpaEntity toEntity(RuleExample domain) {
        if (domain == null) {
            return null;
        }
        return RuleExampleJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.ruleIdValue(),
                domain.exampleTypeName(),
                domain.codeValue(),
                domain.languageName(),
                domain.explanation(),
                highlightLinesToJson(domain.highlightLines()),
                domain.sourceName(),
                domain.feedbackId(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * ExampleType 문자열 -> Enum 변환
     *
     * @param exampleType 예시 타입 문자열
     * @return ExampleType enum
     */
    private ExampleType mapExampleType(String exampleType) {
        if (exampleType == null || exampleType.isBlank()) {
            throw new IllegalArgumentException("ExampleType must not be null or blank");
        }
        return ExampleType.valueOf(exampleType);
    }

    /**
     * ExampleLanguage 문자열 -> Enum 변환
     *
     * @param language 언어 문자열
     * @return ExampleLanguage enum
     */
    private ExampleLanguage mapExampleLanguage(String language) {
        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException("ExampleLanguage must not be null or blank");
        }
        return ExampleLanguage.valueOf(language);
    }

    /**
     * ExampleSource 문자열 -> Enum 변환
     *
     * @param source 소스 문자열 (nullable)
     * @return ExampleSource enum 또는 MANUAL
     */
    private ExampleSource mapExampleSource(String source) {
        if (source == null || source.isBlank()) {
            return ExampleSource.MANUAL;
        }
        return ExampleSource.valueOf(source);
    }

    /**
     * JSON 문자열 -> HighlightLines 변환
     *
     * <p>JSON 배열 형식 또는 쉼표 구분 문자열을 HighlightLines로 변환합니다.
     *
     * @param json JSON 문자열 (nullable)
     * @return HighlightLines
     */
    private HighlightLines parseHighlightLines(String json) {
        if (json == null || json.isBlank()) {
            return HighlightLines.empty();
        }
        List<Integer> lines = parseJsonToIntList(json);
        return HighlightLines.of(lines);
    }

    /**
     * JSON 문자열 -> List<Integer> 변환
     *
     * @param json JSON 문자열 (nullable)
     * @return List<Integer>
     */
    private List<Integer> parseJsonToIntList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        // JSON 배열 형식 처리: [1, 2, 3]
        String cleaned = json.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        if (cleaned.isBlank()) {
            return Collections.emptyList();
        }
        // 쉼표로 분리
        return Arrays.stream(cleaned.split(LIST_DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .toList();
    }

    /**
     * HighlightLines -> JSON 문자열 변환
     *
     * @param highlightLines HighlightLines
     * @return JSON 배열 문자열 또는 null
     */
    private String highlightLinesToJson(HighlightLines highlightLines) {
        if (highlightLines == null || highlightLines.isEmpty()) {
            return null;
        }
        List<Integer> lines = highlightLines.lines();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(lines.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Entity의 삭제 상태 -> DeletionStatus 변환
     *
     * <p>EMAP-008: Null 안전 처리
     *
     * @param entity JPA 엔티티
     * @return DeletionStatus 객체
     */
    private DeletionStatus mapDeletionStatus(RuleExampleJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}

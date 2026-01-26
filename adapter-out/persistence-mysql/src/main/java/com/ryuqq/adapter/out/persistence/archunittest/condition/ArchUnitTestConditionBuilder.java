package com.ryuqq.adapter.out.persistence.archunittest.condition;

import static com.ryuqq.adapter.out.persistence.archunittest.entity.QArchUnitTestJpaEntity.archUnitTestJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestConditionBuilder - ArchUnit 테스트 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ArchUnitTestConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return archUnitTestJpaEntity.deletedAt.isNull();
    }

    /**
     * ID 일치 조건
     *
     * @param id ArchUnit 테스트 ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? archUnitTestJpaEntity.id.eq(id) : null;
    }

    /**
     * 패키지 구조 ID 일치 조건
     *
     * @param structureId 패키지 구조 ID
     * @return structureId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression structureIdEq(Long structureId) {
        return structureId != null ? archUnitTestJpaEntity.structureId.eq(structureId) : null;
    }

    public BooleanExpression structureIdsIn(List<Long> structureIds) {
        return structureIds != null && !structureIds.isEmpty()
                ? archUnitTestJpaEntity.structureId.in(structureIds)
                : null;
    }

    /**
     * 커서 기반 페이징 조건
     *
     * @param cursor 커서 (마지막 ID)
     * @return ID < cursor 조건 (nullable이면 null 반환)
     */
    public BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? archUnitTestJpaEntity.id.lt(cursor) : null;
    }

    /**
     * 테스트 코드 일치 조건
     *
     * @param code 테스트 코드
     * @return code 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression codeEq(String code) {
        return code != null ? archUnitTestJpaEntity.code.eq(code) : null;
    }

    /**
     * 심각도 일치 조건
     *
     * @param severity 심각도
     * @return severity 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression severityEq(String severity) {
        return severity != null ? archUnitTestJpaEntity.severity.eq(severity) : null;
    }

    public BooleanExpression severitiesIn(List<String> severities) {
        return severities != null && !severities.isEmpty()
                ? archUnitTestJpaEntity.severity.in(severities)
                : null;
    }

    public BooleanExpression searchContains(
            ArchUnitTestSearchField searchField, String searchWord) {
        if (searchField == null || searchWord == null || searchWord.isBlank()) {
            return null;
        }
        return switch (searchField) {
            case CODE -> archUnitTestJpaEntity.code.containsIgnoreCase(searchWord);
            case NAME -> archUnitTestJpaEntity.name.containsIgnoreCase(searchWord);
            case DESCRIPTION -> archUnitTestJpaEntity.description.containsIgnoreCase(searchWord);
            case TEST_CLASS_NAME ->
                    archUnitTestJpaEntity.testClassName.containsIgnoreCase(searchWord);
            case TEST_METHOD_NAME ->
                    archUnitTestJpaEntity.testMethodName.containsIgnoreCase(searchWord);
        };
    }

    /**
     * ID 불일치 조건 (특정 ID 제외)
     *
     * @param excludeId 제외할 ID
     * @return ID 불일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? archUnitTestJpaEntity.id.ne(excludeId) : null;
    }
}

package com.ryuqq.domain.classtype.fixture;

import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtype.vo.ClassTypeName;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * ClassType Aggregate Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ClassTypeFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private ClassTypeFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 신규 생성용 ClassType (AGGREGATE) */
    public static ClassType defaultNewClassType() {
        return ClassType.forNew(
                ClassTypeCategoryId.of(1L),
                ClassTypeCode.of("AGGREGATE"),
                ClassTypeName.of("Aggregate Root"),
                "도메인 Aggregate Root 클래스",
                1,
                FIXED_CLOCK.instant());
    }

    /** 기존 저장된 ClassType (AGGREGATE) */
    public static ClassType defaultExistingClassType() {
        Instant now = FIXED_CLOCK.instant();
        return ClassType.of(
                ClassTypeId.of(1L),
                ClassTypeCategoryId.of(1L),
                ClassTypeCode.of("AGGREGATE"),
                ClassTypeName.of("Aggregate Root"),
                "도메인 Aggregate Root 클래스",
                1,
                DeletionStatus.active(),
                now,
                now);
    }

    /** VALUE_OBJECT ClassType */
    public static ClassType valueObjectClassType() {
        Instant now = FIXED_CLOCK.instant();
        return ClassType.of(
                ClassTypeId.of(2L),
                ClassTypeCategoryId.of(1L),
                ClassTypeCode.of("VALUE_OBJECT"),
                ClassTypeName.of("Value Object"),
                "도메인 Value Object 클래스",
                2,
                DeletionStatus.active(),
                now,
                now);
    }

    /** USE_CASE ClassType */
    public static ClassType useCaseClassType() {
        Instant now = FIXED_CLOCK.instant();
        return ClassType.of(
                ClassTypeId.of(3L),
                ClassTypeCategoryId.of(2L),
                ClassTypeCode.of("USE_CASE"),
                ClassTypeName.of("Use Case"),
                "어플리케이션 Use Case 클래스",
                1,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 특정 ID로 ClassType 생성 */
    public static ClassType classTypeWithId(Long id) {
        Instant now = FIXED_CLOCK.instant();
        return ClassType.of(
                ClassTypeId.of(id),
                ClassTypeCategoryId.of(1L),
                ClassTypeCode.of("TYPE_" + id),
                ClassTypeName.of("타입 " + id),
                "테스트 클래스 타입",
                id.intValue(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 특정 카테고리의 ClassType 생성 */
    public static ClassType classTypeForCategory(Long categoryId) {
        Instant now = FIXED_CLOCK.instant();
        return ClassType.of(
                ClassTypeId.of(10L),
                ClassTypeCategoryId.of(categoryId),
                ClassTypeCode.of("CATEGORY_TYPE"),
                ClassTypeName.of("카테고리 타입"),
                "특정 카테고리용 클래스 타입",
                1,
                DeletionStatus.active(),
                now,
                now);
    }
}

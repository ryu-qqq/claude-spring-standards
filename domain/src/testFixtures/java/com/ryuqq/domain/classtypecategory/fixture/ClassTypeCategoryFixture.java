package com.ryuqq.domain.classtypecategory.fixture;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import com.ryuqq.domain.classtypecategory.vo.CategoryName;
import com.ryuqq.domain.common.vo.DeletionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * ClassTypeCategory Aggregate Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ClassTypeCategoryFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private ClassTypeCategoryFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 신규 생성용 카테고리 */
    public static ClassTypeCategory defaultNewCategory() {
        return ClassTypeCategory.forNew(
                ArchitectureId.of(1L),
                CategoryCode.of("DOMAIN"),
                CategoryName.of("도메인 레이어"),
                "도메인 레이어 클래스 타입 카테고리",
                1,
                FIXED_CLOCK.instant());
    }

    /** 기존 저장된 카테고리 */
    public static ClassTypeCategory defaultExistingCategory() {
        Instant now = FIXED_CLOCK.instant();
        return ClassTypeCategory.of(
                ClassTypeCategoryId.of(1L),
                ArchitectureId.of(1L),
                CategoryCode.of("DOMAIN"),
                CategoryName.of("도메인 레이어"),
                "도메인 레이어 클래스 타입 카테고리",
                1,
                DeletionStatus.active(),
                now,
                now);
    }

    /** APPLICATION 카테고리 */
    public static ClassTypeCategory applicationCategory() {
        Instant now = FIXED_CLOCK.instant();
        return ClassTypeCategory.of(
                ClassTypeCategoryId.of(2L),
                ArchitectureId.of(1L),
                CategoryCode.of("APPLICATION"),
                CategoryName.of("어플리케이션 레이어"),
                "어플리케이션 레이어 클래스 타입 카테고리",
                2,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 특정 ID로 카테고리 생성 */
    public static ClassTypeCategory categoryWithId(Long id) {
        Instant now = FIXED_CLOCK.instant();
        return ClassTypeCategory.of(
                ClassTypeCategoryId.of(id),
                ArchitectureId.of(1L),
                CategoryCode.of("CATEGORY_" + id),
                CategoryName.of("카테고리 " + id),
                "테스트 카테고리",
                id.intValue(),
                DeletionStatus.active(),
                now,
                now);
    }
}

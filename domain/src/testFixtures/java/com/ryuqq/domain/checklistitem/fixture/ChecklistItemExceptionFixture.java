package com.ryuqq.domain.checklistitem.fixture;

import com.ryuqq.domain.checklistitem.exception.ChecklistItemNotFoundException;

/**
 * ChecklistItem Exception Test Fixture
 *
 * <p>Domain 예외 테스트에서 사용하는 Exception 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ChecklistItemExceptionFixture {

    private ChecklistItemExceptionFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** ChecklistItemNotFoundException - 기본 케이스 */
    public static ChecklistItemNotFoundException notFound() {
        return new ChecklistItemNotFoundException(1L);
    }

    /** ChecklistItemNotFoundException - 커스텀 ID */
    public static ChecklistItemNotFoundException notFound(Long checklistItemId) {
        return new ChecklistItemNotFoundException(checklistItemId);
    }
}

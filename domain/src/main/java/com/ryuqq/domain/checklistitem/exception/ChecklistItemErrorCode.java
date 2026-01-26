package com.ryuqq.domain.checklistitem.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ChecklistItemErrorCode - 체크리스트 항목 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum ChecklistItemErrorCode implements ErrorCode {
    CHECKLIST_ITEM_NOT_FOUND("CHECKLIST_ITEM-001", 404, "ChecklistItem not found");

    private final String code;
    private final int httpStatus;
    private final String message;

    ChecklistItemErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

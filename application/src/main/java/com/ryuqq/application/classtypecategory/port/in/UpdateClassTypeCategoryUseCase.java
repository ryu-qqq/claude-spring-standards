package com.ryuqq.application.classtypecategory.port.in;

import com.ryuqq.application.classtypecategory.dto.command.UpdateClassTypeCategoryCommand;

/**
 * UpdateClassTypeCategoryUseCase - ClassTypeCategory 수정 UseCase
 *
 * <p>기존 ClassTypeCategory를 수정합니다.
 *
 * <p>UC-001: UseCase는 반드시 interface로 정의.
 *
 * <p>UC-002: UseCase는 execute() 단일 메서드만 제공.
 *
 * <p>UC-006: Command UseCase는 동사 접두어 + UseCase 네이밍.
 *
 * @author ryu-qqq
 */
public interface UpdateClassTypeCategoryUseCase {

    /**
     * ClassTypeCategory 수정 실행
     *
     * @param command 수정 Command
     */
    void execute(UpdateClassTypeCategoryCommand command);
}

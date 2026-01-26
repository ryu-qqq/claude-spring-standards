package com.ryuqq.application.checklistitem.port.in;

import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;

/**
 * UpdateChecklistItemUseCase - 체크리스트 항목 수정 UseCase
 *
 * <p>기존 체크리스트 항목을 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdateChecklistItemUseCase {

    /**
     * 체크리스트 항목 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdateChecklistItemCommand command);
}

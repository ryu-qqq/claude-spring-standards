package com.ryuqq.application.checklistitem.port.in;

import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;

/**
 * CreateChecklistItemUseCase - 체크리스트 항목 생성 UseCase
 *
 * <p>새로운 체크리스트 항목을 생성합니다.
 *
 * @author ryu-qqq
 */
public interface CreateChecklistItemUseCase {

    /**
     * 체크리스트 항목 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 체크리스트 항목 ID
     */
    Long execute(CreateChecklistItemCommand command);
}

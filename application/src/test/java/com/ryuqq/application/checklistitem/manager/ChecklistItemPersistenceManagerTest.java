package com.ryuqq.application.checklistitem.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.checklistitem.port.out.ChecklistItemCommandPort;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ChecklistItemPersistenceManager 단위 테스트
 *
 * <p>ChecklistItem 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ChecklistItemPersistenceManager 단위 테스트")
class ChecklistItemPersistenceManagerTest {

    @Mock private ChecklistItemCommandPort checklistItemCommandPort;

    @Mock private ChecklistItem checklistItem;

    private ChecklistItemPersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ChecklistItemPersistenceManager(checklistItemCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - ChecklistItem 영속화")
        void persist_WithChecklistItem_ShouldReturnId() {
            // given
            ChecklistItemId expectedId = ChecklistItemId.of(1L);
            given(checklistItemCommandPort.persist(checklistItem)).willReturn(expectedId);

            // when
            ChecklistItemId result = sut.persist(checklistItem);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(checklistItemCommandPort).should().persist(checklistItem);
        }
    }
}

package com.ryuqq.adapter.out.persistence.example.fixture;

import com.ryuqq.adapter.out.persistence.example.entity.ExampleJpaEntity;
import com.ryuqq.domain.example.ExampleStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ExampleJpaEntity 테스트 Fixture
 *
 * <p>테스트에서 ExampleJpaEntity 객체를 쉽게 생성하기 위한 Factory 클래스입니다.</p>
 *
 * <h3>네이밍 규칙:</h3>
 * <ul>
 *   <li>클래스명: {@code *Fixture} 접미사 필수</li>
 *   <li>기본 생성 메서드: {@code create*()} - 기본값으로 객체 생성</li>
 *   <li>커스터마이징 메서드: {@code create*With*()} - 특정 값 지정하여 생성</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>{@code
 * // 기본값으로 생성 (ID 없음 - 저장 전)
 * ExampleJpaEntity entity = ExampleJpaEntityFixture.create();
 *
 * // 특정 메시지로 생성
 * ExampleJpaEntity entity = ExampleJpaEntityFixture.createWithMessage("Custom Message");
 *
 * // ID 포함하여 생성 (저장 후 시나리오)
 * ExampleJpaEntity entity = ExampleJpaEntityFixture.createWithId(123L, "Message");
 * }</pre>
 *
 * @author Claude Code
 * @since 1.0.0
 * @see ExampleJpaEntity
 */
public class ExampleJpaEntityFixture {

    /**
     * 기본값으로 ExampleJpaEntity 생성 (ID 없음)
     *
     * <p>저장 전 상태의 Entity를 생성합니다.</p>
     *
     * @return 기본 메시지 "Test Message", 상태 ACTIVE를 가진 ExampleJpaEntity
     */
    public static ExampleJpaEntity create() {
        return createWithMessage("Test Message");
    }

    /**
     * 특정 메시지로 ExampleJpaEntity 생성 (ID 없음)
     *
     * @param message 메시지 내용
     * @return 지정된 메시지와 기본 상태를 가진 ExampleJpaEntity
     */
    public static ExampleJpaEntity createWithMessage(String message) {
        LocalDateTime now = LocalDateTime.now();
        return new ExampleJpaEntity(
            null,  // ID는 JPA가 자동 생성
            message,
            ExampleStatus.ACTIVE,
            now,
            now
        );
    }

    /**
     * ID 포함하여 ExampleJpaEntity 생성 (저장 후 시나리오)
     *
     * <p>영속화된 상태의 Entity를 테스트할 때 사용합니다.</p>
     *
     * @param id Entity ID
     * @param message 메시지 내용
     * @return ID를 가진 ExampleJpaEntity
     */
    public static ExampleJpaEntity createWithId(Long id, String message) {
        LocalDateTime now = LocalDateTime.now();
        return new ExampleJpaEntity(
            id,
            message,
            ExampleStatus.ACTIVE,
            now,
            now
        );
    }

    /**
     * ID와 상태를 지정하여 ExampleJpaEntity 생성
     *
     * @param id Entity ID
     * @param message 메시지 내용
     * @param status Entity 상태
     * @return 지정된 값을 가진 ExampleJpaEntity
     */
    public static ExampleJpaEntity createWithIdAndStatus(
        Long id,
        String message,
        ExampleStatus status
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new ExampleJpaEntity(
            id,
            message,
            status,
            now,
            now
        );
    }

    /**
     * 여러 개의 ExampleJpaEntity 생성 (ID 없음)
     *
     * <p>대량 저장 테스트용입니다.</p>
     *
     * @param count 생성할 개수
     * @return ExampleJpaEntity 리스트
     */
    public static List<ExampleJpaEntity> createMultiple(int count) {
        List<ExampleJpaEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createWithMessage("Test Message " + (i + 1)));
        }
        return entities;
    }

    /**
     * ID를 포함한 여러 개의 ExampleJpaEntity 생성
     *
     * <p>조회 테스트용 (영속화된 상태)입니다.</p>
     *
     * @param startId 시작 ID
     * @param count 생성할 개수
     * @return ExampleJpaEntity 리스트
     */
    public static List<ExampleJpaEntity> createMultipleWithId(long startId, int count) {
        List<ExampleJpaEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createWithId(startId + i, "Test Message " + (i + 1)));
        }
        return entities;
    }

    // Private 생성자 - Utility 클래스이므로 인스턴스화 방지
    private ExampleJpaEntityFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}

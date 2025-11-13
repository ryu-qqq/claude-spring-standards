package com.ryuqq.domain.example;

import com.ryuqq.domain.example.fixture.ExampleDomainFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ExampleDomainFixture 검증 테스트
 *
 * <p>Fixture가 실제로 올바르게 작동하는지 검증합니다.</p>
 */
@DisplayName("ExampleDomainFixture 테스트")
class ExampleDomainFixtureTest {

    @Test
    @DisplayName("기본값으로 ExampleDomain 생성")
    void create() {
        // When
        ExampleDomain example = ExampleDomainFixture.create();

        // Then
        assertThat(example).isNotNull();
        assertThat(example.getMessage()).isEqualTo("Test Message");
        assertThat(example.getStatus()).isEqualTo("ACTIVE");
        assertThat(example.getId()).isNull(); // 신규 생성 시 ID 없음
    }

    @Test
    @DisplayName("특정 메시지로 ExampleDomain 생성")
    void createWithMessage() {
        // Given
        String customMessage = "Custom Message";

        // When
        ExampleDomain example = ExampleDomainFixture.createWithMessage(customMessage);

        // Then
        assertThat(example).isNotNull();
        assertThat(example.getMessage()).isEqualTo(customMessage);
        assertThat(example.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("ID 포함하여 ExampleDomain 생성")
    void createWithId() {
        // Given
        Long id = 123L;
        String message = "Test Message with ID";

        // When
        ExampleDomain example = ExampleDomainFixture.createWithId(id, message);

        // Then
        assertThat(example).isNotNull();
        assertThat(example.getId()).isEqualTo(id);
        assertThat(example.getMessage()).isEqualTo(message);
        assertThat(example.getStatus()).isEqualTo("ACTIVE");
        assertThat(example.getCreatedAt()).isNotNull();
        assertThat(example.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("여러 개의 ExampleDomain 생성")
    void createMultiple() {
        // Given
        int count = 5;

        // When
        ExampleDomain[] examples = ExampleDomainFixture.createMultiple(count);

        // Then
        assertThat(examples).hasSize(count);
        for (int i = 0; i < count; i++) {
            assertThat(examples[i]).isNotNull();
            assertThat(examples[i].getMessage()).isEqualTo("Test Message " + (i + 1));
        }
    }

    @Test
    @DisplayName("ID를 포함한 여러 개의 ExampleDomain 생성")
    void createMultipleWithId() {
        // Given
        long startId = 100L;
        int count = 3;

        // When
        ExampleDomain[] examples = ExampleDomainFixture.createMultipleWithId(startId, count);

        // Then
        assertThat(examples).hasSize(count);
        for (int i = 0; i < count; i++) {
            assertThat(examples[i]).isNotNull();
            assertThat(examples[i].getId()).isEqualTo(startId + i);
            assertThat(examples[i].getMessage()).isEqualTo("Test Message " + (i + 1));
        }
    }

    @Test
    @DisplayName("Fixture로 생성한 Domain 객체로 비즈니스 로직 테스트")
    void testDomainLogicWithFixture() {
        // Given
        ExampleDomain example = ExampleDomainFixture.createWithId(1L, "Original Message");

        // When
        ExampleDomain updated = example.changeMessage("Updated Message");

        // Then
        assertThat(updated.getMessage()).isEqualTo("Updated Message");
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getStatus()).isEqualTo("ACTIVE");
    }
}

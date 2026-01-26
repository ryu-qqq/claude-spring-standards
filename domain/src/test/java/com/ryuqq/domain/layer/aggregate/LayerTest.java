package com.ryuqq.domain.layer.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.fixture.LayerFixture;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
import com.ryuqq.domain.layer.vo.LayerName;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Layer Aggregate 단위 테스트
 *
 * @author ryu-qqq
 */
@DisplayName("Layer Aggregate")
class LayerTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateLayer {

        @Test
        @DisplayName("신규 Layer 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            LayerCode code = LayerCode.of("DOMAIN");
            LayerName name = LayerName.of("도메인 레이어");
            String description = "비즈니스 로직 담당";
            int orderIndex = 1;
            Instant now = FIXED_CLOCK.instant();

            // when
            Layer layer = Layer.forNew(architectureId, code, name, description, orderIndex, now);

            // then
            assertThat(layer.isNew()).isTrue();
            assertThat(layer.architectureId()).isEqualTo(architectureId);
            assertThat(layer.code()).isEqualTo(code);
            assertThat(layer.name()).isEqualTo(name);
            assertThat(layer.description()).isEqualTo(description);
            assertThat(layer.orderIndex()).isEqualTo(orderIndex);
            assertThat(layer.deletionStatus().isDeleted()).isFalse();
            assertThat(layer.createdAt()).isEqualTo(now);
            assertThat(layer.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 Layer는 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            Layer layer = LayerFixture.defaultNewLayer();

            // then
            assertThat(layer.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("설명 없이 Layer 생성 성공")
        void forNew_WithoutDescription_ShouldSucceed() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            LayerCode code = LayerCode.of("APPLICATION");
            LayerName name = LayerName.of("애플리케이션 레이어");
            Instant now = FIXED_CLOCK.instant();

            // when
            Layer layer = Layer.forNew(architectureId, code, name, null, 2, now);

            // then
            assertThat(layer.description()).isNull();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            Layer layer = LayerFixture.defaultNewLayer();
            LayerId id = LayerId.of(1L);

            // when
            layer.assignId(id);

            // then
            assertThat(layer.id()).isEqualTo(id);
            assertThat(layer.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            Layer layer = LayerFixture.defaultExistingLayer();
            LayerId newId = LayerId.of(2L);

            // when & then
            assertThatThrownBy(() -> layer.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateLayer {

        @Test
        @DisplayName("Layer 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            Layer layer = LayerFixture.defaultExistingLayer();
            LayerUpdateData updateData =
                    new LayerUpdateData(
                            LayerCode.of("UPDATED_DOMAIN"),
                            LayerName.of("수정된 도메인 레이어"),
                            "수정된 설명",
                            10);
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            layer.update(updateData, updateTime);

            // then
            assertThat(layer.code()).isEqualTo(updateData.code());
            assertThat(layer.name()).isEqualTo(updateData.name());
            assertThat(layer.description()).isEqualTo(updateData.description());
            assertThat(layer.orderIndex()).isEqualTo(updateData.orderIndex());
            assertThat(layer.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("설명 null로 수정 성공")
        void update_WithNullDescription_ShouldSucceed() {
            // given
            Layer layer = LayerFixture.defaultExistingLayer();
            LayerUpdateData updateData =
                    new LayerUpdateData(LayerCode.of("DOMAIN"), LayerName.of("도메인 레이어"), null, 1);
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            layer.update(updateData, updateTime);

            // then
            assertThat(layer.description()).isNull();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteLayer {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            Layer layer = LayerFixture.defaultExistingLayer();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            layer.delete(deleteTime);

            // then
            assertThat(layer.isDeleted()).isTrue();
            assertThat(layer.deletionStatus().isDeleted()).isTrue();
            assertThat(layer.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(layer.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 Layer 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            Layer layer = LayerFixture.deletedLayer();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            layer.restore(restoreTime);

            // then
            assertThat(layer.isDeleted()).isFalse();
            assertThat(layer.deletionStatus().isActive()).isTrue();
            assertThat(layer.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            Layer activeLayer = LayerFixture.defaultExistingLayer();
            Layer deletedLayer = LayerFixture.deletedLayer();

            // when & then
            assertThat(activeLayer.isDeleted()).isFalse();
            assertThat(deletedLayer.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteLayer {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            LayerId id = LayerId.of(1L);
            ArchitectureId architectureId = ArchitectureId.of(1L);
            LayerCode code = LayerCode.of("DOMAIN");
            LayerName name = LayerName.of("도메인 레이어");
            String description = "설명";
            int orderIndex = 1;
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            Layer layer =
                    Layer.reconstitute(
                            id,
                            architectureId,
                            code,
                            name,
                            description,
                            orderIndex,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(layer.id()).isEqualTo(id);
            assertThat(layer.architectureId()).isEqualTo(architectureId);
            assertThat(layer.code()).isEqualTo(code);
            assertThat(layer.name()).isEqualTo(name);
            assertThat(layer.description()).isEqualTo(description);
            assertThat(layer.orderIndex()).isEqualTo(orderIndex);
            assertThat(layer.createdAt()).isEqualTo(createdAt);
            assertThat(layer.updatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("위임 메서드")
    class DelegationMethods {

        @Test
        @DisplayName("idValue 반환")
        void idValue_ShouldReturnPrimitiveValue() {
            // given
            Layer layer = LayerFixture.defaultExistingLayer();

            // then
            assertThat(layer.idValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("architectureIdValue 반환")
        void architectureIdValue_ShouldReturnPrimitiveValue() {
            // given
            Layer layer = LayerFixture.defaultExistingLayer();

            // then
            assertThat(layer.architectureIdValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("codeValue 반환")
        void codeValue_ShouldReturnPrimitiveValue() {
            // given
            Layer layer = LayerFixture.defaultExistingLayer();

            // then
            assertThat(layer.codeValue()).isEqualTo("DOMAIN");
        }

        @Test
        @DisplayName("nameValue 반환")
        void nameValue_ShouldReturnPrimitiveValue() {
            // given
            Layer layer = LayerFixture.defaultExistingLayer();

            // then
            assertThat(layer.nameValue()).isEqualTo("도메인 레이어");
        }

        @Test
        @DisplayName("deletedAt 반환")
        void deletedAt_ShouldReturnCorrectValue() {
            // given
            Layer activeLayer = LayerFixture.defaultExistingLayer();
            Layer deletedLayer = LayerFixture.deletedLayer();

            // then
            assertThat(activeLayer.deletedAt()).isNull();
            assertThat(deletedLayer.deletedAt()).isNotNull();
        }
    }
}

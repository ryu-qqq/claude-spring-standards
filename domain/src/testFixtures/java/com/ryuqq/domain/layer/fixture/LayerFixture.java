package com.ryuqq.domain.layer.fixture;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
import com.ryuqq.domain.layer.vo.LayerName;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Layer Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 Layer 객체 생성 유틸리티
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class LayerFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private LayerFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 Layer Fixture (신규 생성) */
    public static Layer defaultNewLayer() {
        return Layer.forNew(
                ArchitectureId.of(1L),
                LayerCode.of("DOMAIN"),
                LayerName.of("도메인 레이어"),
                "비즈니스 로직을 담당하는 핵심 레이어",
                1,
                FIXED_CLOCK.instant());
    }

    /** 기존 Layer Fixture (저장된 상태) */
    public static Layer defaultExistingLayer() {
        Instant now = FIXED_CLOCK.instant();
        return Layer.of(
                LayerId.of(1L),
                ArchitectureId.of(1L),
                LayerCode.of("DOMAIN"),
                LayerName.of("도메인 레이어"),
                "비즈니스 로직을 담당하는 핵심 레이어",
                1,
                DeletionStatus.active(),
                now,
                now);
    }

    /** Application Layer */
    public static Layer applicationLayer() {
        Instant now = FIXED_CLOCK.instant();
        return Layer.of(
                LayerId.of(2L),
                ArchitectureId.of(1L),
                LayerCode.of("APPLICATION"),
                LayerName.of("애플리케이션 레이어"),
                "유스케이스 및 서비스를 담당하는 레이어",
                2,
                DeletionStatus.active(),
                now,
                now);
    }

    /** Persistence Layer */
    public static Layer persistenceLayer() {
        Instant now = FIXED_CLOCK.instant();
        return Layer.of(
                LayerId.of(3L),
                ArchitectureId.of(1L),
                LayerCode.of("PERSISTENCE"),
                LayerName.of("영속성 레이어"),
                "데이터베이스 접근을 담당하는 레이어",
                3,
                DeletionStatus.active(),
                now,
                now);
    }

    /** REST API Layer */
    public static Layer restApiLayer() {
        Instant now = FIXED_CLOCK.instant();
        return Layer.of(
                LayerId.of(4L),
                ArchitectureId.of(1L),
                LayerCode.of("REST_API"),
                LayerName.of("REST API 레이어"),
                "외부 요청을 처리하는 레이어",
                4,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 삭제된 Layer */
    public static Layer deletedLayer() {
        Instant now = FIXED_CLOCK.instant();
        return Layer.of(
                LayerId.of(5L),
                ArchitectureId.of(1L),
                LayerCode.of("DELETED"),
                LayerName.of("삭제된 레이어"),
                null,
                99,
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /** 설명 없는 Layer */
    public static Layer layerWithoutDescription() {
        Instant now = FIXED_CLOCK.instant();
        return Layer.of(
                LayerId.of(6L),
                ArchitectureId.of(1L),
                LayerCode.of("INFRASTRUCTURE"),
                LayerName.of("인프라스트럭처 레이어"),
                null,
                5,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 특정 아키텍처의 Layer 생성 */
    public static Layer layerForArchitecture(Long architectureId) {
        Instant now = FIXED_CLOCK.instant();
        return Layer.of(
                LayerId.of(7L),
                ArchitectureId.of(architectureId),
                LayerCode.of("CUSTOM"),
                LayerName.of("커스텀 레이어"),
                "특정 아키텍처용 레이어",
                1,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 커스텀 Layer 생성 */
    public static Layer customLayer(
            Long id,
            Long architectureId,
            String code,
            String name,
            String description,
            int orderIndex,
            Instant createdAt,
            Instant updatedAt) {
        return Layer.of(
                LayerId.of(id),
                ArchitectureId.of(architectureId),
                LayerCode.of(code),
                LayerName.of(name),
                description,
                orderIndex,
                DeletionStatus.active(),
                createdAt,
                updatedAt);
    }

    /** 특정 코드를 가진 Layer 생성 */
    public static Layer layerWithCode(String code) {
        Instant now = FIXED_CLOCK.instant();
        return Layer.of(
                LayerId.of(8L),
                ArchitectureId.of(1L),
                LayerCode.of(code),
                LayerName.of(code + " 레이어"),
                null,
                1,
                DeletionStatus.active(),
                now,
                now);
    }
}

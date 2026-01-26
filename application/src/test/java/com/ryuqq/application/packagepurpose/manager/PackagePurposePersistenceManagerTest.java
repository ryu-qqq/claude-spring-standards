package com.ryuqq.application.packagepurpose.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.packagepurpose.port.out.PackagePurposeCommandPort;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PackagePurposePersistenceManager 단위 테스트
 *
 * <p>PackagePurpose 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("PackagePurposePersistenceManager 단위 테스트")
class PackagePurposePersistenceManagerTest {

    @Mock private PackagePurposeCommandPort packagePurposeCommandPort;

    @Mock private PackagePurpose packagePurpose;

    private PackagePurposePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new PackagePurposePersistenceManager(packagePurposeCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - PackagePurpose 영속화")
        void persist_WithPackagePurpose_ShouldReturnId() {
            // given
            PackagePurposeId expectedId = PackagePurposeId.of(1L);
            given(packagePurposeCommandPort.persist(packagePurpose)).willReturn(expectedId);

            // when
            PackagePurposeId result = sut.persist(packagePurpose);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(packagePurposeCommandPort).should().persist(packagePurpose);
        }
    }
}

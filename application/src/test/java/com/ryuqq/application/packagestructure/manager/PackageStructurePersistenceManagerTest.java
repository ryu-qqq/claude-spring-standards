package com.ryuqq.application.packagestructure.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.packagestructure.port.out.PackageStructureCommandPort;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PackageStructurePersistenceManager 단위 테스트
 *
 * <p>PackageStructure 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("PackageStructurePersistenceManager 단위 테스트")
class PackageStructurePersistenceManagerTest {

    @Mock private PackageStructureCommandPort packageStructureCommandPort;

    @Mock private PackageStructure packageStructure;

    private PackageStructurePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new PackageStructurePersistenceManager(packageStructureCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - PackageStructure 영속화")
        void persist_WithPackageStructure_ShouldReturnId() {
            // given
            PackageStructureId expectedId = PackageStructureId.of(1L);
            given(packageStructureCommandPort.persist(packageStructure)).willReturn(expectedId);

            // when
            PackageStructureId result = sut.persist(packageStructure);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(packageStructureCommandPort).should().persist(packageStructure);
        }
    }
}

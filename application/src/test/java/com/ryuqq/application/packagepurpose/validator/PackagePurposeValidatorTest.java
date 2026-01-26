package com.ryuqq.application.packagepurpose.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.packagepurpose.manager.PackagePurposeReadManager;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurposeUpdateData;
import com.ryuqq.domain.packagepurpose.exception.PackagePurposeDuplicateCodeException;
import com.ryuqq.domain.packagepurpose.fixture.PackagePurposeFixture;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.AllowedClassTypes;
import com.ryuqq.domain.packagepurpose.vo.NamingPattern;
import com.ryuqq.domain.packagepurpose.vo.NamingSuffix;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagepurpose.vo.PurposeName;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PackagePurposeValidator 단위 테스트
 *
 * <p>PackagePurpose 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("PackagePurposeValidator 단위 테스트")
class PackagePurposeValidatorTest {

    @Mock private PackagePurposeReadManager packagePurposeReadManager;

    private PackagePurposeValidator sut;

    @BeforeEach
    void setUp() {
        sut = new PackagePurposeValidator(packagePurposeReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 PackagePurpose 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnPackagePurpose() {
            // given
            PackagePurposeId id = PackagePurposeId.of(1L);
            PackagePurpose expected = PackagePurposeFixture.reconstitute();

            given(packagePurposeReadManager.getById(id)).willReturn(expected);

            // when
            PackagePurpose result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicate 메서드")
    class ValidateNotDuplicate {

        @Test
        @DisplayName("성공 - 중복되지 않는 코드")
        void validateNotDuplicate_WhenNotDuplicate_ShouldNotThrow() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            PurposeCode code = PurposeCode.of("NEW-CODE");

            given(packagePurposeReadManager.existsByStructureIdAndCode(structureId, code))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNotDuplicate(structureId, code))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 중복된 코드인 경우 예외")
        void validateNotDuplicate_WhenDuplicate_ShouldThrowException() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            PurposeCode code = PurposeCode.of("DUPLICATE-CODE");

            given(packagePurposeReadManager.existsByStructureIdAndCode(structureId, code))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicate(structureId, code))
                    .isInstanceOf(PackagePurposeDuplicateCodeException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicateExcluding 메서드")
    class ValidateNotDuplicateExcluding {

        @Test
        @DisplayName("성공 - 자신을 제외하고 중복되지 않는 코드")
        void validateNotDuplicateExcluding_WhenNotDuplicate_ShouldNotThrow() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            PurposeCode code = PurposeCode.of("MY-CODE");
            PackagePurposeId excludeId = PackagePurposeId.of(1L);

            given(
                            packagePurposeReadManager.existsByStructureIdAndCodeExcluding(
                                    structureId, code, excludeId))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNotDuplicateExcluding(structureId, code, excludeId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 다른 목적에서 이미 사용 중인 경우 예외")
        void validateNotDuplicateExcluding_WhenDuplicate_ShouldThrowException() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            PurposeCode code = PurposeCode.of("USED-CODE");
            PackagePurposeId excludeId = PackagePurposeId.of(1L);

            given(
                            packagePurposeReadManager.existsByStructureIdAndCodeExcluding(
                                    structureId, code, excludeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () -> sut.validateNotDuplicateExcluding(structureId, code, excludeId))
                    .isInstanceOf(PackagePurposeDuplicateCodeException.class);
        }
    }

    @Nested
    @DisplayName("validateAndGetForUpdate 메서드")
    class ValidateAndGetForUpdate {

        @Test
        @DisplayName("성공 - 검증 통과 후 PackagePurpose 반환")
        void validateAndGetForUpdate_WhenValid_ShouldReturnPackagePurpose() {
            // given
            PackagePurposeId id = PackagePurposeId.of(1L);
            PackagePurpose packagePurpose = PackagePurposeFixture.reconstitute(id);
            PackagePurposeUpdateData updateData =
                    new PackagePurposeUpdateData(
                            PurposeCode.of("UPDATED-CODE"),
                            PurposeName.of("Updated Name"),
                            "Updated Description",
                            AllowedClassTypes.of(List.of("Service")),
                            NamingPattern.of("*Service"),
                            NamingSuffix.of("Service"));

            given(packagePurposeReadManager.getById(id)).willReturn(packagePurpose);
            given(
                            packagePurposeReadManager.existsByStructureIdAndCodeExcluding(
                                    packagePurpose.structureId(), updateData.code(), id))
                    .willReturn(false);

            // when
            PackagePurpose result = sut.validateAndGetForUpdate(id, updateData);

            // then
            assertThat(result).isEqualTo(packagePurpose);
        }

        @Test
        @DisplayName("실패 - 중복된 코드인 경우 예외")
        void validateAndGetForUpdate_WhenDuplicateCode_ShouldThrowException() {
            // given
            PackagePurposeId id = PackagePurposeId.of(1L);
            PackagePurpose packagePurpose = PackagePurposeFixture.reconstitute(id);
            PackagePurposeUpdateData updateData =
                    new PackagePurposeUpdateData(
                            PurposeCode.of("DUPLICATE-CODE"),
                            PurposeName.of("Name"),
                            "Description",
                            AllowedClassTypes.of(List.of("Service")),
                            NamingPattern.of("*Service"),
                            NamingSuffix.of("Service"));

            given(packagePurposeReadManager.getById(id)).willReturn(packagePurpose);
            given(
                            packagePurposeReadManager.existsByStructureIdAndCodeExcluding(
                                    packagePurpose.structureId(), updateData.code(), id))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateAndGetForUpdate(id, updateData))
                    .isInstanceOf(PackagePurposeDuplicateCodeException.class);
        }
    }
}

package com.ryuqq.domain.packagepurpose.fixture;

import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.AllowedClassTypes;
import com.ryuqq.domain.packagepurpose.vo.NamingPattern;
import com.ryuqq.domain.packagepurpose.vo.NamingSuffix;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagepurpose.vo.PurposeName;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PackagePurpose VO 테스트 Fixture
 *
 * <p>PackagePurpose의 Value Object들을 위한 테스트 데이터 생성 유틸리티입니다.
 *
 * @author ryu-qqq
 */
public final class PackagePurposeVoFixtures {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1L);
    private static final Long DEFAULT_STRUCTURE_ID = 1L;

    private PackagePurposeVoFixtures() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ==================== ID Fixtures ====================

    /**
     * 다음 PackagePurposeId 생성 (시퀀스 증가)
     *
     * @return 새로운 PackagePurposeId
     */
    public static PackagePurposeId nextPackagePurposeId() {
        return PackagePurposeId.of(ID_SEQUENCE.getAndIncrement());
    }

    // ==================== PackageStructureId Fixtures ====================

    /**
     * 기본 패키지 구조 ID
     *
     * @return PackageStructureId
     */
    public static PackageStructureId defaultStructureId() {
        return PackageStructureId.of(DEFAULT_STRUCTURE_ID);
    }

    // ==================== PurposeCode Fixtures ====================

    /**
     * 기본 목적 코드
     *
     * @return PurposeCode
     */
    public static PurposeCode defaultPurposeCode() {
        return PurposeCode.of("AGGREGATE");
    }

    /**
     * 지정된 값의 목적 코드
     *
     * @param code 코드 문자열
     * @return PurposeCode
     */
    public static PurposeCode purposeCodeOf(String code) {
        return PurposeCode.of(code);
    }

    // ==================== PurposeName Fixtures ====================

    /**
     * 기본 목적 이름
     *
     * @return PurposeName
     */
    public static PurposeName defaultPurposeName() {
        return PurposeName.of("애그리거트");
    }

    /**
     * 지정된 값의 목적 이름
     *
     * @param name 이름 문자열
     * @return PurposeName
     */
    public static PurposeName purposeNameOf(String name) {
        return PurposeName.of(name);
    }

    // ==================== AllowedClassTypes Fixtures ====================

    /**
     * 기본 허용 클래스 타입
     *
     * @return AllowedClassTypes
     */
    public static AllowedClassTypes defaultAllowedClassTypes() {
        return AllowedClassTypes.of(Arrays.asList("AGGREGATE_ROOT", "ENTITY"));
    }

    /**
     * 빈 허용 클래스 타입
     *
     * @return AllowedClassTypes
     */
    public static AllowedClassTypes emptyAllowedClassTypes() {
        return AllowedClassTypes.empty();
    }

    // ==================== NamingPattern Fixtures ====================

    /**
     * 기본 네이밍 패턴
     *
     * @return NamingPattern
     */
    public static NamingPattern defaultNamingPattern() {
        return NamingPattern.of(".*");
    }

    /**
     * 빈 네이밍 패턴
     *
     * @return NamingPattern
     */
    public static NamingPattern emptyNamingPattern() {
        return NamingPattern.empty();
    }

    // ==================== NamingSuffix Fixtures ====================

    /**
     * 기본 네이밍 접미사
     *
     * @return NamingSuffix
     */
    public static NamingSuffix defaultNamingSuffix() {
        return NamingSuffix.of("Service");
    }

    /**
     * 빈 네이밍 접미사
     *
     * @return NamingSuffix
     */
    public static NamingSuffix emptyNamingSuffix() {
        return NamingSuffix.empty();
    }
}

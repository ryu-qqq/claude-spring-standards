package com.ryuqq.domain.packagestructure.aggregate;

import com.ryuqq.domain.packagepurpose.vo.AllowedClassTypes;
import com.ryuqq.domain.packagepurpose.vo.NamingPattern;
import com.ryuqq.domain.packagepurpose.vo.NamingSuffix;
import com.ryuqq.domain.packagestructure.vo.PathPattern;

/**
 * PackageStructureUpdateData - 패키지 구조 수정 데이터 Value Object
 *
 * <p>패키지 구조 수정에 필요한 데이터를 전달합니다.
 *
 * <p>요청으로 들어온 데이터를 기반으로 객체를 만들고, JPA의 더티체킹을 활용하여 변경사항을 처리합니다.
 *
 * @param pathPattern 경로 패턴 (필수)
 * @param allowedClassTypes 허용 클래스 타입 목록 (필수)
 * @param namingPattern 네이밍 패턴 (필수)
 * @param namingSuffix 네이밍 접미사 (필수)
 * @param description 설명 (필수)
 * @author ryu-qqq
 */
public record PackageStructureUpdateData(
        PathPattern pathPattern,
        AllowedClassTypes allowedClassTypes,
        NamingPattern namingPattern,
        NamingSuffix namingSuffix,
        String description) {}

package com.ryuqq.application.packagepurpose.dto.command;

import java.util.List;

/**
 * UpdatePackagePurposeCommand - 패키지 목적 수정 커맨드
 *
 * <p>패키지 목적 수정에 필요한 데이터를 전달합니다.
 *
 * @param packagePurposeId 패키지 목적 ID
 * @param code 목적 코드
 * @param name 목적 이름
 * @param description 설명
 * @param defaultAllowedClassTypes 기본 허용 클래스 타입 목록
 * @param defaultNamingPattern 기본 네이밍 패턴 (정규식)
 * @param defaultNamingSuffix 기본 네이밍 접미사
 * @author ryu-qqq
 */
public record UpdatePackagePurposeCommand(
        Long packagePurposeId,
        String code,
        String name,
        String description,
        List<String> defaultAllowedClassTypes,
        String defaultNamingPattern,
        String defaultNamingSuffix) {}

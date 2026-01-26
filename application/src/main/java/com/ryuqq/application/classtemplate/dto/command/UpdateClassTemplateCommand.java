package com.ryuqq.application.classtemplate.dto.command;

import java.util.List;

/**
 * UpdateClassTemplateCommand - 클래스 템플릿 수정 커맨드
 *
 * <p>클래스 템플릿 수정에 필요한 데이터를 전달합니다.
 *
 * @param classTemplateId 수정할 클래스 템플릿 ID
 * @param classTypeId 클래스 타입 ID
 * @param templateCode 템플릿 코드
 * @param namingPattern 네이밍 패턴 (nullable)
 * @param description 템플릿 설명
 * @param requiredAnnotations 필수 어노테이션 목록
 * @param forbiddenAnnotations 금지 어노테이션 목록
 * @param requiredInterfaces 필수 인터페이스 목록
 * @param forbiddenInheritance 금지 상속 목록
 * @param requiredMethods 필수 메서드 목록
 * @author ryu-qqq
 */
public record UpdateClassTemplateCommand(
        Long classTemplateId,
        Long classTypeId,
        String templateCode,
        String namingPattern,
        String description,
        List<String> requiredAnnotations,
        List<String> forbiddenAnnotations,
        List<String> requiredInterfaces,
        List<String> forbiddenInheritance,
        List<String> requiredMethods) {}

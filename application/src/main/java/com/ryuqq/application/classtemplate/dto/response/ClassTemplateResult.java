package com.ryuqq.application.classtemplate.dto.response;

import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import java.time.Instant;
import java.util.List;

/**
 * ClassTemplateResult - 클래스 템플릿 조회 결과 DTO
 *
 * <p>Application Layer의 결과 DTO입니다.
 *
 * @param id 클래스 템플릿 ID
 * @param structureId 패키지 구조 ID (필수)
 * @param classTypeId 클래스 타입 ID
 * @param templateCode 템플릿 코드
 * @param namingPattern 네이밍 패턴 (nullable)
 * @param description 템플릿 설명
 * @param requiredAnnotations 필수 어노테이션 목록
 * @param forbiddenAnnotations 금지 어노테이션 목록
 * @param requiredInterfaces 필수 인터페이스 목록
 * @param forbiddenInheritance 금지 상속 목록
 * @param requiredMethods 필수 메서드 목록
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record ClassTemplateResult(
        Long id,
        Long structureId,
        Long classTypeId,
        String templateCode,
        String namingPattern,
        String description,
        List<String> requiredAnnotations,
        List<String> forbiddenAnnotations,
        List<String> requiredInterfaces,
        List<String> forbiddenInheritance,
        List<String> requiredMethods,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain 객체로부터 Result 생성
     *
     * @param classTemplate ClassTemplate 도메인 객체
     * @return ClassTemplateResult
     */
    public static ClassTemplateResult from(ClassTemplate classTemplate) {
        return new ClassTemplateResult(
                classTemplate.idValue(),
                classTemplate.structureIdValue(),
                classTemplate.classTypeIdValue(),
                classTemplate.templateCode().value(),
                classTemplate.namingPattern() != null
                        ? classTemplate.namingPattern().value()
                        : null,
                classTemplate.description() != null ? classTemplate.description().value() : null,
                classTemplate.requiredAnnotations(),
                classTemplate.forbiddenAnnotations(),
                classTemplate.requiredInterfaces(),
                classTemplate.forbiddenInheritance(),
                classTemplate.requiredMethods(),
                classTemplate.createdAt(),
                classTemplate.updatedAt());
    }
}

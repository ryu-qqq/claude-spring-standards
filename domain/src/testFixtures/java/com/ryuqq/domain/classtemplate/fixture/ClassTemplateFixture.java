package com.ryuqq.domain.classtemplate.fixture;

import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.NamingPattern;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.classtemplate.vo.TemplateDescription;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

/**
 * ClassTemplate Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 ClassTemplate 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ClassTemplateFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private ClassTemplateFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 ClassTemplate Fixture (신규 생성) */
    public static ClassTemplate defaultNewClassTemplate() {
        return ClassTemplate.forNew(
                PackageStructureId.of(1L),
                ClassTypeId.of(1L),
                TemplateCode.of("public class {ClassName} { ... }"),
                NamingPattern.of(".*Aggregate"),
                TemplateDescription.of("Aggregate Root 클래스 템플릿"),
                List.of("@Entity"),
                List.of("@Data"),
                List.of(),
                List.of(),
                List.of(),
                FIXED_CLOCK.instant());
    }

    /** 기존 ClassTemplate Fixture (저장된 상태) */
    public static ClassTemplate defaultExistingClassTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ClassTemplate.of(
                ClassTemplateId.of(1L),
                PackageStructureId.of(1L),
                ClassTypeId.of(1L),
                TemplateCode.of("public class {ClassName} { ... }"),
                NamingPattern.of(".*Aggregate"),
                TemplateDescription.of("Aggregate Root 클래스 템플릿"),
                List.of("@Entity"),
                List.of("@Data"),
                List.of(),
                List.of(),
                List.of(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 필수 어노테이션이 있는 ClassTemplate */
    public static ClassTemplate classTemplateWithRequiredAnnotations() {
        Instant now = FIXED_CLOCK.instant();
        return ClassTemplate.of(
                ClassTemplateId.of(2L),
                PackageStructureId.of(1L),
                ClassTypeId.of(2L),
                TemplateCode.of("@RestController\npublic class {ClassName} { ... }"),
                NamingPattern.of(".*Controller"),
                TemplateDescription.of("REST Controller 템플릿"),
                List.of("@RestController", "@RequestMapping"),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 금지 어노테이션이 있는 ClassTemplate */
    public static ClassTemplate classTemplateWithForbiddenAnnotations() {
        Instant now = FIXED_CLOCK.instant();
        return ClassTemplate.of(
                ClassTemplateId.of(3L),
                PackageStructureId.of(1L),
                ClassTypeId.of(3L),
                TemplateCode.of("public class {ClassName} extends RuntimeException { ... }"),
                NamingPattern.of(".*Exception"),
                TemplateDescription.of("도메인 예외 템플릿"),
                List.of(),
                List.of("@Data", "@Builder"),
                List.of(),
                List.of(),
                List.of(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 필수 인터페이스가 있는 ClassTemplate */
    public static ClassTemplate classTemplateWithRequiredInterfaces() {
        Instant now = FIXED_CLOCK.instant();
        return ClassTemplate.of(
                ClassTemplateId.of(4L),
                PackageStructureId.of(1L),
                ClassTypeId.of(4L),
                TemplateCode.of("public class {ClassName} implements UseCase { ... }"),
                NamingPattern.of(".*Service"),
                TemplateDescription.of("Command Service 템플릿"),
                List.of("@Service"),
                List.of(),
                List.of("UseCase"),
                List.of(),
                List.of(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 삭제된 ClassTemplate */
    public static ClassTemplate deletedClassTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ClassTemplate.of(
                ClassTemplateId.of(5L),
                PackageStructureId.of(1L),
                ClassTypeId.of(5L),
                TemplateCode.of("public record {ClassName} { ... }"),
                NamingPattern.of(".*VO"),
                TemplateDescription.of("삭제된 템플릿"),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /** 패키지 구조가 있는 ClassTemplate (다른 structureId 사용) */
    public static ClassTemplate classTemplateWithDifferentStructure() {
        Instant now = FIXED_CLOCK.instant();
        return ClassTemplate.of(
                ClassTemplateId.of(6L),
                PackageStructureId.of(2L),
                ClassTypeId.of(6L),
                TemplateCode.of("@Entity\npublic class {ClassName} { ... }"),
                NamingPattern.of(".*Entity"),
                TemplateDescription.of("JPA Entity 템플릿"),
                List.of("@Entity"),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 네이밍 패턴이 없는 ClassTemplate */
    public static ClassTemplate classTemplateWithoutNamingPattern() {
        Instant now = FIXED_CLOCK.instant();
        return ClassTemplate.of(
                ClassTemplateId.of(7L),
                PackageStructureId.of(1L),
                ClassTypeId.of(7L),
                TemplateCode.of("@Configuration\npublic class {ClassName} { ... }"),
                null,
                TemplateDescription.of("설정 클래스 템플릿"),
                List.of("@Configuration"),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 커스텀 ClassTemplate 생성 */
    public static ClassTemplate customClassTemplate(
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
        return ClassTemplate.of(
                ClassTemplateId.of(id),
                PackageStructureId.of(structureId),
                ClassTypeId.of(classTypeId),
                TemplateCode.of(templateCode),
                namingPattern != null ? NamingPattern.of(namingPattern) : null,
                TemplateDescription.of(description),
                requiredAnnotations != null ? requiredAnnotations : List.of(),
                forbiddenAnnotations != null ? forbiddenAnnotations : List.of(),
                requiredInterfaces != null ? requiredInterfaces : List.of(),
                forbiddenInheritance != null ? forbiddenInheritance : List.of(),
                requiredMethods != null ? requiredMethods : List.of(),
                DeletionStatus.active(),
                createdAt,
                updatedAt);
    }
}

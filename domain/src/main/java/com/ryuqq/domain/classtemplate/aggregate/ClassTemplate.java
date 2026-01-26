package com.ryuqq.domain.classtemplate.aggregate;

import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.NamingPattern;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.classtemplate.vo.TemplateDescription;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Instant;
import java.util.List;

/**
 * ClassTemplate - 클래스 템플릿 Aggregate Root
 *
 * <p>코딩 컨벤션에 따른 클래스 템플릿을 관리합니다. 각 템플릿은 특정 PackageStructure에 연결됩니다.
 *
 * @author ryu-qqq
 */
public class ClassTemplate {

    private ClassTemplateId id;
    private PackageStructureId structureId;
    private ClassTypeId classTypeId;
    private TemplateCode templateCode;
    private NamingPattern namingPattern;
    private TemplateDescription description;
    private List<String> requiredAnnotations;
    private List<String> forbiddenAnnotations;
    private List<String> requiredInterfaces;
    private List<String> forbiddenInheritance;
    private List<String> requiredMethods;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected ClassTemplate() {
        this.createdAt = null;
    }

    private ClassTemplate(
            ClassTemplateId id,
            PackageStructureId structureId,
            ClassTypeId classTypeId,
            TemplateCode templateCode,
            NamingPattern namingPattern,
            TemplateDescription description,
            List<String> requiredAnnotations,
            List<String> forbiddenAnnotations,
            List<String> requiredInterfaces,
            List<String> forbiddenInheritance,
            List<String> requiredMethods,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.structureId = structureId;
        this.classTypeId = classTypeId;
        this.templateCode = templateCode;
        this.namingPattern = namingPattern;
        this.description = description;
        this.requiredAnnotations =
                requiredAnnotations != null ? List.copyOf(requiredAnnotations) : List.of();
        this.forbiddenAnnotations =
                forbiddenAnnotations != null ? List.copyOf(forbiddenAnnotations) : List.of();
        this.requiredInterfaces =
                requiredInterfaces != null ? List.copyOf(requiredInterfaces) : List.of();
        this.forbiddenInheritance =
                forbiddenInheritance != null ? List.copyOf(forbiddenInheritance) : List.of();
        this.requiredMethods = requiredMethods != null ? List.copyOf(requiredMethods) : List.of();
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param structureId 패키지 구조 ID (필수)
     * @param classTypeId 클래스 타입 ID (필수)
     * @param templateCode 템플릿 코드
     * @param namingPattern 네이밍 패턴
     * @param description 설명
     * @param requiredAnnotations 필수 어노테이션
     * @param forbiddenAnnotations 금지 어노테이션
     * @param requiredInterfaces 필수 인터페이스
     * @param forbiddenInheritance 금지 상속
     * @param requiredMethods 필수 메서드
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 ClassTemplate 인스턴스
     */
    public static ClassTemplate forNew(
            PackageStructureId structureId,
            ClassTypeId classTypeId,
            TemplateCode templateCode,
            NamingPattern namingPattern,
            TemplateDescription description,
            List<String> requiredAnnotations,
            List<String> forbiddenAnnotations,
            List<String> requiredInterfaces,
            List<String> forbiddenInheritance,
            List<String> requiredMethods,
            Instant now) {
        if (structureId == null) {
            throw new IllegalArgumentException("structureId must not be null");
        }
        if (classTypeId == null) {
            throw new IllegalArgumentException("classTypeId must not be null");
        }
        if (templateCode == null) {
            throw new IllegalArgumentException("templateCode must not be null");
        }
        return new ClassTemplate(
                ClassTemplateId.forNew(),
                structureId,
                classTypeId,
                templateCode,
                namingPattern,
                description,
                requiredAnnotations,
                forbiddenAnnotations,
                requiredInterfaces,
                forbiddenInheritance,
                requiredMethods,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 클래스 템플릿 ID
     * @param structureId 패키지 구조 ID (필수)
     * @param classTypeId 클래스 타입 ID
     * @param templateCode 템플릿 코드
     * @param namingPattern 네이밍 패턴
     * @param description 설명
     * @param requiredAnnotations 필수 어노테이션
     * @param forbiddenAnnotations 금지 어노테이션
     * @param requiredInterfaces 필수 인터페이스
     * @param forbiddenInheritance 금지 상속
     * @param requiredMethods 필수 메서드
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return ClassTemplate 인스턴스
     */
    public static ClassTemplate of(
            ClassTemplateId id,
            PackageStructureId structureId,
            ClassTypeId classTypeId,
            TemplateCode templateCode,
            NamingPattern namingPattern,
            TemplateDescription description,
            List<String> requiredAnnotations,
            List<String> forbiddenAnnotations,
            List<String> requiredInterfaces,
            List<String> forbiddenInheritance,
            List<String> requiredMethods,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new ClassTemplate(
                id,
                structureId,
                classTypeId,
                templateCode,
                namingPattern,
                description,
                requiredAnnotations,
                forbiddenAnnotations,
                requiredInterfaces,
                forbiddenInheritance,
                requiredMethods,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 클래스 템플릿 ID
     * @param structureId 패키지 구조 ID (필수)
     * @param classTypeId 클래스 타입 ID
     * @param templateCode 템플릿 코드
     * @param namingPattern 네이밍 패턴
     * @param description 설명
     * @param requiredAnnotations 필수 어노테이션
     * @param forbiddenAnnotations 금지 어노테이션
     * @param requiredInterfaces 필수 인터페이스
     * @param forbiddenInheritance 금지 상속
     * @param requiredMethods 필수 메서드
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 ClassTemplate 인스턴스
     */
    public static ClassTemplate reconstitute(
            ClassTemplateId id,
            PackageStructureId structureId,
            ClassTypeId classTypeId,
            TemplateCode templateCode,
            NamingPattern namingPattern,
            TemplateDescription description,
            List<String> requiredAnnotations,
            List<String> forbiddenAnnotations,
            List<String> requiredInterfaces,
            List<String> forbiddenInheritance,
            List<String> requiredMethods,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                structureId,
                classTypeId,
                templateCode,
                namingPattern,
                description,
                requiredAnnotations,
                forbiddenAnnotations,
                requiredInterfaces,
                forbiddenInheritance,
                requiredMethods,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 신규 엔티티 여부 확인
     *
     * @return ID가 null이면 true
     */
    public boolean isNew() {
        return id.isNew();
    }

    /**
     * ID 할당 (영속화 후 호출)
     *
     * @param id 할당할 ID
     */
    public void assignId(ClassTemplateId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /** 템플릿 정보 업데이트 */
    public void update(ClassTemplateUpdateData data, Instant now) {
        if (data.classTypeId() != null) {
            this.classTypeId = data.classTypeId();
        }
        if (data.templateCode() != null) {
            this.templateCode = data.templateCode();
        }
        if (data.namingPattern() != null) {
            this.namingPattern = data.namingPattern();
        }
        if (data.description() != null) {
            this.description = data.description();
        }
        if (data.requiredAnnotations() != null) {
            this.requiredAnnotations = List.copyOf(data.requiredAnnotations());
        }
        if (data.forbiddenAnnotations() != null) {
            this.forbiddenAnnotations = List.copyOf(data.forbiddenAnnotations());
        }
        if (data.requiredInterfaces() != null) {
            this.requiredInterfaces = List.copyOf(data.requiredInterfaces());
        }
        if (data.forbiddenInheritance() != null) {
            this.forbiddenInheritance = List.copyOf(data.forbiddenInheritance());
        }
        if (data.requiredMethods() != null) {
            this.requiredMethods = List.copyOf(data.requiredMethods());
        }
        this.updatedAt = now;
    }

    /**
     * 클래스 템플릿 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 클래스 템플릿 복원
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    /**
     * 삭제 여부 확인
     *
     * @return 삭제되었으면 true
     */
    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    // Getters
    public ClassTemplateId id() {
        return id;
    }

    public PackageStructureId structureId() {
        return structureId;
    }

    public ClassTypeId classTypeId() {
        return classTypeId;
    }

    public TemplateCode templateCode() {
        return templateCode;
    }

    public NamingPattern namingPattern() {
        return namingPattern;
    }

    public TemplateDescription description() {
        return description;
    }

    public List<String> requiredAnnotations() {
        return requiredAnnotations;
    }

    public List<String> forbiddenAnnotations() {
        return forbiddenAnnotations;
    }

    public List<String> requiredInterfaces() {
        return requiredInterfaces;
    }

    public List<String> forbiddenInheritance() {
        return forbiddenInheritance;
    }

    public List<String> requiredMethods() {
        return requiredMethods;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    // Helper methods
    public boolean hasNamingPattern() {
        return namingPattern != null && !namingPattern.isEmpty();
    }

    public boolean hasRequiredAnnotations() {
        return !requiredAnnotations.isEmpty();
    }

    public boolean hasForbiddenAnnotations() {
        return !forbiddenAnnotations.isEmpty();
    }

    public boolean hasRequiredInterfaces() {
        return !requiredInterfaces.isEmpty();
    }

    public boolean hasForbiddenInheritance() {
        return !forbiddenInheritance.isEmpty();
    }

    public boolean hasRequiredMethods() {
        return !requiredMethods.isEmpty();
    }

    // Value Object 위임 메서드 (Law of Demeter 준수)
    // Persistence Layer Mapper에서 체이닝 방지: domain.id().value() 대신 domain.idValue() 사용

    /**
     * ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return ID 값 (신규인 경우 null)
     */
    public Long idValue() {
        return id != null ? id.value() : null;
    }

    /**
     * Structure ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Structure ID 값
     */
    public Long structureIdValue() {
        return structureId.value();
    }

    /**
     * Class Type ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Class Type ID 값
     */
    public Long classTypeIdValue() {
        return classTypeId != null ? classTypeId.value() : null;
    }

    /**
     * Template Code 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 템플릿 코드 문자열
     */
    public String templateCodeValue() {
        return templateCode.value();
    }

    /**
     * Naming Pattern 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 네이밍 패턴 문자열 (nullable)
     */
    public String namingPatternValue() {
        return namingPattern != null && !namingPattern.isEmpty() ? namingPattern.value() : null;
    }

    /**
     * Description 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 설명 문자열 (nullable)
     */
    public String descriptionValue() {
        return description != null ? description.value() : null;
    }

    /**
     * 삭제 시각 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 삭제 시각 (활성 상태인 경우 null)
     */
    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }
}

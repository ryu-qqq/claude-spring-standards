package com.ryuqq.domain.resourcetemplate.aggregate;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import com.ryuqq.domain.resourcetemplate.vo.TemplateContent;
import com.ryuqq.domain.resourcetemplate.vo.TemplatePath;
import java.time.Instant;

/**
 * ResourceTemplate - 리소스 템플릿 Aggregate Root
 *
 * <p>모듈별 리소스 파일 템플릿을 관리합니다.
 *
 * @author ryu-qqq
 */
public class ResourceTemplate {

    private ResourceTemplateId id;
    private ModuleId moduleId;
    private TemplateCategory category;
    private TemplatePath filePath;
    private FileType fileType;
    private String description;
    private TemplateContent templateContent;
    private boolean required;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected ResourceTemplate() {
        this.createdAt = null;
    }

    private ResourceTemplate(
            ResourceTemplateId id,
            ModuleId moduleId,
            TemplateCategory category,
            TemplatePath filePath,
            FileType fileType,
            String description,
            TemplateContent templateContent,
            boolean required,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.moduleId = moduleId;
        this.category = category;
        this.filePath = filePath;
        this.fileType = fileType;
        this.description = description;
        this.templateContent = templateContent != null ? templateContent : TemplateContent.empty();
        this.required = required;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param moduleId 모듈 ID
     * @param category 템플릿 카테고리
     * @param filePath 파일 경로
     * @param fileType 파일 타입
     * @param description 설명
     * @param templateContent 템플릿 내용
     * @param required 필수 여부
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 ResourceTemplate 인스턴스
     */
    public static ResourceTemplate forNew(
            ModuleId moduleId,
            TemplateCategory category,
            TemplatePath filePath,
            FileType fileType,
            String description,
            TemplateContent templateContent,
            boolean required,
            Instant now) {
        return new ResourceTemplate(
                ResourceTemplateId.forNew(),
                moduleId,
                category,
                filePath,
                fileType,
                description,
                templateContent,
                required,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 리소스 템플릿 ID
     * @param moduleId 모듈 ID
     * @param category 템플릿 카테고리
     * @param filePath 파일 경로
     * @param fileType 파일 타입
     * @param description 설명
     * @param templateContent 템플릿 내용
     * @param required 필수 여부
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return ResourceTemplate 인스턴스
     */
    public static ResourceTemplate of(
            ResourceTemplateId id,
            ModuleId moduleId,
            TemplateCategory category,
            TemplatePath filePath,
            FileType fileType,
            String description,
            TemplateContent templateContent,
            boolean required,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new ResourceTemplate(
                id,
                moduleId,
                category,
                filePath,
                fileType,
                description,
                templateContent,
                required,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 리소스 템플릿 ID
     * @param moduleId 모듈 ID
     * @param category 템플릿 카테고리
     * @param filePath 파일 경로
     * @param fileType 파일 타입
     * @param description 설명
     * @param templateContent 템플릿 내용
     * @param required 필수 여부
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 ResourceTemplate 인스턴스
     */
    public static ResourceTemplate reconstitute(
            ResourceTemplateId id,
            ModuleId moduleId,
            TemplateCategory category,
            TemplatePath filePath,
            FileType fileType,
            String description,
            TemplateContent templateContent,
            boolean required,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                moduleId,
                category,
                filePath,
                fileType,
                description,
                templateContent,
                required,
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
    public void assignId(ResourceTemplateId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 리소스 템플릿 업데이트
     *
     * <p>ResourceTemplateUpdateData의 Optional 필드 중 present인 항목만 업데이트합니다.
     *
     * @param updateData 업데이트 데이터
     * @param now 업데이트 시각
     */
    public void update(ResourceTemplateUpdateData updateData, Instant now) {
        if (updateData == null || !updateData.hasUpdates()) {
            return;
        }

        updateData.category().ifPresent(value -> this.category = value);
        updateData.filePath().ifPresent(value -> this.filePath = value);
        updateData.fileType().ifPresent(value -> this.fileType = value);
        updateData.description().ifPresent(value -> this.description = value);
        updateData.templateContent().ifPresent(value -> this.templateContent = value);
        updateData.required().ifPresent(value -> this.required = value);

        this.updatedAt = now;
    }

    /**
     * 리소스 템플릿 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 리소스 템플릿 복원
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

    /** 설정 파일인지 확인 */
    public boolean isConfig() {
        return category.isConfig();
    }

    /** 필수 템플릿인지 확인 */
    public boolean isRequired() {
        return required;
    }

    /** 템플릿 내용이 있는지 확인 */
    public boolean hasContent() {
        return templateContent.hasContent();
    }

    // Getters
    public ResourceTemplateId id() {
        return id;
    }

    public ModuleId moduleId() {
        return moduleId;
    }

    public TemplateCategory category() {
        return category;
    }

    public TemplatePath filePath() {
        return filePath;
    }

    public FileType fileType() {
        return fileType;
    }

    public String description() {
        return description;
    }

    public TemplateContent templateContent() {
        return templateContent;
    }

    public boolean required() {
        return required;
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
     * Module ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Module ID 값
     */
    public Long moduleIdValue() {
        return moduleId.value();
    }

    /**
     * Category 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Category enum 이름
     */
    public String categoryName() {
        return category.name();
    }

    /**
     * File Path 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 파일 경로 문자열
     */
    public String filePathValue() {
        return filePath.value();
    }

    /**
     * File Type 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return File Type enum 이름
     */
    public String fileTypeName() {
        return fileType.name();
    }

    /**
     * Template Content 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 템플릿 내용 문자열 (nullable)
     */
    public String templateContentValue() {
        return templateContent != null ? templateContent.value() : null;
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

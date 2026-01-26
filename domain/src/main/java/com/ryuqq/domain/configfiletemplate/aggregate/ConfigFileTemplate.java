package com.ryuqq.domain.configfiletemplate.aggregate;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.configfiletemplate.vo.DisplayOrder;
import com.ryuqq.domain.configfiletemplate.vo.FileName;
import com.ryuqq.domain.configfiletemplate.vo.FilePath;
import com.ryuqq.domain.configfiletemplate.vo.TemplateCategory;
import com.ryuqq.domain.configfiletemplate.vo.TemplateContent;
import com.ryuqq.domain.configfiletemplate.vo.TemplateDescription;
import com.ryuqq.domain.configfiletemplate.vo.TemplateVariables;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Instant;

/**
 * ConfigFileTemplate - 설정 파일 템플릿 Aggregate Root
 *
 * <p>AI 도구 설정 파일 템플릿을 관리합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class ConfigFileTemplate {

    private ConfigFileTemplateId id;
    private TechStackId techStackId;
    private ArchitectureId architectureId;
    private ToolType toolType;
    private FilePath filePath;
    private FileName fileName;
    private TemplateContent content;
    private TemplateCategory category;
    private TemplateDescription description;
    private TemplateVariables variables;
    private DisplayOrder displayOrder;
    private boolean isRequired;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected ConfigFileTemplate() {
        this.createdAt = null;
    }

    private ConfigFileTemplate(
            ConfigFileTemplateId id,
            TechStackId techStackId,
            ArchitectureId architectureId,
            ToolType toolType,
            FilePath filePath,
            FileName fileName,
            TemplateContent content,
            TemplateCategory category,
            TemplateDescription description,
            TemplateVariables variables,
            DisplayOrder displayOrder,
            boolean isRequired,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.techStackId = techStackId;
        this.architectureId = architectureId;
        this.toolType = toolType;
        this.filePath = filePath;
        this.fileName = fileName;
        this.content = content != null ? content : TemplateContent.empty();
        this.category = category;
        this.description = description != null ? description : TemplateDescription.empty();
        this.variables = variables != null ? variables : TemplateVariables.empty();
        this.displayOrder = displayOrder != null ? displayOrder : DisplayOrder.defaultOrder();
        this.isRequired = isRequired;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param techStackId 기술 스택 ID
     * @param architectureId 아키텍처 ID (nullable)
     * @param toolType AI 도구 타입
     * @param filePath 파일 경로
     * @param fileName 파일명
     * @param content 템플릿 내용
     * @param category 템플릿 카테고리 (nullable)
     * @param description 템플릿 설명 (nullable)
     * @param variables 템플릿 변수 (nullable)
     * @param displayOrder 표시 순서 (nullable)
     * @param isRequired 필수 여부
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 ConfigFileTemplate 인스턴스
     */
    public static ConfigFileTemplate forNew(
            TechStackId techStackId,
            ArchitectureId architectureId,
            ToolType toolType,
            FilePath filePath,
            FileName fileName,
            TemplateContent content,
            TemplateCategory category,
            TemplateDescription description,
            TemplateVariables variables,
            DisplayOrder displayOrder,
            boolean isRequired,
            Instant now) {
        return new ConfigFileTemplate(
                ConfigFileTemplateId.forNew(),
                techStackId,
                architectureId,
                toolType,
                filePath,
                fileName,
                content,
                category,
                description,
                variables,
                displayOrder,
                isRequired,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @return 복원된 ConfigFileTemplate 인스턴스
     */
    public static ConfigFileTemplate reconstitute(
            ConfigFileTemplateId id,
            TechStackId techStackId,
            ArchitectureId architectureId,
            ToolType toolType,
            FilePath filePath,
            FileName fileName,
            TemplateContent content,
            TemplateCategory category,
            TemplateDescription description,
            TemplateVariables variables,
            DisplayOrder displayOrder,
            boolean isRequired,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new ConfigFileTemplate(
                id,
                techStackId,
                architectureId,
                toolType,
                filePath,
                fileName,
                content,
                category,
                description,
                variables,
                displayOrder,
                isRequired,
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
    public void assignId(ConfigFileTemplateId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 설정 파일 템플릿 정보 수정
     *
     * @param data 수정 데이터
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void update(ConfigFileTemplateUpdateData data, Instant now) {
        this.toolType = data.toolType();
        this.filePath = data.filePath();
        this.fileName = data.fileName();
        this.content = data.content();
        this.category = data.category();
        this.description = data.description();
        this.variables = data.variables();
        this.displayOrder = data.displayOrder();
        this.isRequired = data.isRequired();
        this.updatedAt = now;
    }

    /**
     * 설정 파일 템플릿 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 설정 파일 템플릿 복원
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
    public ConfigFileTemplateId id() {
        return id;
    }

    public TechStackId techStackId() {
        return techStackId;
    }

    public ArchitectureId architectureId() {
        return architectureId;
    }

    public ToolType toolType() {
        return toolType;
    }

    public FilePath filePath() {
        return filePath;
    }

    public FileName fileName() {
        return fileName;
    }

    public TemplateContent content() {
        return content;
    }

    public TemplateCategory category() {
        return category;
    }

    public TemplateDescription description() {
        return description;
    }

    public TemplateVariables variables() {
        return variables;
    }

    public DisplayOrder displayOrder() {
        return displayOrder;
    }

    public boolean isRequired() {
        return isRequired;
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

    /**
     * ID 원시값 반환
     *
     * @return ID 값 (신규인 경우 null)
     */
    public Long idValue() {
        return id != null ? id.value() : null;
    }

    /**
     * Tech Stack ID 원시값 반환
     *
     * @return Tech Stack ID 값
     */
    public Long techStackIdValue() {
        return techStackId.value();
    }

    /**
     * Architecture ID 원시값 반환
     *
     * @return Architecture ID 값 (nullable)
     */
    public Long architectureIdValue() {
        return architectureId != null ? architectureId.value() : null;
    }

    /**
     * Tool Type 이름 반환
     *
     * @return Tool Type enum 이름
     */
    public String toolTypeName() {
        return toolType.name();
    }

    /**
     * File Path 원시값 반환
     *
     * @return 파일 경로 문자열
     */
    public String filePathValue() {
        return filePath.value();
    }

    /**
     * File Name 원시값 반환
     *
     * @return 파일명 문자열
     */
    public String fileNameValue() {
        return fileName.value();
    }

    /**
     * Content 원시값 반환
     *
     * @return 내용 문자열
     */
    public String contentValue() {
        return content.value();
    }

    /**
     * Category 이름 반환
     *
     * @return Category enum 이름 (nullable)
     */
    public String categoryName() {
        return category != null ? category.name() : null;
    }

    /**
     * Description 원시값 반환
     *
     * @return 설명 문자열 (nullable)
     */
    public String descriptionValue() {
        return description != null ? description.value() : null;
    }

    /**
     * Variables 원시값 반환
     *
     * @return 변수 JSON 문자열 (nullable)
     */
    public String variablesValue() {
        return variables != null ? variables.value() : null;
    }

    /**
     * Display Order 원시값 반환
     *
     * @return 표시 순서 정수
     */
    public Integer displayOrderValue() {
        return displayOrder != null ? displayOrder.value() : 0;
    }

    /**
     * 삭제 시각 반환
     *
     * @return 삭제 시각 (활성 상태인 경우 null)
     */
    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }
}

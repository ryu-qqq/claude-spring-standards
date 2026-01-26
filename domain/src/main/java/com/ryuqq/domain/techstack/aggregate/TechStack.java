package com.ryuqq.domain.techstack.aggregate;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.vo.BuildConfigFile;
import com.ryuqq.domain.techstack.vo.BuildToolType;
import com.ryuqq.domain.techstack.vo.FrameworkModules;
import com.ryuqq.domain.techstack.vo.FrameworkType;
import com.ryuqq.domain.techstack.vo.FrameworkVersion;
import com.ryuqq.domain.techstack.vo.LanguageFeatures;
import com.ryuqq.domain.techstack.vo.LanguageType;
import com.ryuqq.domain.techstack.vo.LanguageVersion;
import com.ryuqq.domain.techstack.vo.PlatformType;
import com.ryuqq.domain.techstack.vo.RuntimeEnvironment;
import com.ryuqq.domain.techstack.vo.TechStackName;
import com.ryuqq.domain.techstack.vo.TechStackStatus;
import java.time.Instant;

/**
 * TechStack - 기술 스택 Aggregate Root
 *
 * <p>언어, 프레임워크, 빌드 도구 조합을 정의합니다.
 *
 * @author ryu-qqq
 */
@SuppressWarnings("PMD.TooManyFields")
public class TechStack {

    private TechStackId id;
    private TechStackName name;
    private TechStackStatus status;

    // Language
    private LanguageType languageType;
    private LanguageVersion languageVersion;
    private LanguageFeatures languageFeatures;

    // Framework
    private FrameworkType frameworkType;
    private FrameworkVersion frameworkVersion;
    private FrameworkModules frameworkModules;

    // Platform & Build
    private PlatformType platformType;
    private RuntimeEnvironment runtimeEnvironment;
    private BuildToolType buildToolType;
    private BuildConfigFile buildConfigFile;

    private ReferenceLinks referenceLinks;

    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected TechStack() {
        this.createdAt = null;
    }

    private TechStack(
            TechStackId id,
            TechStackName name,
            TechStackStatus status,
            LanguageType languageType,
            LanguageVersion languageVersion,
            LanguageFeatures languageFeatures,
            FrameworkType frameworkType,
            FrameworkVersion frameworkVersion,
            FrameworkModules frameworkModules,
            PlatformType platformType,
            RuntimeEnvironment runtimeEnvironment,
            BuildToolType buildToolType,
            BuildConfigFile buildConfigFile,
            ReferenceLinks referenceLinks,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.languageType = languageType;
        this.languageVersion = languageVersion;
        this.languageFeatures =
                languageFeatures != null ? languageFeatures : LanguageFeatures.empty();
        this.frameworkType = frameworkType;
        this.frameworkVersion = frameworkVersion;
        this.frameworkModules =
                frameworkModules != null ? frameworkModules : FrameworkModules.empty();
        this.platformType = platformType;
        this.runtimeEnvironment = runtimeEnvironment;
        this.buildToolType = buildToolType;
        this.buildConfigFile = buildConfigFile;
        this.referenceLinks = referenceLinks != null ? referenceLinks : ReferenceLinks.empty();
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param name 기술 스택 이름
     * @param languageType 언어 타입
     * @param languageVersion 언어 버전
     * @param languageFeatures 언어 기능
     * @param frameworkType 프레임워크 타입
     * @param frameworkVersion 프레임워크 버전
     * @param frameworkModules 프레임워크 모듈
     * @param platformType 플랫폼 타입
     * @param runtimeEnvironment 런타임 환경
     * @param buildToolType 빌드 도구 타입
     * @param buildConfigFile 빌드 설정 파일
     * @param referenceLinks 참조 링크 목록
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 TechStack 인스턴스
     */
    public static TechStack forNew(
            TechStackName name,
            LanguageType languageType,
            LanguageVersion languageVersion,
            LanguageFeatures languageFeatures,
            FrameworkType frameworkType,
            FrameworkVersion frameworkVersion,
            FrameworkModules frameworkModules,
            PlatformType platformType,
            RuntimeEnvironment runtimeEnvironment,
            BuildToolType buildToolType,
            BuildConfigFile buildConfigFile,
            ReferenceLinks referenceLinks,
            Instant now) {
        return new TechStack(
                TechStackId.forNew(),
                name,
                TechStackStatus.ACTIVE,
                languageType,
                languageVersion,
                languageFeatures,
                frameworkType,
                frameworkVersion,
                frameworkModules,
                platformType,
                runtimeEnvironment,
                buildToolType,
                buildConfigFile,
                referenceLinks,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 기술 스택 ID
     * @param name 기술 스택 이름
     * @param status 기술 스택 상태
     * @param languageType 언어 타입
     * @param languageVersion 언어 버전
     * @param languageFeatures 언어 기능
     * @param frameworkType 프레임워크 타입
     * @param frameworkVersion 프레임워크 버전
     * @param frameworkModules 프레임워크 모듈
     * @param platformType 플랫폼 타입
     * @param runtimeEnvironment 런타임 환경
     * @param buildToolType 빌드 도구 타입
     * @param buildConfigFile 빌드 설정 파일
     * @param referenceLinks 참조 링크 목록
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return TechStack 인스턴스
     */
    public static TechStack of(
            TechStackId id,
            TechStackName name,
            TechStackStatus status,
            LanguageType languageType,
            LanguageVersion languageVersion,
            LanguageFeatures languageFeatures,
            FrameworkType frameworkType,
            FrameworkVersion frameworkVersion,
            FrameworkModules frameworkModules,
            PlatformType platformType,
            RuntimeEnvironment runtimeEnvironment,
            BuildToolType buildToolType,
            BuildConfigFile buildConfigFile,
            ReferenceLinks referenceLinks,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new TechStack(
                id,
                name,
                status,
                languageType,
                languageVersion,
                languageFeatures,
                frameworkType,
                frameworkVersion,
                frameworkModules,
                platformType,
                runtimeEnvironment,
                buildToolType,
                buildConfigFile,
                referenceLinks,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 기술 스택 ID
     * @param name 기술 스택 이름
     * @param status 기술 스택 상태
     * @param languageType 언어 타입
     * @param languageVersion 언어 버전
     * @param languageFeatures 언어 기능
     * @param frameworkType 프레임워크 타입
     * @param frameworkVersion 프레임워크 버전
     * @param frameworkModules 프레임워크 모듈
     * @param platformType 플랫폼 타입
     * @param runtimeEnvironment 런타임 환경
     * @param buildToolType 빌드 도구 타입
     * @param buildConfigFile 빌드 설정 파일
     * @param referenceLinks 참조 링크 목록
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 TechStack 인스턴스
     */
    public static TechStack reconstitute(
            TechStackId id,
            TechStackName name,
            TechStackStatus status,
            LanguageType languageType,
            LanguageVersion languageVersion,
            LanguageFeatures languageFeatures,
            FrameworkType frameworkType,
            FrameworkVersion frameworkVersion,
            FrameworkModules frameworkModules,
            PlatformType platformType,
            RuntimeEnvironment runtimeEnvironment,
            BuildToolType buildToolType,
            BuildConfigFile buildConfigFile,
            ReferenceLinks referenceLinks,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                name,
                status,
                languageType,
                languageVersion,
                languageFeatures,
                frameworkType,
                frameworkVersion,
                frameworkModules,
                platformType,
                runtimeEnvironment,
                buildToolType,
                buildConfigFile,
                referenceLinks,
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
    public void assignId(TechStackId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 기술 스택 정보 수정
     *
     * @param data 수정 데이터
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void update(TechStackUpdateData data, Instant now) {
        this.name = data.name();
        this.status = data.status();
        this.languageType = data.languageType();
        this.languageVersion = data.languageVersion();
        this.languageFeatures = data.languageFeatures();
        this.frameworkType = data.frameworkType();
        this.frameworkVersion = data.frameworkVersion();
        this.frameworkModules = data.frameworkModules();
        this.platformType = data.platformType();
        this.runtimeEnvironment = data.runtimeEnvironment();
        this.buildToolType = data.buildToolType();
        this.buildConfigFile = data.buildConfigFile();
        this.updatedAt = now;
    }

    /**
     * 비활성화 (Deprecated)
     *
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void deprecate(Instant now) {
        this.status = TechStackStatus.DEPRECATED;
        this.updatedAt = now;
    }

    /**
     * 보관 처리 (Archive)
     *
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void archive(Instant now) {
        this.status = TechStackStatus.ARCHIVED;
        this.updatedAt = now;
    }

    /**
     * 기술 스택 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 기술 스택 복원
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
    public TechStackId id() {
        return id;
    }

    public TechStackName name() {
        return name;
    }

    public TechStackStatus status() {
        return status;
    }

    public LanguageType languageType() {
        return languageType;
    }

    public LanguageVersion languageVersion() {
        return languageVersion;
    }

    public LanguageFeatures languageFeatures() {
        return languageFeatures;
    }

    public FrameworkType frameworkType() {
        return frameworkType;
    }

    public FrameworkVersion frameworkVersion() {
        return frameworkVersion;
    }

    public FrameworkModules frameworkModules() {
        return frameworkModules;
    }

    public PlatformType platformType() {
        return platformType;
    }

    public RuntimeEnvironment runtimeEnvironment() {
        return runtimeEnvironment;
    }

    public BuildToolType buildToolType() {
        return buildToolType;
    }

    public BuildConfigFile buildConfigFile() {
        return buildConfigFile;
    }

    public ReferenceLinks referenceLinks() {
        return referenceLinks;
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
     * Name 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 이름 문자열
     */
    public String nameValue() {
        return name.value();
    }

    /**
     * Status 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Status enum 이름
     */
    public String statusName() {
        return status.name();
    }

    /**
     * Language Type 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Language Type enum 이름
     */
    public String languageTypeName() {
        return languageType.name();
    }

    /**
     * Language Version 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 언어 버전 문자열
     */
    public String languageVersionValue() {
        return languageVersion.value();
    }

    /**
     * Framework Type 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Framework Type enum 이름
     */
    public String frameworkTypeName() {
        return frameworkType.name();
    }

    /**
     * Framework Version 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 프레임워크 버전 문자열
     */
    public String frameworkVersionValue() {
        return frameworkVersion.value();
    }

    /**
     * Platform Type 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Platform Type enum 이름
     */
    public String platformTypeName() {
        return platformType.name();
    }

    /**
     * Runtime Environment 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Runtime Environment enum 이름
     */
    public String runtimeEnvironmentName() {
        return runtimeEnvironment.name();
    }

    /**
     * Build Tool Type 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Build Tool Type enum 이름
     */
    public String buildToolTypeName() {
        return buildToolType.name();
    }

    /**
     * Build Config File 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 빌드 설정 파일 문자열
     */
    public String buildConfigFileValue() {
        return buildConfigFile.value();
    }

    /**
     * Reference Links 원시값 목록 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 참조 링크 문자열 목록
     */
    public java.util.List<String> referenceLinkValues() {
        return referenceLinks.values();
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

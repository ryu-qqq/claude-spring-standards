package com.ryuqq.domain.archunittest.aggregate;

import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestDescription;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestName;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSeverity;
import com.ryuqq.domain.archunittest.vo.TestCode;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Instant;
import java.util.Objects;

/**
 * ArchUnitTest - ArchUnit 테스트 Aggregate Root
 *
 * <p>ArchUnit 기반의 아키텍처 테스트를 표현하는 도메인 객체입니다. 레이어 의존성, 네이밍 규칙, 어노테이션 검사 등의 테스트 코드를 관리합니다.
 *
 * @author ryu-qqq
 */
public class ArchUnitTest {

    private ArchUnitTestId id;
    private PackageStructureId structureId;
    private String code;
    private ArchUnitTestName name;
    private ArchUnitTestDescription description;
    private String testClassName;
    private String testMethodName;
    private TestCode testCode;
    private ArchUnitTestSeverity severity;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected ArchUnitTest() {
        this.createdAt = null;
    }

    private ArchUnitTest(
            ArchUnitTestId id,
            PackageStructureId structureId,
            String code,
            ArchUnitTestName name,
            ArchUnitTestDescription description,
            String testClassName,
            String testMethodName,
            TestCode testCode,
            ArchUnitTestSeverity severity,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.structureId = structureId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.testClassName = testClassName;
        this.testMethodName = testMethodName;
        this.testCode = testCode;
        this.severity = severity;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param structureId 패키지 구조 ID (필수)
     * @param code 테스트 코드 식별자
     * @param name 테스트 이름
     * @param description 테스트 설명
     * @param testClassName 테스트 클래스 이름
     * @param testMethodName 테스트 메서드 이름
     * @param testCode 테스트 코드 내용
     * @param severity 심각도
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 ArchUnitTest 인스턴스
     */
    public static ArchUnitTest forNew(
            PackageStructureId structureId,
            String code,
            ArchUnitTestName name,
            ArchUnitTestDescription description,
            String testClassName,
            String testMethodName,
            TestCode testCode,
            ArchUnitTestSeverity severity,
            Instant now) {
        if (structureId == null) {
            throw new IllegalArgumentException("structureId must not be null");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code must not be null or blank");
        }
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (testCode == null) {
            throw new IllegalArgumentException("testCode must not be null");
        }
        return new ArchUnitTest(
                ArchUnitTestId.forNew(),
                structureId,
                code,
                name,
                description,
                testClassName,
                testMethodName,
                testCode,
                severity,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id ArchUnit 테스트 ID
     * @param structureId 패키지 구조 ID (필수)
     * @param code 테스트 코드 식별자
     * @param name 테스트 이름
     * @param description 테스트 설명
     * @param testClassName 테스트 클래스 이름
     * @param testMethodName 테스트 메서드 이름
     * @param testCode 테스트 코드 내용
     * @param severity 심각도
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return ArchUnitTest 인스턴스
     */
    public static ArchUnitTest of(
            ArchUnitTestId id,
            PackageStructureId structureId,
            String code,
            ArchUnitTestName name,
            ArchUnitTestDescription description,
            String testClassName,
            String testMethodName,
            TestCode testCode,
            ArchUnitTestSeverity severity,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new ArchUnitTest(
                id,
                structureId,
                code,
                name,
                description,
                testClassName,
                testMethodName,
                testCode,
                severity,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id ArchUnit 테스트 ID
     * @param structureId 패키지 구조 ID (필수)
     * @param code 테스트 코드 식별자
     * @param name 테스트 이름
     * @param description 테스트 설명
     * @param testClassName 테스트 클래스 이름
     * @param testMethodName 테스트 메서드 이름
     * @param testCode 테스트 코드 내용
     * @param severity 심각도
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 ArchUnitTest 인스턴스
     */
    public static ArchUnitTest reconstitute(
            ArchUnitTestId id,
            PackageStructureId structureId,
            String code,
            ArchUnitTestName name,
            ArchUnitTestDescription description,
            String testClassName,
            String testMethodName,
            TestCode testCode,
            ArchUnitTestSeverity severity,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                structureId,
                code,
                name,
                description,
                testClassName,
                testMethodName,
                testCode,
                severity,
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
    public void assignId(ArchUnitTestId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * ArchUnit 테스트 정보 수정
     *
     * @param updateData 수정 데이터 (null 필드는 변경 없음)
     * @param now 수정 시각
     */
    public void update(ArchUnitTestUpdateData updateData, Instant now) {
        if (updateData.code() != null) {
            this.code = updateData.code();
        }
        if (updateData.name() != null) {
            this.name = updateData.name();
        }
        if (updateData.description() != null) {
            this.description = updateData.description();
        }
        if (updateData.testClassName() != null) {
            this.testClassName = updateData.testClassName();
        }
        if (updateData.testMethodName() != null) {
            this.testMethodName = updateData.testMethodName();
        }
        if (updateData.testCode() != null) {
            this.testCode = updateData.testCode();
        }
        if (updateData.severity() != null) {
            this.severity = updateData.severity();
        }
        this.updatedAt = now;
    }

    /**
     * ArchUnit 테스트 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 테스트 복원
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    // === Getters ===

    public ArchUnitTestId id() {
        return id;
    }

    public PackageStructureId structureId() {
        return structureId;
    }

    public String code() {
        return code;
    }

    public ArchUnitTestName name() {
        return name;
    }

    public ArchUnitTestDescription description() {
        return description;
    }

    public String testClassName() {
        return testClassName;
    }

    public String testMethodName() {
        return testMethodName;
    }

    public TestCode testCode() {
        return testCode;
    }

    public ArchUnitTestSeverity severity() {
        return severity;
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

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    // === Helper Methods ===

    public boolean hasTestClassName() {
        return testClassName != null && !testClassName.isBlank();
    }

    public boolean hasTestMethodName() {
        return testMethodName != null && !testMethodName.isBlank();
    }

    public boolean hasSeverity() {
        return severity != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArchUnitTest that = (ArchUnitTest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
     * Name 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 테스트 이름 문자열
     */
    public String nameValue() {
        return name.value();
    }

    /**
     * Description 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 테스트 설명 문자열
     */
    public String descriptionValue() {
        return description != null ? description.value() : null;
    }

    /**
     * Test Code 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 테스트 코드 문자열
     */
    public String testCodeValue() {
        return testCode.value();
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

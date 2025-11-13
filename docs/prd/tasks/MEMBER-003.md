# MEMBER-003: Persistence Layer 구현

**Epic**: 간단한 회원 가입
**Layer**: Persistence Layer (MySQL)
**브랜치**: feature/MEMBER-003-persistence
**Jira URL**: (sync-to-jira 후 추가)

---

## 📝 목적

Member Aggregate의 영속성을 담당합니다.
- MemberEntity: JPA Entity 설계
- Repository: JPA + QueryDSL 쿼리
- Adapter: Application Port 구현
- Mapper: Domain ↔ Entity 변환

---

## 🎯 요구사항

### JPA Entity
- [ ] **MemberEntity 설계**
  - 테이블명: `members`
  - 컬럼:
    - `id` BIGINT PRIMARY KEY AUTO_INCREMENT
    - `email` VARCHAR(320) NOT NULL UNIQUE
    - `password` VARCHAR(100) NOT NULL
    - `created_at` TIMESTAMP NOT NULL
    - `updated_at` TIMESTAMP NOT NULL
  - 인덱스:
    - `idx_members_email` UNIQUE INDEX (email)

### Repository
- [ ] **MemberJpaRepository** (JPA 기본 CRUD)
  - `Optional<MemberEntity> findById(Long id)`
  - `Optional<MemberEntity> findByEmail(String email)`
  - `MemberEntity save(MemberEntity entity)`
  - `boolean existsByEmail(String email)`

- [ ] **MemberQueryDslRepository** (복잡한 조회)
  - `boolean existsByEmail(String email)` - 이메일 중복 확인
  - ⚠️ DTO Projection 직접 사용 (Entity 조회 후 변환 금지)

### QueryDSL DTO Projection 전략
- [ ] **DTO Projection 직접 사용**
  ```java
  // ❌ Entity 조회 후 변환 (N+1 위험)
  MemberEntity entity = queryFactory.selectFrom(member).fetchOne();
  return MemberMapper.toDto(entity);

  // ✅ DTO Projection 직접 사용
  return queryFactory
      .select(Projections.constructor(MemberDto.class,
          member.id,
          member.email,
          member.createdAt
      ))
      .from(member)
      .fetchOne();
  ```

### Adapter 구현
- [ ] **SaveMemberAdapter** (Command Adapter)
  - `SaveMemberCommandPort` 인터페이스 구현
  - `MemberJpaRepository` 사용
  - `MemberMapper` 사용 (Domain → Entity)

- [ ] **CheckDuplicateEmailAdapter** (Query Adapter)
  - `CheckDuplicateEmailQueryPort` 인터페이스 구현
  - `MemberQueryDslRepository` 사용
  - DTO Projection으로 성능 최적화

### Mapper
- [ ] **MemberMapper**
  - `MemberEntity toEntity(Member domain)`
  - `Member toDomain(MemberEntity entity)`

### Flyway Migration
- [ ] **V1__create_members_table.sql**
  ```sql
  CREATE TABLE members (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      email VARCHAR(320) NOT NULL,
      password VARCHAR(100) NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      UNIQUE INDEX idx_members_email (email)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
  ```

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙
- [ ] **Lombok 금지**
  - ❌ `@Getter`, `@Setter`, `@Builder` 사용 불가
  - ✅ Plain JPA 어노테이션만 사용

- [ ] **JPA 관계 어노테이션 금지**
  - ❌ `@OneToMany`, `@ManyToOne`, `@OneToOne` 사용 불가
  - ✅ Long FK 전략: 다른 Entity 참조 시 `Long userId` 사용

- [ ] **Long FK 전략**
  ```java
  // ❌ JPA 관계 어노테이션
  @ManyToOne
  private UserEntity user;

  // ✅ Long FK 전략
  private Long userId;
  ```

- [ ] **QueryDSL DTO Projection 필수**
  - ❌ Entity 조회 후 DTO 변환 금지
  - ✅ Projections.constructor() 직접 사용

### 테스트 규칙
- [ ] ArchUnit 테스트 필수
  - Entity는 `Entity` suffix 필수
  - Repository는 `Repository` suffix 필수
  - Adapter는 `Adapter` suffix 필수

- [ ] TestFixture 사용 필수
  - `MemberEntityFixture.java`

- [ ] 테스트 커버리지 > 85%
  - Repository 저장/조회 테스트
  - QueryDSL DTO Projection 테스트
  - Adapter 통합 테스트
  - Mapper 변환 테스트

### 성능 최적화
- [ ] 이메일 중복 확인 쿼리 < 100ms
- [ ] DB 인덱스 활용 (email UNIQUE INDEX)
- [ ] QueryDSL DTO Projection으로 N+1 방지

---

## ✅ 완료 조건

- [ ] 모든 요구사항 구현 완료
  - MemberEntity
  - Repository (JPA + QueryDSL)
  - Adapter (Command + Query)
  - Mapper
  - Flyway Migration

- [ ] 모든 테스트 통과 (Unit + Integration + ArchUnit)
  - `MemberJpaRepositoryTest.java`
  - `MemberQueryDslRepositoryTest.java`
  - `SaveMemberAdapterTest.java`
  - `CheckDuplicateEmailAdapterTest.java`
  - `MemberMapperTest.java`
  - `JpaRepositoryArchTest.java`

- [ ] Zero-Tolerance 규칙 준수
  - Lombok 미사용 확인
  - JPA 관계 어노테이션 미사용 확인
  - Long FK 전략 확인
  - QueryDSL DTO Projection 확인

- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- **PRD**: `docs/prd/simple-member-signup.md`
- **Plan**: `docs/prd/plans/MEMBER-003-persistence-plan.md` (create-plan 후 생성)
- **Jira**: (sync-to-jira 후 추가)
- **코딩 규칙**: `docs/coding_convention/04-persistence-layer/mysql/persistence-mysql-guide.md`

---

## 📚 참고 규칙

- `docs/coding_convention/04-persistence-layer/mysql/persistence-mysql-guide.md`
- `docs/coding_convention/04-persistence-layer/mysql/entity/entity-guide.md`
- `docs/coding_convention/04-persistence-layer/mysql/repository/jpa-repository-guide.md`
- `docs/coding_convention/04-persistence-layer/mysql/repository/querydsl-repository-guide.md`
- `docs/coding_convention/04-persistence-layer/mysql/adapter/command/command-adapter-guide.md`
- `docs/coding_convention/04-persistence-layer/mysql/adapter/query/query-adapter-guide.md`
- `docs/coding_convention/04-persistence-layer/mysql/mapper/mapper-guide.md`

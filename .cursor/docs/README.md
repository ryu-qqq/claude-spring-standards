# Cursor AI Docs Index

이 디렉토리는 Cursor AI가 참조할 수 있는 프로젝트 문서들을 모아놓은 곳입니다.

## 📚 문서 구조

### 1. 코딩 컨벤션 (Coding Conventions)
- **위치**: `coding_convention/` (symlink → `../../docs/coding_convention/`)
- **파일 수**: 86개 규칙
- **총 라인**: 41,425 라인
- **내용**: Spring Boot 3.5 + Java 21 헥사고날 아키텍처 표준

#### 레이어별 규칙
- `00-project-setup/` - 프로젝트 구조 (2개 규칙)
- `01-adapter-in-layer/rest-api/` - REST API (22개 규칙)
- `02-domain-layer/` - Domain Layer (12개 규칙)
- `03-application-layer/` - Application Layer (26개 규칙)
- `04-persistence-layer/` - Persistence Layer (23개 규칙)
- `05-testing/` - Testing (3개 규칙)

## 🎯 사용 방법

### Cursor AI에게 컨벤션 참조 요청
```
@coding_convention/02-domain-layer/aggregate/guide.md
```

### 특정 레이어 규칙 참조
```
@coding_convention/03-application-layer/
```

### 전체 컨벤션 검색
```
@coding_convention
```

## 🚨 Zero-Tolerance 규칙

Cursor AI가 반드시 따라야 할 절대 규칙:

1. **Lombok 금지** - Domain, JPA Entity, Orchestration
2. **Law of Demeter** - Getter 체이닝 금지
3. **Long FK 전략** - JPA 관계 어노테이션 금지
4. **Transaction 경계** - `@Transactional` 내 외부 API 호출 금지
5. **Orchestration Pattern** - executeInternal() @Async 필수

상세 내용: `.cursorrules` 파일 참조

## 📖 추가 문서 (향후)

- `architecture/` - 아키텍처 다이어그램 및 설명
- `api/` - API 문서 및 스펙
- `decisions/` - ADR (Architecture Decision Records)

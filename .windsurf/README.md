# Cascade Workflows - 자동화 & 검증 시스템

**✅ Cascade 완전 호환**: 이 디렉토리의 모든 파일은 IntelliJ Cascade가 직접 실행할 수 있는 Markdown 형식입니다.

---

## 🎯 설계 철학

### Claude Code vs Cascade 역할 분담

| 항목 | Claude Code | Cascade Workflows |
|------|-------------|-------------------|
| **강점** | 컨텍스트 유지, 비즈니스 로직 | 빠른 자동화, 파이프라인 |
| **약점** | 단순 반복 작업 느림 | 컨텍스트 이해 부족 |
| **용도** | 도메인 코드 생성, 상세 구현 | 검증, 테스트, 빌드, 배포 |
| **예시** | OrderDomain 비즈니스 로직 구현 | 컨벤션 체크, Unit 테스트 실행 |

### 통합 워크플로우 예시

```
1. Claude Code: 비즈니스 로직 구현
   /sc:implement Order aggregate with place/cancel/confirm

2. Cascade: 컨벤션 자동 검증
   /validate-conventions
   → 실패 시 TODO 주석 자동 추가

3. Claude Code: TODO 수정
   "Fix the TODO comments in OrderDomain.java"

4. Cascade: 테스트 실행
   /run-unit-tests

5. Cascade: 빌드
   /build-docker

6. Cascade: 배포
   /deploy-dev
```

---

## 📂 디렉토리 구조 (SSOT 적용)

```
프로젝트 루트/
├── tools/pipeline/                    # 단일 진실의 원천 (SSOT)
│   ├── common.sh                      # 공통 설정 및 헬퍼
│   ├── validate_conventions.sh        # 컨벤션 검증 로직
│   ├── test_unit.sh                   # 유닛 테스트 로직
│   └── pr_gate.sh                     # PR 게이트 파이프라인
│
└── .windsurf/
    ├── README.md (이 파일)
    ├── rules.md ⭐ (핵심 규칙 - 7,000자, Cascade 자동 로드)
    └── workflows/ (14개 Markdown - SSOT 얇은 래퍼)
        ├── 검증 (3개)
        ├── 테스트 (4개)
        ├── 빌드/배포 (4개)
        ├── 파이프라인 (3개)
        ├── Git 자동화 (4개)
        ├── 코드 품질 (3개)
        └── 메트릭 (1개)
            └── upload-langfuse.md
```

**주요 사항**:
- ✅ **SSOT 적용**: 실제 로직은 `tools/pipeline/`에만 존재
- ✅ **얇은 래퍼**: Windsurf workflows는 스크립트 호출만
- ✅ **No Drift**: Windsurf와 CI가 동일한 스크립트 사용
---

## 📋 Rules 파일 정보

### rules.md (Cascade 자동 로드) ⭐

**파일 정보**:
- **크기**: ~7,000자 (322줄)
- **내용**: Zero-Tolerance 규칙 9개 + 간단한 예시
- **형식**: Cascade 최적화 버전 (구 rules-core.md)

**장점**:
- ✅ Cascade의 11,500자 권장 사이즈 내
- ✅ 빠른 로딩 및 적용
- ✅ 핵심 규칙에 집중
- ✅ 메모리 효율적

**사용 방법**:
- Windsurf IDE가 자동으로 로드
- `.windsurf/rules.md` 파일명으로 자동 인식
- 상세 규칙은 `docs/coding_convention/`에서 참조

### 권장 사용 전략

```
1. Windsurf IDE 작업: rules.md 자동 로드됨
2. 상세 규칙 참조: docs/coding_convention/ 문서 읽기
3. 자동 검증: tools/pipeline/validate_conventions.sh 실행
4. Cache 시스템: .claude/cache/rules/ 고속 검색
```

---

## 🚀 Cascade Workflows (14개) - 최적화 완료 ✨

### ⭐ 핵심 (Core) - 6개

1. **`/pipeline-pr`** - PR 검증 파이프라인 (Fast/Full Lane) ⭐ NEW
   - **Fast Lane**: 30초, 변경된 Layer만 검증 (로컬 개발)
   - **Full Lane**: 5분, 전체 검증 (PR 최종 승인)
   - Format → Conventions → Tests → Architecture → Coverage
   - 실제 로직: `tools/pipeline/pr_gate.sh`

2. **`/test-runner`** - 지능형 테스트 실행 ⭐ NEW
   - Git Diff 분석 → 변경된 Layer만 테스트
   - Claude Code 자동 수정 통합
   - LangFuse 메트릭 자동 업로드
   - 대체: 기존 4개 test runner workflows

3. **`/validate-conventions`** - Zero-Tolerance 규칙 검증 + Auto-Fix ⭐ ENHANCED
   - Lombok, Law of Demeter, JPA 관계, Setter 등
   - **Auto-Fix**: 위반 감지 → 수정 제안 → 자동 적용
   - **Serena Memory**: 패턴 학습 → 재발 방지
   - 실제 로직: `tools/pipeline/validate_conventions.sh`

4. **`/validate-architecture`** - ArchUnit 검증 + Auto-Fix ⭐ ENHANCED
   - 헥사고날 아키텍처, 레이어 의존성
   - **Auto-Fix**: 아키텍처 위반 자동 수정
   - 테스트 위치: `bootstrap-web-api/src/test/.../architecture/`

5. **`/format-code`** - Spotless 포맷팅 + Pre-commit Hook ⭐ ENHANCED
   - Google Java Format 적용
   - **Pre-commit Hook**: 자동 설치 (`--setup-hook`)
   - 커밋 전 자동 검증

6. **`/git-complete-workflow`** - 통합 Git 워크플로우 ⭐ NEW
   - Feature 브랜치 → 커밋 → PR 생성까지 완전한 가이드
   - Conventional Commits + Git Flow 통합
   - 대체: 기존 3개 git workflows

### 🏗️ 코드 생성 (Code Generation) - 2개 ⭐ NEW

7. **`cc-application.md`** - Application Layer Boilerplate 생성
   - Port/In (UseCase Interface), Port/Out (OutPort Interface)
   - Service (UseCase Implementation)
   - DTO (Command/Query/Response - Record Pattern)
   - Assembler (Domain-DTO Converter)
   - Facade (Multiple UseCase Orchestration)
   - **Zero-Tolerance**: Transaction Boundary, Long FK, Pure Java, Single Responsibility
   - **템플릿 기반**: 10개 컴포넌트 템플릿 제공
   - **사용 예시**: "@workflows/cc-application.md 참고해서 Order UseCase 생성"

8. **`cc-orchestration.md`** - Orchestration Pattern Boilerplate 생성
   - 3-Phase Lifecycle (Accept → Execute → Finalize)
   - Command (Record Pattern, IdemKey)
   - Orchestrator (@Async, BaseOrchestrator 상속)
   - Entities (Operation, WriteAheadLog)
   - Schedulers (Finalizer, Reaper)
   - Controller (202 Accepted, 멱등성 보장)
   - **Zero-Tolerance**: @Async Required, No Lombok, IdemKey Unique, Outcome Modeling
   - **자동화율**: 80-85% (10개 파일 자동 생성)
   - **사용 예시**: "@workflows/cc-orchestration.md 참고해서 Payment Orchestrator 생성"

### 📌 유틸리티 (Utilities) - 3개

9. **`/validate-tests`** - JaCoCo 커버리지 검증
   - 최소 80% 커버리지 요구

10. **`/create-test-fixtures`** - Test Fixture 생성 안내 ⭐ UPDATED
    - Claude Code `/test-gen-fixtures` 명령어 위임
    - Layer별 자동 생성 (Domain, Application, REST, Persistence)

### 📊 메트릭 & 분석 - 3개

11. **`/upload-langfuse`** - LangFuse 메트릭 업로드
   - Claude Code 및 Cascade 로그를 LangFuse로 전송
   - 토큰 사용량, 성능, 품질 메트릭 추적
   - 실제 로직: `tools/pipeline/upload_langfuse.sh`
   - **전제 조건**: 환경 변수 설정 필요
     ```bash
     export LANGFUSE_PUBLIC_KEY="pk-lf-..."
     export LANGFUSE_SECRET_KEY="sk-lf-..."
     export LANGFUSE_HOST="https://us.cloud.langfuse.com"
     ```
   - **2단계 파이프라인**:
     1. `scripts/langfuse/aggregate-logs.py` - 로그 집계 (Claude + Cascade → JSON)
     2. `scripts/langfuse/upload-to-langfuse.py` - LangFuse API 업로드
   - **메트릭 추적 항목**:
     - Traces: Claude Code 세션 추적
     - Observations: Hook 실행, Cascade 작업
     - 토큰 사용량, 실행 시간, 성공/실패율
   - **대시보드**: 업로드 후 LangFuse에서 확인 가능

12. **`/git-cherry-pick`** - 커밋 체리픽
    - 특정 커밋을 현재 브랜치로 가져오기

### 🗑️ 제거된 Workflows (7개)

**Test Runners (4개)** → `/test-runner`로 통합:
- ❌ `run-unit-tests.md`
- ❌ `run-integration-tests.md`
- ❌ `run-e2e-tests.md`
- ❌ `run-all-tests.md`

**Git Workflows (3개)** → `/git-complete-workflow`로 통합:
- ❌ `git-workflow.md`
- ❌ `git-commit-workflow.md`
- ❌ `git-pr.md`

**제거 이유**:
- 단순 Gradle 래퍼로 부가 가치 없음
- 지능형 통합 워크플로우로 대체
- 유지보수 비용 감소 및 일관성 향상

---

## ✅ Cascade 인식 요구사항

### 필수 형식

```markdown
---
description: {간단한 설명}  ← 필수!
---

# {Title}

{Description}  ← 필수! (3번째 줄)

## Parameters
...
```

### 제약사항

- **Description 필수**: 첫 3줄 형식 정확히 준수
- **파일 크기**: 11,500자 이하 (12,000자는 인식 안 됨)
- **디렉토리 권한**: 755 (읽기 권한 필수)

---

**생성일**: 2025-10-29
**버전**: 1.0.0
**IDE**: IntelliJ IDEA + Codeium/Windsurf Plugin

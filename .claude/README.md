# 🤖 Claude Code 설정

**Spring Boot 헥사고날 아키텍처를 위한 최적화된 Claude Code 설정**

이 디렉토리는 Claude Code가 프로젝트 표준을 이해하고 준수하는 코드를 생성하도록 **자동으로 가이드**합니다.

> ⚡ **2025년 1월 최적화 완료**: 토큰 사용량 30-50% 감소, 응답 속도 개선

---

## 📋 목차

- [개요](#개요)
- [최적화 시스템](#최적화-시스템)
- [디렉토리 구조](#디렉토리-구조)
- [슬래시 커맨드](#슬래시-커맨드)
- [동적 훅](#동적-훅)
- [에이전트](#에이전트)
- [사용 가이드](#사용-가이드)

---

## 🎯 개요

### 목적
Spring Boot 헥사고날 아키텍처 프로젝트에 특화된 Claude Code 환경을 제공합니다:
1. **자동화된 코드 품질 검증** - Dynamic Hooks를 통한 실시간 검증
2. **AI 리뷰 분석** - Gemini 코드 리뷰 체계적 분석
3. **최적화된 컨텍스트** - 요약본 우선 로딩으로 토큰 사용량 30-50% 감소
4. **전문 에이전트** - 프롬프트 최적화 등 특화된 작업 지원

### 주요 기능
- ✅ **슬래시 커맨드**: Gemini 리뷰 분석 자동화
- ✅ **동적 훅 (최적화됨)**: 경량 가이드라인 + 문서 참조
- ✅ **요약 문서 시스템**: 핵심 규칙 134-186줄로 압축
- ✅ **전문 에이전트**: 프롬프트 엔지니어링 등 특수 작업

---

## ⚡ 최적화 시스템

### 성능 개선 결과

| 항목 | Before | After | 개선율 |
|------|--------|-------|--------|
| Hook 인라인 텍스트 | ~500줄 | ~30줄 | **94% 감소** |
| 토큰 사용량 | 기준 | 30-50% 감소 | **대폭 절감** |
| 응답 속도 | 기준 | 개선됨 | **향상** |

### 최적화 전략

**문제점**:
- Hook이 매번 전체 가이드라인을 인라인으로 주입 (수백 줄)
- 컨텍스트 창 압박 및 토큰 낭비
- 규칙 변경 시 여러 곳 수정 필요

**해결책**:
```
요약 문서 시스템 도입
  ↓
핵심 규칙만 134-186줄로 압축
  ↓
Hook은 간결한 리마인더 + 문서 참조
  ↓
토큰 30-50% 절감, 유지보수성 향상
```

**문서 계층 구조**:
```
📚 요약본 (Quick Reference)
├── CODING_STANDARDS_SUMMARY.md (134줄)
└── ENTERPRISE_SPRING_STANDARDS_SUMMARY.md (186줄)
     ↓ 상세 내용 필요 시
📖 전체 문서 (Complete Reference)
├── CODING_STANDARDS.md (2,676줄)
├── ENTERPRISE_SPRING_STANDARDS_PROMPT.md (3,361줄)
└── 특화 가이드 (DDD, DTO, Exception 등)
```

### 효과

1. **토큰 절감**: 요약본 우선 로딩으로 30-50% 감소
2. **응답 속도**: 컨텍스트 로딩 시간 단축
3. **유지보수성**: 규칙 변경 시 문서만 수정 (단일 진실 공급원)
4. **확장성**: 새 가이드라인 추가 용이
5. **일관성**: 모든 Hook이 동일한 문서 참조

---

## 📁 디렉토리 구조

```
.claude/
├── README.md                          # 이 문서
├── CLAUDE.md                          # 중앙 설정 파일 (문서 참조 통합)
├── commands/                          # 슬래시 커맨드
│   ├── gemini-review.md              # Gemini 리뷰 분석
│   └── jira-task.md                  # Jira 태스크 분석
├── hooks/                             # Claude Code 동적 훅 (최적화됨)
│   ├── README.md                     # 동적 훅 상세 가이드
│   └── scripts/
│       ├── init-session.sh           # 세션 시작 시 컨텍스트 로딩
│       ├── preserve-rules.sh         # 컨텍스트 압박 시 규칙 보존
│       ├── user-prompt-submit.sh     # 코드 생성 전 가이드라인 주입
│       └── after-tool-use.sh         # 코드 생성 후 실시간 검증
└── agents/                            # 전문 에이전트
    └── prompt-engineer.md            # 프롬프트 최적화 전문가
```

---

## 🚀 슬래시 커맨드

### `/sc:gemini-review` - Gemini 리뷰 분석

**목적**: GitHub PR의 Gemini AI 리뷰 코멘트를 체계적으로 분석하고 리팩토링 전략 수립

**위치**: `commands/gemini-review.md`

**주요 기능**:
- PR 리뷰 자동 수집 (GitHub CLI 사용)
- 리뷰 코멘트 4단계 분류 (Critical/Improvement/Suggestion/Style)
- 영향도 분석 및 우선순위 결정
- 단계별 리팩토링 로드맵 생성
- 의사결정 기록 및 트레이드오프 분석

**사용 예시**:
```bash
# 현재 PR의 Gemini 리뷰 분석
/sc:gemini-review

# 특정 PR 분석
/sc:gemini-review 42

# Critical 항목만 분석
/sc:gemini-review --priority high

# 분석 결과 내보내기
/sc:gemini-review --export refactoring-plan.md

# 대화형 모드
/sc:gemini-review --interactive
```

**필요 조건**:
- GitHub CLI (`gh`) 설치 및 인증
- Repository에 Gemini 리뷰어 설정
- PR이 생성되어 있거나 PR 번호 제공

**관련 문서**:
- [Gemini 리뷰 분석 가이드](../docs/GEMINI_REVIEW_GUIDE.md) - 상세 사용법 및 예시

---

## 🔧 동적 훅 (Dynamic Hooks) - 최적화됨

**목적**: Claude가 코드를 생성하는 시점에 헥사고날 아키텍처 규칙을 주입하고 검증

**위치**: `hooks/scripts/`

> ⚡ **최적화 완료**: 인라인 텍스트 94% 감소, 문서 참조 시스템 도입

### 훅 종류

#### 1. `init-session.sh` ✨ NEW
**실행 시점**: Claude Code 세션 시작 시

**역할**:
- 프로젝트 컨텍스트 로딩 (요약본 우선)
- Git 브랜치에서 Jira 태스크 파싱
- 세션 컨텍스트 생성 (`/tmp/claude-session-context.md`)

**최적화**:
- Before: 전체 문서 참조 (3,361줄)
- After: 요약본 우선 (186줄) + 전체 문서 경로 안내
- 효과: 세션 로딩 속도 대폭 개선

#### 2. `preserve-rules.sh` ✨ NEW
**실행 시점**: Claude가 컨텍스트 압박 시 (PreCompact Hook)

**역할**:
- 핵심 규칙 보존하여 컨텍스트 압축 후에도 규칙 유지
- Zero-Tolerance 규칙 강조
- 문서 계층 구조 명시 (요약본 → 전체 문서)

#### 3. `user-prompt-submit.sh` (최적화됨)
**실행 시점**: 사용자가 Claude에게 요청을 제출할 때 (코드 생성 **전**)

**역할**:
- 요청 분석 (Domain/Application/Adapter 모듈 감지)
- 모듈별 간결한 가이드라인 주입
- 상세 내용은 문서 참조 유도

**최적화**:
- Before: 모듈별 80줄 인라인 가이드라인
- After: 모듈별 15줄 핵심 규칙 + 문서 링크
- 효과: 토큰 사용량 75-81% 감소

**예시**:
```
사용자: "Order 엔티티를 만들어줘"
    ↓
훅: Persistence Adapter 핵심 규칙 주입
    - ❌ 금지: JPA relationships, setter, public constructor
    - ✅ 허용: Long FK, static factory, getter only
    - 📚 상세: docs/ENTERPRISE_SPRING_STANDARDS_SUMMARY.md
    ↓
Claude: 규칙을 준수하는 코드 생성 + 필요 시 문서 참조
```

#### 4. `after-tool-use.sh`
**실행 시점**: Claude가 코드를 생성/수정한 직후 (코드 생성 **후**)

**역할**:
- 생성된 코드 즉시 검증
- 모듈별 규칙 위반 감지
- 위반 발견 시 경고 출력

**검증 항목**:
- Domain: Spring/JPA/Lombok import 금지
- Application: Adapter 직접 참조 금지
- Persistence: JPA relationships, setter, public constructor 금지
- Controller: Inner class DTO, non-record Request/Response 금지
- Law of Demeter: Getter 체이닝 금지
- SRP: 단일 책임 원칙 준수

### 동작 흐름 (최적화된 시스템)

```
┌────────────────────────────────────────────────────────────┐
│  세션 시작: init-session.sh                                 │
│  - 요약본 로딩 (ENTERPRISE_SPRING_STANDARDS_SUMMARY.md)    │
│  - 전체 문서 경로 안내                                       │
│  - Jira 태스크 파싱 (브랜치명에서)                           │
└────────────────────────────────────────────────────────────┘
                            ↓
┌────────────────────────────────────────────────────────────┐
│  사용자: "Order 엔티티 만들어줘"                              │
└────────────────────────────────────────────────────────────┘
                            ↓
┌────────────────────────────────────────────────────────────┐
│  user-prompt-submit.sh (최적화됨)                           │
│  - "entity" 키워드 감지                                     │
│  - Persistence Adapter 핵심 규칙 (15줄)                     │
│  - 문서 참조 링크: docs/ENTERPRISE_SPRING_STANDARDS_SUMMARY│
└────────────────────────────────────────────────────────────┘
                            ↓
┌────────────────────────────────────────────────────────────┐
│  Claude 코드 생성                                            │
│  - Long userId (NOT @ManyToOne)                            │
│  - protected constructor + static create()                 │
│  - NO setters                                              │
│  - 필요시 SUMMARY 문서 참조                                  │
└────────────────────────────────────────────────────────────┘
                            ↓
┌────────────────────────────────────────────────────────────┐
│  after-tool-use.sh                                         │
│  - OrderEntity.java 검증                                   │
│  - ✅ NO JPA relationships                                 │
│  - ✅ NO setters                                            │
│  - ✅ NO public constructor                                 │
│  - ✅ Law of Demeter 준수                                   │
└────────────────────────────────────────────────────────────┘
```

**관련 문서**:
- [동적 훅 상세 가이드](hooks/README.md)
- [코딩 표준 요약본](../docs/CODING_STANDARDS_SUMMARY.md) (134줄)
- [엔터프라이즈 표준 요약본](../docs/ENTERPRISE_SPRING_STANDARDS_SUMMARY.md) (186줄)
- [코딩 표준 전체](../docs/CODING_STANDARDS.md) (2,676줄)
- [엔터프라이즈 표준 전체](../docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md) (3,361줄)

---

## 🤖 에이전트 (Agents)

**목적**: 특정 도메인에 특화된 전문 에이전트 제공

**위치**: `agents/`

### Prompt Engineer

**파일**: `agents/prompt-engineer.md`

**목적**: LLM 및 AI 시스템을 위한 효과적인 프롬프트 작성 전문가

**전문 영역**:
- Few-shot vs zero-shot 선택
- Chain-of-thought 추론
- 역할극 및 관점 설정
- 출력 형식 지정
- 제약 조건 및 경계 설정

**사용 시점**:
- AI 기능 구축 시
- 에이전트 성능 개선 시
- 시스템 프롬프트 작성 시

**최적화 프로세스**:
1. 사용 사례 분석
2. 주요 요구사항 및 제약 조건 식별
3. 적절한 프롬프팅 기법 선택
4. 명확한 구조로 초기 프롬프트 생성
5. 출력 기반 테스트 및 반복
6. 효과적인 패턴 문서화

**출력 형식**:
- 완전한 프롬프트 텍스트 (항상 표시)
- 설계 선택 설명
- 사용 가이드라인
- 예상 출력 예시

---

## 📖 사용 가이드

### 1. 일반 개발 워크플로우

```bash
# 1. Feature 브랜치에서 개발
git checkout -b feature/user-management

# 2. Claude에게 작업 요청
"User 도메인 클래스를 만들어줘"

# → user-prompt-submit.sh가 Domain 규칙 주입
# → Claude가 규칙 준수 코드 생성
# → after-tool-use.sh가 실시간 검증

# 3. 커밋 및 푸시
git add .
git commit -m "feat: add User domain class"
git push origin feature/user-management
```

### 2. Gemini 리뷰 활용 워크플로우

```bash
# 1. PR 생성
gh pr create --title "feat: User Management" --body "..."

# 2. Gemini 자동 리뷰 대기 (1-2분)

# 3. 리뷰 분석 실행
/sc:gemini-review

# 4. 출력된 리팩토링 전략 검토
# 📊 Review Summary: 8 comments (1 critical, 3 improvement, 4 suggestion)
# 🎯 Refactoring Strategy: 3 phases, estimated 1.5 days

# 5. Phase별로 리팩토링 실행
# Phase 1: Critical (보안, 런타임 오류) - 4시간
# Phase 2: Improvement (성능, 유지보수성) - 1일
# Phase 3: Suggestion (스타일, 가독성) - 0.5일

# 6. 변경사항 커밋
git add .
git commit -m "fix: [gemini] address security vulnerability in auth"

# 7. PR 업데이트 및 머지
git push origin feature/user-management
gh pr merge --squash
```

### 3. 프롬프트 최적화 워크플로우

```bash
# 1. Prompt Engineer 에이전트 활용
"프롬프트 엔지니어로서 코드 리뷰를 위한 프롬프트를 만들어줘"

# 2. 에이전트가 최적화된 프롬프트 생성
# - 명확한 역할 설정
# - 평가 기준 제시
# - 출력 형식 지정
# - 실행 가능한 피드백 요구사항

# 3. 생성된 프롬프트 사용 및 반복 개선
```

---

## 🔗 관련 문서

### 핵심 문서
- **[코딩 표준 (87개 규칙)](../docs/CODING_STANDARDS.md)** - 아키텍처 계층별 상세 규칙
- **[Gemini 리뷰 분석 가이드](../docs/GEMINI_REVIEW_GUIDE.md)** - AI 리뷰 활용 전략
- **[동적 훅 가이드](hooks/README.md)** - Claude Code 훅 시스템 상세

### 품질 도구
- **[Checkstyle 설정 가이드](../config/checkstyle/README.md)** - 코드 스타일 검증
- **[SpotBugs 설정 가이드](../config/spotbugs/README.md)** - 정적 분석 및 버그 탐지

### Git Hooks (별도 시스템)
- **[Git Pre-commit Hook](../hooks/README.md)** - Git 커밋 시 강제 검증 (`.claude/hooks/`와 다름)

---

## ⚙️ 커스터마이징

### 새로운 슬래시 커맨드 추가

1. `commands/` 디렉토리에 새 `.md` 파일 생성
2. 프론트매터(front matter)에 메타데이터 정의:
```yaml
---
name: command-name
description: "Command description"
category: utility
complexity: basic|intermediate|advanced
mcp-servers: []
personas: []
---
```
3. 커맨드 사용법 및 예시 작성

### 새로운 에이전트 추가

1. `agents/` 디렉토리에 새 `.md` 파일 생성
2. 에이전트의 역할, 전문 영역, 프로세스 정의
3. 예상 출력 형식 및 가이드라인 작성

### 동적 훅 커스터마이징

1. `hooks/user-prompt-submit.sh` - 새로운 모듈 컨텍스트 추가
2. `hooks/after-tool-use.sh` - 새로운 검증 규칙 추가

자세한 내용은 [동적 훅 가이드](hooks/README.md#커스터마이징) 참조

---

## 🎯 효과

### Before (최적화 전)
```
Claude 요청 → Hook이 500줄 가이드라인 주입 → 컨텍스트 압박 → 느린 응답
    ↓
토큰 과다 사용 → 규칙 변경 시 여러 파일 수정 필요
```

### After (최적화 후)
```
세션 시작 → 요약본 로딩 (186줄) → 빠른 컨텍스트 구성
    ↓
Claude 요청 → 핵심 규칙 (15줄) + 문서 참조 → 필요시 SUMMARY 확인
    ↓
토큰 30-50% 절감 → 응답 속도 개선 → 규칙 변경은 문서만 수정
```

**정량적 효과**:
- ⚡ 토큰 사용량: 30-50% 감소
- 🚀 응답 속도: 개선됨
- 📉 Hook 텍스트: 94% 감소 (500줄 → 30줄)
- 🔧 유지보수: 단일 진실 공급원 (문서)

**정성적 효과**:
- ✅ 처음부터 올바른 코드 생성
- ✅ 수정 시간 절감
- ✅ 코드 품질 향상
- ✅ 확장성 및 일관성 확보

---

**🎯 목표**: Claude Code가 프로젝트 표준을 이해하고 준수하는 코드를 생성하도록 지속적 가이드

© 2024 Ryu-qqq. All Rights Reserved.

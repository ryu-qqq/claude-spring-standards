# Cursor IDE 설정 가이드

Cursor IDE에서 이 프로젝트를 최대한 활용하는 방법입니다.

---

## 🎯 설정 구조

### 1. **Rules** (자동 적용)
- **파일**: `.cursorrules` (344 라인)
- **역할**: AI가 코드 생성 시 반드시 따라야 할 규칙
- **내용**: Zero-Tolerance 규칙 + TDD 철학 + 핵심 컨벤션
- **작동**: AI가 모든 요청마다 자동으로 읽음

### 2. **Docs** (온라인 참고)
- **URL**: https://ryu-qqq.github.io/claude-spring-standards/
- **역할**: 상세한 88개 코딩 컨벤션 가이드
- **내용**: 레이어별 가이드, 예시 코드, 테스트 전략
- **작동**: AI가 필요할 때 크롤링해서 참조

### 3. **Memories** (프로젝트 컨텍스트)
- **위치**: Cursor UI → 프로젝트 설정
- **역할**: 프로젝트별 중요한 컨텍스트 저장
- **추천 내용**: 아래 "Memories 설정" 참조

---

## 🚀 초기 설정

### 1. Docs 추가 (GitHub Pages)

**Cursor IDE에서**:
1. `Ctrl/Cmd + Shift + P` → "Cursor: Settings"
2. **Docs** 탭 클릭
3. **"+ Add URL"** 클릭
4. 다음 URL 입력:
   ```
   https://ryu-qqq.github.io/claude-spring-standards/
   ```
5. **크롤링 시작** 대기 (1-2분)

**결과**:
- ✅ AI가 88개 코딩 컨벤션 참조 가능
- ✅ 레이어별 가이드 자동 검색
- ✅ Zero-Tolerance 규칙 온라인 확인

---

### 2. Memories 설정 (권장)

**Cursor UI → Memories 탭에 추가**:

#### Memory 1: 프로젝트 정체성
```
이 프로젝트는 Spring Boot 3.5 + Java 21 기반의 헥사고날 아키텍처 엔터프라이즈 표준 템플릿입니다.

핵심 철학:
- Kent Beck TDD: Red (test:) → Green (feat:) → Refactor (struct:)
- Tidy First: Structural과 Behavioral 변경 엄격 분리
- Zero-Tolerance: Lombok 금지, Law of Demeter, Long FK 전략
```

#### Memory 2: Zero-Tolerance 규칙
```
절대 위반 불가 규칙 (Zero-Tolerance):

1. Lombok 금지 - Domain, JPA Entity, Orchestration Layer에서 절대 사용 금지
2. Law of Demeter - Getter 체이닝 금지 (order.getCustomer().getAddress() ❌)
3. Long FK 전략 - JPA 관계 어노테이션 금지, Long userId 사용
4. Transaction 경계 - @Transactional 내 외부 API 호출 절대 금지
5. Orchestration Pattern - executeInternal() @Async 필수, Command Record 패턴
```

#### Memory 3: 커밋 규칙
```
TDD 커밋 메시지 규칙:

- test: - 실패하는 테스트 추가 (Red Phase)
- feat: - 테스트 통과 구현 (Green Phase)
- struct: - 구조 개선, 동작 변경 없음 (Refactor Phase)
- fix: - 버그 수정
- chore: - 빌드/설정 변경

핵심 원칙:
- 한 커밋에는 하나의 타입만
- Structural과 Behavioral 절대 섞지 않기
- 작은 커밋 (1-3 파일)
```

#### Memory 4: 컨벤션 위치
```
코딩 컨벤션 88개 규칙:

온라인: https://ryu-qqq.github.io/claude-spring-standards/
로컬: docs/coding_convention/

레이어별 가이드:
- 00-project-setup/ (2개 규칙)
- 01-adapter-in-layer/rest-api/ (22개 규칙)
- 02-domain-layer/ (12개 규칙)
- 03-application-layer/ (26개 규칙)
- 04-persistence-layer/ (23개 규칙)
- 05-testing/ (3개 규칙)
```

---

## 💡 사용 팁

### AI에게 컨벤션 적용시키기

#### 방법 1: @mention (로컬 파일)
```
@docs/coding_convention/02-domain-layer/aggregate/guide.md

위 가이드를 따라서 Order Aggregate를 만들어줘
```

#### 방법 2: Docs 참조 (온라인)
```
Domain Layer의 Aggregate 패턴을 참조해서 Order를 만들어줘
```

#### 방법 3: Rules 자동 적용
```
Order Aggregate를 만들어줘
(AI가 자동으로 .cursorrules 읽고 Zero-Tolerance 준수)
```

---

## ⚠️ Cursor의 한계

### ❌ 불가능한 기능

1. **Claude Code 스킬 시스템**
   - `/kb-domain/go` 같은 커맨드 없음
   - 대안: Cursor AI에게 직접 요청

2. **프롬프트 제출 Hooks**
   - `.claude/hooks/user-prompt-submit.sh` 작동 안 함
   - 대안: `.cursorrules`에 규칙 명시

3. **LangFuse 자동 추적 (AI 생성 시)**
   - Cursor AI가 코드 생성할 때 자동 추적 불가
   - 대안: Git commit 시 post-commit hook으로 추적

### ✅ 작동하는 기능

1. **Git Hooks** (Cursor 밖에서 작동)
   ```bash
   # Cursor 터미널에서:
   git commit -m "test: Email VO 테스트"
   # → post-commit hook → LangFuse 업로드 ✅
   ```

2. **컨벤션 적용**
   - `.cursorrules` (자동 적용)
   - GitHub Pages Docs (AI가 크롤링)
   - `@mention` (직접 파일 참조)

---

## 🔧 트러블슈팅

### 문제: Docs에 추가했는데 AI가 참조 안 함
**해결**:
1. Cursor 재시작
2. Docs 탭에서 크롤링 완료 확인
3. AI에게 명시적으로 요청: "Docs에서 Domain Layer 가이드를 참조해줘"

### 문제: Rules가 적용되지 않음
**해결**:
1. `.cursorrules` 파일 존재 확인
2. Cursor 재시작
3. Settings → General → Rules File 경로 확인

### 문제: LangFuse 업로드 안 됨
**해결**:
1. Git Hooks 설치 확인:
   ```bash
   ./scripts/setup-hooks.sh
   ```
2. `.env` 파일 생성 (LangFuse Cloud 사용 시)
3. `pip3 install langfuse` 설치

---

## 📚 추가 리소스

- **GitHub Repository**: https://github.com/ryu-qqq/claude-spring-standards
- **온라인 문서**: https://ryu-qqq.github.io/claude-spring-standards/
- **Main README**: [../README.md](../README.md)

---

**Last Updated**: 2025-11-18

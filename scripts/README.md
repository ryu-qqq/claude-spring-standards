# Scripts

이 디렉토리는 프로젝트 자동화 스크립트를 포함합니다.

---

## 📦 설치 스크립트

### `install-complete-system.sh` ⭐ 권장

**목적**: 모든 컴포넌트를 한 번에 다른 프로젝트로 복사

**포함 내용**:
- ✅ **Claude Code** (Hooks + Cache + Commands + Serena)
- ✅ **Windsurf/Cascade** (Rules + Workflows + Templates)
- ✅ **Coding Convention Docs** (90+ 규칙)
- ✅ **CodeRabbit** 설정 (.coderabbit.yaml)
- ✅ **Scripts** (Pipeline, LangFuse)
- ✅ **Tools** (Gradle 설정, ArchUnit)
- ✅ **Git Hooks** (Pre-commit 검증)

**사용법**:
```bash
# 1. 이 프로젝트 클론
git clone <repo-url> /tmp/spring-standards

# 2. 대상 프로젝트로 이동
cd your-project

# 3. 실행
bash /tmp/spring-standards/scripts/install-complete-system.sh

# 4. 임시 디렉토리 삭제
rm -rf /tmp/spring-standards
```

**특징**:
- 📦 **7단계 설치**: Claude → Windsurf → Docs → CodeRabbit → Scripts → Hooks → Cache
- 💬 **대화형**: 각 컴포넌트 설치 여부 선택 가능
- 💾 **자동 백업**: 기존 파일을 `.backup.YYYYMMDD_HHMMSS` 형식으로 백업
- 🔍 **의존성 확인**: Python, tiktoken, jq 자동 확인 및 설치 안내
- ⚙️ **Cache 빌드**: 설치 후 즉시 Cache 빌드 옵션
- 📊 **텔레메트리**: 익명화된 사용 통계 선택적 활성화

**설치 후 작업**:
```bash
# 1. Cache 빌드
python3 .claude/hooks/scripts/build-rule-cache.py

# 2. Serena 메모리 초기화 (1회만)
bash .claude/hooks/scripts/setup-serena-conventions.sh

# 3. Claude Code 실행
claude code
/cc:load  # 코딩 컨벤션 로드
```

---

### `install-claude-hooks.sh`

**목적**: Claude Code 관련 컴포넌트만 복사 (부분 설치)

**포함 내용**:
- ✅ `.claude/hooks/` (user-prompt-submit.sh, after-tool-use.sh)
- ✅ `.claude/hooks/scripts/` (로그, 검증, Cache 빌드)
- ✅ `.claude/commands/` (코드 생성, 검증 명령어)
- ✅ 선택적: CLAUDE.md, Windsurf, Git Hooks, Coding Convention Docs

**사용법**:
```bash
bash /tmp/spring-standards/scripts/install-claude-hooks.sh
```

**특징**:
- 🎯 **경량 설치**: Claude Code에만 집중
- 📋 **선택적 컴포넌트**: Windsurf, Docs, Hooks 선택 가능
- 💬 **대화형**: 동일한 방식으로 설치 과정 진행

**차이점**:
| 항목 | install-complete-system.sh | install-claude-hooks.sh |
|------|---------------------------|------------------------|
| **초점** | 모든 컴포넌트 통합 | Claude Code만 |
| **CodeRabbit** | ✅ 자동 설치 | ❌ 미포함 |
| **Scripts** | ✅ 자동 설치 | ❌ 미포함 |
| **Tools** | ✅ 자동 설치 | ❌ 미포함 |
| **Windsurf** | ✅ 자동 설치 | 📋 선택 가능 |
| **Docs** | ✅ 자동 설치 | 📋 선택 가능 |
| **Git Hooks** | ✅ 자동 설치 | 📋 선택 가능 |

---

## 📊 LangFuse 스크립트

### `langfuse/aggregate-logs.py`

**목적**: Claude Code 및 Cascade 로그를 집계하여 LangFuse 형식으로 변환

**입력**:
- `.claude/hooks/logs/hook-execution.jsonl` (Claude Code 로그)
- `.pipeline-metrics/metrics.jsonl` (Pipeline 메트릭, 있으면)

**출력**:
- `langfuse-data.json` (LangFuse Ingestion API 형식)

**사용법**:
```bash
python3 scripts/langfuse/aggregate-logs.py
```

**변환 과정**:
```
JSON Lines (JSONL)
    ↓
Traces & Observations 구조화
    ↓
LangFuse Ingestion API 형식
```

**추적 메트릭**:
- 📊 **Traces**: Claude Code 세션별 추적
- 🔍 **Observations**: Hook 실행, Cascade 작업
- 💰 **토큰 사용량**: 입력/출력 토큰 계산
- ⏱️ **실행 시간**: 각 작업의 소요 시간
- ✅/❌ **성공/실패율**: 작업 결과 추적

---

### `langfuse/upload-to-langfuse.py`

**목적**: 집계된 데이터를 LangFuse Ingestion API로 업로드

**환경 변수** (필수):
```bash
export LANGFUSE_PUBLIC_KEY="pk-lf-..."
export LANGFUSE_SECRET_KEY="sk-lf-..."
export LANGFUSE_HOST="https://us.cloud.langfuse.com"
```

**사용법**:
```bash
python3 scripts/langfuse/upload-to-langfuse.py
```

**업로드 과정**:
```
langfuse-data.json 읽기
    ↓
LangFuse Ingestion API POST
    ↓
성공 응답 확인
    ↓
langfuse-data.json 삭제 (임시 파일 정리)
```

**에러 처리**:
- ❌ 환경 변수 누락: 명확한 오류 메시지
- ❌ API 실패: HTTP 상태 코드 및 응답 출력
- ❌ 파일 없음: langfuse-data.json 존재 여부 확인

---

## 🔄 통합 워크플로우

### LangFuse 메트릭 업로드 (전체 파이프라인)

**Windsurf Cascade에서**:
```bash
/upload-langfuse
```

**또는 직접 실행**:
```bash
bash tools/pipeline/upload_langfuse.sh
```

**파이프라인 단계**:
```
1️⃣ aggregate-logs.py 실행
   → 로그 수집 및 변환
   ↓
2️⃣ upload-to-langfuse.py 실행
   → LangFuse API 업로드
   ↓
3️⃣ langfuse-data.json 삭제
   → 임시 파일 정리
   ↓
4️⃣ 성공 메시지 출력
```

**환경 변수 확인**:
```bash
# .langfuse.telemetry 파일에서 자동 로드
# 또는 직접 설정:
export LANGFUSE_PUBLIC_KEY="pk-lf-..."
export LANGFUSE_SECRET_KEY="sk-lf-..."
export LANGFUSE_HOST="https://us.cloud.langfuse.com"
```

---

## 🛠️ 기타 스크립트

### 추가 스크립트 (개발 예정)

향후 추가될 스크립트:
- `setup-new-project.sh` - 새 프로젝트 초기화
- `migrate-to-latest.sh` - 최신 버전으로 마이그레이션
- `health-check.sh` - 설치 상태 및 구성 확인
- `uninstall.sh` - 완전 제거 스크립트

---

## 📋 설치 비교표

| 기능 | install-complete-system.sh | install-claude-hooks.sh | 수동 설치 |
|------|---------------------------|------------------------|----------|
| **Claude Code** | ✅ 자동 | ✅ 자동 | 🔧 수동 |
| **Windsurf** | ✅ 자동 | 📋 선택 | 🔧 수동 |
| **Docs** | ✅ 자동 | 📋 선택 | 🔧 수동 |
| **CodeRabbit** | ✅ 자동 | ❌ 없음 | 🔧 수동 |
| **Scripts** | ✅ 자동 | ❌ 없음 | 🔧 수동 |
| **Tools** | ✅ 자동 | ❌ 없음 | 🔧 수동 |
| **Git Hooks** | ✅ 자동 | 📋 선택 | 🔧 수동 |
| **백업** | ✅ 자동 | ✅ 자동 | ❌ 없음 |
| **의존성 확인** | ✅ 자동 | ✅ 자동 | ❌ 없음 |
| **Cache 빌드** | 📋 선택 | 📋 선택 | 🔧 수동 |
| **텔레메트리** | 📋 선택 | 📋 선택 | 🔧 수동 |

---

## 📚 관련 문서

### 설치 가이드
- [Complete Installation Guide](../README.md#빠른-시작)
- [Claude + Windsurf Integration](../docs/workflows/CLAUDE_CASCADE_INTEGRATION.md)
- [Installation Report](../.windsurf/workflows/cc-installation-report.md) - fileflow, crawlinghub 설치 결과

### LangFuse 통합
- [LangFuse Integration Guide](../docs/LANGFUSE_INTEGRATION_GUIDE.md)
- [LangFuse Monitoring Guide](../docs/LANGFUSE_MONITORING_GUIDE.md)
- [Telemetry Guide](../docs/LANGFUSE_TELEMETRY_GUIDE.md)

### 개발 가이드
- [Usage Guide](../docs/USAGE_GUIDE.md)
- [Dynamic Hooks Guide](../docs/DYNAMIC_HOOKS_GUIDE.md)
- [Getting Started Tutorial](../docs/tutorials/01-getting-started.md)

---

## 🐛 문제 해결

### 권한 오류
```bash
chmod +x /tmp/spring-standards/scripts/install-complete-system.sh
chmod +x /tmp/spring-standards/scripts/install-claude-hooks.sh
```

### Python 없음
```bash
# macOS
brew install python3

# Ubuntu
sudo apt-get install python3
```

### tiktoken 설치 실패
```bash
pip3 install --upgrade pip
pip3 install tiktoken
```

### jq 없음
```bash
# macOS
brew install jq

# Ubuntu
sudo apt-get install jq
```

### 환경 변수 누락 (LangFuse)
```bash
# .langfuse.telemetry 파일 확인
cat .langfuse.telemetry

# 또는 수동 설정
export LANGFUSE_PUBLIC_KEY="pk-lf-..."
export LANGFUSE_SECRET_KEY="sk-lf-..."
export LANGFUSE_HOST="https://us.cloud.langfuse.com"
```

---

**최종 업데이트**: 2025-10-30

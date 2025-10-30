# Windsurf Cascade 로깅 가이드

## 문제

Windsurf IDE의 Cascade는 기본적으로 로그를 남기지 않습니다. LangFuse 통합을 위해서는 Cascade의 입력(프롬프트)과 출력(생성된 코드)을 기록해야 합니다.

## 해결 방법

### 방법 1: 수동 로깅 (권장)

Cascade 작업 후 수동으로 로그를 기록합니다.

```bash
# Cascade 작업 완료 후 실행
bash .windsurf/cascade-logger.sh "task_name" "status_code" "duration_seconds"

# 예시
bash .windsurf/cascade-logger.sh "create_order_aggregate" 0 120
bash .windsurf/cascade-logger.sh "implement_usecase" 0 300
```

**장점**: 간단하고 즉시 사용 가능
**단점**: 수동 작업 필요

### 방법 2: Windsurf 설정 (자동화)

Windsurf가 자동으로 로그를 남기도록 설정합니다.

#### 2.1. Windsurf 로그 경로 확인

Windsurf의 기본 로그 위치를 확인합니다:

```bash
# macOS
~/Library/Application Support/Windsurf/logs/

# Linux
~/.config/Windsurf/logs/

# Windows
%APPDATA%\Windsurf\logs\
```

#### 2.2. 로그 파서 스크립트 생성

```bash
# Windsurf 로그를 파싱하여 .cascade/metrics.jsonl로 변환
python3 .windsurf/parse-windsurf-logs.py
```

#### 2.3. Cron/Launchd로 자동화

```bash
# 5분마다 로그 파싱 (macOS)
echo "*/5 * * * * cd /Users/sangwon-ryu/claude-spring-standards && python3 .windsurf/parse-windsurf-logs.py" | crontab -
```

### 방법 3: LangFuse Direct Integration (고급)

Windsurf/Cascade가 LangFuse Python SDK를 직접 사용하도록 설정합니다.

#### 3.1. Windsurf 확장 설정

Windsurf 설정 파일 (`settings.json`)에 LangFuse 정보 추가:

```json
{
  "cascade.telemetry": {
    "enabled": true,
    "provider": "langfuse",
    "langfuse": {
      "publicKey": "pk-lf-...",
      "secretKey": "sk-lf-...",
      "host": "https://us.cloud.langfuse.com"
    }
  }
}
```

**참고**: Windsurf가 이 기능을 지원하는지 확인 필요

## 현재 상태

### Claude Code Hooks ✅
- `.claude/hooks/logs/hook-execution.jsonl` - 자동 로깅 활성화
- `user-prompt-submit.sh`, `after-tool-use.sh`로 모든 인터랙션 기록

### Cascade ❌
- `.cascade/metrics.jsonl` - **로그 없음**
- 수동 로깅 또는 자동화 설정 필요

## 통합 워크플로우

### 현재 (Claude Code만)

```
사용자 프롬프트
    ↓
Claude Code Hooks (.claude/hooks/)
    ↓
hook-execution.jsonl (자동 생성)
    ↓
aggregate-logs.py
    ↓
LangFuse
```

### 이상적 (Claude Code + Cascade)

```
┌─────────────────────┬─────────────────────┐
│ Claude Code         │ Windsurf Cascade    │
│ hook-execution.jsonl│ metrics.jsonl       │
└──────────┬──────────┴──────────┬──────────┘
           │                     │
           └─────────┬───────────┘
                     ↓
            aggregate-logs.py
                     ↓
               LangFuse
```

## LangFuse에서 보고 싶은 메트릭

### Claude Code (현재 수집 중 ✅)
- 프롬프트 입력
- 생성된 코드
- 토큰 사용량
- Layer 감지
- 규칙 위반 여부

### Cascade (수집 필요 ❌)
- Cascade 프롬프트 입력
- 생성된 Boilerplate 코드
- 실행 시간
- 성공/실패 여부
- 워크플로우 이름 (예: `create-aggregate.yaml`)

## 다음 단계

1. **방법 1 선택 (빠른 시작)**: 수동 로깅으로 시작
2. **방법 2 구현 (자동화)**: Windsurf 로그 파서 작성
3. **방법 3 검토 (고급)**: Windsurf가 LangFuse 직접 지원하는지 확인

## FAQ

### Q: Windsurf가 로그를 어디에 저장하나요?

A: Windsurf의 로그 위치는 확인이 필요합니다. 일반적으로:
- macOS: `~/Library/Application Support/Windsurf/logs/`
- Linux: `~/.config/Windsurf/logs/`

### Q: Claude Code Hooks처럼 자동으로 할 수 없나요?

A: Windsurf는 별도의 IDE이므로 Claude Code Hooks를 직접 사용할 수 없습니다. 대신:
1. Windsurf 로그를 파싱하거나
2. Windsurf 확장을 통해 LangFuse 직접 연동하거나
3. 수동으로 로그를 기록해야 합니다.

### Q: aggregate-logs.py가 Cascade를 지원한다고 했는데?

A: `aggregate-logs.py`는 `.cascade/metrics.jsonl` 파일을 **읽을 준비**는 되어 있습니다. 하지만 **Windsurf가 그 파일을 생성하지 않습니다**.

```python
# aggregate-logs.py의 Cascade 지원 코드 (이미 구현됨)
def load_cascade_logs(self, log_path: str) -> None:
    if not Path(log_path).exists():
        print(f"⚠️  Cascade logs not found: {log_path}")
        return
```

파일만 생성되면 자동으로 처리됩니다!

---

**생성일**: 2025-10-30
**버전**: 1.0.0

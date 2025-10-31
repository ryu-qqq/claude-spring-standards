# /cc:load - 코딩 컨벤션 로드

Spring Standards 프로젝트의 코딩 컨벤션을 Serena 메모리에서 자동으로 로드합니다.

**cc** = **C**oding **C**onvention

## 실행 흐름

1. **프로젝트 활성화**: Serena MCP에 프로젝트 등록
2. **컨벤션 로드**: 코딩 컨벤션 인덱스 및 레이어별 규칙 로드
3. **세션 준비**: Spring Standards 개발 환경 활성화

## 사용법

```bash
/cc:load
```

## 자동 로드되는 메모리

### 1. 마스터 인덱스
- `coding_convention_index`: 전체 컨벤션 개요 및 Zero-Tolerance 규칙

### 2. 레이어별 컨벤션 (선택적)
- `coding_convention_domain_layer`: Domain Layer 규칙
- `coding_convention_application_layer`: Application Layer 규칙
- `coding_convention_persistence_layer`: Persistence Layer 규칙
- `coding_convention_rest_api_layer`: REST API Layer 규칙

## 실행 내용

아래 작업들이 자동으로 수행됩니다:

```python
# 0. Slash Command 로깅 (LangFuse 추적용)
# 실행: bash .claude/hooks/scripts/log-slash-command.sh "cc:load" "start"
# 목적: LangFuse에서 /cc:load 실행 추적 가능하도록 로그 기록

# 1. Serena 프로젝트 활성화
mcp__serena__activate_project("/Users/sangwon-ryu/claude-spring-standards")

# 2. 사용 가능한 메모리 목록 확인
memories = mcp__serena__list_memories()

# 3. 코딩 컨벤션 마스터 인덱스 로드
index = mcp__serena__read_memory("coding_convention_index")

# 4. 세션 컨텍스트 확인 및 복원
onboarding_status = mcp__serena__check_onboarding_performed()

# 5. Slash Command 완료 로깅
# 실행: bash .claude/hooks/scripts/log-slash-command.sh "cc:load" "complete" '{"memories_loaded": 5}'
```

## 출력 예시

```
✅ 프로젝트 활성화: claude-spring-standards
✅ 사용 가능한 메모리: 7개

📚 코딩 컨벤션 로드 완료:
- ✅ coding_convention_index (마스터 인덱스)
- ✅ coding_convention_domain_layer
- ✅ coding_convention_application_layer
- ✅ coding_convention_persistence_layer
- ✅ coding_convention_rest_api_layer

🎯 Zero-Tolerance 규칙:
1. Lombok 금지 (Domain)
2. Law of Demeter (Domain)
3. Long FK Strategy (Persistence)
4. Transaction Boundary (Application)
5. Spring 프록시 제약사항 (Application)
6. Javadoc 필수 (All Layers)

📖 상세 규칙 확인:
- read_memory("coding_convention_domain_layer")
- read_memory("coding_convention_application_layer")
- read_memory("coding_convention_persistence_layer")
- read_memory("coding_convention_rest_api_layer")
```

## 다음 단계

세션 로드 후 다음 작업을 수행할 수 있습니다:

### 코드 생성
```bash
/code-gen-domain Order       # Domain Aggregate 생성
/code-gen-usecase CreateOrder # Application UseCase 생성
/code-gen-controller Order    # REST Controller 생성
```

### 코드 검증
```bash
/validate-domain domain/src/.../Order.java
/validate-architecture
```

### 분석
```bash
/sc:analyze adapter-out/persistence-mysql
```

## LangFuse 통합 (선택 사항)

### 자동 로깅

`/cc:load` 실행 시 다음 이벤트가 Hook logs에 자동 기록됩니다:
- `slash_command_start`: 명령어 시작
- `slash_command_complete`: 명령어 완료 (메모리 로드 개수 포함)

이 로그는 LangFuse 업로드 시 자동으로 포함됩니다.

### LangFuse로 업로드

Hook logs를 LangFuse로 전송하려면:

```bash
# 환경 변수 설정 (1회만)
export LANGFUSE_PUBLIC_KEY="pk-lf-..."
export LANGFUSE_SECRET_KEY="sk-lf-..."
export LANGFUSE_HOST="https://us.cloud.langfuse.com"

# 로그 업로드
bash tools/pipeline/upload_langfuse.sh
```

또는 Windsurf Workflow 사용:
```bash
/upload-langfuse
```

### 추적되는 메트릭

- `/cc:load` 실행 횟수 및 시간
- 메모리 로드 개수
- 컨벤션 참조 횟수 (keyword_analysis 이벤트)
- 토큰 사용량 (cache_injection 이벤트)
- 컨벤션 위반 건수 (validation_complete 이벤트)

## 참고

- 이 명령어는 프로젝트 시작 시 한 번만 실행하면 됩니다
- Serena 메모리는 세션 간 지속되므로 재로드 불필요
- 메모리가 없으면 자동으로 생성됩니다 (초기 설정 시)

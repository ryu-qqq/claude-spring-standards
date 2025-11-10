# LangFuse + TDD 워크플로우 가이드

**목적**: LangFuse 프롬프트를 활용한 TDD 기반 코드 생성 자동화

**작성일**: 2025-11-10
**버전**: v1.0

---

## 📋 개요

LangFuse에 등록된 4개 레이어 프롬프트를 활용하여:
1. **Claude Code Hook**에서 자동으로 프롬프트 불러오기
2. **TDD 방식**으로 코드 생성 (Test First)
3. **A/B 테스트**로 프롬프트 버전별 성능 측정

---

## 🎯 현재 상태

### LangFuse에 등록된 프롬프트 (v1.0, production)

| 프롬프트 | Tags | Labels | 크기 |
|----------|------|--------|------|
| `prompt-adapter-rest-layer-v1.0` | adapter-rest, spring-boot, hexagonal-architecture | v1.0, production | 614줄 |
| `prompt-domain-layer-v1.0` | domain, ddd, aggregate, hexagonal-architecture | v1.0, production | 670줄 |
| `prompt-persistence-layer-v1.0` | persistence, jpa, querydsl, hexagonal-architecture | v1.0, production | 894줄 |
| `prompt-application-layer-v1.0` | application, usecase, orchestration, hexagonal-architecture | v1.0, production | 1636줄 |

### 로컬 프롬프트 파일 위치
```
.claude/prompts/
├── adapter-rest-layer-v1.0.md
├── domain-layer-v1.0.md
├── persistence-layer-v1.0.md
└── application-layer-v1.0.md
```

---

## 🔧 Phase 1: Hook 통합 (다음 작업)

### 목표
**user-prompt-submit.sh**에서 LangFuse 프롬프트 자동 주입

### 작업 내용

#### 1.1 LangFuse 프롬프트 가져오기 스크립트
```bash
# .claude/hooks/scripts/fetch-langfuse-prompt.py

from langfuse import Langfuse
import os
import sys

def fetch_prompt(layer_name):
    """LangFuse에서 프롬프트 가져오기"""
    langfuse = Langfuse(
        public_key=os.environ['LANGFUSE_PUBLIC_KEY'],
        secret_key=os.environ['LANGFUSE_SECRET_KEY'],
        host=os.environ.get('LANGFUSE_HOST', 'https://us.cloud.langfuse.com')
    )

    # 프롬프트 이름 매핑
    prompt_names = {
        'domain': 'prompt-domain-layer-v1.0',
        'application': 'prompt-application-layer-v1.0',
        'persistence': 'prompt-persistence-layer-v1.0',
        'adapter-rest': 'prompt-adapter-rest-layer-v1.0'
    }

    prompt_name = prompt_names.get(layer_name)
    if not prompt_name:
        print(f"Unknown layer: {layer_name}", file=sys.stderr)
        return None

    # 프롬프트 가져오기 (production 라벨)
    prompt = langfuse.get_prompt(prompt_name, label='production')

    return prompt.prompt  # 프롬프트 내용 반환

if __name__ == '__main__':
    layer = sys.argv[1] if len(sys.argv) > 1 else 'domain'
    content = fetch_prompt(layer)
    if content:
        print(content)
```

#### 1.2 Hook 수정
```bash
# .claude/hooks/user-prompt-submit.sh (수정)

# 기존: inject-rules.py (로컬 파일 읽기)
python3 .claude/hooks/scripts/inject-rules.py "$DETECTED_LAYERS"

# 신규: fetch-langfuse-prompt.py (LangFuse에서 가져오기)
for layer in $DETECTED_LAYERS; do
    LANGFUSE_PROMPT=$(python3 .claude/hooks/scripts/fetch-langfuse-prompt.py "$layer")

    # 프롬프트 주입
    echo "$LANGFUSE_PROMPT" >> "$PROMPT_FILE"

    # LangFuse Trace 로깅
    python3 .claude/hooks/scripts/log-to-langfuse.py \
        --event "prompt_injected" \
        --layer "$layer" \
        --version "v1.0"
done
```

---

## 🧪 Phase 2: TDD 워크플로우 구현

### 목표
**Test First** 방식으로 코드 생성

### 작업 내용

#### 2.1 TDD 프롬프트 확장
```markdown
# LangFuse 프롬프트에 추가 (각 레이어별)

## TDD 워크플로우 (필수)

### 1단계: 테스트 먼저 작성
- ✅ Given-When-Then 구조
- ✅ Fixture/Object Mother 패턴
- ✅ ArchUnit 테스트 (아키텍처 검증)

### 2단계: 최소 구현
- ✅ 테스트 통과하는 최소 코드
- ✅ 컨벤션 준수 (Lombok 금지, Law of Demeter 등)

### 3단계: 리팩토링
- ✅ 중복 제거
- ✅ 명확한 네이밍
- ✅ SRP 준수
```

#### 2.2 Slash Command 구현
```bash
# .claude/commands/tdd-generate.md

#!/bin/bash
# /tdd-generate Domain Order

LAYER=$1
AGGREGATE=$2

# 1. LangFuse 프롬프트 가져오기
PROMPT=$(python3 .claude/hooks/scripts/fetch-langfuse-prompt.py "$LAYER")

# 2. TDD 템플릿 추가
PROMPT="$PROMPT

## 현재 작업: ${AGGREGATE} Aggregate TDD 생성

1. **테스트 먼저 작성**: ${AGGREGATE}Test.java
2. **최소 구현**: ${AGGREGATE}.java
3. **ArchUnit 검증**: ${AGGREGATE}ArchitectureTest.java
"

# 3. Claude Code에 전달
echo "$PROMPT"
```

---

## 📊 Phase 3: A/B 테스트 시스템

### 목표
프롬프트 버전별 성능 측정

### 작업 내용

#### 3.1 메트릭 수집
```python
# .claude/hooks/scripts/log-to-langfuse.py

from langfuse import Langfuse

def log_tdd_cycle(layer, version, metrics):
    """TDD 사이클 메트릭 로깅"""
    langfuse = Langfuse(...)

    langfuse.trace(
        name=f"tdd-{layer}-generation",
        input={
            "layer": layer,
            "prompt_version": version,
            "aggregate": metrics['aggregate']
        },
        output={
            "test_written": metrics['test_lines'],
            "impl_written": metrics['impl_lines'],
            "archunit_passed": metrics['archunit_passed'],
            "convention_violations": metrics['violations']
        },
        metadata={
            "time_taken_seconds": metrics['time_taken'],
            "ai_cycles": metrics['ai_cycles']
        }
    )
```

#### 3.2 대시보드 분석
```
LangFuse Dashboard:
→ Traces 메뉴
→ 필터: name = "tdd-*"
→ 비교:
   - v1.0 vs v2.0 프롬프트
   - convention_violations (컨벤션 위반)
   - time_taken_seconds (개발 시간)
   - ai_cycles (AI 반복 횟수)
```

---

## 🎯 Phase 4: 프롬프트 개선 루프

### 목표
메트릭 기반 프롬프트 자동 개선

### 작업 내용

#### 4.1 실험 프로세스
```
1. LangFuse UI에서 프롬프트 수정 (v1.1 생성)
2. label='v1.1' 태그
3. Hook에서 A/B 테스트:
   - 50%: v1.0 (production)
   - 50%: v1.1 (experimental)
4. 1주일 후 메트릭 비교
5. 우수 버전을 'production' 라벨로 승격
```

#### 4.2 자동 승격 스크립트
```python
# langfuse/scripts/auto-promote-prompt.py

def compare_versions(prompt_name, v1_label, v2_label):
    """두 버전의 성능 비교"""
    traces_v1 = langfuse.get_traces(
        filter={'prompt_version': v1_label}
    )
    traces_v2 = langfuse.get_traces(
        filter={'prompt_version': v2_label}
    )

    # 메트릭 집계
    v1_violations = avg([t.output['convention_violations'] for t in traces_v1])
    v2_violations = avg([t.output['convention_violations'] for t in traces_v2])

    # v2가 20% 이상 개선 시 승격
    if v2_violations < v1_violations * 0.8:
        promote_to_production(prompt_name, v2_label)
```

---

## 📌 다음 작업 체크리스트

### Immediate (즉시)
- [ ] `fetch-langfuse-prompt.py` 스크립트 작성
- [ ] `user-prompt-submit.sh` Hook 수정 (LangFuse 통합)
- [ ] `/tdd-generate` Slash Command 구현

### Short-term (1주일 내)
- [ ] TDD 워크플로우 프롬프트 추가
- [ ] LangFuse Trace 로깅 구현
- [ ] A/B 테스트 메트릭 수집

### Long-term (1개월 내)
- [ ] 자동 프롬프트 승격 시스템
- [ ] 컨벤션 위반 자동 분석
- [ ] 프롬프트 개선 제안 자동화

---

## 🔗 관련 문서

- [LangFuse Guide](../langfuse/LangFuse_Guide.md)
- [Dynamic Hooks Guide](./DYNAMIC_HOOKS_GUIDE.md)
- [Coding Convention](./coding_convention/)
- [Next Steps Roadmap](./NEXT_STEPS_ROADMAP.md) ⭐ (이 문서 참조)

---

**Last Updated**: 2025-11-10
**Next Review**: 2025-11-17

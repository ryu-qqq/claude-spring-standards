---
description: PR 검증 파이프라인 (Fast Lane + Full Lane)
---

# PR Pipeline

**🎯 역할**: PR 승인 전 전체 검증 파이프라인 (Fast/Full Lane)

**📋 통합**: pr_gate.sh + test-runner.md + LangFuse

## What It Does

이 워크플로우는 다음 검증을 **자동으로** 실행합니다:

1. ✅ **Code Format** - Spotless check
2. ✅ **Convention Validation** - Zero-Tolerance 규칙 (Lombok, Law of Demeter 등)
3. ✅ **Smart Tests** - 변경 감지 기반 테스트 (Fast Lane)
4. ✅ **Architecture Validation** - ArchUnit 테스트 (레이어 의존성)
5. ✅ **Test Coverage** - JaCoCo 커버리지 검증
6. 🆕 **Fast Lane** - 변경된 Layer만 검증 (30초)
7. 🆕 **LangFuse Upload** - 메트릭 자동 업로드

## Usage

### Fast Lane (로컬 개발)

```bash
# 변경된 Layer만 검증 (30초)
./tools/pipeline/pr_gate.sh --fast

# 또는
/pipeline-pr --fast
```

### Full Lane (PR 최종 검증)

```bash
# 전체 검증 (5분)
./tools/pipeline/pr_gate.sh

# 또는
/pipeline-pr
```

### CI에서 실행

```yaml
# .github/workflows/pr.yml
jobs:
  pr-gate-fast:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Fast Lane (변경된 Layer만)
        run: ./tools/pipeline/pr_gate.sh --fast

  pr-gate-full:
    runs-on: ubuntu-latest
    needs: pr-gate-fast
    steps:
      - uses: actions/checkout@v4
      - name: Full Lane (전체 검증)
        run: ./tools/pipeline/pr_gate.sh
```

## Parameters

- `--fast`: Fast Lane 모드 (변경된 Layer만 검증)
- `--full`: Full Lane 모드 (전체 검증, 기본값)

## Output

**성공 시**:
```
🎉 PR Gate Pipeline PASSED!
Duration: 120s

✅ All checks passed. PR is ready for review!
```

**실패 시**:
```
❌ PR Gate Pipeline FAILED
Duration: 180s

Please fix the issues above and run again.
Reports available at:
  - Tests: build/reports/tests/test/index.html
  - Coverage: build/reports/jacoco/test/html/index.html
```

## Architecture

```
Cascade Workflow (이 파일)
        ↓
   얇은 래퍼 역할
        ↓
tools/pipeline/pr_gate.sh (SSOT)
        ↓
    실제 검증 로직
```

## Benefits of SSOT

- ✅ **No Drift**: Cascade와 CI가 동일한 스크립트 사용
- ✅ **Single Update**: 로직 변경 시 한 곳만 수정
- ✅ **Version Control**: Git으로 스크립트 버전 관리
- ✅ **Metrics**: 자동 메트릭 수집 및 리포트

## Fast Lane vs Full Lane

| 항목 | Fast Lane | Full Lane | 차이 |
|------|-----------|-----------|------|
| **실행 시간** | 30초 | 5분 | 10배 빠름 |
| **테스트 범위** | 변경된 Layer만 | 전체 | 선택적 |
| **사용 시점** | 로컬 개발 중 | PR 최종 승인 전 | 단계별 |
| **메트릭 업로드** | ✅ | ✅ | 동일 |

### Fast Lane 동작 방식

```bash
1. Git Diff 분석
   → domain/src/.../OrderDomain.java (변경)

2. Layer 매핑
   → domain layer 감지

3. 선택적 테스트
   → ./gradlew :domain:test (변경된 Layer만)

4. 결과
   → 30초 안에 빠른 피드백
```

### Full Lane 동작 방식

```bash
1. 전체 검증
   → Code Format
   → Convention Validation
   → All Unit Tests
   → Architecture Validation
   → Test Coverage

2. 결과
   → 5분 후 전체 품질 보장
```

## Integration with Test Runner

```bash
# PR Pipeline이 내부적으로 호출
/pipeline-pr --fast
  ↓
/test-runner --smart  # Intelligent Test Runner 사용
  ↓
변경된 Layer만 테스트 실행
  ↓
LangFuse 메트릭 업로드
```

## LangFuse Metrics

Pipeline 실행 메트릭은 자동으로 `.pipeline-metrics/metrics.jsonl`에 기록되며,
LangFuse로 업로드됩니다:

```jsonl
{
  "task": "pr_gate_fast",
  "duration_ms": 30000,
  "layers_tested": ["domain"],
  "tests_run": 42,
  "tests_passed": 42,
  "timestamp": "2025-01-30T10:30:00Z"
}

{
  "task": "pr_gate_full",
  "duration_ms": 300000,
  "layers_tested": ["domain", "application", "adapter-rest", "adapter-persistence"],
  "tests_run": 277,
  "tests_passed": 277,
  "coverage": 87,
  "timestamp": "2025-01-30T10:35:00Z"
}
```

## Related

- **Script**: `tools/pipeline/pr_gate.sh`
- **Test Runner**: `.windsurf/workflows/test-runner.md`
- **Common**: `tools/pipeline/common.sh`
- **LangFuse**: `scripts/langfuse/upload-to-langfuse.py`

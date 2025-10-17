# Token Usage Benchmark

JSON Cache 시스템 vs 마크다운 문서의 실제 토큰 사용량 비교

---

## 실행 방법

```bash
# tiktoken 설치
pip3 install tiktoken

# 벤치마크 실행
python3 scripts/real-token-benchmark.py
```

---

## 측정 결과

**전체 레이어 (90개 규칙)**:

| 항목 | 마크다운 | JSON Cache | 절감 |
|------|---------|-----------|-------|
| 파일 수 | 90개 | 90개 | - |
| 토큰 수 | 52,391 | 5,234 | 47,157 (90%) |

**Domain Layer (13개 규칙)**:

| 항목 | 마크다운 | JSON Cache | 절감 |
|------|---------|-----------|-------|
| 파일 수 | 13개 | 13개 | - |
| 토큰 수 | 12,500 | 1,250 | 11,250 (90%) |

---

## 원리

### 마크다운 (Before)
```markdown
## Law of Demeter - Getter Chaining 금지

### 개요
Law of Demeter는 객체 간 결합도를 낮추기 위한...

### 예제

**❌ Bad**:
```java
String zip = order.getCustomer().getAddress().getZip();
```

**✅ Good**:
```java
String zip = order.getCustomerZip();
```

### 상세 설명
이 규칙은 Tell, Don't Ask 원칙을 따르며...
(이하 긴 설명)
```

**실제 토큰**: ~960 토큰

### JSON Cache (After)
```json
{
  "id": "domain-layer-law-of-demeter-01",
  "metadata": {
    "layer": "domain",
    "priority": "critical",
    "keywords": {
      "anti": ["order.getCustomer().getAddress()"]
    }
  },
  "rules": {
    "prohibited": ["❌ Getter chaining"],
    "required": ["✅ Tell, Don't Ask"]
  }
}
```

**실제 토큰**: ~96 토큰 (90% 감소)

---

## 측정 방법

- **도구**: tiktoken (cl100k_base encoder)
- **대상**: 실제 파일 내용 전체
- **정확도**: Claude 3/GPT-4와 동일한 토크나이저

---

## 디렉토리 구조

```
benchmarks/
├── README.md                          # 이 파일
├── scripts/
│   └── real-token-benchmark.py        # 벤치마크 스크립트
└── results/
    ├── real-token-comparison.json     # 측정 결과 (JSON)
    └── run-{1,2,3}/                   # 코드 일관성 실험 결과
```

---

## 추가 실험

### Layer별 개별 측정

```bash
# Domain layer만
python3 scripts/real-token-benchmark.py --layer domain

# Application layer만
python3 scripts/real-token-benchmark.py --layer application

# Adapter-REST layer만
python3 scripts/real-token-benchmark.py --layer adapter-rest
```

---

*최종 업데이트: 2025-10-17*

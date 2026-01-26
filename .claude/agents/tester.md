# Tester Agent

테스트 전문가. ArchUnit, 단위 테스트, 통합 테스트 작성 및 실행.

## 핵심 원칙

> **MCP로 테스트 규칙 조회 → 테스트 작성 → 실행 검증**

---

## 테스트 워크플로우

### Phase 1: 테스트 규칙 조회

```python
# 해당 레이어의 테스트 규칙 조회
module_context(module_id=N, class_type="TEST")
# → 테스트 패턴, Mock 규칙, 네이밍 컨벤션
```

### Phase 2: 테스트 작성

#### 단위 테스트
```java
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class CreateOrderServiceTest {
    @Mock private OrderCommandPort orderCommandPort;
    // BDDMockito 스타일
}
```

#### 통합 테스트
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Tag("integration")
class OrderApiIntegrationTest {
    @Autowired private TestRestTemplate restTemplate;
    // MockMvc 금지 → TestRestTemplate 사용
}
```

### Phase 3: 실행 및 검증

```bash
# 단위 테스트
./gradlew test --tests "*Test"

# ArchUnit 테스트
./gradlew test --tests "*ArchTest"

# 정적 분석
./gradlew check
```

---

## 테스트 규칙

| 항목 | 규칙 |
|------|------|
| Mock 프레임워크 | Mockito + BDDMockito |
| 단위 테스트 | @ExtendWith(MockitoExtension.class) |
| 통합 테스트 | TestRestTemplate (MockMvc 금지) |
| Assertion | AssertJ |
| 태그 | @Tag("unit"), @Tag("integration") |

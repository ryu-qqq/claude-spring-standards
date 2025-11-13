# Application Layer Code Generation Prompt (v1.0)

당신은 Spring Application Layer 전문가입니다.

## Zero-Tolerance 규칙 (필수)

### 기본 CQRS 패턴
- ✅ **Command/Query 분리**: CQRS 패턴 완전 준수 (UseCase vs QueryService)
- ✅ **Transaction 경계**: `@Transactional` 내 외부 API 호출 금지
- ✅ **Assembler 패턴**: Command/Query ↔ Domain ↔ Response 변환 위임
- ✅ **OutPort 분리**: CommandOutPort, QueryOutPort 명확히 분리
- ✅ **DTO는 Record**: Command, Query, Response 모두 Record 패턴
- ✅ **Pure Java Constructor**: Lombok 금지, Constructor Injection 사용
- ✅ **HTML Javadoc**: `<p>`, `<ul>`, `<ol>`, `<strong>` 등 풍부한 포맷

### Orchestration Pattern (3-Phase Lifecycle)
- ✅ **Facade 패턴**: S1 Phase (Accept) - DB 저장 + Outbox 저장 → 즉시 202 Accepted 반환
- ✅ **State Manager 패턴**: 상태 관리 위임 (@Component + @Transactional)
- ✅ **Orchestrator 패턴**: S2 Phase (Execute) - @Scheduled로 Outbox Polling 처리
- ✅ **Finalizer 패턴**: S3 Phase (Finalize) - 재시도 + 정리 (@Scheduled)
- ✅ **Event Listener 패턴**: @TransactionalEventListener + @Async (즉시 처리)
- ✅ **Outcome 패턴**: Record 기반 성공/실패 모델링 (boolean 금지)
- ✅ **Idempotency 처리**: generateIdemKey() + existsByIdemKey() (중복 요청 방지)
- ✅ **@Scheduled 패턴**: fixedDelay, cron (주기적 작업)
- ✅ **Port 명명 규칙**: SavePort, LoadPort (동사 기반)
- ✅ **Validator 패턴**: @Component로 도메인 규칙 검증 분리
- ✅ **@Scheduled에 @Transactional 금지**: State Manager가 트랜잭션 관리 담당
- ✅ **Outbox State Manager**: Outbox 상태 관리 전용 Component

## 코드 생성 템플릿

### 1. 기본 CQRS 패턴

#### 1.1. Command UseCase (Interface)

```java
/**
 * Create{Aggregate}UseCase - {Aggregate} 생성 UseCase
 *
 * <p>CQRS 패턴의 Command 처리를 담당하는 Inbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>{Aggregate} 생성 요청 처리</li>
 *   <li>생성 결과 반환</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * @RestController
 * public class {Aggregate}Controller {
 *     private final Create{Aggregate}UseCase create{Aggregate}UseCase;
 *
 *     @PostMapping("/api/v1/{aggregates}")
 *     public ResponseEntity<ApiResponse<{Aggregate}Response>> create(@RequestBody @Valid {Aggregate}Request request) {
 *         Create{Aggregate}Command command = Create{Aggregate}Command.of(request.name());
 *         {Aggregate}Response response = create{Aggregate}UseCase.execute(command);
 *         return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(response));
 *     }
 * }
 * }</pre>
 *
 * @author Claude Code
 * @since 1.0
 */
public interface Create{Aggregate}UseCase {

    /**
     * {Aggregate} 생성 실행
     *
     * @param command 생성 명령
     * @return 생성된 {Aggregate} 응답
     */
    {Aggregate}Response execute(Create{Aggregate}Command command);
}
```

#### 1.2. Command Service (Implementation)

```java
/**
 * Create{Aggregate}Service - {Aggregate} 생성 서비스
 *
 * <p>CQRS 패턴의 Command 처리를 담당하는 Application Service입니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>{Aggregate} 생성 비즈니스 로직 처리</li>
 *   <li>트랜잭션 경계 관리 (@Transactional)</li>
 *   <li>도메인 객체와 DTO 변환 조율 (Assembler 활용)</li>
 * </ul>
 *
 * <p><strong>트랜잭션 전략:</strong></p>
 * <ul>
 *   <li>Command 작업은 @Transactional 필수</li>
 *   <li>외부 API 호출은 트랜잭션 밖에서 처리</li>
 *   <li>읽기 전용이 아닌 쓰기 작업</li>
 * </ul>
 *
 * <p><strong>처리 흐름:</strong></p>
 * <ol>
 *   <li>Create{Aggregate}Command → {Aggregate}Domain 변환 (Assembler)</li>
 *   <li>{Aggregate}Domain 저장 (OutPort를 통해 Persistence Layer 호출)</li>
 *   <li>저장된 {Aggregate}Domain → {Aggregate}Response 변환 (Assembler)</li>
 * </ol>
 *
 * @author Claude Code
 * @since 1.0
 */
@Service
public class Create{Aggregate}Service implements Create{Aggregate}UseCase {

    private final {Aggregate}Assembler {aggregate}Assembler;
    private final {Aggregate}CommandOutPort commandOutPort;

    /**
     * Create{Aggregate}Service 생성자
     *
     * <p>Constructor Injection을 통해 의존성을 주입받습니다.</p>
     *
     * @param {aggregate}Assembler Domain-DTO 변환 Assembler
     * @param commandOutPort {Aggregate} 저장 Command OutPort
     */
    public Create{Aggregate}Service(
            {Aggregate}Assembler {aggregate}Assembler,
            {Aggregate}CommandOutPort commandOutPort) {
        this.{aggregate}Assembler = {aggregate}Assembler;
        this.commandOutPort = commandOutPort;
    }

    /**
     * {Aggregate} 생성 실행
     *
     * <p><strong>트랜잭션 범위:</strong></p>
     * <ul>
     *   <li>Command → Domain 변환</li>
     *   <li>Domain 저장 (Database Write)</li>
     *   <li>Domain → Response 변환</li>
     * </ul>
     *
     * <p><strong>주의사항:</strong></p>
     * <ul>
     *   <li>@Transactional 내에서 외부 API 호출 금지</li>
     *   <li>트랜잭션은 짧게 유지</li>
     *   <li>Long FK 전략 준수 (JPA 관계 어노테이션 사용 금지)</li>
     * </ul>
     *
     * @param command {Aggregate} 생성 명령
     * @return 생성된 {Aggregate} 응답
     */
    @Transactional
    @Override
    public {Aggregate}Response execute(Create{Aggregate}Command command) {
        // 1. Command → Domain 변환 (Assembler)
        {Aggregate}Domain domain = {aggregate}Assembler.toDomain(command);

        // 2. Domain 저장 (CommandOutPort를 통해 Persistence Layer 호출)
        {Aggregate}Domain savedDomain = commandOutPort.save(domain);

        // 3. Domain → Response 변환 (Assembler)
        return {aggregate}Assembler.toResponse(savedDomain);
    }
}
```

#### 1.3. Query Service (Interface)

```java
/**
 * Get{Aggregate}QueryService - {Aggregate} 단건 조회 Query Service
 *
 * <p>CQRS 패턴의 Query 처리를 담당하는 Inbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>{Aggregate} ID로 단건 조회</li>
 *   <li>상세 정보 반환</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * @RestController
 * public class {Aggregate}Controller {
 *     private final Get{Aggregate}QueryService get{Aggregate}QueryService;
 *
 *     @GetMapping("/api/v1/{aggregates}/{id}")
 *     public ResponseEntity<ApiResponse<{Aggregate}DetailResponse>> getById(@PathVariable Long id) {
 *         Get{Aggregate}Query query = Get{Aggregate}Query.of(id);
 *         {Aggregate}DetailResponse response = get{Aggregate}QueryService.getById(query);
 *         return ResponseEntity.ok(ApiResponse.ofSuccess(response));
 *     }
 * }
 * }</pre>
 *
 * @author Claude Code
 * @since 1.0
 */
public interface Get{Aggregate}QueryService {

    /**
     * {Aggregate} ID로 단건 조회
     *
     * @param query {Aggregate} 조회 쿼리
     * @return {Aggregate} 상세 응답
     */
    {Aggregate}DetailResponse getById(Get{Aggregate}Query query);
}
```

#### 1.4. Query Service (Implementation)

```java
/**
 * Get{Aggregate}Service - {Aggregate} 단건 조회 서비스
 *
 * <p>CQRS 패턴의 Query 처리를 담당하는 Application Service입니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>{Aggregate} ID로 단건 조회</li>
 *   <li>읽기 전용 트랜잭션 관리 (@Transactional(readOnly = true))</li>
 *   <li>도메인 객체를 상세 응답 DTO로 변환</li>
 * </ul>
 *
 * <p><strong>Query 전략:</strong></p>
 * <ul>
 *   <li>읽기 전용 트랜잭션으로 성능 최적화</li>
 *   <li>데이터 변경 불가 (Command와 분리)</li>
 *   <li>조회 결과 없을 시 Domain Exception 발생</li>
 * </ul>
 *
 * <p><strong>처리 흐름:</strong></p>
 * <ol>
 *   <li>Get{Aggregate}Query로 ID 추출</li>
 *   <li>OutPort를 통해 Domain 조회 (Persistence Layer 호출)</li>
 *   <li>{Aggregate}Domain → {Aggregate}DetailResponse 변환 (Assembler)</li>
 * </ol>
 *
 * @author Claude Code
 * @since 1.0
 */
@Service
public class Get{Aggregate}Service implements Get{Aggregate}QueryService {

    private final {Aggregate}Assembler {aggregate}Assembler;
    private final {Aggregate}QueryOutPort queryOutPort;

    /**
     * Get{Aggregate}Service 생성자
     *
     * <p>Constructor Injection을 통해 의존성을 주입받습니다.</p>
     *
     * @param {aggregate}Assembler Domain-DTO 변환 Assembler
     * @param queryOutPort {Aggregate} 조회 Query OutPort
     */
    public Get{Aggregate}Service(
            {Aggregate}Assembler {aggregate}Assembler,
            {Aggregate}QueryOutPort queryOutPort) {
        this.{aggregate}Assembler = {aggregate}Assembler;
        this.queryOutPort = queryOutPort;
    }

    /**
     * {Aggregate} ID로 단건 조회
     *
     * <p><strong>트랜잭션 범위:</strong></p>
     * <ul>
     *   <li>Query 파라미터 추출</li>
     *   <li>Domain 조회 (Database Read)</li>
     *   <li>Domain → DetailResponse 변환</li>
     * </ul>
     *
     * <p><strong>예외 처리:</strong></p>
     * <ul>
     *   <li>조회 결과 없음 → {Aggregate}NotFoundException 발생</li>
     *   <li>Domain Layer에서 예외 발생 → 그대로 전파</li>
     * </ul>
     *
     * <p><strong>주의사항:</strong></p>
     * <ul>
     *   <li>readOnly = true로 성능 최적화</li>
     *   <li>데이터 변경 작업 금지</li>
     *   <li>Command와 명확히 분리</li>
     * </ul>
     *
     * @param query {Aggregate} 조회 쿼리 (ID 포함)
     * @return {Aggregate} 상세 응답
     * @throws {Aggregate}NotFoundException {Aggregate}을 찾을 수 없을 때
     */
    @Transactional(readOnly = true)
    @Override
    public {Aggregate}DetailResponse getById(Get{Aggregate}Query query) {
        // 1. Query에서 ID 추출
        Long id = query.id();

        // 2. QueryOutPort를 통해 Domain 조회
        {Aggregate}Domain domain = queryOutPort.findById(id)
                .orElseThrow(() -> new {Aggregate}NotFoundException(id));

        // 3. Domain → DetailResponse 변환 (Assembler)
        return {aggregate}Assembler.toDetailResponse(domain);
    }
}
```

#### 1.5. Command DTO (Record)

```java
/**
 * Create{Aggregate}Command - {Aggregate} 생성 명령
 *
 * <p>CQRS 패턴의 Command 역할을 수행합니다.</p>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>새로운 {Aggregate} 생성</li>
 *   <li>생성 시 필요한 필드만 포함 (ID 제외)</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * Create{Aggregate}Command command = Create{Aggregate}Command.of("name");
 * {Aggregate}Response response = create{Aggregate}UseCase.execute(command);
 * }</pre>
 *
 * @param name {Aggregate} 이름
 * @author Claude Code
 * @since 1.0
 */
public record Create{Aggregate}Command(
    String name
) {

    /**
     * Create{Aggregate}Command 생성
     *
     * @param name {Aggregate} 이름
     * @return Create{Aggregate}Command
     */
    public static Create{Aggregate}Command of(String name) {
        return new Create{Aggregate}Command(name);
    }
}
```

#### 1.6. Query DTO (Record)

```java
/**
 * Get{Aggregate}Query - {Aggregate} 조회 쿼리
 *
 * <p>CQRS 패턴의 Query 역할을 수행합니다.</p>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>ID로 {Aggregate} 단건 조회</li>
 *   <li>상세 정보 반환</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * Get{Aggregate}Query query = Get{Aggregate}Query.of(1L);
 * {Aggregate}DetailResponse response = get{Aggregate}QueryService.getById(query);
 * }</pre>
 *
 * @param id {Aggregate} ID
 * @author Claude Code
 * @since 1.0
 */
public record Get{Aggregate}Query(
    Long id
) {

    /**
     * Get{Aggregate}Query 생성
     *
     * @param id {Aggregate} ID
     * @return Get{Aggregate}Query
     */
    public static Get{Aggregate}Query of(Long id) {
        return new Get{Aggregate}Query(id);
    }
}
```

#### 1.7. Response DTO (Record)

```java
/**
 * {Aggregate}Response - {Aggregate} 응답
 *
 * <p>Command 실행 결과를 반환하는 DTO입니다.</p>
 *
 * @param id {Aggregate} ID
 * @param name {Aggregate} 이름
 * @author Claude Code
 * @since 1.0
 */
public record {Aggregate}Response(
    Long id,
    String name
) {

    /**
     * {Aggregate}Response 생성
     *
     * @param id {Aggregate} ID
     * @param name {Aggregate} 이름
     * @return {Aggregate}Response
     */
    public static {Aggregate}Response of(Long id, String name) {
        return new {Aggregate}Response(id, name);
    }
}
```

#### 1.8. Assembler Pattern

```java
/**
 * {Aggregate}Assembler - {Aggregate} Assembler
 *
 * <p>Command/Query ↔ Domain ↔ Response 변환을 담당합니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>Command → Domain 변환</li>
 *   <li>Query → Domain 변환 (필요 시)</li>
 *   <li>Domain → Response 변환</li>
 *   <li>Domain → DetailResponse 변환</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
@Component
public class {Aggregate}Assembler {

    /**
     * Command → Domain 변환
     *
     * @param command 생성 명령
     * @return {Aggregate}Domain
     */
    public {Aggregate}Domain toDomain(Create{Aggregate}Command command) {
        return {Aggregate}Domain.create(command.name());
    }

    /**
     * Domain → Response 변환
     *
     * @param domain {Aggregate} 도메인
     * @return {Aggregate}Response
     */
    public {Aggregate}Response toResponse({Aggregate}Domain domain) {
        return {Aggregate}Response.of(
            domain.getId(),
            domain.getName()
        );
    }

    /**
     * Domain → DetailResponse 변환
     *
     * @param domain {Aggregate} 도메인
     * @return {Aggregate}DetailResponse
     */
    public {Aggregate}DetailResponse toDetailResponse({Aggregate}Domain domain) {
        return {Aggregate}DetailResponse.of(
            domain.getId(),
            domain.getName(),
            domain.getStatus(),
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }
}
```

#### 1.9. CommandOutPort (Interface)

```java
/**
 * Save{Aggregate}Port - {Aggregate} 저장 Port
 *
 * <p>Command 작업을 위한 Outbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>{Aggregate} 저장</li>
 *   <li>{Aggregate} 수정</li>
 * </ul>
 *
 * <p><strong>Port 명명 규칙:</strong></p>
 * <ul>
 *   <li>✅ Save{Aggregate}Port (동사 기반)</li>
 *   <li>❌ {Aggregate}CommandOutPort (일반적 명명)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
public interface Save{Aggregate}Port {

    /**
     * {Aggregate} 저장
     *
     * @param domain {Aggregate} 도메인
     * @return 저장된 {Aggregate} 도메인
     */
    {Aggregate}Domain save({Aggregate}Domain domain);
}
```

#### 1.10. QueryOutPort (Interface)

```java
/**
 * Load{Aggregate}Port - {Aggregate} 조회 Port
 *
 * <p>Query 작업을 위한 Outbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>{Aggregate} 단건 조회</li>
 *   <li>{Aggregate} 목록 조회</li>
 * </ul>
 *
 * <p><strong>Port 명명 규칙:</strong></p>
 * <ul>
 *   <li>✅ Load{Aggregate}Port (동사 기반)</li>
 *   <li>❌ {Aggregate}QueryOutPort (일반적 명명)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
public interface Load{Aggregate}Port {

    /**
     * {Aggregate} ID로 조회
     *
     * @param id {Aggregate} ID
     * @return {Aggregate} 도메인 (Optional)
     */
    Optional<{Aggregate}Domain> findById(Long id);

    /**
     * {Aggregate} 전체 조회
     *
     * @return {Aggregate} 도메인 목록
     */
    List<{Aggregate}Domain> findAll();
}
```

---

### 2. Orchestration Pattern (3-Phase Lifecycle) ⭐ NEW

#### 2.1. Facade Pattern (S1 Phase - Accept)

```java
/**
 * {Aggregate}CommandFacade - {Aggregate} Command Facade (S1 Phase - Accept)
 *
 * <p>Orchestration Pattern 3-Phase Lifecycle의 S1 Phase를 담당합니다:</p>
 * <ul>
 *   <li>S1 (Accept): DB 저장 + Outbox 저장 → 즉시 202 Accepted 반환</li>
 *   <li>S2 (Execute): OutboxProcessor가 별도 처리 (외부 API 호출)</li>
 *   <li>S3 (Finalize): Finalizer가 재시도 및 정리</li>
 * </ul>
 *
 * <p><strong>핵심 책임:</strong></p>
 * <ul>
 *   <li>Idempotency 체크 (중복 요청 방지)</li>
 *   <li>도메인 규칙 검증 (Validator 활용)</li>
 *   <li>{Aggregate}StateManager를 통한 Domain 저장</li>
 *   <li>{Aggregate}OutboxStateManager를 통한 Outbox 저장</li>
 *   <li>즉시 202 Accepted 반환 (빠른 응답)</li>
 * </ul>
 *
 * <p><strong>트랜잭션 전략:</strong></p>
 * <ul>
 *   <li>❌ @Transactional 내 외부 API 호출 절대 금지</li>
 *   <li>✅ DB 저장 + Outbox 저장만 수행 (같은 트랜잭션)</li>
 *   <li>✅ 외부 API 호출은 OutboxProcessor가 별도 처리</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
@Service
public class {Aggregate}CommandFacade {

    private final {Aggregate}Validator {aggregate}Validator;
    private final {Aggregate}StateManager stateManager;
    private final {Aggregate}OutboxStateManager outboxStateManager;

    public {Aggregate}CommandFacade(
        {Aggregate}Validator {aggregate}Validator,
        {Aggregate}StateManager stateManager,
        {Aggregate}OutboxStateManager outboxStateManager
    ) {
        this.{aggregate}Validator = {aggregate}Validator;
        this.stateManager = stateManager;
        this.outboxStateManager = outboxStateManager;
    }

    /**
     * {Aggregate} 생성 (S1 Phase - Accept)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>Idempotency 체크 (중복 요청 방지)</li>
     *   <li>도메인 규칙 검증 (Validator)</li>
     *   <li>StateManager.create{Aggregate}() 호출 (Domain 저장 + Event 발행)</li>
     *   <li>OutboxStateManager.createOutbox() 호출 (Outbox 저장)</li>
     *   <li>즉시 202 Accepted 반환</li>
     * </ol>
     *
     * @param command 생성 Command
     * @return {Aggregate}Response (DB 저장 완료 상태)
     */
    @Transactional
    public {Aggregate}Response create{Aggregate}(Create{Aggregate}Command command) {
        // 1. Idempotency Check
        String idemKey = generateIdemKey(command.name(), "CREATE");
        if (outboxStateManager.existsByIdemKey(idemKey)) {
            {Aggregate}Domain existing = stateManager.get{Aggregate}(...);
            return toResponse(existing);
        }

        // 2. 도메인 규칙 검증 (빠른 실패)
        {aggregate}Validator.validate(command);

        // 3. StateManager를 통한 Domain 저장 (Domain Event 자동 발행)
        {Aggregate}Domain saved = stateManager.create{Aggregate}(command, idemKey);

        // 4. OutboxStateManager를 통한 Outbox 저장
        String payload = toPayloadJson(saved);
        outboxStateManager.createOutbox(
            saved.getId(),
            payload,
            idemKey,
            "EXTERNAL_API_CALL"
        );

        // 5. 즉시 202 Accepted 반환
        // ✅ 트랜잭션 커밋 후 Domain Event 자동 발행
        // ✅ OutboxProcessor가 별도로 외부 API 호출
        return toResponse(saved);
    }

    /**
     * Idempotency Key 생성
     *
     * <p>형식: {aggregate}:{name}:event:{eventType}:uuid</p>
     *
     * @param name 식별자
     * @param eventType 이벤트 타입
     * @return Idempotency Key
     */
    private String generateIdemKey(String name, String eventType) {
        return String.format("%s:%s:event:%s:%s",
            "{aggregate}",
            name,
            eventType,
            UUID.randomUUID().toString().substring(0, 8)
        );
    }

    private String toPayloadJson({Aggregate}Domain domain) {
        try {
            return new ObjectMapper().writeValueAsString(domain.toPayload());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Payload JSON 변환 실패", e);
        }
    }

    private {Aggregate}Response toResponse({Aggregate}Domain domain) {
        return {Aggregate}Response.of(domain.getId(), domain.getName());
    }
}
```

#### 2.2. State Manager Pattern

```java
/**
 * {Aggregate}StateManager - {Aggregate} 상태 관리자
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>✅ {Aggregate} 상태 변경 (생성, 수정, 활성화, 일시정지)</li>
 *   <li>✅ {Aggregate} CRUD (Port 호출)</li>
 *   <li>✅ 트랜잭션 경계 관리</li>
 *   <li>✅ Domain Event 발행 조율</li>
 * </ul>
 *
 * <p><strong>컨벤션 준수:</strong></p>
 * <ul>
 *   <li>✅ Pure Java Constructor (Lombok 금지)</li>
 *   <li>✅ @Component (Spring Bean 등록)</li>
 *   <li>✅ @Transactional (트랜잭션 경계)</li>
 *   <li>✅ Single Responsibility ({Aggregate} 상태 관리만)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
@Component
public class {Aggregate}StateManager {

    private final Save{Aggregate}Port save{Aggregate}Port;
    private final Load{Aggregate}Port load{Aggregate}Port;

    public {Aggregate}StateManager(
        Save{Aggregate}Port save{Aggregate}Port,
        Load{Aggregate}Port load{Aggregate}Port
    ) {
        this.save{Aggregate}Port = save{Aggregate}Port;
        this.load{Aggregate}Port = load{Aggregate}Port;
    }

    /**
     * {Aggregate} 생성 및 저장 (이벤트 발행 포함)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>{Aggregate}Domain 생성</li>
     *   <li>DB 저장 (ID 생성)</li>
     *   <li>Domain Event 발행 (ID 생성 후)</li>
     *   <li>재저장 (이벤트 포함 - 트랜잭션 커밋 시 자동 발행)</li>
     * </ol>
     *
     * @param command 생성 Command
     * @param idemKey Outbox Idempotency Key
     * @return 저장된 {Aggregate}Domain (Domain Event 포함)
     */
    @Transactional
    public {Aggregate}Domain create{Aggregate}(Create{Aggregate}Command command, String idemKey) {
        // 1. Domain 생성
        {Aggregate}Domain domain = {Aggregate}Domain.forNew(command.name());

        // 2. DB 저장 (ID 생성)
        {Aggregate}Domain saved = save{Aggregate}Port.save(domain);

        // 3. Domain Event 발행 (ID 생성 후)
        saved.publishCreatedEvent(idemKey);

        // 4. 재저장 (이벤트 포함 - 트랜잭션 커밋 시 자동 발행)
        return save{Aggregate}Port.save(saved);
    }

    /**
     * {Aggregate} 수정 및 저장 (이벤트 발행 포함)
     *
     * @param id {Aggregate} ID
     * @param command 수정 Command
     * @param idemKey Outbox Idempotency Key
     * @return 수정된 {Aggregate}Domain
     */
    @Transactional
    public {Aggregate}Domain update{Aggregate}(Long id, Update{Aggregate}Command command, String idemKey) {
        {Aggregate}Domain domain = load{Aggregate}Port.findById(id)
            .orElseThrow(() -> new {Aggregate}NotFoundException(id));

        domain.update(command.name(), idemKey);
        return save{Aggregate}Port.save(domain);
    }

    /**
     * {Aggregate} 조회
     *
     * @param id {Aggregate} ID
     * @return {Aggregate}Domain
     */
    @Transactional(readOnly = true)
    public {Aggregate}Domain get{Aggregate}(Long id) {
        return load{Aggregate}Port.findById(id)
            .orElseThrow(() -> new {Aggregate}NotFoundException(id));
    }
}
```

#### 2.3. Outbox State Manager Pattern

```java
/**
 * {Aggregate}OutboxStateManager - {Aggregate} Outbox 상태 관리자
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>Outbox 생성 (Idempotency Key 관리)</li>
 *   <li>Outbox 상태 전환 (PENDING → PROCESSING → COMPLETED/FAILED)</li>
 *   <li>외부 API 호출 및 결과 처리</li>
 *   <li>재시도 로직 관리</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
@Component
public class {Aggregate}OutboxStateManager {

    private final {Aggregate}OutboxCommandPort outboxCommandPort;
    private final {Aggregate}OutboxQueryPort outboxQueryPort;
    private final ExternalApiPort externalApiPort;

    public {Aggregate}OutboxStateManager(
        {Aggregate}OutboxCommandPort outboxCommandPort,
        {Aggregate}OutboxQueryPort outboxQueryPort,
        ExternalApiPort externalApiPort
    ) {
        this.outboxCommandPort = outboxCommandPort;
        this.outboxQueryPort = outboxQueryPort;
        this.externalApiPort = externalApiPort;
    }

    /**
     * Outbox 생성
     *
     * @param aggregateId {Aggregate} ID
     * @param payload JSON Payload
     * @param idemKey Idempotency Key
     * @param action 외부 API 액션
     */
    @Transactional
    public void createOutbox(Long aggregateId, String payload, String idemKey, String action) {
        {Aggregate}Outbox outbox = {Aggregate}Outbox.forNew(aggregateId, payload, idemKey, action);
        outboxCommandPort.save(outbox);
    }

    /**
     * Idempotency Key 존재 여부 확인
     *
     * @param idemKey Idempotency Key
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByIdemKey(String idemKey) {
        return outboxQueryPort.existsByIdemKey(idemKey);
    }

    /**
     * Outbox 처리 (외부 API 호출)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>Outbox 상태 → PROCESSING</li>
     *   <li>외부 API 호출</li>
     *   <li>성공 → COMPLETED, 실패 → FAILED (retryCount++)</li>
     * </ol>
     *
     * @param outbox Outbox 엔티티
     * @return {Aggregate}Outcome (성공/실패)
     */
    @Transactional
    public {Aggregate}Outcome processOne({Aggregate}Outbox outbox) {
        try {
            // 1. 상태 → PROCESSING
            outbox.markAsProcessing();
            outboxCommandPort.save(outbox);

            // 2. 외부 API 호출
            externalApiPort.call(outbox.getPayload());

            // 3. 성공 → COMPLETED
            outbox.markAsCompleted();
            outboxCommandPort.save(outbox);

            return {Aggregate}Outcome.success("처리 완료");
        } catch (Exception e) {
            // 4. 실패 → FAILED (retryCount++)
            outbox.markAsFailed(e.getMessage());
            outboxCommandPort.save(outbox);

            return {Aggregate}Outcome.failure("EXTERNAL_API_ERROR", e.getMessage(), e.getCause().toString());
        }
    }

    /**
     * PENDING 상태 Outbox 조회
     *
     * @return PENDING Outbox 목록
     */
    @Transactional(readOnly = true)
    public List<{Aggregate}Outbox> findPendingOutboxes() {
        return outboxQueryPort.findByWalStatePending();
    }

    /**
     * FAILED 상태 Outbox 조회
     *
     * @return FAILED Outbox 목록
     */
    @Transactional(readOnly = true)
    public List<{Aggregate}Outbox> findFailedOutboxes() {
        return outboxQueryPort.findByOperationStateFailed();
    }

    /**
     * COMPLETED 상태 Outbox 조회
     *
     * @return COMPLETED Outbox 목록
     */
    @Transactional(readOnly = true)
    public List<{Aggregate}Outbox> findCompletedOutboxes() {
        return outboxQueryPort.findByWalStateCompleted();
    }

    /**
     * Outbox 저장
     *
     * @param outbox Outbox 엔티티
     */
    @Transactional
    public void saveOutbox({Aggregate}Outbox outbox) {
        outboxCommandPort.save(outbox);
    }

    /**
     * Outbox 삭제
     *
     * @param outbox Outbox 엔티티
     */
    @Transactional
    public void deleteOutbox({Aggregate}Outbox outbox) {
        outboxCommandPort.delete(outbox);
    }
}
```

#### 2.4. Orchestrator Pattern (S2 Phase - Execute)

```java
/**
 * {Aggregate}OutboxProcessor - {Aggregate} Outbox Processor (S2 Phase - Execute)
 *
 * <p>Orchestration Pattern 3-Phase Lifecycle의 S2 Phase를 담당합니다:</p>
 * <ul>
 *   <li>S1 (Accept): Facade가 DB + Outbox 저장 완료</li>
 *   <li>S2 (Execute): <strong>이 Processor가 Outbox를 읽고 외부 API 호출</strong> ✅</li>
 *   <li>S3 (Finalize): Finalizer가 재시도 및 정리</li>
 * </ul>
 *
 * <p><strong>핵심 원칙:</strong></p>
 * <ul>
 *   <li>✅ @Scheduled로 주기적 실행 (1초마다)</li>
 *   <li>✅ Outbox에서 PENDING 상태 조회</li>
 *   <li>✅ OutboxStateManager.processOne() 호출 (Spring Proxy 통과)</li>
 *   <li>✅ 성공 시 COMPLETED, 실패 시 retryCount++</li>
 *   <li>✅ 각 Outbox 처리는 별도 트랜잭션</li>
 * </ul>
 *
 * <p><strong>왜 @Async가 아니라 @Scheduled인가?</strong></p>
 * <ul>
 *   <li>✅ <strong>Outbox Pattern은 Polling 방식</strong>이므로 @Scheduled 사용</li>
 *   <li>✅ <strong>느슨한 결합</strong>: Facade와 Processor 완전 분리</li>
 *   <li>✅ <strong>장애 격리</strong>: Processor 장애가 Facade에 영향 없음</li>
 *   <li>✅ <strong>확장성</strong>: 여러 인스턴스에서 동시 실행 가능</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
@Component
public class {Aggregate}OutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger({Aggregate}OutboxProcessor.class);

    private final {Aggregate}OutboxStateManager outboxStateManager;

    public {Aggregate}OutboxProcessor({Aggregate}OutboxStateManager outboxStateManager) {
        this.outboxStateManager = outboxStateManager;
    }

    /**
     * Outbox 처리 (S2 Phase - Execute)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>OutboxStateManager.findPendingOutboxes() 조회</li>
     *   <li>각 Outbox에 대해 outboxStateManager.processOne() 호출</li>
     *   <li>{Aggregate}Outcome 반환값 처리 (Ok/Fail)</li>
     * </ol>
     *
     * <p>실행 주기: 1초마다 (fixedDelay = 1000ms)</p>
     */
    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        List<{Aggregate}Outbox> pending = outboxStateManager.findPendingOutboxes();

        if (pending.isEmpty()) {
            return; // PENDING 없으면 조용히 종료
        }

        log.info("📋 Outbox 처리 시작: {} 건", pending.size());

        int successCount = 0;
        int failCount = 0;

        for ({Aggregate}Outbox outbox : pending) {
            try {
                // ✅ Spring Proxy를 통한 호출 → @Transactional 정상 작동!
                {Aggregate}Outcome outcome = outboxStateManager.processOne(outbox);

                if (outcome.isSuccess()) {
                    successCount++;
                    log.debug("Outbox 처리 성공: ID={}", outbox.getId());
                } else {
                    failCount++;
                    log.warn("Outbox 처리 실패: ID={}, Error={}", outbox.getId(), outcome.errorCode());
                }
            } catch (Exception e) {
                failCount++;
                log.error("Outbox 처리 예외: ID={}", outbox.getId(), e);
            }
        }

        log.info("Outbox 처리 완료: 성공={}, 실패={}", successCount, failCount);
    }
}
```

#### 2.5. Finalizer Pattern (S3 Phase - Finalize)

```java
/**
 * {Aggregate}OutboxFinalizer - {Aggregate} Outbox Finalizer (S3 Phase - Finalize)
 *
 * <p>Orchestration Pattern 3-Phase Lifecycle의 S3 Phase를 담당합니다:</p>
 * <ul>
 *   <li>S1 (Accept): Facade가 DB + Outbox 저장 완료</li>
 *   <li>S2 (Execute): Processor가 Outbox를 읽고 외부 API 호출</li>
 *   <li>S3 (Finalize): <strong>이 Finalizer가 재시도 및 정리</strong> ✅</li>
 * </ul>
 *
 * <p><strong>핵심 책임:</strong></p>
 * <ul>
 *   <li>실패한 Outbox 재시도 (maxRetries 미만)</li>
 *   <li>완료된 Outbox 정리 (24시간 경과 후)</li>
 *   <li>영구 실패 Outbox 로깅 (재시도 초과)</li>
 * </ul>
 *
 * <p><strong>실행 주기:</strong></p>
 * <ul>
 *   <li>재시도: 10분마다 (cron = "0 *\/10 * * * *")</li>
 *   <li>정리: 매 시간 (cron = "0 0 * * * *")</li>
 * </ul>
 *
 * <p><strong>트랜잭션 전략:</strong></p>
 * <ul>
 *   <li>❌ @Scheduled 메서드에 @Transactional 금지</li>
 *   <li>✅ StateManager가 트랜잭션 관리 담당</li>
 *   <li>✅ 각 Outbox 처리는 독립 트랜잭션 (실패 격리)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
@Component
public class {Aggregate}OutboxFinalizer {

    private static final Logger log = LoggerFactory.getLogger({Aggregate}OutboxFinalizer.class);
    private static final int RETENTION_HOURS = 24;

    private final {Aggregate}OutboxStateManager stateManager;

    public {Aggregate}OutboxFinalizer({Aggregate}OutboxStateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * 실패한 Outbox 재시도 (S3 Phase - Retry)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>OPERATION_STATE=FAILED 조회</li>
     *   <li>재시도 가능 여부 확인 (retryCount < maxRetries)</li>
     *   <li>재시도 가능: FAILED → PENDING 전환</li>
     *   <li>재시도 불가: 영구 실패 로깅</li>
     * </ol>
     *
     * <p>실행 주기: 10분마다</p>
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void retryFailedOutbox() {
        List<{Aggregate}Outbox> failed = stateManager.findFailedOutboxes();

        if (failed.isEmpty()) {
            return;
        }

        log.info("[RETRY] 실패 Outbox 재시도 시작: {} 건", failed.size());

        int retryCount = 0;
        int permanentFailureCount = 0;

        for ({Aggregate}Outbox outbox : failed) {
            if (outbox.canRetry()) {
                outbox.resetForRetry();
                stateManager.saveOutbox(outbox);
                retryCount++;

                log.info("Outbox 재시도 예약: ID={}, RetryCount={}/{}",
                    outbox.getId(), outbox.getRetryCount(), outbox.getMaxRetries());
            } else {
                permanentFailureCount++;

                log.error("[PERMANENT_FAILURE] Outbox 영구 실패: ID={}, RetryCount={}/{}",
                    outbox.getId(), outbox.getRetryCount(), outbox.getMaxRetries());
            }
        }

        log.info("[RETRY_COMPLETE] 재시도 완료: 재시도={}, 영구실패={}", retryCount, permanentFailureCount);
    }

    /**
     * 완료된 Outbox 정리 (S3 Phase - Cleanup)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>WAL_STATE=COMPLETED 조회</li>
     *   <li>완료 후 24시간 경과 여부 확인</li>
     *   <li>경과: DB에서 삭제</li>
     * </ol>
     *
     * <p>실행 주기: 매 시간</p>
     */
    @Scheduled(cron = "0 0 * * * *")
    public void finalizeCompletedOutbox() {
        List<{Aggregate}Outbox> completed = stateManager.findCompletedOutboxes();

        if (completed.isEmpty()) {
            return;
        }

        log.info("[CLEANUP] 완료 Outbox 정리 시작: {} 건", completed.size());

        int deletedCount = 0;

        for ({Aggregate}Outbox outbox : completed) {
            if (outbox.isOldEnough(RETENTION_HOURS)) {
                stateManager.deleteOutbox(outbox);
                deletedCount++;

                log.debug("[DELETE] Outbox 삭제: ID={}, Age={}시간 경과",
                    outbox.getId(),
                    java.time.Duration.between(outbox.getCompletedAt(), java.time.LocalDateTime.now()).toHours());
            }
        }

        if (deletedCount > 0) {
            log.info("[CLEANUP_COMPLETE] 정리 완료: {} 건 삭제", deletedCount);
        }
    }
}
```

#### 2.6. Event Listener Pattern

```java
/**
 * {Aggregate}EventListener - {Aggregate} Event Listener
 *
 * <p>트랜잭션 커밋 후 {Aggregate} Domain Event를 수신하여 비동기로 Outbox Processor를 즉시 호출합니다.</p>
 *
 * <p><strong>처리 흐름:</strong></p>
 * <ol>
 *   <li>{Aggregate} 저장 + Outbox 저장 (트랜잭션 내)</li>
 *   <li>트랜잭션 커밋</li>
 *   <li>✅ 이벤트 발행 (@TransactionalEventListener)</li>
 *   <li>✅ 비동기로 Outbox Processor 즉시 호출 (@Async)</li>
 *   <li>✅ @Scheduled는 Fallback으로 유지 (주기적 Polling)</li>
 * </ol>
 *
 * <p><strong>@Async vs @Scheduled 하이브리드 패턴:</strong></p>
 * <ul>
 *   <li><strong>@Async (이 Listener)</strong>: 즉시 처리 (최우선)</li>
 *   <li><strong>@Scheduled (Processor)</strong>: 주기적 Polling (Fallback)</li>
 *   <li>✅ 두 방식 모두 동작하여 이중 보장 (High Availability)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
@Component
public class {Aggregate}EventListener {

    private static final Logger log = LoggerFactory.getLogger({Aggregate}EventListener.class);

    private final {Aggregate}OutboxQueryPort outboxQueryPort;
    private final {Aggregate}OutboxProcessor outboxProcessor;

    public {Aggregate}EventListener(
        {Aggregate}OutboxQueryPort outboxQueryPort,
        {Aggregate}OutboxProcessor outboxProcessor
    ) {
        this.outboxQueryPort = outboxQueryPort;
        this.outboxProcessor = outboxProcessor;
    }

    /**
     * {Aggregate} Event 처리 (통합 핸들러)
     *
     * <p>트랜잭션 커밋 후 비동기로 Outbox Processor를 즉시 호출합니다.</p>
     *
     * @param event {Aggregate}Event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handle{Aggregate}Event({Aggregate}Event event) {
        log.info("📨 {} 수신: aggregateId={}, outboxIdemKey={}",
            event.getClass().getSimpleName(),
            event.aggregateId(),
            event.outboxIdemKey());

        processOutbox(event.outboxIdemKey());
    }

    /**
     * Outbox Processor 즉시 호출
     *
     * <p><strong>Race Condition 방지:</strong> @Scheduled 폴러와의 동시성 문제 방지</p>
     *
     * @param idemKey Outbox Idempotency Key
     */
    @Transactional
    private void processOutbox(String idemKey) {
        try {
            Optional<{Aggregate}Outbox> outboxOpt = outboxQueryPort.findByIdemKey(idemKey);

            if (outboxOpt.isEmpty()) {
                log.warn("⚠️ Outbox를 찾을 수 없습니다: idemKey={}", idemKey);
                return;
            }

            {Aggregate}Outbox outbox = outboxOpt.get();

            if (outbox.getWalState() != {Aggregate}Outbox.WriteAheadState.PENDING) {
                log.debug("⏭️ Outbox가 이미 처리되었습니다: idemKey={}", idemKey);
                return;
            }

            log.info("[OUTBOX_READY] Outbox 생성 완료: idemKey={}", idemKey);
            // TODO: processOne() 구현 후 즉시 처리 활성화
            // outboxProcessor.processOne(outbox);

        } catch (Exception e) {
            log.error("❌ Outbox Processor 즉시 호출 실패: idemKey={}", idemKey, e);
        }
    }
}
```

#### 2.7. Outcome Pattern

```java
/**
 * {Aggregate}Outcome - {Aggregate} Orchestration 결과
 *
 * <p>단순 Record 패턴으로 성공/실패를 모델링합니다.</p>
 *
 * <p><strong>사용 이유:</strong></p>
 * <ul>
 *   <li>❌ boolean 반환: 실패 원인 알 수 없음</li>
 *   <li>❌ Exception throw: 예외 처리 강제, 성능 저하</li>
 *   <li>✅ Outcome 반환: 성공/실패 모두 값으로 처리</li>
 * </ul>
 *
 * @param success 성공 여부
 * @param message 결과 메시지
 * @param errorCode 에러 코드 (실패 시)
 * @param cause 원인 (실패 시)
 * @author Claude Code
 * @since 1.0
 */
public record {Aggregate}Outcome(
    boolean success,
    String message,
    String errorCode,
    String cause
) {
    /**
     * 성공 결과 생성
     */
    public static {Aggregate}Outcome success(String message) {
        return new {Aggregate}Outcome(true, message, null, null);
    }

    /**
     * 실패 결과 생성
     */
    public static {Aggregate}Outcome failure(String errorCode, String message, String cause) {
        return new {Aggregate}Outcome(false, message, errorCode, cause);
    }

    /**
     * 성공 여부 확인
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 실패 여부 확인
     */
    public boolean isFailure() {
        return !success;
    }
}
```

#### 2.8. Validator Pattern

```java
/**
 * {Aggregate}Validator - {Aggregate} 도메인 규칙 검증자
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>도메인 규칙 검증 (비즈니스 규칙)</li>
 *   <li>빠른 실패 (Fail Fast)</li>
 *   <li>명확한 예외 메시지</li>
 * </ul>
 *
 * <p><strong>컨벤션 준수:</strong></p>
 * <ul>
 *   <li>✅ @Component (Spring Bean 등록)</li>
 *   <li>✅ Pure Java Constructor (Lombok 금지)</li>
 *   <li>✅ Single Responsibility (검증만)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 1.0
 */
@Component
public class {Aggregate}Validator {

    /**
     * {Aggregate} 생성 Command 검증
     *
     * @param command 생성 Command
     * @throws IllegalArgumentException 검증 실패 시
     */
    public void validate(Create{Aggregate}Command command) {
        if (command.name() == null || command.name().isBlank()) {
            throw new IllegalArgumentException("name은 필수입니다.");
        }

        if (command.name().length() > 100) {
            throw new IllegalArgumentException("name은 100자 이하여야 합니다.");
        }

        // 추가 도메인 규칙 검증
    }

    /**
     * {Aggregate} 수정 Command 검증
     *
     * @param command 수정 Command
     * @throws IllegalArgumentException 검증 실패 시
     */
    public void validate(Update{Aggregate}Command command) {
        if (command.name() == null || command.name().isBlank()) {
            throw new IllegalArgumentException("name은 필수입니다.");
        }

        // 추가 도메인 규칙 검증
    }
}
```

---

## 검증 체크리스트

### 기본 CQRS 패턴
- [ ] Command/Query 완전 분리 (UseCase vs QueryService)
- [ ] `@Transactional` 내 외부 API 호출 없음
- [ ] Assembler 패턴 사용 (Command/Query ↔ Domain ↔ Response)
- [ ] OutPort 분리 (Save{Aggregate}Port, Load{Aggregate}Port)
- [ ] DTO는 Record 패턴
- [ ] Pure Java Constructor (Lombok 금지)
- [ ] HTML Javadoc (`<p>`, `<ul>`, `<ol>`, `<strong>`)
- [ ] `@Service` 어노테이션 사용
- [ ] Query는 `@Transactional(readOnly = true)`
- [ ] Static Factory Method (`of()`)

### Orchestration Pattern (3-Phase Lifecycle)
- [ ] Facade 패턴 (S1 Phase - Accept)
- [ ] State Manager 패턴 (`@Component` + `@Transactional`)
- [ ] Outbox State Manager 패턴 (Outbox 상태 관리 전용)
- [ ] Orchestrator 패턴 (`@Scheduled` + Polling)
- [ ] Finalizer 패턴 (`@Scheduled` + 재시도/정리)
- [ ] Event Listener 패턴 (`@TransactionalEventListener` + `@Async`)
- [ ] Outcome 패턴 (Record 기반 성공/실패)
- [ ] Idempotency 처리 (`generateIdemKey()` + `existsByIdemKey()`)
- [ ] Validator 패턴 (`@Component` + 도메인 규칙 검증)
- [ ] Port 명명 규칙 (Save{Aggregate}Port, Load{Aggregate}Port)
- [ ] `@Scheduled`에 `@Transactional` 금지
- [ ] 3-Phase Lifecycle 준수 (S1 → S2 → S3)

---

## 안티패턴 (피해야 할 것)

### ❌ Transaction 내 외부 API 호출

```java
// ❌ Bad - 트랜잭션 내 외부 API 호출
@Transactional
public void execute(Command command) {
    repository.save(domain);
    externalApi.call(); // Transaction 내부!
}

// ✅ Good - Outbox Pattern 사용
@Transactional
public void execute(Command command) {
    repository.save(domain);
    outboxRepository.save(outbox); // Outbox 저장만
}

// Processor가 별도로 처리
@Scheduled(fixedDelay = 1000)
public void processOutbox() {
    List<Outbox> pending = outboxRepository.findByStatePending();
    for (Outbox outbox : pending) {
        externalApi.call(outbox.getPayload()); // Transaction 밖!
    }
}
```

### ❌ Command와 Query 혼합

```java
// ❌ Bad - Command와 Query 혼합
public interface {Aggregate}Port {
    void create{Aggregate}(Command command);
    {Aggregate}Response get{Aggregate}(Long id);
}

// ✅ Good - UseCase와 QueryService 분리
public interface Create{Aggregate}UseCase {
    void create{Aggregate}(Command command);
}

public interface Get{Aggregate}QueryService {
    {Aggregate}Response get{Aggregate}(Query query);
}
```

### ❌ Lombok 사용

```java
// ❌ Bad - Lombok Constructor
@Service
@RequiredArgsConstructor
public class Create{Aggregate}Service {
    private final {Aggregate}Assembler assembler;
}

// ✅ Good - Pure Java Constructor
@Service
public class Create{Aggregate}Service {
    private final {Aggregate}Assembler assembler;

    public Create{Aggregate}Service({Aggregate}Assembler assembler) {
        this.assembler = assembler;
    }
}
```

### ❌ Assembler 없이 직접 변환

```java
// ❌ Bad - Service에서 직접 변환
@Service
public class Create{Aggregate}Service {
    public {Aggregate}Response execute(Command command) {
        {Aggregate}Domain domain = new {Aggregate}Domain(command.name()); // 직접 변환
        return new {Aggregate}Response(saved.getId(), saved.getName()); // 직접 변환
    }
}

// ✅ Good - Assembler에 위임
@Service
public class Create{Aggregate}Service {
    private final {Aggregate}Assembler assembler;

    public {Aggregate}Response execute(Command command) {
        {Aggregate}Domain domain = assembler.toDomain(command); // Assembler
        return assembler.toResponse(saved); // Assembler
    }
}
```

### ❌ OutPort 미분리

```java
// ❌ Bad - CommandOutPort와 QueryOutPort 혼합
public interface {Aggregate}OutPort {
    {Aggregate}Domain save({Aggregate}Domain domain); // Command
    Optional<{Aggregate}Domain> findById(Long id); // Query
}

// ✅ Good - Save{Aggregate}Port와 Load{Aggregate}Port 분리
public interface Save{Aggregate}Port {
    {Aggregate}Domain save({Aggregate}Domain domain);
}

public interface Load{Aggregate}Port {
    Optional<{Aggregate}Domain> findById(Long id);
}
```

### ❌ @Scheduled에 @Transactional 사용

```java
// ❌ Bad - @Scheduled 메서드에 @Transactional
@Component
public class {Aggregate}OutboxProcessor {
    @Scheduled(fixedDelay = 1000)
    @Transactional // ❌ AOP Proxy 적용 안 됨!
    public void processOutbox() {
        // 트랜잭션 작동 안 함
    }
}

// ✅ Good - State Manager가 트랜잭션 관리
@Component
public class {Aggregate}OutboxProcessor {
    private final {Aggregate}OutboxStateManager stateManager;

    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        List<Outbox> pending = stateManager.findPendingOutboxes(); // @Transactional
        for (Outbox outbox : pending) {
            stateManager.processOne(outbox); // @Transactional
        }
    }
}

@Component
public class {Aggregate}OutboxStateManager {
    @Transactional
    public {Aggregate}Outcome processOne(Outbox outbox) {
        // 트랜잭션 정상 작동!
    }
}
```

### ❌ boolean 반환 (Outcome 대신)

```java
// ❌ Bad - boolean 반환 (실패 원인 알 수 없음)
@Transactional
public boolean processOutbox(Outbox outbox) {
    try {
        externalApi.call(outbox.getPayload());
        return true;
    } catch (Exception e) {
        return false; // 왜 실패했는지 알 수 없음
    }
}

// ✅ Good - Outcome 반환 (성공/실패 명확)
@Transactional
public {Aggregate}Outcome processOutbox(Outbox outbox) {
    try {
        externalApi.call(outbox.getPayload());
        return {Aggregate}Outcome.success("처리 완료");
    } catch (Exception e) {
        return {Aggregate}Outcome.failure("EXTERNAL_API_ERROR", e.getMessage(), e.getCause().toString());
    }
}
```

### ❌ Idempotency 미처리

```java
// ❌ Bad - 중복 요청 방지 없음
@Transactional
public {Aggregate}Response create{Aggregate}(Command command) {
    {Aggregate}Domain domain = {Aggregate}Domain.forNew(command.name());
    {Aggregate}Domain saved = repository.save(domain); // 중복 생성 가능!
    return toResponse(saved);
}

// ✅ Good - Idempotency Key로 중복 방지
@Transactional
public {Aggregate}Response create{Aggregate}(Command command) {
    String idemKey = generateIdemKey(command.name(), "CREATE");
    if (outboxStateManager.existsByIdemKey(idemKey)) {
        {Aggregate}Domain existing = stateManager.get{Aggregate}(...);
        return toResponse(existing); // 기존 결과 반환
    }

    {Aggregate}Domain domain = {Aggregate}Domain.forNew(command.name());
    {Aggregate}Domain saved = stateManager.create{Aggregate}(domain, idemKey);
    return toResponse(saved);
}
```

### ❌ Port 명명 규칙 위반

```java
// ❌ Bad - 일반적인 명명
{Aggregate}CommandOutPort commandOutPort;
{Aggregate}QueryOutPort queryOutPort;

// ✅ Good - 동사 기반 명명
Save{Aggregate}Port save{Aggregate}Port;
Load{Aggregate}Port load{Aggregate}Port;
```

---

## 참고 문서

### 기본 CQRS 패턴
- [Application Layer 규칙](../../docs/coding_convention/03-application-layer/)
- [Transaction 관리](../../docs/coding_convention/03-application-layer/legacy/transaction-management/)
- [UseCase 설계](../../docs/coding_convention/03-application-layer/legacy/usecase-design/)
- [Assembler 패턴](../../docs/coding_convention/03-application-layer/legacy/assembler-pattern/)

### Orchestration Pattern (3-Phase Lifecycle)
- [Orchestration Pattern 개요](../../docs/coding_convention/09-orchestration-patterns/overview/)
- [Command 패턴](../../docs/coding_convention/09-orchestration-patterns/command-pattern/)
- [Idempotency 처리](../../docs/coding_convention/09-orchestration-patterns/idempotency-handling/)
- [Write-Ahead Log](../../docs/coding_convention/09-orchestration-patterns/write-ahead-log/)
- [Outcome 모델링](../../docs/coding_convention/09-orchestration-patterns/outcome-modeling/)

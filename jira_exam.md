“헥사고날 + CQRS 템플릿” 기반 샘플 프로젝트를
지라 에픽/스토리/태스크로 쪼개서, 클로드 코드 커맨드 자동화 + 세션 훅 + 아키유닛 + 체크스타일 + CI까지 한 방에 굴러가게 설계.
아래는 바로 지라에 넣어도 되는 수준의 에픽 구조 + 각 에픽별 산출물/수용기준/태스크/자동화 커맨드 예시야.

상위 에픽(EPIC)

EPIC-1. 프로젝트 부트스트랩 & 공통 개발 표준

목표: 템플릿 레포 생성, 모듈 구조/패키징/빌드/코딩 표준 확정, 기본 훅/스캐폴딩 자동화.
•	산출물
•	멀티모듈 스켈레톤(adapter-in-rest, adapter-out-*, application-*, domain-*)
•	코딩 컨벤션 문서(우리 템플릿 .md)
•	공통 Gradle 설정(플러그인 버전, Jacoco, SpotBugs/PMD 선택, Checkstyle)
•	Git Hook(커밋 메시지, pre-commit lint, format)
•	수용기준(AC)
•	./gradlew build 성공
•	Checkstyle/Spotless/PMD 설정 적용 및 기본 규칙 통과
•	패키징 구조가 템플릿 문서와 1:1 매칭
•	주요 태스크
•	T1-1 템플릿 레포/모듈 생성
•	T1-2 공통 Gradle 설정 & BOM 관리
•	T1-3 Checkstyle/Spotless 설정 & 기본 룰셋 확정
•	T1-4 Git hooks(pre-commit, commit-msg) 적용
•	T1-5 템플릿 문서 레포 내 /docs/architecture/DDD_Hexagonal_CQRS_Template.md 포함
•	자동화 커맨드(예)
•	code init --template hex-cqrs --modules adapter-in-rest,adapter-out-jpa,application-sample,domain-sample --checkstyle --spotless --pmd --jacoco

⸻

EPIC-2. 예외/에러 응답 표준화 & Global Handler

목표: BaseException + ErrorCategory + ErrorCode + ErrorResponse + @RestControllerAdvice.
•	산출물
•	BaseException, ErrorCategory, ErrorCode, ErrorResponse (adapter-in-rest 레이어)
•	GlobalExceptionHandler
•	수용기준
•	카테고리→HTTP 매핑: 400/404/409/422/500
•	샘플 컨트롤러에서 의도적 예외 던질 때 표준 응답 JSON 나옴
•	태스크
•	T2-1 예외 계층 구현
•	T2-2 Global Handler 구현
•	T2-3 표준 에러 JSON 스냅샷 테스트
•	커맨드
•	code gen exception --base --categories BAD_REQUEST,NOT_FOUND,CONFLICT,UNPROCESSABLE,INTERNAL --error-response
•	code gen rest-handler --global-exception-handler

⸻

EPIC-3. VO/Enum/식별자 표준 라이브러리

목표: 공통 VO/Enum/Identifier 스캐폴드.
•	산출물
•	domain.shared.vo/* (예: PolicyKey, IdempotencyKey, FileSize, ContentType)
•	domain.shared.id/* (예: AggregateId/각 컨텍스트 ID)
•	domain.shared.event.DomainEvent 인터페이스
•	수용기준
•	모든 VO는 record + 정적 팩토리 + 생성 검증 포함
•	단위 테스트 100% 라인 커버리지(VO)
•	태스크
•	T3-1 VO 세트 생성
•	T3-2 Enum/타입 세트 생성
•	T3-3 단위 테스트 작성
•	커맨드
•	code gen vo PolicyKey --fields tenantId:String,userType:String,serviceType:String --parse "tenant:user:service"
•	code gen vo IdempotencyKey --generate
•	code gen enum UploadStatus PENDING,IN_PROGRESS,COMPLETED,FAILED

⸻

EPIC-4. Port & Adapter(Out) CQRS 분리 기반

목표: CommandPort, QueryPort 인터페이스와 JPA/Redis/HTTP 어댑터 뼈대.
•	산출물
•	application.[context].port.out.* 정의
•	adapter-out-persistence의 JPA 어댑터 스켈레톤
•	(옵션) adapter-out-external의 HTTP 클라이언트 스켈레톤
•	수용기준
•	포트 인터페이스명, 메서드 시그니처 CQRS 컨벤션 일치
•	어댑터 구현은 프레임워크 의존을 감싼다(도메인 import 금지)
•	태스크
•	T4-1 CommandPort/QueryPort 정의
•	T4-2 JPA 리포지토리 및 어댑터 구현 뼈대
•	T4-3 통합 테스트(메모리 DB)
•	커맨드
•	code gen port-out --context sample --aggregate Session --split command,query
•	code gen adapter-out jpa --context sample --aggregate Session

⸻

EPIC-5. UseCase & Application 서비스(CQRS)

목표: CreateXxxUseCase 등 Inbound Port 및 Command/Query 서비스 구현.
•	산출물
•	application.[context].port.in.* UseCase 인터페이스
•	application.[context].service.command/..query 구현
•	트랜잭션 어노테이션 규칙 적용
•	수용기준
•	서비스 간 직접 호출 없음 (읽기 필요시 QueryPort 사용)
•	외부 I/O는 트랜잭션 밖에서 오케스트레이션
•	태스크
•	T5-1 UseCase 정의 (Create/Get/Complete/Cancel 등 샘플)
•	T5-2 CommandService/QueryService 구현
•	T5-3 단위 테스트 (Outbound Port mock)
•	커맨드
•	code gen usecase --context sample --aggregate Session --types Create,Get,Complete,Cancel
•	code gen service --cqrs --context sample --aggregate Session

⸻

EPIC-6. Assembler(명령→도메인) & Mapper(REST↔명령)

목표: DTO→Command 매핑(REST), Command→Domain 조립(Application).
•	산출물
•	adapter.in.rest.mapper.XxxDtoMapper
•	application.[context].assembler.CreateXxxAssembler
•	수용기준
•	REST DTO에만 @Valid, 어댑터 변환 에러는 400 처리
•	도메인 생성은 팩토리/정적 메서드로 불변성 보장
•	태스크
•	T6-1 Request/Response DTO 정의
•	T6-2 DtoMapper 구현
•	T6-3 Assembler 구현
•	커맨드
•	code gen dto --context sample --aggregate Session --request CreateSessionRequest --response CreateSessionResponse
•	code gen mapper rest --dto CreateSessionRequest --to-command CreateSessionCommand
•	code gen assembler --context sample --aggregate Session --from CreateSessionCommand

⸻

EPIC-7. REST 어댑터(Controller) & 표준 응답/오류

목표: 샘플 리소스 엔드포인트(생성/조회/완료/취소) + 표준 에러 응답.
•	산출물
•	adapter.in.rest.controller.SessionController
•	Swagger/OpenAPI 요약(선택)
•	수용기준
•	201 + Location 헤더, 400/404/409/422/500 응답 확인
•	E2E 스모크 테스트
•	태스크
•	T7-1 컨트롤러 구현
•	T7-2 E2E 스모크 테스트
•	커맨드
•	code gen controller --context sample --aggregate Session --endpoints create,get,complete,cancel --openapi

⸻

EPIC-8. ArchUnit/QA 게이트/정적 분석

목표: 아키텍처 룰과 코드 품질 자동화 게이트.
•	산출물
•	archunit 테스트(레이어 의존성: adapter→application→domain)
•	Checkstyle/Spotless/PMD/Jacoco 리포트
•	수용기준
•	빌드 시 ArchUnit/Checkstyle/Jacoco 통과 시에만 성공
•	태스크
•	T8-1 ArchUnit 룰 작성
•	T8-2 CI에서 품질 게이트 적용
•	커맨드
•	code gen archunit --layers adapter,application,domain --rules "application no depend on adapter"
•	code qa gate --enable checkstyle,pmd,spotbugs,jacoco

⸻

EPIC-9. CI/CD 파이프라인 & 세션 훅

목표: PR 시 자동 포맷/린트/테스트/ArchUnit/커버리지/빌드, 세션 훅으로 컨벤션 주입.
•	산출물
•	GitHub Actions(or Jenkins) 워크플로우(YAML)
•	“클로드 코드 세션 훅” 스크립트(컨벤션 자동 주입, 생성 후 검증)
•	수용기준
•	PR 생성 → 포맷/린트/테스트/아키유닛/커버리지/빌드 → 배지/코멘트
•	CLI 커맨드 실행 후 자동 검증 및 실패 시 수정 제안
•	태스크
•	T9-1 CI Workflow 작성
•	T9-2 세션 훅 스크립트 작성(컨벤션 주입 + 생성 후 ArchUnit/Checkstyle 실행)
•	커맨드
•	code ci github --with archunit,checkstyle,jacoco --java 17
•	code hook session --inject-convention ./docs/architecture/DDD_Hexagonal_CQRS_Template.md --post-gen "gradlew check"

⸻

EPIC-10. 샘플 도메인(세션) 완결 & 문서/가이드

목표: 세션(예시) 도메인 풀사이클 구현 및 문서화.
•	산출물
•	domain.sample.session Aggregate + VO/이벤트/예외
•	UseCase/Controller/Adapter 테스트 통과
•	운영 문서: API 명세, 트러블슈팅, 규약 요약
•	수용기준
•	엔드투엔드 시나리오(생성→조회→완료/취소) 통과
•	문서 최신화
•	태스크
•	T10-1 도메인 구현
•	T10-2 E2E 통합 테스트
•	T10-3 문서/예제 업데이트
•	커맨드
•	code gen domain aggregate Session --vo UploadRequest,SessionId --events SessionCompleted,SessionCancelled --exceptions SessionNotFound
•	code e2e run --scenario session-basic-flow

⸻

이슈 템플릿(스토리/태스크) 공통 Definition of Done
•	생성 코드는 컨벤션 템플릿 준수(패키징/네이밍/CQRS/헥사고날).
•	모든 public API에 Javadoc/코틀린이면 KDoc.
•	./gradlew clean check 통과(ArchUnit/Checkstyle/PMD/Jacoco 포함).
•	단위 테스트(도메인: 순수/무목, 애플리케이션: 포트 목) 작성.
•	REST API는 에러 응답 표준(ErrorResponse)을 따른다.
•	PR 템플릿 체크리스트 모두 체크.
•	문서(/docs) 갱신.


예시일뿐 더 좋은 예시가 있다면 변경해도 무관 

# 1) 프로젝트 스캐폴드
code init --template hex-cqrs \
--modules adapter-in-rest,adapter-out-jpa,application-sample,domain-sample \
--checkstyle --spotless --pmd --jacoco \
--git-hooks

# 2) 예외/핸들러
code gen exception --base --categories BAD_REQUEST,NOT_FOUND,CONFLICT,UNPROCESSABLE,INTERNAL --error-response
code gen rest-handler --global-exception-handler

# 3) 도메인 공용 VO/Enum
code gen vo PolicyKey --fields tenantId:String,userType:String,serviceType:String --parse "tenant:user:service"
code gen vo IdempotencyKey --generate
code gen enum UploadStatus PENDING,IN_PROGRESS,COMPLETED,FAILED

# 4) 포트/어댑터(out)
code gen port-out --context sample --aggregate Session --split command,query
code gen adapter-out jpa --context sample --aggregate Session

# 5) 유즈케이스 & 서비스
code gen usecase --context sample --aggregate Session --types Create,Get,Complete,Cancel
code gen service --cqrs --context sample --aggregate Session

# 6) DTO/매퍼/어셈블러
code gen dto --context sample --aggregate Session --request CreateSessionRequest --response CreateSessionResponse
code gen mapper rest --dto CreateSessionRequest --to-command CreateSessionCommand
code gen assembler --context sample --aggregate Session --from CreateSessionCommand

# 7) 컨트롤러
code gen controller --context sample --aggregate Session --endpoints create,get,complete,cancel --openapi

# 8) ArchUnit/QA 게이트
code gen archunit --layers adapter,application,domain --rules "application no depend on adapter"
code qa gate --enable checkstyle,pmd,spotbugs,jacoco

# 9) CI & 세션 훅
code ci github --with archunit,checkstyle,jacoco --java 17
code hook session --inject-convention ./docs/architecture/DDD_Hexagonal_CQRS_Template.md --post-gen "gradlew check"

# 10) 도메인 샘플 완결
code gen domain aggregate Session --vo UploadRequest,SessionId --events SessionCompleted,SessionCancelled --exceptions SessionNotFound
code e2e run --scenario session-basic-flow
₩₩₩
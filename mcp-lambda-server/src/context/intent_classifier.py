"""
Intent Classifier

사용자 요청에서 작업 의도를 분류하는 모듈
헥사고날 아키텍처 기반 Spring Boot 프로젝트의 코드 생성 의도를 파악
"""

import re
from typing import Optional

from ..models import ClassType, IntentResult, IntentType, Layer


# 신뢰도 계산 상수
KEYWORD_SCORE_DIVISOR = 3.0  # 키워드 점수 계산 분모
PATTERN_SCORE_DIVISOR = 2.0  # 패턴 점수 계산 분모
MAX_KEYWORD_SCORE = 0.5  # 키워드 점수 최대값
MAX_PATTERN_SCORE = 0.5  # 패턴 점수 최대값
MAX_CONFIDENCE = 1.0  # 신뢰도 최대값
KEYWORD_DISPLAY_LIMIT = 3  # 추론 설명에서 표시할 키워드 최대 개수


# 의도 분류를 위한 키워드 패턴 정의
INTENT_PATTERNS: dict[IntentType, dict] = {
    # Domain Layer - Aggregate
    IntentType.CREATE_AGGREGATE: {
        "keywords": [
            "aggregate",
            "aggregateroot",
            "도메인",
            "domain",
            "루트 엔티티",
            "root entity",
            "애그리거트",
            "애그리게이트",
            "비즈니스 엔티티",
            "business entity",
        ],
        "patterns": [
            r"(생성|만들|create|add|새로운?)\s*(aggregate|애그리거트|도메인)",
            r"(aggregate|애그리거트|도메인)\s*(클래스|class|객체)?\s*(생성|만들|create)",
            r"domain\s*(model|object|entity)",
        ],
        "layer": Layer.DOMAIN,
        "class_type": ClassType.AGGREGATE,
    },
    # Domain Layer - Value Object
    IntentType.CREATE_VALUE_OBJECT: {
        "keywords": [
            "value object",
            "vo",
            "값 객체",
            "밸류 오브젝트",
            "불변 객체",
            "immutable",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*(value\s*object|vo|값\s*객체)",
            r"(value\s*object|vo|값\s*객체)\s*(생성|만들|create)",
        ],
        "layer": Layer.DOMAIN,
        "class_type": ClassType.VALUE_OBJECT,
    },
    # Domain Layer - Domain Event
    IntentType.CREATE_DOMAIN_EVENT: {
        "keywords": [
            "domain event",
            "이벤트",
            "event",
            "도메인 이벤트",
            "published",
            "발행",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*(domain\s*)?event",
            r"(도메인\s*)?이벤트\s*(생성|만들|create)",
            r"event\s*(class|클래스)\s*(생성|만들|create)",
        ],
        "layer": Layer.DOMAIN,
        "class_type": ClassType.DOMAIN_EVENT,
    },
    # Domain Layer - Domain Exception
    IntentType.CREATE_DOMAIN_EXCEPTION: {
        "keywords": [
            "exception",
            "예외",
            "도메인 예외",
            "domain exception",
            "에러",
            "error",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*(domain\s*)?(exception|예외)",
            r"(도메인\s*)?(예외|exception)\s*(클래스|class)?\s*(생성|만들|create)",
        ],
        "layer": Layer.DOMAIN,
        "class_type": ClassType.DOMAIN_EXCEPTION,
    },
    # Application Layer - UseCase
    IntentType.CREATE_USE_CASE: {
        "keywords": [
            "usecase",
            "use case",
            "유스케이스",
            "유즈케이스",
            "application service",
            "애플리케이션 서비스",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*(use\s*case|유스케이스)",
            r"(use\s*case|유스케이스)\s*(생성|만들|create)",
            r"application\s*(service|layer)",
        ],
        "layer": Layer.APPLICATION,
        "class_type": ClassType.USE_CASE,
    },
    # Application Layer - Command Service
    IntentType.CREATE_COMMAND_SERVICE: {
        "keywords": [
            "command",
            "커맨드",
            "명령",
            "command service",
            "command handler",
            "cqrs command",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*command\s*(service|handler)?",
            r"command\s*(service|handler)\s*(생성|만들|create)",
            r"cqrs\s*command",
        ],
        "layer": Layer.APPLICATION,
        "class_type": ClassType.COMMAND_SERVICE,
    },
    # Application Layer - Query Service
    IntentType.CREATE_QUERY_SERVICE: {
        "keywords": [
            "query",
            "쿼리",
            "조회",
            "query service",
            "query handler",
            "cqrs query",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*query\s*(service|handler)?",
            r"query\s*(service|handler)\s*(생성|만들|create)",
            r"cqrs\s*query",
            r"(조회|검색)\s*(서비스|service)",
        ],
        "layer": Layer.APPLICATION,
        "class_type": ClassType.QUERY_SERVICE,
    },
    # Application Layer - Port
    IntentType.CREATE_PORT: {
        "keywords": [
            "port",
            "포트",
            "인터페이스",
            "interface",
            "inbound port",
            "outbound port",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*(inbound|outbound)?\s*port",
            r"(포트|port)\s*(인터페이스|interface)?\s*(생성|만들|create)",
            r"hexagonal\s*(port|interface)",
        ],
        "layer": Layer.APPLICATION,
        "class_type": ClassType.PORT_IN,
    },
    # Persistence Layer - Entity
    IntentType.CREATE_ENTITY: {
        "keywords": [
            "entity",
            "엔티티",
            "jpa entity",
            "persistence entity",
            "영속성 엔티티",
            "테이블 매핑",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*(jpa\s*)?entity",
            r"(엔티티|entity)\s*(클래스|class)?\s*(생성|만들|create)",
            r"@entity\s*(class|클래스)",
            r"jpa\s*(entity|매핑)",
        ],
        "layer": Layer.ADAPTER_OUT,
        "class_type": ClassType.ENTITY,
    },
    # Persistence Layer - Repository
    IntentType.CREATE_REPOSITORY: {
        "keywords": [
            "repository",
            "레포지토리",
            "리포지토리",
            "jpa repository",
            "custom repository",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*(jpa\s*)?(repository|레포지토리)",
            r"(repository|레포지토리)\s*(생성|만들|create)",
            r"querydsl\s*repository",
        ],
        "layer": Layer.ADAPTER_OUT,
        "class_type": ClassType.JPA_REPOSITORY,
    },
    # Persistence Layer - Adapter
    IntentType.CREATE_PERSISTENCE_ADAPTER: {
        "keywords": [
            "persistence adapter",
            "영속성 어댑터",
            "adapter",
            "jpa adapter",
            "repository adapter",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*(persistence\s*)?adapter",
            r"(영속성|persistence)\s*adapter\s*(생성|만들|create)",
            r"hexagonal\s*adapter",
        ],
        "layer": Layer.ADAPTER_OUT,
        "class_type": ClassType.ADAPTER,
    },
    # REST API Layer - Controller
    IntentType.CREATE_CONTROLLER: {
        "keywords": [
            "controller",
            "컨트롤러",
            "rest controller",
            "api endpoint",
            "rest api",
            "엔드포인트",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*(rest\s*)?(controller|컨트롤러)",
            r"(controller|컨트롤러)\s*(생성|만들|create)",
            r"api\s*(endpoint|엔드포인트)",
            r"@restcontroller",
        ],
        "layer": Layer.ADAPTER_IN,
        "class_type": ClassType.CONTROLLER,
    },
    # REST API Layer - Request DTO
    IntentType.CREATE_REQUEST_DTO: {
        "keywords": [
            "request dto",
            "request",
            "요청 dto",
            "요청",
            "request body",
            "api request",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*request\s*(dto|바디|body)?",
            r"(request|요청)\s*(dto|객체)\s*(생성|만들|create)",
            r"api\s*request",
        ],
        "layer": Layer.ADAPTER_IN,
        "class_type": ClassType.REQUEST_DTO,
    },
    # REST API Layer - Response DTO
    IntentType.CREATE_RESPONSE_DTO: {
        "keywords": [
            "response dto",
            "response",
            "응답 dto",
            "응답",
            "response body",
            "api response",
        ],
        "patterns": [
            r"(생성|만들|create|add)\s*response\s*(dto|바디|body)?",
            r"(response|응답)\s*(dto|객체)\s*(생성|만들|create)",
            r"api\s*response",
        ],
        "layer": Layer.ADAPTER_IN,
        "class_type": ClassType.RESPONSE_DTO,
    },
    # Modification Intents
    IntentType.ADD_METHOD: {
        "keywords": [
            "메서드 추가",
            "add method",
            "함수 추가",
            "method",
            "기능 추가",
        ],
        "patterns": [
            r"(추가|add|구현|implement)\s*(메서드|method|함수|function)",
            r"(메서드|method|함수)\s*(추가|add|구현)",
            r"새로운?\s*(메서드|method|기능)",
        ],
        "layer": None,
        "class_type": None,
    },
    IntentType.MODIFY_LOGIC: {
        "keywords": [
            "수정",
            "modify",
            "변경",
            "change",
            "update",
            "로직 변경",
            "logic change",
        ],
        "patterns": [
            r"(수정|modify|변경|change|update)\s*(로직|logic|코드|code)?",
            r"(로직|logic)\s*(수정|modify|변경)",
            r"기존\s*(코드|로직)\s*(수정|변경)",
        ],
        "layer": None,
        "class_type": None,
    },
    IntentType.REFACTOR_CODE: {
        "keywords": [
            "리팩토링",
            "refactor",
            "개선",
            "improve",
            "코드 정리",
            "cleanup",
        ],
        "patterns": [
            r"(리팩토링|refactor|리펙토링)",
            r"(코드|code)\s*(개선|improve|정리|cleanup)",
            r"구조\s*(개선|변경)",
        ],
        "layer": None,
        "class_type": None,
    },
    # Analysis Intents
    IntentType.ANALYZE_CODE: {
        "keywords": [
            "분석",
            "analyze",
            "analysis",
            "검토",
            "review",
            "위반 검사",
            "violation check",
        ],
        "patterns": [
            r"(분석|analyze|검토|review)\s*(코드|code)?",
            r"(코드|code)\s*(분석|analyze|검토)",
            r"(위반|violation)\s*(검사|check|확인)",
        ],
        "layer": None,
        "class_type": None,
    },
    IntentType.EXPLAIN_CODE: {
        "keywords": [
            "설명",
            "explain",
            "이해",
            "understand",
            "코드 설명",
            "what does",
        ],
        "patterns": [
            r"(설명|explain|이해|understand)",
            r"what\s*(does|is)",
            r"어떻게\s*(동작|작동|work)",
            r"(이|이것|this)\s*(뭐|무엇|what)",
        ],
        "layer": None,
        "class_type": None,
    },
}


def _calculate_confidence(
    keywords_matched: list[str],
    patterns_matched: int,
    total_keywords: int,
) -> float:
    """매칭 결과를 기반으로 신뢰도 계산"""
    if total_keywords == 0:
        return 0.0

    keyword_score = min(
        len(keywords_matched) / KEYWORD_SCORE_DIVISOR, MAX_KEYWORD_SCORE
    )
    pattern_score = min(patterns_matched / PATTERN_SCORE_DIVISOR, MAX_PATTERN_SCORE)

    confidence = keyword_score + pattern_score
    return round(min(confidence, MAX_CONFIDENCE), 2)


def _generate_reasoning(
    intent_type: IntentType,
    keywords_matched: list[str],
    patterns_matched: int,
    layer: Optional[Layer],
    class_type: Optional[ClassType],
) -> str:
    """분류 근거 설명 생성"""
    parts = []

    if keywords_matched:
        parts.append(
            f"키워드 매칭: {', '.join(keywords_matched[:KEYWORD_DISPLAY_LIMIT])}"
        )

    if patterns_matched > 0:
        parts.append(f"패턴 매칭: {patterns_matched}개")

    if layer:
        parts.append(f"대상 레이어: {layer.value}")

    if class_type:
        parts.append(f"클래스 타입: {class_type.value}")

    if not parts:
        parts.append("명확한 의도를 파악하지 못함")

    return " | ".join(parts)


def classify_intent(prompt: str) -> IntentResult:
    """
    사용자 프롬프트에서 작업 의도를 분류

    Args:
        prompt: 사용자 요청 텍스트

    Returns:
        IntentResult: 분류된 의도 정보

    Example:
        >>> result = classify_intent("Order Aggregate 생성해줘")
        >>> result.intent_type
        'CREATE_AGGREGATE'
        >>> result.target_layer
        'DOMAIN'
    """
    prompt_lower = prompt.lower()
    best_match: Optional[tuple[IntentType, dict, list[str], int]] = None
    best_score = 0.0

    for intent_type, config in INTENT_PATTERNS.items():
        keywords_matched = []
        patterns_matched = 0

        # 키워드 매칭
        for keyword in config["keywords"]:
            if keyword.lower() in prompt_lower:
                keywords_matched.append(keyword)

        # 패턴 매칭
        for pattern in config["patterns"]:
            if re.search(pattern, prompt_lower, re.IGNORECASE):
                patterns_matched += 1

        # 점수 계산
        score = _calculate_confidence(
            keywords_matched,
            patterns_matched,
            len(config["keywords"]),
        )

        if score > best_score:
            best_score = score
            best_match = (intent_type, config, keywords_matched, patterns_matched)

    # 결과 생성
    if best_match and best_score >= 0.2:
        intent_type, config, keywords_matched, patterns_matched = best_match
        layer = config.get("layer")
        class_type = config.get("class_type")

        return IntentResult(
            intent_type=intent_type.value,
            target_layer=layer.value if layer else None,
            class_type=class_type.value if class_type else None,
            confidence=best_score,
            keywords_matched=keywords_matched,
            reasoning=_generate_reasoning(
                intent_type, keywords_matched, patterns_matched, layer, class_type
            ),
        )

    # 매칭 실패 시 UNKNOWN 반환
    return IntentResult(
        intent_type=IntentType.UNKNOWN.value,
        target_layer=None,
        class_type=None,
        confidence=0.0,
        keywords_matched=[],
        reasoning="의도를 분류할 수 있는 명확한 키워드나 패턴을 찾지 못함",
    )

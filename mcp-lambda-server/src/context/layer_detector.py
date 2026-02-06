"""
Layer & Class Type Detector

파일 경로 및 코드 내용에서 레이어와 클래스 타입을 자동 감지하는 모듈
헥사고날 아키텍처 기반 Spring Boot 프로젝트의 파일 분류
"""

import re
from typing import Optional

from pydantic import BaseModel, Field

from ..models import ClassType, Layer


# 신뢰도 계산 상수
PATH_MATCH_WEIGHT = 0.4  # 경로 패턴 매칭 가중치 (40%)
FILENAME_MATCH_WEIGHT = 0.3  # 파일명 패턴 매칭 가중치 (30%)
CODE_MATCH_WEIGHT = 0.3  # 코드 패턴 매칭 가중치 (30%)
CODE_PATTERN_DIVISOR = 3.0  # 코드 패턴 점수 계산 분모
MAX_CONFIDENCE = 1.0  # 신뢰도 최대값


class DetectionResult(BaseModel):
    """레이어/클래스 타입 감지 결과 모델"""

    layer: Optional[str] = Field(
        None, description="감지된 레이어 (DOMAIN, APPLICATION, ADAPTER_OUT, ADAPTER_IN)"
    )
    class_type: Optional[str] = Field(None, description="감지된 클래스 타입")
    confidence: float = Field(description="감지 신뢰도 (0.0 ~ 1.0)")
    reasoning: str = Field(description="감지 근거 설명")


# 경로 패턴 → 레이어 매핑
PATH_LAYER_PATTERNS: dict[Layer, list[str]] = {
    Layer.DOMAIN: [
        r"/domain/",
        r"/model/",
        r"/aggregate/",
        r"/valueobject/",
        r"/vo/",
        r"/event/",
        r"/exception/",
    ],
    Layer.APPLICATION: [
        r"/application/",
        r"/usecase/",
        r"/use-case/",
        r"/service/",
        r"/port/",
        r"/command/",
        r"/query/",
    ],
    Layer.ADAPTER_OUT: [
        r"/persistence/",
        r"/infrastructure/",
        r"/adapter/out/",
        r"/repository/",
        r"/entity/",
        r"/jpa/",
        r"/mapper/",
    ],
    Layer.ADAPTER_IN: [
        r"/adapter/in/",
        r"/web/",
        r"/rest/",
        r"/api/",
        r"/controller/",
        r"/dto/",
    ],
}

# 파일명 패턴 → 클래스 타입 매핑
FILENAME_CLASS_TYPE_PATTERNS: dict[ClassType, list[str]] = {
    # Domain Layer
    ClassType.AGGREGATE: [
        r"Aggregate\.java$",
        r"Root\.java$",
    ],
    ClassType.VALUE_OBJECT: [
        r"VO\.java$",
        r"ValueObject\.java$",
        r"Id\.java$",
        r"Name\.java$",
        r"Amount\.java$",
        r"Money\.java$",
        r"Address\.java$",
        r"Email\.java$",
        r"Phone\.java$",
    ],
    ClassType.DOMAIN_EVENT: [
        r"Event\.java$",
        r"Created\.java$",
        r"Updated\.java$",
        r"Deleted\.java$",
        r"Changed\.java$",
    ],
    ClassType.DOMAIN_EXCEPTION: [
        r"Exception\.java$",
        r"Error\.java$",
    ],
    # Application Layer
    ClassType.USE_CASE: [
        r"UseCase\.java$",
        r"UseCaseImpl\.java$",
        r"Interactor\.java$",
    ],
    ClassType.COMMAND_SERVICE: [
        r"CommandService\.java$",
        r"CommandHandler\.java$",
    ],
    ClassType.QUERY_SERVICE: [
        r"QueryService\.java$",
        r"QueryHandler\.java$",
        r"Finder\.java$",
    ],
    ClassType.PORT_IN: [
        r"Port\.java$",
        r"InPort\.java$",
        r"InputPort\.java$",
    ],
    ClassType.PORT_OUT: [
        r"OutPort\.java$",
        r"OutputPort\.java$",
        r"Gateway\.java$",
    ],
    # Persistence Layer
    ClassType.ENTITY: [
        r"Entity\.java$",
        r"JpaEntity\.java$",
    ],
    ClassType.JPA_REPOSITORY: [
        r"Repository\.java$",
        r"JpaRepository\.java$",
        r"RepositoryImpl\.java$",
    ],
    ClassType.ADAPTER: [
        r"Adapter\.java$",
        r"PersistenceAdapter\.java$",
        r"AdapterImpl\.java$",
    ],
    ClassType.MAPPER: [
        r"Mapper\.java$",
        r"MapperImpl\.java$",
        r"Converter\.java$",
    ],
    # REST API Layer
    ClassType.CONTROLLER: [
        r"Controller\.java$",
        r"RestController\.java$",
        r"Resource\.java$",
    ],
    ClassType.REQUEST_DTO: [
        r"Request\.java$",
        r"RequestDto\.java$",
        r"Command\.java$",
    ],
    ClassType.RESPONSE_DTO: [
        r"Response\.java$",
        r"ResponseDto\.java$",
        r"Result\.java$",
    ],
}

# 코드 내용 패턴 → 클래스 타입 매핑
CODE_CLASS_TYPE_PATTERNS: dict[ClassType, list[str]] = {
    # Domain Layer
    ClassType.AGGREGATE: [
        r"public\s+class\s+\w+\s*\{",  # 기본 클래스 (Lombok 없음)
        r"private\s+final\s+\w+Id\s+",  # ID 필드
        r"public\s+static\s+\w+\s+create\(",  # 팩토리 메서드
    ],
    ClassType.VALUE_OBJECT: [
        r"public\s+record\s+\w+\s*\(",  # Java record
        r"private\s+final\s+",  # 불변 필드
    ],
    ClassType.DOMAIN_EVENT: [
        r"extends\s+(Domain)?Event",
        r"implements\s+.*Event",
        r"@DomainEvent",
    ],
    ClassType.DOMAIN_EXCEPTION: [
        r"extends\s+.*Exception",
        r"extends\s+RuntimeException",
    ],
    # Application Layer
    ClassType.USE_CASE: [
        r"@UseCase",
        r"@Service",
        r"@Transactional",
        r"implements\s+\w+UseCase",
    ],
    ClassType.PORT_IN: [
        r"public\s+interface\s+\w+(UseCase|Port|InputPort)",
    ],
    ClassType.PORT_OUT: [
        r"public\s+interface\s+\w+(Port|OutputPort|Gateway)",
    ],
    # Persistence Layer
    ClassType.ENTITY: [
        r"@Entity",
        r"@Table",
        r"@Id",
        r"@GeneratedValue",
    ],
    ClassType.JPA_REPOSITORY: [
        r"extends\s+(Jpa)?Repository",
        r"@Repository",
    ],
    ClassType.ADAPTER: [
        r"@Component",
        r"@Repository",
        r"implements\s+\w+Port",
    ],
    ClassType.MAPPER: [
        r"@Mapper",
        r"@Component.*Mapper",
        r"toEntity\s*\(",
        r"toDomain\s*\(",
    ],
    # REST API Layer
    ClassType.CONTROLLER: [
        r"@RestController",
        r"@Controller",
        r"@RequestMapping",
        r"@GetMapping",
        r"@PostMapping",
    ],
    ClassType.REQUEST_DTO: [
        r"@Valid",
        r"@NotNull",
        r"@NotBlank",
        r"public\s+record\s+\w+Request",
    ],
    ClassType.RESPONSE_DTO: [
        r"public\s+record\s+\w+Response",
        r"public\s+static\s+\w+Response\s+from\(",
    ],
}

# 클래스 타입 → 레이어 매핑
CLASS_TYPE_TO_LAYER: dict[ClassType, Layer] = {
    ClassType.AGGREGATE: Layer.DOMAIN,
    ClassType.VALUE_OBJECT: Layer.DOMAIN,
    ClassType.DOMAIN_EVENT: Layer.DOMAIN,
    ClassType.DOMAIN_EXCEPTION: Layer.DOMAIN,
    ClassType.USE_CASE: Layer.APPLICATION,
    ClassType.COMMAND_SERVICE: Layer.APPLICATION,
    ClassType.QUERY_SERVICE: Layer.APPLICATION,
    ClassType.PORT_IN: Layer.APPLICATION,
    ClassType.PORT_OUT: Layer.APPLICATION,
    ClassType.ENTITY: Layer.ADAPTER_OUT,
    ClassType.JPA_REPOSITORY: Layer.ADAPTER_OUT,
    ClassType.ADAPTER: Layer.ADAPTER_OUT,
    ClassType.MAPPER: Layer.ADAPTER_OUT,
    ClassType.CONTROLLER: Layer.ADAPTER_IN,
    ClassType.REQUEST_DTO: Layer.ADAPTER_IN,
    ClassType.RESPONSE_DTO: Layer.ADAPTER_IN,
}


def _detect_layer_from_path(file_path: str) -> tuple[Optional[Layer], list[str]]:
    """경로에서 레이어 감지"""
    path_lower = file_path.lower().replace("\\", "/")
    matched_patterns = []

    for layer, patterns in PATH_LAYER_PATTERNS.items():
        for pattern in patterns:
            if re.search(pattern, path_lower, re.IGNORECASE):
                matched_patterns.append(pattern)
                return layer, matched_patterns

    return None, matched_patterns


def _detect_class_type_from_filename(
    file_path: str,
) -> tuple[Optional[ClassType], list[str]]:
    """파일명에서 클래스 타입 감지"""
    # 파일명만 추출
    filename = file_path.split("/")[-1].split("\\")[-1]
    matched_patterns = []

    for class_type, patterns in FILENAME_CLASS_TYPE_PATTERNS.items():
        for pattern in patterns:
            if re.search(pattern, filename):
                matched_patterns.append(pattern)
                return class_type, matched_patterns

    return None, matched_patterns


def _detect_class_type_from_code(code: str) -> tuple[Optional[ClassType], list[str]]:
    """코드 내용에서 클래스 타입 감지"""
    if not code:
        return None, []

    matched_patterns = []
    best_match: Optional[ClassType] = None
    best_score = 0

    for class_type, patterns in CODE_CLASS_TYPE_PATTERNS.items():
        score = 0
        type_patterns = []

        for pattern in patterns:
            if re.search(pattern, code, re.IGNORECASE | re.MULTILINE):
                score += 1
                type_patterns.append(pattern)

        if score > best_score:
            best_score = score
            best_match = class_type
            matched_patterns = type_patterns

    return best_match, matched_patterns


def _calculate_detection_confidence(
    layer_detected: bool,
    class_type_detected: bool,
    path_patterns: list[str],
    filename_patterns: list[str],
    code_patterns: list[str],
) -> float:
    """감지 신뢰도 계산"""
    score = 0.0

    # 경로 패턴 매칭
    if layer_detected and path_patterns:
        score += PATH_MATCH_WEIGHT

    # 파일명 패턴 매칭
    if class_type_detected and filename_patterns:
        score += FILENAME_MATCH_WEIGHT

    # 코드 패턴 매칭
    if code_patterns:
        code_score = (
            min(len(code_patterns) / CODE_PATTERN_DIVISOR, MAX_CONFIDENCE)
            * CODE_MATCH_WEIGHT
        )
        score += code_score

    return round(min(score, MAX_CONFIDENCE), 2)


def _generate_detection_reasoning(
    layer: Optional[Layer],
    class_type: Optional[ClassType],
    path_patterns: list[str],
    filename_patterns: list[str],
    code_patterns: list[str],
) -> str:
    """감지 근거 설명 생성"""
    parts = []

    if path_patterns:
        parts.append(f"경로 패턴: {path_patterns[0]}")

    if filename_patterns:
        parts.append(f"파일명 패턴: {filename_patterns[0]}")

    if code_patterns:
        parts.append(f"코드 패턴 {len(code_patterns)}개 매칭")

    if layer:
        parts.append(f"레이어: {layer.value}")

    if class_type:
        parts.append(f"클래스 타입: {class_type.value}")

    if not parts:
        parts.append("명확한 패턴을 감지하지 못함")

    return " | ".join(parts)


def detect_layer_and_class_type(
    file_path: str,
    code_snippet: Optional[str] = None,
) -> DetectionResult:
    """
    파일 경로 및 코드 내용에서 레이어와 클래스 타입을 자동 감지

    Args:
        file_path: 파일 경로 (예: "order/domain/PaymentAggregate.java")
        code_snippet: 코드 내용 (선택, 추가 분석용)

    Returns:
        DetectionResult: 감지 결과 (layer, class_type, confidence, reasoning)

    Example:
        >>> result = detect_layer_and_class_type("order/domain/Order.java")
        >>> result.layer
        'DOMAIN'
        >>> result = detect_layer_and_class_type(
        ...     "OrderController.java",
        ...     "@RestController\\npublic class OrderController {}"
        ... )
        >>> result.class_type
        'CONTROLLER'
    """
    # 1. 경로에서 레이어 감지
    layer_from_path, path_patterns = _detect_layer_from_path(file_path)

    # 2. 파일명에서 클래스 타입 감지
    class_type_from_filename, filename_patterns = _detect_class_type_from_filename(
        file_path
    )

    # 3. 코드에서 클래스 타입 감지
    class_type_from_code, code_patterns = _detect_class_type_from_code(code_snippet)

    # 4. 최종 클래스 타입 결정 (파일명 > 코드, 단 코드 매칭이 더 강하면 코드 우선)
    # 파일명 패턴이 있고 코드 패턴이 적으면 파일명 우선
    if class_type_from_filename and len(code_patterns) <= 1:
        final_class_type = class_type_from_filename
    else:
        final_class_type = class_type_from_code or class_type_from_filename

    # 5. 레이어 결정 (경로 > 클래스 타입에서 유추)
    final_layer = layer_from_path
    if not final_layer and final_class_type:
        final_layer = CLASS_TYPE_TO_LAYER.get(final_class_type)

    # 6. 신뢰도 계산
    confidence = _calculate_detection_confidence(
        layer_detected=final_layer is not None,
        class_type_detected=final_class_type is not None,
        path_patterns=path_patterns,
        filename_patterns=filename_patterns,
        code_patterns=code_patterns,
    )

    # 7. 근거 생성
    reasoning = _generate_detection_reasoning(
        layer=final_layer,
        class_type=final_class_type,
        path_patterns=path_patterns,
        filename_patterns=filename_patterns,
        code_patterns=code_patterns,
    )

    return DetectionResult(
        layer=final_layer.value if final_layer else None,
        class_type=final_class_type.value if final_class_type else None,
        confidence=confidence,
        reasoning=reasoning,
    )

"""
Template Engine Models

Jinja2 템플릿에 전달되는 컨텍스트 모델과 생성 결과 모델
"""

from typing import Any, Optional

from pydantic import BaseModel, Field


class FieldDefinition(BaseModel):
    """필드 정의 모델"""

    name: str = Field(description="필드 이름 (camelCase)")
    type: str = Field(description="Java 타입 (예: String, Long, LocalDateTime)")
    access_modifier: str = Field(
        default="private", description="접근 제어자 (private, protected, public)"
    )
    is_final: bool = Field(default=False, description="final 여부")
    default_value: Optional[str] = Field(None, description="기본값 (Optional)")
    description: Optional[str] = Field(None, description="필드 설명 (JavaDoc용)")


class MethodDefinition(BaseModel):
    """메서드 정의 모델"""

    name: str = Field(description="메서드 이름 (camelCase)")
    return_type: str = Field(default="void", description="반환 타입")
    parameters: list[tuple[str, str]] = Field(
        default_factory=list, description="파라미터 목록 [(타입, 이름), ...]"
    )
    access_modifier: str = Field(default="public", description="접근 제어자")
    is_static: bool = Field(default=False, description="static 여부")
    body: Optional[str] = Field(None, description="메서드 본문 (Optional)")
    description: Optional[str] = Field(None, description="메서드 설명 (JavaDoc용)")
    annotations: list[str] = Field(default_factory=list, description="어노테이션 목록")


class TemplateContext(BaseModel):
    """템플릿 렌더링 컨텍스트"""

    # 클래스 기본 정보
    class_name: str = Field(description="클래스 이름 (PascalCase)")
    package_name: str = Field(description="패키지 이름 (예: com.example.domain)")
    class_type: str = Field(description="클래스 타입 (AGGREGATE, VALUE_OBJECT, 등)")
    layer: str = Field(
        description="레이어 (DOMAIN, APPLICATION, ADAPTER_OUT, ADAPTER_IN)"
    )

    # 선택적 정보
    description: Optional[str] = Field(None, description="클래스 설명 (JavaDoc)")
    author: Optional[str] = Field(None, description="작성자")

    # 필드 및 메서드
    fields: list[FieldDefinition] = Field(
        default_factory=list, description="필드 정의 목록"
    )
    methods: list[MethodDefinition] = Field(
        default_factory=list, description="메서드 정의 목록"
    )

    # 의존성
    imports: list[str] = Field(default_factory=list, description="import 문 목록")
    annotations: list[str] = Field(
        default_factory=list, description="클래스 레벨 어노테이션"
    )
    interfaces: list[str] = Field(
        default_factory=list, description="구현할 인터페이스 목록"
    )
    extends: Optional[str] = Field(None, description="상속할 클래스")

    # 추가 컨텍스트
    extra: dict[str, Any] = Field(
        default_factory=dict, description="템플릿별 추가 컨텍스트"
    )


class GeneratedCode(BaseModel):
    """생성된 코드 결과"""

    class_name: str = Field(description="클래스 이름")
    class_type: str = Field(description="클래스 타입")
    layer: str = Field(description="레이어")
    package_name: str = Field(description="패키지 이름")
    file_path: str = Field(description="권장 파일 경로")
    code: str = Field(description="생성된 Java 코드")
    applied_rules: list[str] = Field(
        default_factory=list, description="적용된 규칙 코드 목록"
    )
    warnings: list[str] = Field(default_factory=list, description="경고 메시지 목록")

"""
Validation Engine Models
AESA-125~128: 검증 결과 및 위반 사항 모델

Zero-Tolerance 검증 결과, 위반 사항, 수정 제안을 위한 Pydantic 모델
"""

from enum import Enum
from typing import Optional
from pydantic import BaseModel, Field


class ViolationSeverity(str, Enum):
    """위반 심각도"""

    CRITICAL = "CRITICAL"  # Zero-Tolerance 위반 (빌드 차단)
    ERROR = "ERROR"  # 심각한 규칙 위반
    WARNING = "WARNING"  # 권장 사항 위반
    INFO = "INFO"  # 참고 사항


class Violation(BaseModel):
    """코드 위반 사항"""

    rule_code: str = Field(..., description="규칙 코드 (예: AGG-001, ENT-002)")
    rule_name: str = Field(..., description="규칙 명칭")
    severity: ViolationSeverity = Field(..., description="심각도")
    message: str = Field(..., description="위반 설명")
    line_number: Optional[int] = Field(None, description="위반 위치 (라인 번호)")
    column: Optional[int] = Field(None, description="위반 위치 (컬럼)")
    node_type: Optional[str] = Field(None, description="AST 노드 타입")
    code_snippet: Optional[str] = Field(None, description="위반 코드 조각")
    layer: Optional[str] = Field(None, description="해당 레이어")

    def to_dict(self) -> dict:
        """딕셔너리 변환"""
        return {
            "rule_code": self.rule_code,
            "rule_name": self.rule_name,
            "severity": self.severity.value,
            "message": self.message,
            "line_number": self.line_number,
            "column": self.column,
            "node_type": self.node_type,
            "code_snippet": self.code_snippet,
            "layer": self.layer,
        }


class FixSuggestion(BaseModel):
    """수정 제안"""

    violation: Violation = Field(..., description="관련 위반 사항")
    suggestion: str = Field(..., description="수정 제안 설명")
    before_code: Optional[str] = Field(None, description="수정 전 코드")
    after_code: Optional[str] = Field(None, description="수정 후 코드 제안")
    auto_fixable: bool = Field(False, description="자동 수정 가능 여부")
    confidence: float = Field(0.8, ge=0.0, le=1.0, description="수정 제안 신뢰도")

    def to_dict(self) -> dict:
        """딕셔너리 변환"""
        return {
            "violation": self.violation.to_dict(),
            "suggestion": self.suggestion,
            "before_code": self.before_code,
            "after_code": self.after_code,
            "auto_fixable": self.auto_fixable,
            "confidence": self.confidence,
        }


class ValidationResult(BaseModel):
    """검증 결과"""

    is_valid: bool = Field(..., description="검증 통과 여부")
    violations: list[Violation] = Field(
        default_factory=list, description="위반 사항 목록"
    )
    critical_count: int = Field(0, description="CRITICAL 위반 수")
    error_count: int = Field(0, description="ERROR 위반 수")
    warning_count: int = Field(0, description="WARNING 위반 수")
    info_count: int = Field(0, description="INFO 위반 수")
    layer: Optional[str] = Field(None, description="검증 대상 레이어")
    class_type: Optional[str] = Field(None, description="검증 대상 클래스 타입")
    suggestions: list[FixSuggestion] = Field(
        default_factory=list, description="수정 제안 목록"
    )
    validated_code: Optional[str] = Field(None, description="검증된 코드")

    def add_violation(self, violation: Violation) -> None:
        """위반 사항 추가"""
        self.violations.append(violation)
        if violation.severity == ViolationSeverity.CRITICAL:
            self.critical_count += 1
            self.is_valid = False
        elif violation.severity == ViolationSeverity.ERROR:
            self.error_count += 1
            self.is_valid = False
        elif violation.severity == ViolationSeverity.WARNING:
            self.warning_count += 1
        else:
            self.info_count += 1

    def add_suggestion(self, suggestion: FixSuggestion) -> None:
        """수정 제안 추가"""
        self.suggestions.append(suggestion)

    def get_critical_violations(self) -> list[Violation]:
        """CRITICAL 위반만 반환"""
        return [v for v in self.violations if v.severity == ViolationSeverity.CRITICAL]

    def get_zero_tolerance_violations(self) -> list[Violation]:
        """Zero-Tolerance 위반 반환 (CRITICAL)"""
        return self.get_critical_violations()

    def to_dict(self) -> dict:
        """딕셔너리 변환"""
        return {
            "is_valid": self.is_valid,
            "violations": [v.to_dict() for v in self.violations],
            "critical_count": self.critical_count,
            "error_count": self.error_count,
            "warning_count": self.warning_count,
            "info_count": self.info_count,
            "layer": self.layer,
            "class_type": self.class_type,
            "suggestions": [s.to_dict() for s in self.suggestions],
            "validated_code": self.validated_code,
        }

    def summary(self) -> str:
        """검증 결과 요약"""
        status = "✅ PASS" if self.is_valid else "❌ FAIL"
        lines = [
            f"Validation Result: {status}",
            f"  - Critical: {self.critical_count}",
            f"  - Error: {self.error_count}",
            f"  - Warning: {self.warning_count}",
            f"  - Info: {self.info_count}",
        ]
        if self.layer:
            lines.append(f"  - Layer: {self.layer}")
        if self.class_type:
            lines.append(f"  - Class Type: {self.class_type}")
        return "\n".join(lines)


class ValidationContext(BaseModel):
    """검증 컨텍스트"""

    code: str = Field(..., description="검증 대상 코드")
    layer: Optional[str] = Field(None, description="대상 레이어")
    class_type: Optional[str] = Field(None, description="클래스 타입")
    class_name: Optional[str] = Field(None, description="클래스 명")
    package_name: Optional[str] = Field(None, description="패키지 명")
    file_path: Optional[str] = Field(None, description="파일 경로")
    strict_mode: bool = Field(True, description="엄격 모드 (Zero-Tolerance 강제)")
    include_suggestions: bool = Field(True, description="수정 제안 포함 여부")
    max_suggestions: int = Field(10, description="최대 수정 제안 수")

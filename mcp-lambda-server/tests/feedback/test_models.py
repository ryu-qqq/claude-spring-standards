"""
Feedback Models 테스트
AESA-129 Task 5.1: ViolationLog, LogContext, RuleWeight 모델 테스트
"""

import pytest
from datetime import datetime
from uuid import UUID

from src.feedback.models import (
    ViolationLog,
    LogContext,
    ViolationPattern,
    RuleWeight,
    PatternType,
)
from src.validation.models import Violation, ViolationSeverity


class TestLogContext:
    """LogContext 모델 테스트"""

    def test_default_values(self):
        """기본값 생성 테스트"""
        context = LogContext()

        assert context.project_id is None
        assert context.project_name is None
        assert context.file_path is None
        assert context.class_name is None
        assert context.method_name is None
        assert context.session_id is None
        assert context.user_id is None
        assert context.metadata == {}

    def test_with_all_fields(self):
        """모든 필드 설정 테스트"""
        context = LogContext(
            project_id="proj-123",
            project_name="MyProject",
            file_path="src/Order.java",
            class_name="Order",
            method_name="process",
            session_id="sess-abc",
            user_id="user-001",
            metadata={"attempt": 1},
        )

        assert context.project_id == "proj-123"
        assert context.project_name == "MyProject"
        assert context.file_path == "src/Order.java"
        assert context.class_name == "Order"
        assert context.method_name == "process"
        assert context.session_id == "sess-abc"
        assert context.user_id == "user-001"
        assert context.metadata == {"attempt": 1}


class TestViolationLog:
    """ViolationLog 모델 테스트"""

    def test_create_violation_log(self):
        """ViolationLog 생성 테스트"""
        log = ViolationLog(
            rule_code="AGG-001",
            rule_name="Lombok 금지",
            severity="CRITICAL",
            message="Domain에서 Lombok 사용 금지",
            line_number=10,
            code_snippet="@Data",
            layer="DOMAIN",
        )

        assert isinstance(log.id, UUID)
        assert isinstance(log.timestamp, datetime)
        assert log.rule_code == "AGG-001"
        assert log.rule_name == "Lombok 금지"
        assert log.severity == "CRITICAL"
        assert log.message == "Domain에서 Lombok 사용 금지"
        assert log.line_number == 10
        assert log.was_auto_fixed is False

    def test_from_violation(self):
        """Violation에서 ViolationLog 생성 테스트"""
        violation = Violation(
            rule_code="AGG-001",
            rule_name="Lombok 금지",
            severity=ViolationSeverity.CRITICAL,
            message="Domain에서 Lombok 사용 금지",
            line_number=10,
            column=5,
            code_snippet="@Data",
            layer="DOMAIN",
        )

        context = LogContext(
            project_id="proj-123",
            file_path="src/Order.java",
        )

        log = ViolationLog.from_violation(
            violation=violation,
            context=context,
            class_type="AGGREGATE",
        )

        assert log.rule_code == "AGG-001"
        assert log.rule_name == "Lombok 금지"
        assert log.severity == "CRITICAL"
        assert log.line_number == 10
        assert log.column == 5
        assert log.layer == "DOMAIN"
        assert log.class_type == "AGGREGATE"
        assert log.context.project_id == "proj-123"
        assert log.context.file_path == "src/Order.java"

    def test_to_db_dict(self):
        """데이터베이스 저장용 딕셔너리 변환 테스트"""
        context = LogContext(
            project_id="proj-123",
            file_path="src/Order.java",
        )

        log = ViolationLog(
            rule_code="AGG-001",
            rule_name="Lombok 금지",
            severity="CRITICAL",
            message="테스트 메시지",
            line_number=10,
            layer="DOMAIN",
            class_type="AGGREGATE",
            context=context,
            was_auto_fixed=True,
            fix_applied="@Data 제거",
        )

        db_dict = log.to_db_dict()

        assert db_dict["rule_code"] == "AGG-001"
        assert db_dict["severity"] == "CRITICAL"
        assert db_dict["project_id"] == "proj-123"
        assert db_dict["file_path"] == "src/Order.java"
        assert db_dict["was_auto_fixed"] is True
        assert db_dict["fix_applied"] == "@Data 제거"
        assert isinstance(db_dict["id"], str)
        assert isinstance(db_dict["timestamp"], str)


class TestViolationPattern:
    """ViolationPattern 모델 테스트"""

    def test_create_pattern(self):
        """ViolationPattern 생성 테스트"""
        pattern = ViolationPattern(
            pattern_type=PatternType.RECURRING,
            rule_codes=["AGG-001", "AGG-002"],
            occurrence_count=15,
            confidence=0.85,
            description="Lombok 관련 반복 위반",
            project_id="proj-123",
        )

        assert isinstance(pattern.id, UUID)
        assert pattern.pattern_type == PatternType.RECURRING
        assert "AGG-001" in pattern.rule_codes
        assert pattern.occurrence_count == 15
        assert pattern.confidence == 0.85
        assert pattern.project_id == "proj-123"

    def test_pattern_types(self):
        """PatternType enum 테스트"""
        assert PatternType.RECURRING.value == "RECURRING"
        assert PatternType.CORRELATED.value == "CORRELATED"
        assert PatternType.TIME_BASED.value == "TIME_BASED"
        assert PatternType.PROJECT_SPECIFIC.value == "PROJECT_SPECIFIC"
        assert PatternType.USER_SPECIFIC.value == "USER_SPECIFIC"


class TestRuleWeight:
    """RuleWeight 모델 테스트"""

    def test_default_weights(self):
        """기본 가중치 테스트"""
        weight = RuleWeight(rule_code="AGG-001")

        assert weight.rule_code == "AGG-001"
        assert weight.base_weight == 1.0
        assert weight.adjusted_weight == 1.0
        assert weight.violation_count == 0
        assert weight.auto_fix_rate == 0.0
        assert weight.project_overrides == {}

    def test_get_weight_for_project_default(self):
        """프로젝트별 가중치 - 기본값 테스트"""
        weight = RuleWeight(
            rule_code="AGG-001",
            adjusted_weight=1.5,
        )

        # 프로젝트 ID 없으면 adjusted_weight 반환
        assert weight.get_weight_for_project() == 1.5
        assert weight.get_weight_for_project(None) == 1.5

    def test_get_weight_for_project_override(self):
        """프로젝트별 가중치 - 오버라이드 테스트"""
        weight = RuleWeight(
            rule_code="AGG-001",
            adjusted_weight=1.5,
            project_overrides={
                "proj-123": 2.0,
                "proj-456": 0.5,
            },
        )

        # 오버라이드 있으면 해당 값 반환
        assert weight.get_weight_for_project("proj-123") == 2.0
        assert weight.get_weight_for_project("proj-456") == 0.5

        # 오버라이드 없는 프로젝트는 adjusted_weight 반환
        assert weight.get_weight_for_project("proj-789") == 1.5

    def test_weight_validation(self):
        """가중치 범위 검증 테스트"""
        # 정상 범위
        weight = RuleWeight(
            rule_code="AGG-001",
            base_weight=3.0,
            adjusted_weight=4.5,
            auto_fix_rate=0.75,
        )
        assert weight.base_weight == 3.0
        assert weight.adjusted_weight == 4.5
        assert weight.auto_fix_rate == 0.75

        # 범위 초과 시 ValidationError
        with pytest.raises(ValueError):
            RuleWeight(rule_code="AGG-001", base_weight=6.0)

        with pytest.raises(ValueError):
            RuleWeight(rule_code="AGG-001", auto_fix_rate=1.5)

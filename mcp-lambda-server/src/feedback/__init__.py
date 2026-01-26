"""
Feedback Loop Module
AESA-129 Phase 5: 위반 분석 및 학습 시스템

비동기 위반 로깅, 패턴 분석, 규칙 가중치 동적 조정
"""

from .models import ViolationLog, LogContext, ViolationPattern, RuleWeight, PatternType
from .storage import ViolationStorage, get_violation_storage
from .logger import ViolationLogger, get_violation_logger
from .integration import LoggingValidationEngine, get_logging_validation_engine
from .analyzer import PatternAnalyzer, get_pattern_analyzer
from .weight_adjuster import (
    RuleWeightAdjuster,
    WeightCalculationConfig,
    get_weight_adjuster,
    reset_weight_adjuster,
)
from .project_learner import (
    ProjectRuleLearner,
    ProjectRuleConfig,
    LearningResult,
    get_project_learner,
    reset_project_learner,
)

__all__ = [
    # Models
    "ViolationLog",
    "LogContext",
    "ViolationPattern",
    "RuleWeight",
    "PatternType",
    # Storage
    "ViolationStorage",
    "get_violation_storage",
    # Logger
    "ViolationLogger",
    "get_violation_logger",
    # Integration
    "LoggingValidationEngine",
    "get_logging_validation_engine",
    # Analyzer
    "PatternAnalyzer",
    "get_pattern_analyzer",
    # Weight Adjuster
    "RuleWeightAdjuster",
    "WeightCalculationConfig",
    "get_weight_adjuster",
    "reset_weight_adjuster",
    # Project Learner
    "ProjectRuleLearner",
    "ProjectRuleConfig",
    "LearningResult",
    "get_project_learner",
    "reset_project_learner",
]

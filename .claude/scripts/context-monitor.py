#!/usr/bin/env python3
"""
Claude Code Statusline - Context + Queue 통합 모니터

실시간 Context 사용량 + Queue 상태 표시
"""

import json
import sys
from pathlib import Path


def get_queue_status() -> str:
    """
    현재 Queue 상태 가져오기

    Returns:
        "📋 N tasks" 형식 문자열
    """
    active_file = Path(".claude/queue/active.json")

    if not active_file.exists():
        return "📋 0"

    try:
        with open(active_file) as f:
            queue = json.load(f)
            task_count = len(queue.get("tasks", []))
            return f"📋 {task_count}"
    except (json.JSONDecodeError, FileNotFoundError):
        return "📋 0"


def get_context_indicator(usage_pct: float) -> tuple:
    """
    Context 사용량 → 색상 + 막대

    Args:
        usage_pct: Context 사용량 (0-100)

    Returns:
        (color_emoji, bar_string)
    """
    if usage_pct < 50:
        return ("🟢", "█" * 8)
    elif usage_pct < 75:
        return ("🟡", "█" * 6 + "░" * 2)
    elif usage_pct < 90:
        return ("🟠", "█" * 4 + "░" * 4)
    elif usage_pct < 95:
        return ("🔴", "█" * 2 + "░" * 6)
    else:
        return ("🚨", "░" * 8)


def main():
    """
    Statusline 출력

    Input (stdin JSON):
        {
            "model": "claude-sonnet-4-5-20250929",
            "context_usage": {"used": 50000, "limit": 200000},
            "session_duration": 300,
            "lines_changed": 142
        }

    Output:
        [Claude Sonnet 4.5] 📋 3 | 🧠 🟢████████ 25% | 💰 2¢ ⏱ 5m 📝 +142
    """
    try:
        # stdin에서 JSON 읽기
        data = json.load(sys.stdin)

        # Model 이름 (간략화)
        model = data.get("model", "claude-sonnet-4-5-20250929")
        if "sonnet" in model.lower():
            model_display = "Claude Sonnet 4.5"
        elif "opus" in model.lower():
            model_display = "Claude Opus"
        else:
            model_display = "Claude"

        # Context 사용량
        context = data.get("context_usage", {"used": 0, "limit": 200000})
        used = context.get("used", 0)
        limit = context.get("limit", 200000)
        usage_pct = (used / limit * 100) if limit > 0 else 0

        color, bar = get_context_indicator(usage_pct)

        # Queue 상태
        queue_status = get_queue_status()

        # 세션 정보
        duration_sec = data.get("session_duration", 0)
        duration_min = int(duration_sec / 60)

        lines_changed = data.get("lines_changed", 0)

        # 비용 추정 (간단한 계산)
        # Sonnet 4.5: $3/MTok input, $15/MTok output
        input_cost = (used / 1_000_000) * 3
        cost_cents = int(input_cost * 100)

        # 최종 Statusline
        print(
            f"[{model_display}] {queue_status} | "
            f"🧠 {color}{bar} {usage_pct:.0f}% | "
            f"💰 {cost_cents}¢ ⏱ {duration_min}m 📝 +{lines_changed}"
        )

    except json.JSONDecodeError:
        # JSON 파싱 실패 시 기본 출력
        queue_status = get_queue_status()
        print(f"[Claude] {queue_status} | 🧠 🟢████████ 0%")
    except Exception as e:
        # 기타 에러
        print(f"[Claude] ⚠️ Error: {e}")


if __name__ == "__main__":
    main()

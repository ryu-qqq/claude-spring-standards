#!/usr/bin/env python3
"""
Token Usage Benchmark Tool

캐시 시스템 vs 비캐시 시스템의 토큰 사용량을 비교 측정합니다.

Usage:
    python3 token-benchmark.py [--layer domain|application|adapter-rest]
"""

import os
import sys
import json
import glob
from pathlib import Path
from typing import Dict, List, Tuple

# 프로젝트 루트 디렉토리
PROJECT_ROOT = Path(__file__).parent.parent.parent
CACHE_DIR = PROJECT_ROOT / ".claude" / "cache" / "rules"
DOCS_DIR = PROJECT_ROOT / "docs" / "coding_convention"

# 평균 토큰/KB 비율 (경험적 추정값)
TOKENS_PER_KB = 250


def get_file_size_kb(file_path: Path) -> float:
    """파일 크기를 KB 단위로 반환"""
    return os.path.getsize(file_path) / 1024


def estimate_tokens(size_kb: float) -> int:
    """파일 크기로부터 토큰 수 추정"""
    return int(size_kb * TOKENS_PER_KB)


def measure_cache_scenario(layer: str = None) -> Dict:
    """Cache 시스템 시나리오 측정"""
    cache_files = []

    if layer:
        # 특정 레이어의 캐시 파일만
        pattern = f"{layer}-*.json"
        cache_files = list(CACHE_DIR.glob(pattern))
    else:
        # 모든 캐시 파일
        cache_files = [f for f in CACHE_DIR.glob("*.json") if f.name != "index.json"]

    total_size_kb = sum(get_file_size_kb(f) for f in cache_files)
    total_tokens = estimate_tokens(total_size_kb)

    return {
        "scenario": "Cache System",
        "layer": layer or "all",
        "file_count": len(cache_files),
        "total_size_kb": round(total_size_kb, 2),
        "estimated_tokens": total_tokens,
        "files": [f.name for f in cache_files]
    }


def measure_non_cache_scenario(layer: str = None) -> Dict:
    """Non-Cache 시스템 시나리오 측정 (전체 마크다운 로딩)"""
    md_files = []

    if layer:
        # 특정 레이어의 문서만
        if layer == "domain":
            layer_dir = "02-domain-layer"
        elif layer == "application":
            layer_dir = "03-application-layer"
        elif layer == "adapter-rest":
            layer_dir = "01-adapter-rest-api-layer"
        else:
            layer_dir = f"*{layer}*"

        layer_path = DOCS_DIR / layer_dir
        md_files = list(layer_path.rglob("*.md"))
    else:
        # 모든 마크다운 문서
        md_files = list(DOCS_DIR.rglob("*.md"))

    total_size_kb = sum(get_file_size_kb(f) for f in md_files)
    total_tokens = estimate_tokens(total_size_kb)

    return {
        "scenario": "Non-Cache System",
        "layer": layer or "all",
        "file_count": len(md_files),
        "total_size_kb": round(total_size_kb, 2),
        "estimated_tokens": total_tokens,
        "files": [f.name for f in md_files]
    }


def calculate_savings(cache_result: Dict, non_cache_result: Dict) -> Dict:
    """절감 효과 계산"""
    cache_tokens = cache_result["estimated_tokens"]
    non_cache_tokens = non_cache_result["estimated_tokens"]

    saved_tokens = non_cache_tokens - cache_tokens
    savings_percent = (saved_tokens / non_cache_tokens * 100) if non_cache_tokens > 0 else 0

    return {
        "tokens_saved": saved_tokens,
        "savings_percent": round(savings_percent, 2),
        "size_reduction_kb": round(
            non_cache_result["total_size_kb"] - cache_result["total_size_kb"], 2
        )
    }


def run_benchmark(layer: str = None) -> Dict:
    """전체 벤치마크 실행"""
    print(f"\n{'='*60}")
    print(f"  Token Usage Benchmark")
    print(f"  Layer: {layer or 'ALL'}")
    print(f"{'='*60}\n")

    # Cache 시나리오 측정
    print("📊 Measuring Cache System...")
    cache_result = measure_cache_scenario(layer)
    print(f"   Files: {cache_result['file_count']}")
    print(f"   Size: {cache_result['total_size_kb']} KB")
    print(f"   Tokens: ~{cache_result['estimated_tokens']:,}")

    # Non-Cache 시나리오 측정
    print("\n📊 Measuring Non-Cache System...")
    non_cache_result = measure_non_cache_scenario(layer)
    print(f"   Files: {non_cache_result['file_count']}")
    print(f"   Size: {non_cache_result['total_size_kb']} KB")
    print(f"   Tokens: ~{non_cache_result['estimated_tokens']:,}")

    # 절감 효과 계산
    print("\n💰 Calculating Savings...")
    savings = calculate_savings(cache_result, non_cache_result)
    print(f"   Tokens Saved: ~{savings['tokens_saved']:,}")
    print(f"   Savings: {savings['savings_percent']}%")
    print(f"   Size Reduction: {savings['size_reduction_kb']} KB")

    # 결과 저장
    result = {
        "timestamp": "2025-10-17",
        "layer": layer or "all",
        "cache_system": cache_result,
        "non_cache_system": non_cache_result,
        "savings": savings
    }

    # JSON 저장
    output_file = PROJECT_ROOT / "benchmarks" / "results" / "token-comparison.json"
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(result, f, indent=2, ensure_ascii=False)

    print(f"\n✅ Results saved to: {output_file}")
    print(f"\n{'='*60}\n")

    return result


def main():
    """메인 실행 함수"""
    layer = None

    if len(sys.argv) > 1:
        if sys.argv[1] == "--layer" and len(sys.argv) > 2:
            layer = sys.argv[2]
            if layer not in ["domain", "application", "adapter-rest", "all"]:
                print(f"❌ Invalid layer: {layer}")
                print("   Valid options: domain, application, adapter-rest, all")
                sys.exit(1)

            if layer == "all":
                layer = None

    run_benchmark(layer)


if __name__ == "__main__":
    main()

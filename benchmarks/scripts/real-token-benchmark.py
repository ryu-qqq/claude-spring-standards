#!/usr/bin/env python3
"""
Real Token Usage Benchmark Tool (using tiktoken)

tiktoken을 사용하여 실제 토큰 수를 정확히 측정합니다.

Usage:
    python3 real-token-benchmark.py [--layer domain|application|adapter-rest|all]
"""

import os
import sys
import json
import glob
from pathlib import Path
from typing import Dict, List
import tiktoken

# 프로젝트 루트 디렉토리
PROJECT_ROOT = Path(__file__).parent.parent.parent
CACHE_DIR = PROJECT_ROOT / ".claude" / "cache" / "rules"
DOCS_DIR = PROJECT_ROOT / "docs" / "coding_convention"

# Claude 모델의 인코더 (cl100k_base: GPT-4, Claude 3 등)
ENCODER = tiktoken.get_encoding("cl100k_base")


def count_tokens(text: str) -> int:
    """텍스트의 실제 토큰 수 계산"""
    return len(ENCODER.encode(text))


def count_file_tokens(file_path: Path) -> int:
    """파일의 실제 토큰 수 계산"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        return count_tokens(content)
    except Exception as e:
        print(f"⚠️ Error reading {file_path.name}: {e}")
        return 0


def measure_cache_scenario(layer: str = None) -> Dict:
    """Cache 시스템 시나리오 측정 (실제 토큰)"""
    cache_files = []

    if layer:
        # 특정 레이어의 캐시 파일만
        pattern = f"{layer}-*.json"
        cache_files = list(CACHE_DIR.glob(pattern))
    else:
        # 모든 캐시 파일
        cache_files = [f for f in CACHE_DIR.glob("*.json") if f.name != "index.json"]

    print(f"   Counting tokens for {len(cache_files)} cache files...")

    file_tokens = {}
    total_tokens = 0

    for i, file_path in enumerate(cache_files, 1):
        tokens = count_file_tokens(file_path)
        file_tokens[file_path.name] = tokens
        total_tokens += tokens

        if i % 10 == 0:
            print(f"   ... processed {i}/{len(cache_files)} files")

    total_size_kb = sum(os.path.getsize(f) / 1024 for f in cache_files)

    return {
        "scenario": "Cache System",
        "layer": layer or "all",
        "file_count": len(cache_files),
        "total_size_kb": round(total_size_kb, 2),
        "total_tokens": total_tokens,
        "files": [f.name for f in cache_files],
        "file_tokens": file_tokens
    }


def measure_non_cache_scenario(layer: str = None) -> Dict:
    """Non-Cache 시스템 시나리오 측정 (실제 토큰)"""
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

    print(f"   Counting tokens for {len(md_files)} markdown files...")

    file_tokens = {}
    total_tokens = 0

    for i, file_path in enumerate(md_files, 1):
        tokens = count_file_tokens(file_path)
        file_tokens[file_path.name] = tokens
        total_tokens += tokens

        if i % 10 == 0:
            print(f"   ... processed {i}/{len(md_files)} files")

    total_size_kb = sum(os.path.getsize(f) / 1024 for f in md_files)

    return {
        "scenario": "Non-Cache System",
        "layer": layer or "all",
        "file_count": len(md_files),
        "total_size_kb": round(total_size_kb, 2),
        "total_tokens": total_tokens,
        "files": [f.name for f in md_files],
        "file_tokens": file_tokens
    }


def calculate_savings(cache_result: Dict, non_cache_result: Dict) -> Dict:
    """절감 효과 계산"""
    cache_tokens = cache_result["total_tokens"]
    non_cache_tokens = non_cache_result["total_tokens"]

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
    print(f"  Real Token Usage Benchmark (tiktoken)")
    print(f"  Layer: {layer or 'ALL'}")
    print(f"{'='*60}\n")

    # Cache 시나리오 측정
    print("📊 Measuring Cache System (real tokens)...")
    cache_result = measure_cache_scenario(layer)
    print(f"   ✅ Files: {cache_result['file_count']}")
    print(f"   ✅ Size: {cache_result['total_size_kb']} KB")
    print(f"   ✅ Tokens: {cache_result['total_tokens']:,} (REAL)")

    # Non-Cache 시나리오 측정
    print("\n📊 Measuring Non-Cache System (real tokens)...")
    non_cache_result = measure_non_cache_scenario(layer)
    print(f"   ✅ Files: {non_cache_result['file_count']}")
    print(f"   ✅ Size: {non_cache_result['total_size_kb']} KB")
    print(f"   ✅ Tokens: {non_cache_result['total_tokens']:,} (REAL)")

    # 절감 효과 계산
    print("\n💰 Calculating Real Savings...")
    savings = calculate_savings(cache_result, non_cache_result)
    print(f"   ✅ Tokens Saved: {savings['tokens_saved']:,}")
    print(f"   ✅ Savings: {savings['savings_percent']}%")
    print(f"   ✅ Size Reduction: {savings['size_reduction_kb']} KB")

    # 결과 저장
    result = {
        "timestamp": "2025-10-17",
        "layer": layer or "all",
        "measurement_method": "tiktoken (cl100k_base encoder)",
        "cache_system": cache_result,
        "non_cache_system": non_cache_result,
        "savings": savings
    }

    # JSON 저장
    output_file = PROJECT_ROOT / "benchmarks" / "results" / "real-token-comparison.json"
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

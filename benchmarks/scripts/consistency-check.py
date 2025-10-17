#!/usr/bin/env python3
"""
Code Consistency Verification Tool

동일한 PRD로 여러 번 생성된 코드의 일관성을 검증합니다.

Usage:
    python3 consistency-check.py run-1/ run-2/ run-3/
"""

import os
import sys
import difflib
import hashlib
from pathlib import Path
from typing import Dict, List, Tuple


def get_all_java_files(directory: Path) -> List[Path]:
    """디렉토리에서 모든 .java 파일 찾기"""
    return sorted(directory.rglob("*.java"))


def calculate_file_hash(file_path: Path) -> str:
    """파일의 SHA256 해시 계산"""
    sha256 = hashlib.sha256()
    with open(file_path, "rb") as f:
        for chunk in iter(lambda: f.read(4096), b""):
            sha256.update(chunk)
    return sha256.hexdigest()


def extract_structure(file_path: Path) -> Dict:
    """Java 파일의 구조 정보 추출"""
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    structure = {
        "file_name": file_path.name,
        "has_javadoc": "/**" in content and "@author" in content,
        "class_count": content.count("public class ") + content.count("public record ") + content.count("public enum "),
        "method_count": content.count("public ") - content.count("public class ") - content.count("public record "),
        "has_lombok": any(anno in content for anno in ["@Data", "@Getter", "@Setter", "@Builder"]),
        "has_law_of_demeter_violation": ".get" in content and "().get" in content,
        "lines": len(content.split("\n"))
    }

    return structure


def compare_files(file1: Path, file2: Path) -> Dict:
    """두 파일을 비교하여 유사도 계산"""
    with open(file1, "r", encoding="utf-8") as f1:
        content1 = f1.readlines()

    with open(file2, "r", encoding="utf-8") as f2:
        content2 = f2.readlines()

    # difflib을 사용한 유사도 계산
    matcher = difflib.SequenceMatcher(None, content1, content2)
    similarity = matcher.ratio() * 100

    # Diff 정보
    diff = list(difflib.unified_diff(content1, content2, lineterm=""))
    diff_lines = len([line for line in diff if line.startswith(("+", "-"))])

    return {
        "similarity_percent": round(similarity, 2),
        "diff_lines": diff_lines,
        "identical": similarity == 100.0
    }


def verify_run_consistency(run_dirs: List[Path]) -> Dict:
    """여러 실행 결과의 일관성 검증"""
    print(f"\n{'='*60}")
    print(f"  Code Consistency Verification")
    print(f"  Runs: {', '.join([d.name for d in run_dirs])}")
    print(f"{'='*60}\n")

    results = {
        "runs": [d.name for d in run_dirs],
        "file_structure_consistency": {},
        "code_similarity": {},
        "rule_compliance": {},
        "overall_consistency_score": 0.0
    }

    # 1. 파일 구조 일관성 검증
    print("📁 Verifying File Structure...")
    file_sets = []
    for run_dir in run_dirs:
        java_files = get_all_java_files(run_dir)
        file_names = {f.name for f in java_files}
        file_sets.append(file_names)
        print(f"   {run_dir.name}: {len(file_names)} files")

    # 공통 파일 찾기
    common_files = set.intersection(*file_sets) if file_sets else set()
    unique_files = set.union(*file_sets) - common_files if file_sets else set()

    file_structure_score = (len(common_files) / len(set.union(*file_sets)) * 100) if file_sets else 0

    results["file_structure_consistency"] = {
        "common_files": sorted(list(common_files)),
        "unique_files": sorted(list(unique_files)),
        "consistency_score": round(file_structure_score, 2)
    }

    print(f"   Common Files: {len(common_files)}")
    print(f"   Unique Files: {len(unique_files)}")
    print(f"   Score: {file_structure_score:.2f}%\n")

    # 2. 코드 유사도 검증 (공통 파일만)
    print("📊 Analyzing Code Similarity...")
    similarity_scores = []

    for file_name in common_files:
        file_paths = [run_dir / file_name for run_dir in run_dirs]

        # Run-1 vs Run-2
        if len(file_paths) >= 2:
            comp_12 = compare_files(file_paths[0], file_paths[1])
            similarity_scores.append(comp_12["similarity_percent"])

        # Run-1 vs Run-3
        if len(file_paths) >= 3:
            comp_13 = compare_files(file_paths[0], file_paths[2])
            similarity_scores.append(comp_13["similarity_percent"])

        # Run-2 vs Run-3
        if len(file_paths) >= 3:
            comp_23 = compare_files(file_paths[1], file_paths[2])
            similarity_scores.append(comp_23["similarity_percent"])

    avg_similarity = sum(similarity_scores) / len(similarity_scores) if similarity_scores else 0

    results["code_similarity"] = {
        "average_similarity_percent": round(avg_similarity, 2),
        "min_similarity": round(min(similarity_scores), 2) if similarity_scores else 0,
        "max_similarity": round(max(similarity_scores), 2) if similarity_scores else 0
    }

    print(f"   Average Similarity: {avg_similarity:.2f}%")
    print(f"   Min: {min(similarity_scores):.2f}%" if similarity_scores else "   N/A")
    print(f"   Max: {max(similarity_scores):.2f}%\n" if similarity_scores else "   N/A")

    # 3. 규칙 준수 검증
    print("✅ Checking Rule Compliance...")
    all_violations = []

    for run_dir in run_dirs:
        java_files = get_all_java_files(run_dir)

        for java_file in java_files:
            structure = extract_structure(java_file)

            if structure["has_lombok"]:
                all_violations.append(f"{run_dir.name}/{java_file.name}: Lombok detected")

            if structure["has_law_of_demeter_violation"]:
                all_violations.append(f"{run_dir.name}/{java_file.name}: Law of Demeter violation")

            if not structure["has_javadoc"]:
                all_violations.append(f"{run_dir.name}/{java_file.name}: Missing Javadoc")

    rule_compliance_score = 100.0 if len(all_violations) == 0 else max(0, 100 - len(all_violations) * 10)

    results["rule_compliance"] = {
        "violations": all_violations,
        "violation_count": len(all_violations),
        "compliance_score": round(rule_compliance_score, 2)
    }

    print(f"   Violations: {len(all_violations)}")
    print(f"   Compliance Score: {rule_compliance_score:.2f}%\n")

    # 4. 전체 일관성 점수 계산
    overall_score = (
        file_structure_score * 0.3 +
        avg_similarity * 0.4 +
        rule_compliance_score * 0.3
    )

    results["overall_consistency_score"] = round(overall_score, 2)

    print(f"{'='*60}")
    print(f"  Overall Consistency Score: {overall_score:.2f}%")
    print(f"{'='*60}\n")

    return results


def main():
    """메인 실행 함수"""
    if len(sys.argv) < 3:
        print("❌ Usage: python3 consistency-check.py <run-1-dir> <run-2-dir> [run-3-dir]")
        print("   Example: python3 consistency-check.py results/run-1 results/run-2 results/run-3")
        sys.exit(1)

    run_dirs = [Path(arg) for arg in sys.argv[1:]]

    # 디렉토리 존재 확인
    for run_dir in run_dirs:
        if not run_dir.exists():
            print(f"❌ Directory not found: {run_dir}")
            sys.exit(1)

    # 일관성 검증 실행
    results = verify_run_consistency(run_dirs)

    # 결과 저장
    import json
    output_file = Path("benchmarks/results/consistency-report.json")
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(results, f, indent=2, ensure_ascii=False)

    print(f"✅ Results saved to: {output_file}\n")


if __name__ == "__main__":
    main()

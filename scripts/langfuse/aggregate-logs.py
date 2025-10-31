#!/usr/bin/env python3
"""
LangFuse Log Aggregator

Claude Code Hook 로그 및 Pipeline 메트릭을 LangFuse Trace/Observation 형식으로 변환

Usage:
    python3 aggregate-logs.py --anonymize --output langfuse-data.json
"""

import json
import hashlib
import argparse
from datetime import datetime, timedelta
from pathlib import Path
from typing import Dict, List, Optional

class LangFuseAggregator:
    """Claude Code Hook 로그 및 Pipeline 메트릭을 LangFuse 형식으로 변환"""

    def __init__(self, anonymize: bool = False):
        self.anonymize = anonymize
        self.traces: Dict[str, Dict] = {}
        self.observations: List[Dict] = []
        self.session_traces: Dict[str, str] = {}  # timestamp → trace_id 매핑

    def load_claude_logs(self, log_path: str) -> None:
        """Claude Code 로그 로드 및 변환"""
        if not Path(log_path).exists():
            print(f"⚠️  Claude logs not found: {log_path}")
            return

        with open(log_path, 'r') as f:
            for line in f:
                try:
                    event = json.loads(line.strip())
                    self._process_claude_event(event)
                except json.JSONDecodeError:
                    continue

    def load_pipeline_metrics(self, log_path: str) -> None:
        """Pipeline 메트릭 로드 및 변환"""
        if not Path(log_path).exists():
            print(f"⚠️  Pipeline metrics not found: {log_path}")
            return

        with open(log_path, 'r') as f:
            for line in f:
                try:
                    event = json.loads(line.strip())
                    self._process_pipeline_event(event)
                except json.JSONDecodeError:
                    continue

    def _normalize_timestamp(self, timestamp: str) -> str:
        """Timestamp를 LangFuse 호환 ISO 8601 UTC 형식으로 변환"""
        try:
            # 이미 Z나 타임존이 있으면 그대로 반환
            if timestamp.endswith('Z') or '+' in timestamp or timestamp.count(':') > 2:
                return timestamp

            # 타임존 정보 없으면 Z 추가 (UTC)
            return f"{timestamp}Z"
        except:
            # 실패 시 현재 시간 (UTC)
            return datetime.utcnow().isoformat() + 'Z'

    def _process_claude_event(self, event: Dict) -> None:
        """Claude Code 이벤트 → LangFuse Observation"""
        event_type = event.get('event')
        timestamp = self._normalize_timestamp(event.get('timestamp', datetime.utcnow().isoformat()))

        if event_type == 'session_start':
            # 새 Trace 생성
            trace_id = event.get('session_id', self._generate_trace_id(event))
            self.traces[trace_id] = {
                'id': trace_id,
                'name': f"Claude Session",
                'timestamp': timestamp,
                'tags': self._extract_tags(event),
                'metadata': {
                    'project': self._anonymize_string(event.get('project', 'unknown')),
                    'user': self._anonymize_string(event.get('user', 'unknown')),
                    'tool': 'claude-code'
                }
            }
            # 타임스탬프 → trace_id 매핑 저장
            self.session_traces[timestamp] = trace_id

        elif event_type in ['keyword_analysis', 'cache_injection', 'validation_complete',
                            'slash_command_start', 'slash_command_complete']:
            # Observation 생성
            # NOTE: session_id는 hook 내부 ID로 trace ID와 다를 수 있음
            # timestamp로 가장 가까운 trace를 찾아 연결
            trace_id = self._find_trace_by_time(timestamp)
            if not trace_id:
                return

            self.observations.append({
                'traceId': trace_id,
                'name': self._format_event_name(event_type, event),
                'type': 'SPAN',
                'startTime': timestamp,
                'endTime': timestamp,
                'level': self._get_level(event),
                'metadata': self._extract_metadata(event),
                'tags': self._extract_tags(event)
            })

    def _process_pipeline_event(self, event: Dict) -> None:
        """Pipeline 메트릭 이벤트 → LangFuse Observation"""
        timestamp = self._normalize_timestamp(event.get('timestamp', datetime.utcnow().isoformat()))
        task_name = event.get('task', 'unknown')
        status_code = event.get('status', 1)
        duration = event.get('duration', 0)

        # 가장 가까운 Claude 세션 찾기
        trace_id = self._find_trace_by_time(timestamp)
        if not trace_id:
            # Pipeline 전용 Trace 생성
            trace_id = f"pipeline-{timestamp}"
            self.traces[trace_id] = {
                'id': trace_id,
                'name': 'Pipeline Session',
                'timestamp': timestamp,
                'tags': ['pipeline'],
                'metadata': {'tool': 'pipeline'}
            }

        # 시작 시간 계산
        start_time = self._calculate_start_time(timestamp, duration)

        self.observations.append({
            'traceId': trace_id,
            'name': f"Pipeline: {task_name}",
            'type': 'SPAN',
            'startTime': start_time,
            'endTime': timestamp,
            'level': 'DEFAULT' if status_code == 0 else 'ERROR',
            'statusMessage': f"Exit code: {status_code}",
            'metadata': {
                'task': task_name,
                'duration_seconds': duration,
                'exit_code': status_code
            },
            'tags': ['pipeline', task_name]
        })

    def _anonymize_string(self, value: str) -> str:
        """문자열 익명화"""
        if not self.anonymize or not value:
            return value

        # 파일명 익명화
        if value.endswith(('.java', '.kt', '.py')):
            return '*.java'

        # 사용자명 익명화 (이메일 제외)
        if '@' not in value:
            hashed = hashlib.sha256(value.encode()).hexdigest()[:8]
            return f"user-{hashed}"

        return value

    def _extract_tags(self, event: Dict) -> List[str]:
        """이벤트에서 태그 추출"""
        tags = []

        if 'layer' in event:
            tags.append(event['layer'])
        if 'environment' in event:
            tags.append(event['environment'])
        if 'command' in event:
            tags.append(f"slash-command:{event['command']}")

        return tags

    def _extract_metadata(self, event: Dict) -> Dict:
        """이벤트에서 메타데이터 추출"""
        metadata = {}

        # 익명화가 필요한 필드
        if 'file' in event:
            metadata['file'] = self._anonymize_string(event['file'])

        # 숫자/통계 필드 (익명화 불필요)
        for key in ['context_score', 'rules_loaded', 'estimated_tokens',
                    'validation_time_ms', 'total_rules', 'threshold',
                    'total_rules_available']:
            if key in event:
                metadata[key] = event[key]

        # Layer 정보 (분석에 매우 유용)
        if 'detected_layers' in event:
            metadata['detected_layers'] = event['detected_layers']
            metadata['layer_count'] = len(event['detected_layers'])

        if 'layer' in event:
            metadata['layer'] = event['layer']

        # 키워드 분석 정보
        if 'detected_keywords' in event:
            metadata['detected_keywords'] = event['detected_keywords']
            metadata['keyword_count'] = len(event['detected_keywords'])

        # Cache 파일 정보
        if 'cache_files' in event:
            metadata['cache_files_count'] = len(event['cache_files'])

        # Slash Command 정보
        if 'command' in event:
            metadata['slash_command'] = event['command']

        # 효율성 메트릭 계산
        if 'rules_loaded' in event and 'total_rules_available' in event:
            total = event['total_rules_available']
            loaded = event['rules_loaded']
            if total > 0:
                metadata['rules_efficiency'] = round((loaded / total) * 100, 2)

        return metadata

    def _get_level(self, event: Dict) -> str:
        """이벤트 레벨 결정"""
        if event.get('status') == 'failed':
            return 'ERROR'
        elif event.get('status') == 'warning':
            return 'WARNING'
        return 'DEFAULT'

    def _format_event_name(self, event_type: str, event: Dict) -> str:
        """이벤트 이름 포맷팅"""
        if event_type == 'keyword_analysis':
            return "Keyword Analysis"
        elif event_type == 'cache_injection':
            layer = event.get('layer', 'unknown')
            return f"Cache Injection: {layer}"
        elif event_type == 'validation_complete':
            return "Code Validation"
        elif event_type == 'slash_command_start':
            command = event.get('command', 'unknown')
            return f"/{command}"
        elif event_type == 'slash_command_complete':
            command = event.get('command', 'unknown')
            return f"/{command} (completed)"
        return event_type

    def _find_trace_by_time(self, timestamp: str) -> Optional[str]:
        """타임스탬프로 가장 가까운 Trace 찾기"""
        if not self.session_traces:
            return None

        # 간단한 구현: 가장 최근 trace 반환
        return list(self.traces.keys())[-1] if self.traces else None

    def _calculate_start_time(self, end_time: str, duration: float) -> str:
        """시작 시간 계산"""
        try:
            end = datetime.fromisoformat(end_time.replace('Z', '+00:00'))
            start = end - timedelta(seconds=duration)
            return start.isoformat()
        except:
            return end_time

    def _generate_trace_id(self, event: Dict) -> str:
        """Trace ID 생성"""
        timestamp = event.get('timestamp', datetime.utcnow().isoformat())
        return f"session-{timestamp}"

    def export_to_langfuse(self) -> Dict:
        """LangFuse API 형식으로 내보내기"""
        # Trace별 통계 계산
        self._calculate_trace_statistics()

        return {
            'traces': list(self.traces.values()),
            'observations': self.observations
        }

    def _calculate_trace_statistics(self):
        """각 Trace의 Observations 통계 계산"""
        for trace_id, trace in self.traces.items():
            # 이 trace에 속한 observations 찾기
            trace_obs = [obs for obs in self.observations if obs['traceId'] == trace_id]

            if not trace_obs:
                continue

            # 통계 계산
            stats = {
                'total_observations': len(trace_obs),
                'total_tokens': 0,
                'total_rules_loaded': 0,
                'layers_used': set(),
                'keywords_detected': set(),
                'avg_context_score': 0
            }

            context_scores = []

            for obs in trace_obs:
                metadata = obs.get('metadata', {})

                # 토큰 합산
                if 'estimated_tokens' in metadata:
                    stats['total_tokens'] += metadata['estimated_tokens']

                # 규칙 합산
                if 'rules_loaded' in metadata:
                    stats['total_rules_loaded'] += metadata['rules_loaded']

                # Layer 수집
                if 'detected_layers' in metadata:
                    stats['layers_used'].update(metadata['detected_layers'])
                elif 'layer' in metadata:
                    stats['layers_used'].add(metadata['layer'])

                # 키워드 수집
                if 'detected_keywords' in metadata:
                    stats['keywords_detected'].update(metadata['detected_keywords'])

                # Context score 수집
                if 'context_score' in metadata:
                    context_scores.append(metadata['context_score'])

            # 평균 계산
            if context_scores:
                stats['avg_context_score'] = round(sum(context_scores) / len(context_scores), 2)

            # Set를 list로 변환 (JSON 직렬화를 위해)
            stats['layers_used'] = list(stats['layers_used'])
            stats['keywords_detected'] = list(stats['keywords_detected'])

            # Trace metadata에 추가
            if 'metadata' not in trace:
                trace['metadata'] = {}
            trace['metadata'].update(stats)

def main():
    parser = argparse.ArgumentParser(
        description='Aggregate Claude Code and Pipeline logs for LangFuse'
    )
    parser.add_argument(
        '--claude-logs',
        default='.claude/hooks/logs/hook-execution.jsonl',
        help='Path to Claude Code logs'
    )
    parser.add_argument(
        '--pipeline-metrics',
        default='.pipeline-metrics/metrics.jsonl',
        help='Path to Pipeline metrics'
    )
    parser.add_argument(
        '--output',
        default='langfuse-data.json',
        help='Output file path'
    )
    parser.add_argument(
        '--anonymize',
        action='store_true',
        help='Anonymize sensitive data (usernames, filenames, etc.)'
    )
    parser.add_argument(
        '--telemetry',
        action='store_true',
        help='Enable telemetry mode (auto-read .langfuse.telemetry config)'
    )

    args = parser.parse_args()

    # 텔레메트리 모드: .langfuse.telemetry 파일 확인
    if args.telemetry:
        telemetry_file = Path('.langfuse.telemetry')
        if not telemetry_file.exists():
            print("⚠️  Telemetry mode enabled but .langfuse.telemetry not found")
            print("   Telemetry is disabled. Continuing with normal operation.")
            return

        # 텔레메트리 설정 읽기
        telemetry_config = {}
        with open(telemetry_file, 'r') as f:
            for line in f:
                if '=' in line:
                    key, value = line.strip().split('=', 1)
                    telemetry_config[key] = value

        # 텔레메트리가 비활성화되어 있으면 종료
        if telemetry_config.get('enabled', 'false').lower() != 'true':
            print("⚠️  Telemetry is disabled in .langfuse.telemetry")
            print("   Skipping telemetry upload.")
            return

        # 익명화 강제
        args.anonymize = True
        print("🔒 Telemetry mode: Anonymization enforced")

    print("🚀 LangFuse Log Aggregator")
    print(f"   Claude logs: {args.claude_logs}")
    print(f"   Pipeline metrics: {args.pipeline_metrics}")
    print(f"   Anonymize: {args.anonymize}")

    aggregator = LangFuseAggregator(anonymize=args.anonymize)

    # 로그 로드
    aggregator.load_claude_logs(args.claude_logs)
    aggregator.load_pipeline_metrics(args.pipeline_metrics)

    # LangFuse 형식으로 내보내기
    data = aggregator.export_to_langfuse()

    with open(args.output, 'w') as f:
        json.dump(data, f, indent=2)

    print(f"\n✅ Export complete!")
    print(f"   Output: {args.output}")
    print(f"   Traces: {len(data['traces'])}")
    print(f"   Observations: {len(data['observations'])}")

if __name__ == '__main__':
    main()

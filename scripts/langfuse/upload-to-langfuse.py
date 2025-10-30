#!/usr/bin/env python3
"""
LangFuse Uploader

LangFuse API로 Trace/Observation 데이터 전송
timestamp 정규화 포함

Usage:
    export LANGFUSE_PUBLIC_KEY="pk-lf-..."
    export LANGFUSE_SECRET_KEY="sk-lf-..."
    python3 upload-to-langfuse.py --input langfuse-data.json
"""

import json
import os
import argparse
from typing import Dict, List

# requests가 없으면 설치 안내
try:
    import requests
    from requests.auth import HTTPBasicAuth
except ImportError:
    print("❌ Error: 'requests' module not found")
    print("   Install: pip install requests")
    exit(1)

class LangFuseUploader:
    """LangFuse API 클라이언트"""

    def __init__(self,
                 public_key: str,
                 secret_key: str,
                 host: str = "https://cloud.langfuse.com"):
        self.public_key = public_key
        self.secret_key = secret_key
        self.host = host.rstrip('/')
        self.session = requests.Session()
        self.session.auth = HTTPBasicAuth(public_key, secret_key)
        self.session.headers.update({
            'Content-Type': 'application/json'
        })

    def upload_traces(self, traces: List[Dict]) -> int:
        """Trace 업로드"""
        url = f"{self.host}/api/public/ingestion"
        success_count = 0

        # Batch 형식으로 전송
        batch = {
            'batch': [{
                'id': trace.get('id'),  # batch item id (required)
                'type': 'trace-create',
                'timestamp': trace.get('timestamp'),
                'body': {
                    'id': trace.get('id'),
                    'name': trace.get('name'),
                    'timestamp': trace.get('timestamp'),
                    'metadata': trace.get('metadata', {}),
                    'tags': trace.get('tags', [])
                }
            } for trace in traces]
        }

        try:
            response = self.session.post(url, json=batch)
            response.raise_for_status()
            success_count = len(traces)
            print(f"   ✅ Traces uploaded: {success_count}")
        except requests.exceptions.HTTPError as e:
            print(f"   ❌ Traces upload failed")
            print(f"      Error: {e.response.text if hasattr(e, 'response') else str(e)}")
        except Exception as e:
            print(f"   ❌ Traces upload failed")
            print(f"      Error: {str(e)}")

        return success_count

    def upload_observations(self, observations: List[Dict]) -> int:
        """Observation 업로드"""
        url = f"{self.host}/api/public/ingestion"
        success_count = 0

        # Batch 형식으로 전송
        batch = {
            'batch': [{
                'id': f"{obs.get('traceId')}-{obs.get('name')}-{obs.get('startTime')}",  # batch item id (required)
                'type': 'event-create',
                'timestamp': obs.get('startTime'),
                'body': {
                    'traceId': obs.get('traceId'),
                    'name': obs.get('name'),
                    'startTime': obs.get('startTime'),
                    'level': obs.get('level', 'DEFAULT'),
                    'statusMessage': obs.get('statusMessage'),
                    'metadata': obs.get('metadata', {}),
                    'input': obs.get('input'),
                    'output': obs.get('output')
                }
            } for obs in observations]
        }

        try:
            response = self.session.post(url, json=batch)
            response.raise_for_status()
            success_count = len(observations)
            print(f"   ✅ Observations uploaded: {success_count}")
        except requests.exceptions.HTTPError as e:
            print(f"   ❌ Observations upload failed")
            print(f"      Error: {e.response.text if hasattr(e, 'response') else str(e)}")
        except Exception as e:
            print(f"   ❌ Observations upload failed")
            print(f"      Error: {str(e)}")

        return success_count

    def upload_from_file(self, file_path: str) -> Dict[str, int]:
        """파일에서 데이터 읽고 업로드"""
        with open(file_path, 'r') as f:
            data = json.load(f)

        traces = data.get('traces', [])
        observations = data.get('observations', [])

        print(f"\n📤 Uploading to LangFuse ({self.host})")
        print(f"   Traces: {len(traces)}")
        print(f"   Observations: {len(observations)}")
        print()

        print("📊 Uploading Traces...")
        traces_uploaded = self.upload_traces(traces)

        print("\n📊 Uploading Observations...")
        observations_uploaded = self.upload_observations(observations)

        return {
            'traces_total': len(traces),
            'traces_uploaded': traces_uploaded,
            'observations_total': len(observations),
            'observations_uploaded': observations_uploaded
        }

    def test_connection(self) -> bool:
        """LangFuse 연결 테스트"""
        try:
            # Health check endpoint (실제 LangFuse API에 맞게 조정 필요)
            response = self.session.get(f"{self.host}/api/public/health")
            return response.status_code == 200
        except:
            return False

def main():
    parser = argparse.ArgumentParser(
        description='Upload aggregated logs to LangFuse'
    )
    parser.add_argument(
        '--input',
        default='langfuse-data.json',
        help='Input file (aggregated logs)'
    )
    parser.add_argument(
        '--public-key',
        default=os.getenv('LANGFUSE_PUBLIC_KEY'),
        help='LangFuse public key (or set LANGFUSE_PUBLIC_KEY env var)'
    )
    parser.add_argument(
        '--secret-key',
        default=os.getenv('LANGFUSE_SECRET_KEY'),
        help='LangFuse secret key (or set LANGFUSE_SECRET_KEY env var)'
    )
    parser.add_argument(
        '--host',
        default=os.getenv('LANGFUSE_HOST', 'https://cloud.langfuse.com'),
        help='LangFuse host (or set LANGFUSE_HOST env var)'
    )
    parser.add_argument(
        '--telemetry',
        action='store_true',
        help='Enable telemetry mode (auto-read .langfuse.telemetry config)'
    )

    args = parser.parse_args()

    # 텔레메트리 모드: .langfuse.telemetry 파일에서 credentials 읽기
    if args.telemetry:
        telemetry_file = '.langfuse.telemetry'
        if not os.path.exists(telemetry_file):
            print("❌ Error: Telemetry mode enabled but .langfuse.telemetry not found")
            return 1

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
            return 0

        # credentials 설정
        args.public_key = telemetry_config.get('public_key')
        args.secret_key = telemetry_config.get('secret_key')
        args.host = telemetry_config.get('host', 'https://us.cloud.langfuse.com')

        print("🔒 Telemetry mode: Using credentials from .langfuse.telemetry")

    # 필수 인자 확인
    if not args.public_key or not args.secret_key:
        print("❌ Error: LangFuse credentials required")
        print()
        print("   Set environment variables:")
        print("   export LANGFUSE_PUBLIC_KEY='pk-lf-...'")
        print("   export LANGFUSE_SECRET_KEY='sk-lf-...'")
        print()
        print("   Or pass as arguments:")
        print("   --public-key pk-lf-... --secret-key sk-lf-...")
        return 1

    print("🚀 LangFuse Uploader")
    print(f"   Input: {args.input}")
    print(f"   Host: {args.host}")

    # 파일 존재 확인
    if not os.path.exists(args.input):
        print(f"\n❌ Error: Input file not found: {args.input}")
        print("   Run aggregate-logs.py first")
        return 1

    # LangFuse 클라이언트 생성
    uploader = LangFuseUploader(args.public_key, args.secret_key, args.host)

    # 업로드 실행
    try:
        stats = uploader.upload_from_file(args.input)

        print("\n✅ Upload complete!")
        print(f"   Traces: {stats['traces_uploaded']}/{stats['traces_total']}")
        print(f"   Observations: {stats['observations_uploaded']}/{stats['observations_total']}")

        return 0

    except Exception as e:
        print(f"\n❌ Upload failed: {str(e)}")
        return 1

if __name__ == '__main__':
    exit(main())

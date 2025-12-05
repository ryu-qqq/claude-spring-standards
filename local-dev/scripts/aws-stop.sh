#!/bin/bash
# ========================================
# AWS 연결 환경 종료 스크립트
# ========================================
# 주의: 포트 포워딩 터미널은 별도로 Ctrl+C로 종료하세요.
# ========================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOCAL_DEV_DIR="$(dirname "$SCRIPT_DIR")"

echo "🛑 AWS 연결 환경을 종료합니다..."

cd "$LOCAL_DEV_DIR"

# Docker Compose 종료
docker-compose -f docker-compose.aws.yml down

echo ""
echo "✅ AWS 연결 환경이 종료되었습니다!"
echo ""
echo "💡 포트 포워딩도 종료하려면:"
echo "   aws-port-forward.sh 터미널에서 Ctrl+C"

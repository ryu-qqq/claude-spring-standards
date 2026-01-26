#!/bin/bash
# ========================================
# Spring Standards Template - 로컬 환경 종료
# ========================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOCAL_DEV_DIR="$(dirname "$SCRIPT_DIR")"

echo "🛑 로컬 개발 환경을 종료합니다..."

cd "$LOCAL_DEV_DIR"

# Docker Compose 종료
docker-compose -f docker-compose.local.yml down

echo ""
echo "✅ 로컬 환경이 종료되었습니다!"
echo ""
echo "💡 데이터 볼륨도 삭제하려면:"
echo "   docker-compose -f docker-compose.local.yml down -v"

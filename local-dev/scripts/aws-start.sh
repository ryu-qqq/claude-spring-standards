#!/bin/bash
# ========================================
# AWS 연결 환경 시작 스크립트
# ========================================
# 주의: 먼저 별도 터미널에서 aws-port-forward.sh 를 실행하세요!
#
# 사용법:
#   터미널 1: ./scripts/aws-port-forward.sh
#   터미널 2: ./scripts/aws-start.sh
# ========================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOCAL_DEV_DIR="$(dirname "$SCRIPT_DIR")"

echo "🚀 AWS 연결 환경을 시작합니다..."
echo ""

# .env.aws 파일 확인
if [ ! -f "$LOCAL_DEV_DIR/.env.aws" ]; then
    echo "❌ .env.aws 파일이 없습니다."
    echo "   cp .env.aws.example .env.aws 로 생성 후 값을 설정하세요."
    exit 1
fi

# 포트 포워딩 확인
echo "🔍 포트 포워딩 상태 확인..."
source "$LOCAL_DEV_DIR/.env.aws"

RDS_PORT=${AWS_RDS_LOCAL_PORT:-13307}
REDIS_PORT=${AWS_REDIS_LOCAL_PORT:-16380}

check_port() {
    nc -z localhost $1 2>/dev/null
    return $?
}

if ! check_port $RDS_PORT; then
    echo "⚠️  RDS 포트 ($RDS_PORT)가 열려있지 않습니다."
    echo "   먼저 ./scripts/aws-port-forward.sh 를 실행하세요."
    read -p "   계속 진행하시겠습니까? (y/N): " confirm
    if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
        exit 1
    fi
fi

if ! check_port $REDIS_PORT; then
    echo "⚠️  Redis 포트 ($REDIS_PORT)가 열려있지 않습니다."
    echo "   먼저 ./scripts/aws-port-forward.sh 를 실행하세요."
    read -p "   계속 진행하시겠습니까? (y/N): " confirm
    if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
        exit 1
    fi
fi

cd "$LOCAL_DEV_DIR"

# Docker Compose 실행
docker-compose -f docker-compose.aws.yml up -d --build

echo ""
echo "✅ AWS 연결 환경이 시작되었습니다!"
echo ""
echo "📍 서비스 URL:"
echo "   - Web API:          http://localhost:8080"
echo "   - Swagger UI:       http://localhost:8080/swagger-ui.html"
echo ""
echo "🖥️  Admin Tools (AWS 리소스 연결):"
echo "   - phpMyAdmin:       http://localhost:18080  (RDS 관리)"
echo "   - Redis Commander:  http://localhost:18081  (ElastiCache 관리, admin/admin)"
echo ""
echo "📦 AWS 리소스 (SSM 포트 포워딩 경유):"
echo "   - MySQL (RDS):      localhost:$RDS_PORT"
echo "   - Redis (Cache):    localhost:$REDIS_PORT"
echo ""
echo "📝 로그 확인: docker-compose -f docker-compose.aws.yml logs -f"
echo "🛑 종료: ./scripts/aws-stop.sh"
echo ""
echo "⚠️  주의: 프로덕션 데이터에 연결됩니다. 데이터 수정 시 주의하세요!"

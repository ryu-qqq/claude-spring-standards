# 로컬 개발 환경 설정 가이드

Docker를 사용한 완전 독립 로컬 개발 환경 설정 방법을 설명합니다.

## 📋 개요

```
┌─────────────────────────────────────────────────────────────────┐
│                        로컬 개발 환경                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   Docker Compose                         │  │
│  │                                                          │  │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐               │  │
│  │  │ web-api  │  │  MySQL   │  │  Redis   │               │  │
│  │  │  :8080   │  │  :13306  │  │  :16379  │               │  │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘               │  │
│  │       │             │             │                      │  │
│  │       └─────────────┴─────────────┘                      │  │
│  │              template-network                            │  │
│  │                                                          │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  📁 Volumes:                                                   │
│     - mysql_data (영구 저장)                                    │
│     - redis_data (영구 저장)                                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 🔧 사전 준비

### Docker Desktop 설치

```bash
# macOS
brew install --cask docker

# 설치 확인
docker --version
docker-compose --version
```

## 🚀 빠른 시작

### Step 1: 환경 시작

```bash
cd local-dev
./scripts/start.sh
```

또는 직접 실행:

```bash
docker-compose -f docker-compose.local.yml up -d --build
```

### Step 2: 상태 확인

```bash
docker-compose -f docker-compose.local.yml ps
```

출력 예시:
```
NAME                 STATUS          PORTS
template-mysql       running         0.0.0.0:13306->3306/tcp
template-redis       running         0.0.0.0:16379->6379/tcp
template-web-api     running         0.0.0.0:8080->8080/tcp
```

### Step 3: API 테스트

```bash
# Health Check
curl http://localhost:8080/actuator/health

# 응답 예시
# {"status":"UP"}
```

### Step 4: 환경 종료

```bash
./scripts/stop.sh
```

## 🔧 서비스 상세

### Web API

| 항목 | 값 |
|------|-----|
| URL | http://localhost:8080 |
| Swagger | http://localhost:8080/swagger-ui.html |
| Health | http://localhost:8080/actuator/health |
| Profile | local |

### MySQL

| 항목 | 값 |
|------|-----|
| Host | localhost |
| Port | 13306 |
| Database | template |
| Username | root |
| Password | root |

```bash
# 연결 방법
mysql -h localhost -P 13306 -u root -proot template

# Docker 컨테이너 접속
docker exec -it template-mysql mysql -u root -proot
```

### Redis

| 항목 | 값 |
|------|-----|
| Host | localhost |
| Port | 16379 |
| Password | (없음) |

```bash
# 연결 방법
redis-cli -h localhost -p 16379

# Docker 컨테이너 접속
docker exec -it template-redis redis-cli
```

## 📝 로그 확인

```bash
# 전체 로그
docker-compose -f docker-compose.local.yml logs -f

# 특정 서비스 로그
docker-compose -f docker-compose.local.yml logs -f web-api
docker-compose -f docker-compose.local.yml logs -f mysql
docker-compose -f docker-compose.local.yml logs -f redis
```

## 🔄 재시작 / 재빌드

```bash
# 서비스 재시작
docker-compose -f docker-compose.local.yml restart web-api

# 이미지 재빌드 (코드 변경 시)
docker-compose -f docker-compose.local.yml up -d --build web-api

# 전체 재빌드 (캐시 없이)
docker-compose -f docker-compose.local.yml build --no-cache
docker-compose -f docker-compose.local.yml up -d
```

## 🗃️ 데이터 관리

### 데이터 유지

Docker 볼륨에 데이터가 저장되어 컨테이너를 종료해도 데이터가 유지됩니다.

```bash
# 볼륨 확인
docker volume ls | grep template
```

### 데이터 초기화

```bash
# 볼륨 포함 종료 (데이터 삭제)
docker-compose -f docker-compose.local.yml down -v

# 다시 시작
docker-compose -f docker-compose.local.yml up -d
```

## 🔍 문제 해결

### 포트 충돌

```bash
# 사용 중인 포트 확인
lsof -i :8080
lsof -i :13306
lsof -i :16379

# 프로세스 종료
kill -9 <PID>
```

### 컨테이너 상태 확인

```bash
# 상태 확인
docker-compose -f docker-compose.local.yml ps

# 상세 정보
docker inspect template-web-api
```

### MySQL 연결 대기

MySQL이 완전히 시작되기 전에 web-api가 연결을 시도할 수 있습니다.
docker-compose에 `depends_on` + `healthcheck`가 설정되어 있지만,
문제가 발생하면:

```bash
# MySQL 상태 확인
docker-compose -f docker-compose.local.yml logs mysql

# MySQL 준비 확인
docker exec template-mysql mysqladmin ping -u root -proot
```

### 빌드 실패

```bash
# Gradle 빌드 테스트
cd ..
./gradlew :bootstrap:bootstrap-web-api:bootJar

# Docker 빌드 테스트
docker build -f bootstrap/bootstrap-web-api/Dockerfile -t test-build .
```

## ⚙️ 환경 변수 커스터마이징

`.env.local` 파일을 수정하여 환경 변수를 변경할 수 있습니다:

```bash
# .env.local 편집
vim .env.local

# 환경 변수 파일 지정하여 실행
docker-compose --env-file .env.local -f docker-compose.local.yml up -d
```

## 🔮 확장 예정

추후 다음 서비스들이 추가될 예정입니다:

- `bootstrap-scheduler`: 배치/스케줄러 서비스
- `bootstrap-worker`: 워커 프로세스

`docker-compose.local.yml`의 주석 섹션을 참고하세요.

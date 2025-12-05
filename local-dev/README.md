# 로컬 개발 환경

Spring Standards Template 프로젝트의 로컬 개발 환경입니다.

## 📦 구성

```
local-dev/
├── README.md                     # 이 파일
├── docker-compose.local.yml      # 방법 1: 완전 독립 로컬 환경
├── docker-compose.aws.yml        # 방법 2: AWS 리소스 연결 환경
├── .env.local                    # 로컬 환경 변수
├── .env.aws.example              # AWS 환경 변수 예시 (복사해서 .env.aws 생성)
├── scripts/
│   ├── start.sh                  # 로컬 환경 시작
│   ├── stop.sh                   # 로컬 환경 종료
│   ├── aws-port-forward.sh       # AWS SSM 포트 포워딩
│   ├── aws-start.sh              # AWS 환경 시작
│   └── aws-stop.sh               # AWS 환경 종료
└── docs/
    ├── LOCAL_SETUP.md            # 로컬 환경 상세 가이드
    └── AWS_SETUP.md              # AWS 연결 환경 상세 가이드
```

## 🚀 빠른 시작

### 방법 1: 완전 독립 로컬 환경 (권장 - 일반 개발용)

로컬 MySQL, Redis를 Docker로 실행하여 완전히 독립된 환경에서 개발합니다.

```bash
cd local-dev

# 시작
./scripts/start.sh

# 종료
./scripts/stop.sh
```

**특징:**
- ✅ 인터넷 연결 불필요 (빌드 후)
- ✅ AWS 계정 불필요
- ✅ 빠른 시작/종료 (~30초)
- ✅ 데이터 격리 (로컬 볼륨)
- ✅ Admin UI 포함 (phpMyAdmin, Redis Commander)
- ❌ AWS 서비스(S3, SQS) 비활성화

### 방법 2: AWS 리소스 연결 환경 (프로덕션 데이터 테스트용)

실제 AWS RDS, ElastiCache에 SSM 포트 포워딩으로 연결합니다.

```bash
cd local-dev

# 1. 환경 변수 설정 (최초 1회)
cp .env.aws.example .env.aws
vim .env.aws  # AWS 자격 증명 및 리소스 정보 입력

# 2. AWS 포트 포워딩 시작 (터미널 1)
./scripts/aws-port-forward.sh

# 3. Docker Compose 시작 (터미널 2)
./scripts/aws-start.sh

# 4. 종료
./scripts/aws-stop.sh
# 포트 포워딩 터미널에서 Ctrl+C
```

**특징:**
- ✅ 실제 프로덕션 데이터 접근
- ✅ AWS 서비스(S3, SQS) 활성화 가능
- ✅ SSH 키 없이 SSM으로 안전하게 연결
- ✅ Admin UI로 AWS 데이터 직접 조회/관리
- ❌ AWS 계정 및 권한 필요
- ❌ 인터넷 연결 필수
- ⚠️ 프로덕션 데이터 주의

## 📊 환경 비교

| 항목 | 로컬 환경 | AWS 연결 환경 |
|------|----------|--------------|
| **MySQL** | 로컬 Docker 컨테이너 | AWS RDS (SSM 포워딩) |
| **Redis** | 로컬 Docker 컨테이너 | AWS ElastiCache (SSM 포워딩) |
| **S3** | 비활성화 | 실제 AWS S3 (직접 연결) |
| **SQS** | 비활성화 | 실제 AWS SQS (직접 연결) |
| **데이터** | 로컬 테스트 데이터 | 프로덕션 데이터 |
| **AWS 계정** | 불필요 | 필요 |
| **인터넷** | 불필요 | 필요 |
| **시작 속도** | 빠름 (~30초) | 느림 (~2분) |
| **용도** | 일반 개발, 단위 테스트 | 통합 테스트, 디버깅 |

## 🔧 서비스 포트

| 서비스 | 로컬 환경 | AWS 연결 환경 | 용도 |
|--------|----------|--------------|------|
| **Web API** | http://localhost:8080 | http://localhost:8080 | REST API |
| **phpMyAdmin** | http://localhost:18080 | http://localhost:18080 | MySQL 관리 UI |
| **Redis Commander** | http://localhost:18081 | http://localhost:18081 | Redis 관리 UI |
| **MySQL** | localhost:13306 | localhost:13307 (포워딩) | 데이터베이스 |
| **Redis** | localhost:16379 | localhost:16380 (포워딩) | 캐시/세션 |

## 🖥️ Admin Tools 사용법

### phpMyAdmin (MySQL 관리)

```
URL: http://localhost:18080
```

- **로컬 환경**: 자동 로그인 (root/root)
- **AWS 환경**: .env.aws의 자격 증명 사용

**주요 기능:**
- 테이블 구조 확인 및 수정
- SQL 쿼리 직접 실행
- 데이터 조회/수정/삭제
- 데이터 Import/Export

### Redis Commander (Redis 관리)

```
URL: http://localhost:18081
ID/PW: admin / admin
```

**주요 기능:**
- 키 목록 조회 및 검색
- 키 값 조회/수정/삭제
- TTL 확인 및 변경
- 실시간 모니터링

## 📝 데이터베이스 CLI 접속

### 로컬 환경

```bash
# MySQL
mysql -h localhost -P 13306 -u root -proot template

# Redis
redis-cli -h localhost -p 16379
```

### AWS 연결 환경

```bash
# MySQL (포트 포워딩 실행 후)
mysql -h localhost -P 13307 -u admin -p

# Redis (포트 포워딩 실행 후)
redis-cli -h localhost -p 16380
```

## 📚 상세 가이드

- [로컬 환경 상세 가이드](docs/LOCAL_SETUP.md)
- [AWS 연결 환경 상세 가이드](docs/AWS_SETUP.md)

## 🛠️ 트러블슈팅

### 포트 충돌

```bash
# 포트 사용 확인
lsof -i :8080
lsof -i :18080
lsof -i :18081

# 프로세스 종료
kill -9 <PID>
```

### Docker 빌드 실패

```bash
# 캐시 없이 재빌드
docker-compose -f docker-compose.local.yml build --no-cache
```

### Admin UI 접속 불가

```bash
# 컨테이너 상태 확인
docker ps | grep template

# 로그 확인
docker logs template-phpmyadmin
docker logs template-redis-commander
```

### AWS 연결 실패

```bash
# AWS 자격 증명 확인
aws sts get-caller-identity

# Bastion Host 상태 확인
aws ec2 describe-instances --instance-ids i-xxxx
```

## 🔒 보안 주의사항

### 로컬 환경
- `.env.local` 파일은 Git에 커밋하지 마세요
- Redis Commander 비밀번호(admin/admin)는 개발용입니다

### AWS 환경
- `.env.aws` 파일은 **절대** Git에 커밋하지 마세요
- AWS 자격 증명은 최소 권한 원칙 적용
- 프로덕션 데이터 수정 시 각별히 주의
- AWS SSO 사용 권장 (임시 자격 증명)

## 🔮 확장 예정

추후 다음 서비스들이 추가될 예정입니다:

- `bootstrap-scheduler`: 배치/스케줄러 서비스
- `bootstrap-worker`: 워커 프로세스

확장 시 `docker-compose.*.yml`의 주석 처리된 섹션을 참고하세요.

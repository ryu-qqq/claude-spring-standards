# AWS 연결 환경 설정 가이드

로컬 개발 환경에서 실제 AWS 리소스(RDS, ElastiCache, SQS 등)에 연결하는 방법을 설명합니다.

## 📋 개요

```
┌─────────────────────────────────────────────────────────────────┐
│                        로컬 개발 환경                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐    ┌──────────────────────────────────────┐  │
│  │  Docker      │    │  SSM Port Forwarding                 │  │
│  │  Container   │    │  (aws-port-forward.sh)               │  │
│  │              │    │                                      │  │
│  │  web-api     │───▶│  localhost:13307 ─────────┐          │  │
│  │              │    │  localhost:16380 ────────┐│          │  │
│  └──────────────┘    └────────────────────────┐││          │  │
│                                               │││          │  │
└───────────────────────────────────────────────┼┼┼──────────┘  │
                                                │││              │
                            AWS SSM Tunnel      │││              │
                                                │││              │
┌───────────────────────────────────────────────┼┼┼──────────────┤
│                          AWS VPC              │││              │
├───────────────────────────────────────────────┼┼┼──────────────┤
│                                               ▼▼▼              │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │  Bastion     │    │  RDS         │    │  ElastiCache │     │
│  │  Host (EC2)  │───▶│  MySQL       │    │  Redis       │     │
│  │              │    │  :3306       │    │  :6379       │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
│                                                                │
│  ┌──────────────┐    ┌──────────────┐                         │
│  │  SQS         │    │  S3          │  ← 직접 연결 (IAM)       │
│  │              │    │              │                         │
│  └──────────────┘    └──────────────┘                         │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

## 🔧 사전 준비

### 1. AWS CLI 설치

```bash
# macOS
brew install awscli

# 설치 확인
aws --version
```

### 2. Session Manager Plugin 설치

```bash
# macOS
brew install --cask session-manager-plugin

# 또는 수동 설치
# https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html

# 설치 확인
session-manager-plugin
```

### 3. AWS 자격 증명 설정

```bash
# 방법 1: AWS Configure
aws configure
# AWS Access Key ID: AKIA...
# AWS Secret Access Key: ...
# Default region name: ap-northeast-2

# 방법 2: AWS SSO (권장)
aws sso login --profile your-profile

# 자격 증명 확인
aws sts get-caller-identity
```

### 4. IAM 권한 확인

SSM Session Manager를 사용하려면 다음 권한이 필요합니다:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ssm:StartSession",
        "ssm:TerminateSession",
        "ssm:ResumeSession"
      ],
      "Resource": [
        "arn:aws:ec2:*:*:instance/i-*",
        "arn:aws:ssm:*:*:document/AWS-StartPortForwardingSessionToRemoteHost"
      ]
    }
  ]
}
```

## 📝 환경 설정

### 1. .env.aws 파일 생성

```bash
cd local-dev
cp .env.aws.example .env.aws
```

### 2. 값 설정

```bash
# .env.aws 편집
vim .env.aws
```

필수 설정 항목:

| 변수 | 설명 | 예시 |
|------|------|------|
| `AWS_BASTION_INSTANCE_ID` | Bastion EC2 인스턴스 ID | `i-0123456789abcdef0` |
| `AWS_RDS_ENDPOINT` | RDS 클러스터 엔드포인트 | `mydb.cluster-xxx.rds.amazonaws.com` |
| `AWS_RDS_PASSWORD` | RDS 마스터 비밀번호 | `your-password` |
| `AWS_REDIS_ENDPOINT` | ElastiCache 엔드포인트 | `mycache.xxx.cache.amazonaws.com` |
| `AWS_ACCESS_KEY_ID` | IAM Access Key | `AKIA...` |
| `AWS_SECRET_ACCESS_KEY` | IAM Secret Key | `...` |

## 🚀 실행 방법

### Step 1: 포트 포워딩 시작 (터미널 1)

```bash
cd local-dev
./scripts/aws-port-forward.sh
```

출력 예시:
```
==========================================
 AWS SSM Port Forwarding
==========================================

📍 Bastion Host: i-0123456789abcdef0

📍 포트 매핑:
   - MySQL (RDS):   localhost:13307 → mydb.xxx.rds.amazonaws.com:3306
   - Redis (Cache): localhost:16380 → mycache.xxx.cache.amazonaws.com:6379

⏳ 포트 포워딩 세션을 시작합니다... (Ctrl+C로 종료)

Starting session with SessionId: user-xxx
Port 13307 opened for sessionId user-xxx
...
```

### Step 2: 애플리케이션 시작 (터미널 2)

```bash
cd local-dev
./scripts/aws-start.sh
```

### Step 3: 연결 테스트

```bash
# MySQL 연결 테스트
mysql -h localhost -P 13307 -u admin -p

# Redis 연결 테스트
redis-cli -h localhost -p 16380 ping
```

### Step 4: 종료

```bash
# 터미널 2: 애플리케이션 종료
./scripts/aws-stop.sh

# 터미널 1: 포트 포워딩 종료
# Ctrl+C
```

## 🔍 문제 해결

### 포트 포워딩 연결 실패

```bash
# 1. AWS 자격 증명 확인
aws sts get-caller-identity

# 2. Bastion Host 상태 확인
aws ec2 describe-instances --instance-ids i-xxxx --query 'Reservations[].Instances[].State.Name'

# 3. SSM Agent 상태 확인
aws ssm describe-instance-information --filters "Key=InstanceIds,Values=i-xxxx"
```

### 포트 충돌

```bash
# 사용 중인 포트 확인
lsof -i :13307
lsof -i :16380

# 프로세스 종료
kill -9 <PID>
```

### RDS 연결 실패

```bash
# Security Group 확인
# - Bastion Host의 Security Group이 RDS Security Group의 인바운드 규칙에 있는지 확인

# RDS 연결 테스트 (포트 포워딩 후)
mysql -h localhost -P 13307 -u admin -p -e "SELECT 1"
```

### ElastiCache 연결 실패

```bash
# Security Group 확인
# - Bastion Host의 Security Group이 ElastiCache Security Group의 인바운드 규칙에 있는지 확인

# Redis 연결 테스트 (포트 포워딩 후)
redis-cli -h localhost -p 16380 ping
```

## 🔒 보안 주의사항

1. **`.env.aws` 파일 보호**
   - 절대 Git에 커밋하지 마세요
   - `.gitignore`에 포함되어 있는지 확인

2. **최소 권한 원칙**
   - 필요한 리소스에만 접근 가능한 IAM 권한 사용
   - AWS SSO 임시 자격 증명 사용 권장

3. **프로덕션 데이터 주의**
   - 실제 프로덕션 데이터에 연결됩니다
   - 데이터 수정/삭제 시 각별히 주의

4. **네트워크 보안**
   - SSM은 암호화된 터널 사용 (SSH 대비 보안 향상)
   - 별도의 SSH 키 관리 불필요

## 📚 참고 자료

- [AWS SSM Session Manager](https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager.html)
- [AWS SSM 포트 포워딩](https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-sessions-start.html#sessions-remote-port-forwarding)
- [Session Manager Plugin 설치](https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html)

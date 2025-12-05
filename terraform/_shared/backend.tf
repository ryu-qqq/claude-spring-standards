# ============================================================================
# Backend Configuration Template
# ============================================================================
# 이 파일은 Terraform State Backend 설정 템플릿입니다.
#
# ⚠️  주의: 이 파일을 직접 사용하지 마세요!
#     각 모듈의 provider.tf에서 backend 블록을 정의합니다.
#
# 사용법:
#   1. AWS에 S3 버킷과 DynamoDB 테이블을 먼저 생성하세요
#   2. 아래 값들을 실제 값으로 변경하세요
#   3. 각 모듈의 provider.tf에 복사하세요
# ============================================================================

# ============================================================================
# 🔴 Backend 리소스 생성 (최초 1회만 수동 실행)
# ============================================================================
#
# AWS CLI로 생성:
#
# # S3 버킷 생성
# aws s3api create-bucket \
#   --bucket {project-name}-terraform-state \
#   --region ap-northeast-2 \
#   --create-bucket-configuration LocationConstraint=ap-northeast-2
#
# # 버킷 버전관리 활성화
# aws s3api put-bucket-versioning \
#   --bucket {project-name}-terraform-state \
#   --versioning-configuration Status=Enabled
#
# # 버킷 암호화 활성화
# aws s3api put-bucket-encryption \
#   --bucket {project-name}-terraform-state \
#   --server-side-encryption-configuration '{
#     "Rules": [{"ApplyServerSideEncryptionByDefault": {"SSEAlgorithm": "AES256"}}]
#   }'
#
# # DynamoDB Lock 테이블 생성
# aws dynamodb create-table \
#   --table-name {project-name}-terraform-lock \
#   --attribute-definitions AttributeName=LockID,AttributeType=S \
#   --key-schema AttributeName=LockID,KeyType=HASH \
#   --billing-mode PAY_PER_REQUEST \
#   --region ap-northeast-2
#
# ============================================================================

# ============================================================================
# Backend 설정 템플릿
# ============================================================================
#
# terraform {
#   backend "s3" {
#     bucket         = "{project-name}-terraform-state"
#     key            = "{module-name}/terraform.tfstate"
#     region         = "ap-northeast-2"
#     dynamodb_table = "{project-name}-terraform-lock"
#     encrypt        = true
#   }
# }
#
# ============================================================================

# ============================================================================
# 각 모듈별 State Key 컨벤션
# ============================================================================
#
# | 모듈            | State Key                        |
# |-----------------|----------------------------------|
# | ecr             | ecr/terraform.tfstate            |
# | ecs-cluster     | ecs-cluster/terraform.tfstate    |
# | ecs-web-api     | ecs-web-api/terraform.tfstate    |
# | ecs-scheduler   | ecs-scheduler/terraform.tfstate  |
# | elasticache     | elasticache/terraform.tfstate    |
# | s3              | s3/terraform.tfstate             |
# | sqs             | sqs/terraform.tfstate            |
#
# ============================================================================

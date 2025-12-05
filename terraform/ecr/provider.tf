# ============================================================================
# Terraform & Provider Configuration
# ============================================================================

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # ============================================================================
  # Backend Configuration
  # ============================================================================
  # 🔴 프로젝트 시작 시 아래 값들을 수정하세요!
  #
  # 사전 조건:
  #   1. S3 버킷 생성: {project-name}-terraform-state
  #   2. DynamoDB 테이블 생성: {project-name}-terraform-lock
  #   생성 방법은 ../_shared/backend.tf 참조
  # ============================================================================
  backend "s3" {
    bucket         = "my-project-terraform-state"    # TODO: 변경 필요
    key            = "ecr/terraform.tfstate"
    region         = "ap-northeast-2"
    dynamodb_table = "my-project-terraform-lock"     # TODO: 변경 필요
    encrypt        = true
  }
}

# ============================================================================
# AWS Provider
# ============================================================================

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      ManagedBy   = "terraform"
      Project     = var.project_name
      Environment = var.environment
      Team        = var.team
      Owner       = var.owner
      CostCenter  = var.cost_center
    }
  }
}

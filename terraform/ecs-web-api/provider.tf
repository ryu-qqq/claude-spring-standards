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
  backend "s3" {
    bucket         = "my-project-terraform-state"    # TODO: 변경 필요
    key            = "ecs-web-api/terraform.tfstate"
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

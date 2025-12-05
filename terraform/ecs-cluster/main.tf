# ============================================================================
# ECS Cluster
# ============================================================================
# ECS 클러스터 - 컨벤션 강제 Wrapper
#
# 🎯 컨벤션 강제 항목:
#   - 네이밍: {project}-cluster-{env}
#   - 태그: 8개 필수 태그 자동 적용
#   - 모니터링: Container Insights 기본 활성화
#   - 용량 제공자: FARGATE_SPOT 기본 설정
# ============================================================================

# ============================================================================
# ECS Cluster
# ============================================================================
resource "aws_ecs_cluster" "this" {
  name = local.naming.cluster

  # 🔒 Container Insights (컨벤션 강제)
  setting {
    name  = "containerInsights"
    value = var.enable_container_insights ? "enabled" : "disabled"
  }

  tags = merge(
    local.common_tags,
    {
      Name        = local.naming.cluster
      Description = "ECS Cluster for ${var.project_name}"
    }
  )
}

# ============================================================================
# Capacity Providers
# ============================================================================
resource "aws_ecs_cluster_capacity_providers" "this" {
  cluster_name = aws_ecs_cluster.this.name

  capacity_providers = var.enable_fargate_spot ? ["FARGATE", "FARGATE_SPOT"] : ["FARGATE"]

  # 🔒 기본 전략: FARGATE_SPOT 우선 사용 (비용 최적화)
  default_capacity_provider_strategy {
    base              = var.fargate_base_count
    weight            = 1
    capacity_provider = var.enable_fargate_spot ? "FARGATE_SPOT" : "FARGATE"
  }

  dynamic "default_capacity_provider_strategy" {
    for_each = var.enable_fargate_spot ? [1] : []
    content {
      weight            = 0
      capacity_provider = "FARGATE"
    }
  }
}

# ============================================================================
# SSM Parameters (Cross-Stack Reference)
# ============================================================================
resource "aws_ssm_parameter" "cluster_arn" {
  name        = "/${var.project_name}/ecs/cluster-arn"
  type        = "String"
  value       = aws_ecs_cluster.this.arn
  description = "ECS Cluster ARN for ${var.project_name}"

  tags = local.common_tags
}

resource "aws_ssm_parameter" "cluster_name" {
  name        = "/${var.project_name}/ecs/cluster-name"
  type        = "String"
  value       = aws_ecs_cluster.this.name
  description = "ECS Cluster name for ${var.project_name}"

  tags = local.common_tags
}

# ============================================================================
# Locals
# ============================================================================
locals {
  naming = {
    cluster = "${var.project_name}-cluster-${var.environment}"
  }

  common_tags = {
    Environment = var.environment
    Team        = var.team
    Owner       = var.owner
    CostCenter  = var.cost_center
    Project     = var.project_name
    ManagedBy   = "terraform"
  }
}

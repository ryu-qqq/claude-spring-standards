# ============================================================================
# ECS Web API Service
# ============================================================================
# Spring Boot Web API 서비스 - Infrastructure 모듈 Wrapper
#
# 🎯 컨벤션 강제 항목:
#   - 네이밍: {project}-web-api-{env}
#   - 태그: 8개 필수 태그 자동 적용
#   - 배포: Circuit Breaker 활성화, Rolling Update
#   - 보안: Private Subnet, Execute Command 개발환경만 허용
#   - 모니터링: CloudWatch Logs 자동 생성, 7일 보관
# ============================================================================

# ============================================================================
# Data Sources - SSM Parameters (Cross-Stack Reference)
# ============================================================================

data "aws_ssm_parameter" "cluster_arn" {
  name = "/${var.project_name}/ecs/cluster-arn"
}

data "aws_ssm_parameter" "ecr_repository_url" {
  name = "/${var.project_name}/ecr/${var.project_name}-web-api-${var.environment}/repository-url"
}

data "aws_ssm_parameter" "vpc_id" {
  name = "/shared/network/vpc-id"
}

data "aws_ssm_parameter" "private_subnets" {
  name = "/shared/network/private-subnets"
}

# ============================================================================
# ECS Service: web-api
# ============================================================================
module "ecs_web_api" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/ecs-service?ref=${var.infrastructure_module_ref}"

  # 🔒 네이밍 컨벤션 강제
  name           = local.naming.service
  container_name = local.naming.container

  # ECS Cluster (SSM에서 참조)
  cluster_id = data.aws_ssm_parameter.cluster_arn.value

  # 컨테이너 이미지 (ECR에서 참조)
  container_image = "${data.aws_ssm_parameter.ecr_repository_url.value}:${var.image_tag}"
  container_port  = var.container_port

  # 리소스 설정
  cpu    = var.cpu
  memory = var.memory

  # 네트워크 설정 (Private Subnet 강제)
  subnet_ids         = split(",", data.aws_ssm_parameter.private_subnets.value)
  security_group_ids = var.security_group_ids
  assign_public_ip   = false  # 🔒 Private Subnet 강제

  # IAM Roles
  execution_role_arn = var.execution_role_arn
  task_role_arn      = var.task_role_arn

  # 🔒 배포 설정 (컨벤션 강제)
  desired_count                       = var.desired_count
  deployment_maximum_percent          = 200
  deployment_minimum_healthy_percent  = 100
  deployment_circuit_breaker_enable   = true   # 🔒 Circuit Breaker 필수
  deployment_circuit_breaker_rollback = true   # 🔒 자동 롤백 필수

  # 🔒 ECS Exec (개발환경만 허용)
  enable_execute_command = var.environment == "dev" ? var.enable_execute_command : false

  # Health Check
  health_check_command      = var.health_check_path != null ? ["CMD-SHELL", "curl -f http://localhost:${var.container_port}${var.health_check_path} || exit 1"] : null
  health_check_interval     = 30
  health_check_timeout      = 5
  health_check_retries      = 3
  health_check_start_period = var.health_check_grace_period

  # Load Balancer (선택)
  load_balancer_config              = var.target_group_arn != null ? {
    target_group_arn = var.target_group_arn
    container_name   = local.naming.container
    container_port   = var.container_port
  } : null
  health_check_grace_period_seconds = var.target_group_arn != null ? var.health_check_grace_period : null

  # 환경 변수
  container_environment = concat(
    [
      { name = "SPRING_PROFILES_ACTIVE", value = var.environment },
      { name = "SERVER_PORT", value = tostring(var.container_port) },
    ],
    var.container_environment
  )

  # Secrets
  container_secrets = var.container_secrets

  # 🔒 로깅 (컨벤션 강제 - 7일 보관)
  log_retention_days = var.environment == "prod" ? 30 : 7

  # Auto Scaling (선택)
  enable_autoscaling       = var.enable_autoscaling
  autoscaling_min_capacity = var.autoscaling_min_capacity
  autoscaling_max_capacity = var.autoscaling_max_capacity
  autoscaling_target_cpu   = var.autoscaling_target_cpu
  autoscaling_target_memory = var.autoscaling_target_memory

  # Sidecar (선택)
  sidecars = var.sidecars

  # 🔒 필수 태그 (project-context에서 주입)
  environment  = var.environment
  service_name = "${var.project_name}-web-api"
  team         = var.team
  owner        = var.owner
  cost_center  = var.cost_center
  project      = var.project_name
  data_class   = "confidential"
}

# ============================================================================
# SSM Parameters (Cross-Stack Reference)
# ============================================================================
resource "aws_ssm_parameter" "service_name" {
  name        = "/${var.project_name}/ecs/web-api/service-name"
  type        = "String"
  value       = module.ecs_web_api.service_name
  description = "ECS Service name for ${var.project_name}-web-api"

  tags = local.common_tags
}

# ============================================================================
# Locals
# ============================================================================
locals {
  naming = {
    service   = "${var.project_name}-web-api-${var.environment}"
    container = "${var.project_name}-web-api"
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

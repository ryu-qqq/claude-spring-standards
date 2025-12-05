# ============================================================================
# ElastiCache (Redis)
# ============================================================================
# Redis 캐시 클러스터 - Infrastructure 모듈 Wrapper
#
# 🎯 컨벤션 강제 항목:
#   - 네이밍: {project}-redis-{env}
#   - 태그: 8개 필수 태그 자동 적용
#   - 보안: 암호화 필수 (at-rest, in-transit)
#   - 백업: 자동 스냅샷 7일 보관 (prod: 14일)
#   - 모니터링: CloudWatch 알람 기본 활성화
# ============================================================================

# ============================================================================
# Data Sources - SSM Parameters (Cross-Stack Reference)
# ============================================================================

data "aws_ssm_parameter" "private_subnets" {
  name = "/shared/network/private-subnets"
}

# ============================================================================
# ElastiCache: Redis
# ============================================================================
module "redis" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/elasticache?ref=${var.infrastructure_module_ref}"

  # 🔒 네이밍 컨벤션 강제
  cluster_id             = local.naming.cluster
  replication_group_id   = var.enable_replication ? local.naming.replication_group : null
  replication_group_description = var.enable_replication ? "Redis replication group for ${var.project_name}" : null

  # 🔒 엔진 (Redis 강제)
  engine         = "redis"
  engine_version = var.redis_version

  # 노드 설정
  node_type = var.node_type

  # 네트워크 설정 (Private Subnet 강제)
  subnet_ids         = split(",", data.aws_ssm_parameter.private_subnets.value)
  security_group_ids = var.security_group_ids

  # 🔒 보안 설정 (컨벤션 강제)
  at_rest_encryption_enabled  = true   # 🔒 암호화 필수
  transit_encryption_enabled  = true   # 🔒 암호화 필수
  auth_token                  = var.auth_token
  kms_key_id                  = var.kms_key_id

  # 복제 설정 (선택)
  num_node_groups             = var.enable_replication ? var.num_shards : 1
  replicas_per_node_group     = var.enable_replication ? var.replicas_per_shard : 0
  automatic_failover_enabled  = var.enable_replication && var.environment != "dev"
  multi_az_enabled            = var.enable_replication && var.environment == "prod"

  # 🔒 백업 설정 (컨벤션 강제)
  snapshot_retention_limit = var.environment == "prod" ? 14 : 7
  snapshot_window          = "03:00-04:00"
  maintenance_window       = "sun:04:00-sun:05:00"

  # 파라미터 그룹
  parameter_group_family = "redis${split(".", var.redis_version)[0]}"
  parameters             = var.redis_parameters

  # 🔒 CloudWatch 알람 (컨벤션 강제)
  enable_cloudwatch_alarms = true
  alarm_cpu_threshold      = 75
  alarm_memory_threshold   = 75
  alarm_connection_threshold = var.alarm_connection_threshold
  alarm_actions            = var.alarm_actions

  # 🔒 필수 태그 (project-context에서 주입)
  environment  = var.environment
  service_name = "${var.project_name}-redis"
  team         = var.team
  owner        = var.owner
  cost_center  = var.cost_center
  project      = var.project_name
  data_class   = "confidential"
}

# ============================================================================
# SSM Parameters (Cross-Stack Reference)
# ============================================================================
resource "aws_ssm_parameter" "redis_endpoint" {
  name        = "/${var.project_name}/elasticache/redis-endpoint"
  type        = "String"
  value       = var.enable_replication ? module.redis.replication_group_primary_endpoint_address : module.redis.cluster_endpoint
  description = "Redis endpoint for ${var.project_name}"

  tags = local.common_tags
}

resource "aws_ssm_parameter" "redis_port" {
  name        = "/${var.project_name}/elasticache/redis-port"
  type        = "String"
  value       = "6379"
  description = "Redis port for ${var.project_name}"

  tags = local.common_tags
}

# ============================================================================
# Locals
# ============================================================================
locals {
  naming = {
    cluster           = "${var.project_name}-redis-${var.environment}"
    replication_group = "${var.project_name}-redis-rg-${var.environment}"
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

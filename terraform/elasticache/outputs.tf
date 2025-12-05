# ============================================================================
# ElastiCache Module Outputs
# ============================================================================

output "cluster_id" {
  description = "ElastiCache 클러스터 ID"
  value       = module.redis.cluster_id
}

output "cluster_endpoint" {
  description = "ElastiCache 클러스터 엔드포인트"
  value       = var.enable_replication ? null : module.redis.cluster_endpoint
}

output "replication_group_id" {
  description = "복제 그룹 ID (복제 활성화 시)"
  value       = var.enable_replication ? module.redis.replication_group_id : null
}

output "replication_group_primary_endpoint" {
  description = "복제 그룹 Primary 엔드포인트 (복제 활성화 시)"
  value       = var.enable_replication ? module.redis.replication_group_primary_endpoint_address : null
}

output "replication_group_reader_endpoint" {
  description = "복제 그룹 Reader 엔드포인트 (복제 활성화 시)"
  value       = var.enable_replication ? module.redis.replication_group_reader_endpoint_address : null
}

output "port" {
  description = "Redis 포트"
  value       = 6379
}

# ============================================================================
# SSM Parameter Paths
# ============================================================================

output "ssm_endpoint_path" {
  description = "Redis Endpoint SSM Parameter 경로"
  value       = aws_ssm_parameter.redis_endpoint.name
}

output "ssm_port_path" {
  description = "Redis Port SSM Parameter 경로"
  value       = aws_ssm_parameter.redis_port.name
}

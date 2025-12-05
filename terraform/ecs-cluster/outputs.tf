# ============================================================================
# ECS Cluster Module Outputs
# ============================================================================

output "cluster_id" {
  description = "ECS 클러스터 ID"
  value       = aws_ecs_cluster.this.id
}

output "cluster_arn" {
  description = "ECS 클러스터 ARN"
  value       = aws_ecs_cluster.this.arn
}

output "cluster_name" {
  description = "ECS 클러스터 이름"
  value       = aws_ecs_cluster.this.name
}

# ============================================================================
# SSM Parameter Paths (Cross-Stack Reference)
# ============================================================================

output "ssm_cluster_arn_path" {
  description = "Cluster ARN SSM Parameter 경로"
  value       = aws_ssm_parameter.cluster_arn.name
}

output "ssm_cluster_name_path" {
  description = "Cluster Name SSM Parameter 경로"
  value       = aws_ssm_parameter.cluster_name.name
}

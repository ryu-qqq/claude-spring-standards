# ============================================================================
# S3 Module Outputs
# ============================================================================

output "bucket_id" {
  description = "S3 버킷 ID (이름)"
  value       = module.s3_uploads.bucket_id
}

output "bucket_arn" {
  description = "S3 버킷 ARN"
  value       = module.s3_uploads.bucket_arn
}

output "bucket_domain_name" {
  description = "S3 버킷 도메인 이름"
  value       = module.s3_uploads.bucket_domain_name
}

output "bucket_regional_domain_name" {
  description = "S3 버킷 리전 도메인 이름"
  value       = module.s3_uploads.bucket_regional_domain_name
}

# ============================================================================
# SSM Parameter Paths
# ============================================================================

output "ssm_bucket_name_path" {
  description = "Bucket Name SSM Parameter 경로"
  value       = aws_ssm_parameter.bucket_name.name
}

output "ssm_bucket_arn_path" {
  description = "Bucket ARN SSM Parameter 경로"
  value       = aws_ssm_parameter.bucket_arn.name
}

# ============================================================================
# Shared Infrastructure References
# ============================================================================
# 중앙 Infrastructure 레포에서 관리하는 공유 리소스들을 SSM Parameter Store로 참조
#
# ⚠️  전제조건:
#   Infrastructure 레포에서 다음 SSM Parameter들이 생성되어 있어야 합니다.
#   없는 경우 해당 data source를 주석 처리하세요.
#
# SSM Parameter 네이밍 컨벤션:
#   /shared/{category}/{resource-name}
# ============================================================================

# ============================================================================
# VPC & Network
# ============================================================================

data "aws_ssm_parameter" "vpc_id" {
  name = "/shared/network/vpc-id"
}

data "aws_ssm_parameter" "private_subnets" {
  name = "/shared/network/private-subnets"
}

data "aws_ssm_parameter" "public_subnets" {
  name = "/shared/network/public-subnets"
}

# ============================================================================
# ACM Certificate
# ============================================================================

data "aws_ssm_parameter" "certificate_arn" {
  name = "/shared/network/certificate-arn"
}

# ============================================================================
# Route53
# ============================================================================

data "aws_ssm_parameter" "route53_zone_id" {
  name = "/shared/network/route53-zone-id"
}

# ============================================================================
# Monitoring (Optional - AMP)
# ============================================================================

# data "aws_ssm_parameter" "amp_workspace_arn" {
#   name = "/shared/monitoring/amp-workspace-arn"
# }
#
# data "aws_ssm_parameter" "amp_remote_write_url" {
#   name = "/shared/monitoring/amp-remote-write-url"
# }

# ============================================================================
# Locals - 파싱된 값들
# ============================================================================

locals {
  # Network
  vpc_id          = data.aws_ssm_parameter.vpc_id.value
  private_subnets = split(",", data.aws_ssm_parameter.private_subnets.value)
  public_subnets  = split(",", data.aws_ssm_parameter.public_subnets.value)

  # TLS
  certificate_arn = data.aws_ssm_parameter.certificate_arn.value

  # DNS
  route53_zone_id = data.aws_ssm_parameter.route53_zone_id.value

  # Monitoring (주석 해제 시)
  # amp_workspace_arn    = data.aws_ssm_parameter.amp_workspace_arn.value
  # amp_remote_write_url = data.aws_ssm_parameter.amp_remote_write_url.value
}

# ============================================================================
# Outputs
# ============================================================================

output "vpc_id" {
  value = local.vpc_id
}

output "private_subnets" {
  value = local.private_subnets
}

output "public_subnets" {
  value = local.public_subnets
}

output "certificate_arn" {
  value = local.certificate_arn
}

output "route53_zone_id" {
  value = local.route53_zone_id
}

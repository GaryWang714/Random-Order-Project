output "orders_ecr_url" {
  description = "ECR repository URL for orders service"
  value = aws_ecr_repository.orders.repository_url
}

output "payments_ecr_url" {
  description = "ECR repository URL for payments service"
  value = aws_ecr_repository.payments.repository_url
}

output "frontend_ecr_url" {
  description = "ECR repository URL for frontend service"
  value = aws_ecr_repository.frontend.repository_url
}

output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value = aws_db_instance.postgres.endpoint
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value = aws_ecs_cluster.main.name
}
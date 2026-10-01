output "ec2_instance_id" {
  description = "EC2 instance ID"
  value       = aws_instance.app.id
}

output "ec2_public_ip" {
  description = "EC2 public IP address"
  value       = aws_eip.app.public_ip
}

output "ec2_public_dns" {
  description = "EC2 public DNS"
  value       = aws_eip.app.public_dns
}

output "rds_endpoint" {
  description = "RDS database endpoint"
  value       = aws_db_instance.main.endpoint
  sensitive   = true
}

output "rds_address" {
  description = "RDS database address (hostname)"
  value       = aws_db_instance.main.address
}

output "rds_port" {
  description = "RDS database port"
  value       = aws_db_instance.main.port
}

output "api_url" {
  description = "API URL"
  value       = "http://${aws_eip.app.public_ip}:8080"
}

output "frontend_url" {
  description = "Frontend URL"
  value       = "http://${aws_eip.app.public_ip}:3000"
}

output "ssh_command" {
  description = "SSH command to connect to EC2 instance"
  value       = "ssh -i /path/to/your/key.pem ec2-user@${aws_eip.app.public_ip}"
}

output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}

output "database_name" {
  description = "Database name"
  value       = var.db_name
}

output "database_username" {
  description = "Database username"
  value       = var.db_username
  sensitive   = true
}

output "deployment_summary" {
  description = "Deployment summary"
  value = {
    instance_id    = aws_instance.app.id
    public_ip      = aws_eip.app.public_ip
    rds_endpoint   = aws_db_instance.main.endpoint
    api_url        = "http://${aws_eip.app.public_ip}:8080"
    frontend_url   = "http://${aws_eip.app.public_ip}:3000"
    region         = var.aws_region
  }
}

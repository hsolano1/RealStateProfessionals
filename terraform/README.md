# Real Estate Professionals - Infrastructure as Code (Terraform)

Terraform configuration for deploying the Real Estate Professionals application to AWS.

## Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                   AWS Account (us-east-1)           │
│                                                      │
│  ┌──────────────────────────────────────────────┐   │
│  │         VPC (10.0.0.0/16)                     │   │
│  │                                                │   │
│  │  ┌──────────────────────────────────────┐    │   │
│  │  │  Public Subnets                       │    │   │
│  │  │  ┌─────────────────────────────┐     │    │   │
│  │  │  │ EC2 Instance (t3.medium)     │     │    │   │
│  │  │  │ - Spring Boot Backend (8080) │     │    │   │
│  │  │  │ - React Frontend (3000)      │     │    │   │
│  │  │  │ - Docker Daemon               │     │    │   │
│  │  │  └─────────────────────────────┘     │    │   │
│  │  │  EIP: X.X.X.X                         │    │   │
│  │  └──────────────────────────────────────┘    │   │
│  │                                                │   │
│  │  ┌──────────────────────────────────────┐    │   │
│  │  │  Private Subnets                      │    │   │
│  │  │  ┌─────────────────────────────┐     │    │   │
│  │  │  │ RDS PostgreSQL (db.t3.micro)│     │    │   │
│  │  │  │ - Multi-AZ: Disabled         │     │    │   │
│  │  │  │ - Automated Backups          │     │    │   │
│  │  │  └─────────────────────────────┘     │    │   │
│  │  └──────────────────────────────────────┘    │   │
│  └──────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

## Prerequisites

- Terraform 1.0+
- AWS CLI configured with credentials
- Appropriate IAM permissions
- SSH key pair for EC2 access

## Files Overview

| File | Purpose |
|------|---------|
| `main.tf` | Provider configuration and locals |
| `variables.tf` | Input variables and defaults |
| `vpc.tf` | VPC, subnets, security groups, NAT |
| `rds.tf` | RDS PostgreSQL database setup |
| `ec2.tf` | EC2 instance with Docker |
| `outputs.tf` | Exported values for reference |
| `user_data.sh` | EC2 initialization script |
| `terraform.tfvars.example` | Example variable values |

## Quick Start

### 1. Initialize Terraform

```bash
cd terraform
terraform init
```

### 2. Configure Variables

Copy and customize the variables file:
```bash
cp terraform.tfvars.example terraform.tfvars
```

Edit `terraform.tfvars` with your preferred values:
```hcl
aws_region        = "us-east-1"
environment       = "dev"
instance_type     = "t3.medium"
db_instance_class = "db.t3.micro"
db_password       = "YourStrongPassword123!"
```

⚠️ **Security Note**: Never commit `terraform.tfvars` with real passwords. Add it to `.gitignore`.

### 3. Plan Deployment

```bash
terraform plan -out=tfplan
```

Review the plan to ensure correctness.

### 4. Apply Configuration

```bash
terraform apply tfplan
```

This will create:
- VPC with public and private subnets
- NAT gateway for outbound internet access
- EC2 instance in public subnet
- RDS PostgreSQL in private subnet
- Security groups and routing tables
- CloudWatch alarms for monitoring

### 5. Retrieve Outputs

```bash
terraform output
```

**Key Outputs:**
- `ec2_public_ip` - IP to access your application
- `api_url` - Backend API endpoint
- `frontend_url` - Frontend URL
- `rds_endpoint` - Database connection string
- `ssh_command` - SSH into EC2 instance

## Configuration Details

### EC2 Instance

| Parameter | Value | Notes |
|-----------|-------|-------|
| AMI | Amazon Linux 2 | Latest x86_64 AMI |
| Instance Type | t3.medium | Configurable |
| Root Volume | 30 GB gp3 | Encrypted |
| Monitoring | Enabled | CloudWatch metrics |
| Auto IP | Enabled | Elastic IP assigned |

### RDS Database

| Parameter | Default | Configurable |
|-----------|---------|--------------|
| Engine | PostgreSQL | postgres15 |
| Instance Class | db.t3.micro | Recommended for dev |
| Storage | 20 GB gp3 | Encrypted by default |
| Backups | 7 days | Configurable |
| Multi-AZ | Disabled | Enable for production |
| Publicly Accessible | No | Security best practice |

### Security Groups

**EC2 Security Group (realestate-ec2-sg):**
- Inbound: 80 (HTTP), 443 (HTTPS), 8080 (API), 3000 (Frontend), 22 (SSH)
- Outbound: All (0.0.0.0/0)

**RDS Security Group (realestate-rds-sg):**
- Inbound: 5432 (PostgreSQL) from EC2 SG and 0.0.0.0/0
- Outbound: All (0.0.0.0/0)

## Deploying Your Application

After Terraform completes, SSH into the EC2 instance:

```bash
ssh -i /path/to/your/key.pem ec2-user@<public-ip>
```

Clone and deploy the application:

```bash
cd /opt/realestate
git clone <your-repo-url> .
docker-compose build
docker-compose up -d
```

Verify deployment:

```bash
curl http://localhost:8080/api/v1/health
curl http://localhost:3000
```

## Environment Variables for EC2

The `user_data.sh` script automatically sets:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://<rds-endpoint>:5432/realestate
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<your-password>
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

## Monitoring & Logging

### CloudWatch Metrics

Terraform creates alarms for:
- **EC2 CPU Utilization** - Alert if > 80%
- **EC2 Status Checks** - Alert on failures
- **RDS CPU Utilization** - Alert if > 80%
- **RDS Free Storage** - Alert if < 2GB

### Accessing Logs

```bash
# View EC2 logs (if SSM agent is enabled)
aws ssm start-session --target <instance-id> --region us-east-1

# View RDS logs
aws rds describe-db-log-files --db-instance-identifier realestate-db

# View CloudWatch logs
aws logs tail /aws/rds/instance/realestate-db --follow
```

## Updating Infrastructure

### Modify Variables and Reapply

```bash
# Edit terraform.tfvars
nano terraform.tfvars

# Plan changes
terraform plan -out=tfplan

# Apply changes
terraform apply tfplan
```

### Changing Instance Type

```hcl
# In terraform.tfvars
instance_type = "t3.large"

terraform plan -out=tfplan
terraform apply tfplan
```

⚠️ **Note**: Some changes require instance termination/recreation.

## Destroying Resources

### Destroy Everything

```bash
terraform destroy
```

**Warning**: This will delete:
- EC2 instance and Elastic IP
- RDS database and backups (if `skip_final_snapshot = true`)
- VPC, subnets, and all networking

### Preserve RDS Snapshot

```hcl
# In terraform.tfvars
skip_final_snapshot = false

terraform destroy
```

This creates a final snapshot before deletion.

## Cost Estimation

Typical monthly costs (us-east-1):

| Resource | Instance | Cost/Month |
|----------|----------|-----------|
| EC2 | t3.medium | ~$30 |
| RDS | db.t3.micro | ~$20 |
| NAT Gateway | 1 | ~$32 |
| Data Transfer | Minimal | ~$5 |
| **Total** | | **~$87** |

💡 Use AWS Cost Calculator for accurate estimates: https://calculator.aws/

## Troubleshooting

### Terraform Initialization Fails

```bash
# Clear local state
rm -rf .terraform

# Reinitialize
terraform init
```

### EC2 Instance Not Accessible

```bash
# Check security group
aws ec2 describe-security-groups --group-ids <sg-id>

# Check instance status
aws ec2 describe-instances --instance-ids <instance-id>
```

### RDS Connection Fails

```bash
# Test from EC2
psql -h <rds-endpoint> -U postgres -d realestate

# Check security group rules
aws ec2 describe-security-groups --group-ids <sg-id>
```

### Docker Containers Not Running

```bash
# SSH into EC2
ssh -i key.pem ec2-user@<ip>

# Check Docker status
docker ps -a
docker logs <container-id>
```

## Best Practices

1. **Always review plan before apply**: `terraform plan` shows all changes
2. **Use separate environments**: Create dev, staging, production
3. **Store state remotely**: Use S3 backend for team collaboration
4. **Lock state file**: Enable DynamoDB locking
5. **Secure sensitive values**: Use AWS Secrets Manager
6. **Enable MFA Delete**: For critical RDS instances
7. **Regular backups**: Leverage RDS automated backups
8. **Monitor costs**: Use CloudWatch billing alarms

## State Management

### Local State (Current)

State stored in `terraform.tfstate` locally. Good for development.

### Remote State (Recommended for Production)

Add to `main.tf`:

```hcl
terraform {
  backend "s3" {
    bucket         = "your-terraform-state"
    key            = "realestate/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "terraform-locks"
  }
}
```

## Advanced Configuration

### Multi-AZ Setup

```hcl
# In terraform.tfvars
enable_multi_az = true
```

This enables RDS Multi-AZ for high availability.

### Custom VPC CIDR

```hcl
# In terraform.tfvars
vpc_cidr_block = "172.16.0.0/16"
```

### Larger Database

```hcl
# In terraform.tfvars
db_instance_class = "db.t3.small"
allocated_storage = 50
```

## Support & Documentation

- Terraform AWS Provider: https://registry.terraform.io/providers/hashicorp/aws/latest
- AWS Terraform Best Practices: https://docs.aws.amazon.com/
- Troubleshooting: See main README.md

## License

Private project for interview assessment.

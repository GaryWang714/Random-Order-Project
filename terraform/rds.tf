// db subnet group tells rds which subnet to use. needs at least 2 subnet in different AZs
// security group is the firewall rules for the db. below allows only traffic on port 5432 from within vpc
// db instance is the actual postgre db
// - db.t3.micro (smallest free instance), skip_final_snapshot (don't take backup when deleting), public_accessible (only accessible from within vpc)

resource "aws_db_subnet_group" "main" {
  name = "${var.project_name}-db-subnet-group"
  subnet_ids = [aws_subnet.public_1.id, aws_subnet.public_2.id]

  tags = {
    Name = "${var.project_name}-db-subnet-group"
  }
}

resource "aws_security_group" "rds" {
  name = "${var.project_name}-rds-sg"
  description = "Allow PostgreSQL access"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port = 5432
    to_port = 5432
    protocol = "tcp"
    cidr_blocks = ["10.0.0.0/16"]
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-rds-sg"
  }
}

resource "aws_db_instance" "postgres" {
  identifier = "${var.project_name}-postgres"
  engine = "postgres"
  engine_version = "16"
  instance_class = "db.t3.micro"
  allocated_storage = 20

  db_name = "orders"
  username = var.db_username
  password = var.db_password

  db_subnet_group_name = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  skip_final_snapshot = true
  publicly_accessible = false

  tags = {
    Name = "${var.project_name}-postgres"
  }
}
resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-cluster"

  tags = {
    Name = "${var.project_name}-cluster"
  }
}

resource "aws_security_group" "ecs" {
  name = "${var.project_name}-ecs-sg"
  description = "Allow inbound traffic to ECS tasks"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port = 8080
    to_port = 8080
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 8081
    to_port = 8081
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-ecs-sg"
  }
}

resource "aws_cloudwatch_log_group" "orders" {
  name = "/ecs/${var.project_name}-orders"
  retention_in_days = 7
}

resource "aws_cloudwatch_log_group" "payments" {
  name = "/ecs/${var.project_name}-payments"
  retention_in_days = 7
}

resource "aws_cloudwatch_log_group" "frontend" {
  name = "/ecs/${var.project_name}-frontend"
  retention_in_days = 7
}

resource "aws_ecs_task_definition" "orders" {
  family = "${var.project_name}-orders"
  network_mode = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu = "256"
  memory = "512"
  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name = "orders"
      image = "${aws_ecr_repository.orders.repository_url}:latest"
      portMappings = [
        {
          containerPort = 8080
          protocol = "tcp"
        }
      ]
      environment = [
        {
          name = "SPRING_KAFKA_BOOTSTRAP_SERVERS"
          value = "kafka:29092"
        },
        {
          name = "SPRING_DATASOURCE_URL"
          value = "jdbc:postgresql://${aws_db_instance.postgres.endpoint}/orders"
        },
        {
          name = "SPRING_DATASOURCE_USERNAME"
          value = var.db_username
        },
        {
          name = "SPRING_DATASOURCE_PASSWORD"
          value = var.db_password
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group = "/ecs/${var.project_name}-orders"
          awslogs-region = var.aws_region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])
}

resource "aws_ecs_task_definition" "payments" {
  family = "${var.project_name}-payments"
  network_mode = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu = "256"
  memory = "512"
  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn = aws_iam_role.ecs_task_execution_role.arn

  container_definitions = jsonencode([
    {
      name = "payments"
      image = "${aws_ecr_repository.payments.repository_url}:latest"
      portMappings = [
        {
          containerPort = 8081
          protocol = "tcp"
        }
      ]
      environment = [
        {
          name = "SPRING_KAFKA_BOOTSTRAP_SERVERS"
          value = "kafka:29092"
        },
        {
          name = "SPRING_DATA_MONGODB_URI"
          value = "mongodb://mongodb:27017/payments"
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group = "/ecs/${var.project_name}-payments"
          awslogs-region = var.aws_region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])
}

resource "aws_ecs_task_definition" "frontend" {
  family                   = "${var.project_name}-frontend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name  = "frontend"
      image = "${aws_ecr_repository.frontend.repository_url}:latest"
      portMappings = [
        {
          containerPort = 80
          protocol      = "tcp"
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/${var.project_name}-frontend"
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "orders" {
  name = "${var.project_name}-orders"
  cluster = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.orders.arn
  desired_count = 1
  launch_type = "FARGATE"

  network_configuration {
    subnets = [aws_subnet.public_1.id, aws_subnet.public_2.id]
    security_groups = [aws_security_group.ecs.id]
    assign_public_ip = true
  }
}

resource "aws_ecs_service" "payments" {
  name            = "${var.project_name}-payments"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.payments.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_subnet.public_1.id, aws_subnet.public_2.id]
    security_groups  = [aws_security_group.ecs.id]
    assign_public_ip = true
  }
}

resource "aws_ecs_service" "frontend" {
  name            = "${var.project_name}-frontend"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.frontend.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_subnet.public_1.id, aws_subnet.public_2.id]
    security_groups  = [aws_security_group.ecs.id]
    assign_public_ip = true
  }
}

# ECS Cluster — a logical grouping of your services in AWS
# Security Group — firewall rules allowing traffic on ports 80, 8080, 8081
# CloudWatch Log Groups — where your container logs will be stored (we'll use these in the CloudWatch monitoring step)
# Task Definitions — blueprints for your containers. Defines the Docker image, CPU/memory, environment variables, and where to send logs
# ECS Services — keeps your tasks running. If a container crashes, the service restarts it automatically. desired_count = 1 means always keep one instance running
resource "aws_service_discovery_private_dns_namespace" "main" {
  name        = "random-order.local"
  description = "Private DNS namespace for ECS service discovery"
  vpc         = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-namespace"
  }
}

resource "aws_service_discovery_service" "kafka" {
  name = "kafka"

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.main.id
    dns_records {
      ttl  = 10
      type = "A"
    }
    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_cloudwatch_log_group" "kafka" {
  name              = "/ecs/${var.project_name}-kafka"
  retention_in_days = 7
}

resource "aws_ecs_task_definition" "kafka" {
  family                   = "${var.project_name}-kafka"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name  = "kafka"
      image = "apache/kafka:latest"
      portMappings = [
        {
          containerPort = 9092
          protocol      = "tcp"
        },
        {
          containerPort = 9093
          protocol      = "tcp"
        }
      ]
      environment = [
        { name = "KAFKA_NODE_ID",                                   value = "1" },
        { name = "KAFKA_PROCESS_ROLES",                             value = "broker,controller" },
        { name = "KAFKA_LISTENERS",                                 value = "PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093" },
        { name = "KAFKA_ADVERTISED_LISTENERS",                      value = "PLAINTEXT://kafka.random-order.local:9092" },
        { name = "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP",           value = "PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT" },
        { name = "KAFKA_INTER_BROKER_LISTENER_NAME",               value = "PLAINTEXT" },
        { name = "KAFKA_CONTROLLER_LISTENER_NAMES",                value = "CONTROLLER" },
        { name = "KAFKA_CONTROLLER_QUORUM_VOTERS",                 value = "1@localhost:9093" },
        { name = "KAFKA_LOG_DIRS",                                  value = "/tmp/kafka-logs" },
        { name = "KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR",         value = "1" },
        { name = "KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", value = "1" },
        { name = "KAFKA_TRANSACTION_STATE_LOG_MIN_ISR",            value = "1" },
        { name = "KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS",         value = "0" }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/${var.project_name}-kafka"
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "kafka" {
  name            = "${var.project_name}-kafka"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.kafka.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_subnet.public_1.id]
    security_groups  = [aws_security_group.ecs.id]
    assign_public_ip = true
  }

  service_registries {
    registry_arn = aws_service_discovery_service.kafka.arn
  }
}
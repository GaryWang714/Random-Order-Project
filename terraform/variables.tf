variable "aws_region" {
    description = "AWS region to deploy resources"
    type = string
    default = "us-east-1"
}

variable "project_name" {
    description = "Name prefix for all resources"
    type = string
    default = "random-order"
}

variable "db_username" {
    description = "PostgreSQL database username"
    type = string
    default = "postgres"
}

variable "db_password" {
    description = "PostgreSQL database password"
    type = string
    default = true
}
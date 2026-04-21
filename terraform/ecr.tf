// ecr store docker images
// one repo created per service
// image_tag_mutability = mutable allows overwriting the 'latest' tag when pushing new image
// scan_on_push = true will automatically scan images for security vulnerabilities

resource "aws_ecr_repository" "orders" {
  name = "${var.project_name}-orders"
  image_tag_mutability = "MUTABLE"
  force_delete = true

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "${var.project_name}-orders-ecr"
  }
}

resource "aws_ecr_repository" "payments" {
  name = "${var.project_name}-payments"
  image_tag_mutability = "MUTABLE"
  force_delete = true

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "${var.project_name}-payments-ecr"
  }
}

resource "aws_ecr_repository" "frontend" {
  name = "${var.project_name}-frontend"
  image_tag_mutability = "MUTABLE"
  force_delete = true

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "${var.project_name}-front-ecr"
  }
}
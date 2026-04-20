// is like pom.xml
// configuring aws provider plugin (library that talks to aws) using version 5.x while requiring terraform be version 1.0 or greater
terraform {
    required_providers {
        aws = {
            source = "hashicorp/aws"
            version = "~> 5.0"
            }
        }

    required_version = ">= 1.0"
}

// configures aws plugin. tells it which aws region to create resources in
provider "aws" {
    region = var.aws_region
}
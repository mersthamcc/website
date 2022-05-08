terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.8.0"
    }
  }

  required_version = "1.1.7"
}

provider "aws" {
  region              = "eu-west-2"
  allowed_account_ids = ["830398123788"]
  profile             = "mcc-dev"
}

locals {
  default_tags = {
    terraform   = "aws"
    environment = "dev"
  }
}
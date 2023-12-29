terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.31.0"
    }
  }

  required_version = "1.6.6"
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

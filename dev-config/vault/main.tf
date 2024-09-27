terraform {
  backend "local" {
    path = "/tmp/terraform.tfstate"
  }

  required_providers {
    vault = {
      source  = "hashicorp/vault"
      version = "4.4.0"
    }
  }
}

provider "vault" {
  address = var.vault_address
  token   = var.vault_token
}

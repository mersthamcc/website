resource "aws_cognito_user_pool" "dev_pool" {
  name = var.developer_name

  username_configuration {
    case_sensitive = false
  }

  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_uppercase = true
    require_numbers   = true
    require_symbols   = false

    temporary_password_validity_days = 7
  }

  software_token_mfa_configuration {
    enabled = true
  }

  username_attributes      = ["email"]
  auto_verified_attributes = ["email"]
  mfa_configuration        = "OPTIONAL"
}

resource "aws_cognito_user_pool_domain" "dev_pool_domain" {
  domain       = "mcc-${var.developer_name}"
  user_pool_id = aws_cognito_user_pool.dev_pool.id
}

resource "aws_cognito_user_pool_ui_customization" "dev_pool_ui" {
  user_pool_id = aws_cognito_user_pool.dev_pool.id
  image_file   = filebase64("data/header-logo.png")
  css          = file("data/cognito-login.css")
}

resource "aws_cognito_resource_server" "graphql" {
  name = "graphql"

  identifier   = "graphql"
  user_pool_id = aws_cognito_user_pool.dev_pool.id

  scope {
    scope_description = "GraphQL Trusted Client (website only)"
    scope_name        = "trusted-client"
  }
}

resource "aws_cognito_identity_provider" "saml_provider" {
  user_pool_id  = aws_cognito_user_pool.dev_pool.id
  provider_name = "mersthamcc.co.uk"
  provider_type = "SAML"

  provider_details = {
    MetadataFile = file("../../infrastructure/config/google-idp-metadata.xml")
  }

  attribute_mapping = {
    email       = "email"
    family_name = "last_name"
    given_name  = "first_name"
  }
}

resource "aws_cognito_user_pool_client" "website" {
  name         = "website"
  user_pool_id = aws_cognito_user_pool.dev_pool.id

  allowed_oauth_flows = [
    "code",
  ]
  allowed_oauth_flows_user_pool_client = true

  allowed_oauth_scopes = [
    "email",
    "openid",
    "phone",
    "profile",
  ]

  supported_identity_providers = [
    "COGNITO",
    aws_cognito_identity_provider.saml_provider.provider_name
  ]

  callback_urls = [
    "http://localhost:8080/login/code",
  ]

  generate_secret = true
  explicit_auth_flows = [
#    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
#    "ALLOW_USER_SRP_AUTH",
    "ALLOW_ADMIN_USER_PASSWORD_AUTH",
  ]

  prevent_user_existence_errors = "LEGACY"
}

resource "aws_cognito_user_pool_client" "website_credentials" {
  name         = "website-client"
  user_pool_id = aws_cognito_user_pool.dev_pool.id

  allowed_oauth_flows = [
    "client_credentials",
  ]
  allowed_oauth_flows_user_pool_client = true

  allowed_oauth_scopes = aws_cognito_resource_server.graphql.scope_identifiers

  supported_identity_providers = [
    "COGNITO",
  ]

  generate_secret = true

  prevent_user_existence_errors = "LEGACY"
}
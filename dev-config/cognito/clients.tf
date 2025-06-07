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

resource "aws_cognito_user_pool_client" "jupyterhub" {
  name         = "jupyter"
  user_pool_id = aws_cognito_user_pool.dev_pool.id

  allowed_oauth_flows = [
    "code",
  ]
  allowed_oauth_flows_user_pool_client = true

  allowed_oauth_scopes = [
    "email",
    "openid",
    "phone",
    "profile"
  ]

  supported_identity_providers = [
    "COGNITO",
    aws_cognito_identity_provider.saml_provider.provider_name
  ]

  callback_urls = [
    "https://hub.dev.merstham.cricket/hub/oauth_callback",
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
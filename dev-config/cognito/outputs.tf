output "website_client_id" {
  value = aws_cognito_user_pool_client.website.id
}

output "website_client_secret" {
  value     = aws_cognito_user_pool_client.website.client_secret
  sensitive = true
}

output "website_credentials_client_id" {
  value = aws_cognito_user_pool_client.website_credentials.id
}

output "website_credentials_client_secret" {
  value     = aws_cognito_user_pool_client.website_credentials.client_secret
  sensitive = true
}

output "temporary_password" {
  value     = random_password.developer_password.result
  sensitive = true
}

output "jupyter_client_id" {
  value = aws_cognito_user_pool_client.jupyterhub.id
}

output "jupyter_client_secret" {
  value     = aws_cognito_user_pool_client.jupyterhub.client_secret
  sensitive = true
}

output "cognito_user_pool_id" {
  value = aws_cognito_user_pool.dev_pool.id
}

output "cognito_ui_uri" {
  value = aws_cognito_user_pool_domain.dev_pool_domain.domain
}

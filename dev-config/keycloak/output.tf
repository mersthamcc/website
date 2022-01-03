output "website_temporary_password" {
  value = keycloak_user.keycloak_user_dev.initial_password[0].value
}

output "website_client_secret" {
  value = keycloak_openid_client.website_client.client_secret
}
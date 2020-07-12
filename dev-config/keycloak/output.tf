output "website_temporary_password" {
  value = keycloak_user.keycloak_user_dev.initial_password[0].value
}

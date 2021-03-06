variable "keycloak_administrator_username" {
  default = "admin"
}

variable "keycloak_administrator_password" {
  description = "The Keycloak Administrator password"
}

variable "keycloak_url" {
  description = "The external URL for the Keycloack server"
}

variable "realm_name" {
  description = "The name of the realm to create in Keycloak"
}

variable "website_client_secret" {
  description = "The secret to use for any Keycloack clients created"
}

variable "keycloak_email_address" {
  description = "You e-mail address, this will be the username for logging into Keycloak from your app"
}

variable "keycloak_family_name" {
  description = "Your family name"
}

variable "keycloak_given_name" {
  description = "Your given name"
}

variable "base_url" {
  description = "The base URL for your application"
}

variable "graphql_base_url" {
  description = "The base URL for your GraphQL API application"
}

variable "clubname" {
  description = "The club name; ths will be display name for the realm"
}

variable "theme" {
  description = "The theme to use for the realm"
  default = "mcc"
}
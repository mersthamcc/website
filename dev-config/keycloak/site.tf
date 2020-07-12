provider "keycloak" {
  client_id = "admin-cli"
  username  = var.keycloack_administrator_username
  password  = var.keycloack_administrator_password
  url       = var.keycloak_url

  initial_login = false
}

resource "random_password" "user_password" {
  length      = 32
  min_lower   = 5
  min_upper   = 5
  min_numeric = 5
  min_special = 5
}
resource "keycloak_realm" "dev_realm" {
  realm        = var.realm_name
  enabled      = true
  display_name = var.realm_name

  login_theme   = "keycloak"
  account_theme = "keycloak"
  admin_theme   = "keycloak"
  email_theme   = "keycloak"

  reset_password_allowed   = true
  remember_me              = true
  login_with_email_allowed = true
  verify_email             = true

  access_code_lifespan = "3h"

  smtp_server {
    host = "smtp"
    from = "donotreply@${var.realm_name}"

    port     = 25
    starttls = false
  }
}

resource "keycloak_openid_client" "website_client" {
  realm_id    = keycloak_realm.dev_realm.id
  client_id   = "website"
  name        = "website"
  description = "website"
  enabled     = true

  access_type   = "CONFIDENTIAL"
  client_secret = var.website_client_secret

  standard_flow_enabled        = true
  implicit_flow_enabled        = false
  direct_access_grants_enabled = false
  service_accounts_enabled     = false

  valid_redirect_uris = [
    "${var.base_url}/login_check"
  ]

  web_origins = [
    var.base_url,
  ]

  depends_on = [keycloak_realm.dev_realm]
}

resource "keycloak_role" "test_role" {
  realm_id    = keycloak_realm.dev_realm.id
  name        = "test-role"
  description = "A test OIDC role"

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_openid_client.website_client,
  ]
}

resource "keycloak_group" "test_group" {
  realm_id = keycloak_realm.dev_realm.id
  name     = "test-role"

  depends_on = [keycloak_realm.dev_realm]
}

resource "keycloak_group_roles" "test_group_roles" {
  realm_id = keycloak_realm.dev_realm.id
  group_id = keycloak_group.test_group.id

  role_ids = [
    keycloak_role.test_role.id,
  ]

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_group.test_group,
    keycloak_role.test_role,
  ]
}

resource "keycloak_user" "keycloak_user_dev" {
  realm_id = keycloak_realm.dev_realm.id
  username = var.keycloak_email_address
  enabled  = true

  email      = var.keycloak_email_address
  first_name = var.keycloak_given_name
  last_name  = var.keycloak_family_name

  initial_password {
    value     = random_password.user_password.result
    temporary = true
  }

  depends_on = [keycloak_realm.dev_realm]
}

resource "keycloak_group_memberships" "concourse_admin_group_members" {
  realm_id = keycloak_realm.dev_realm.id
  group_id = keycloak_group.test_group.id

  members = [
    keycloak_user.keycloak_user_dev.username,
  ]

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_group.test_group,
    keycloak_user.keycloak_user_dev,
  ]
}

resource "keycloak_openid_user_realm_role_protocol_mapper" "concourse_realm_role_mapper" {
  realm_id  = keycloak_realm.dev_realm.id
  client_id = keycloak_openid_client.website_client.id
  name      = "website-role-mapper"

  claim_name  = "roles"
  multivalued = true

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_openid_client.website_client,
  ]
}

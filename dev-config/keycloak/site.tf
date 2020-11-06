terraform {
  required_providers {
    keycloak = {
      source  = "mrparkers/keycloak"
      version = "2.0.0"
    }
  }
}

provider "keycloak" {
  client_id = "admin-cli"
  username  = var.keycloak_administrator_username
  password  = var.keycloak_administrator_password
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
  display_name = var.clubname

  login_theme   = var.theme
  account_theme = var.theme
  admin_theme   = var.theme
  email_theme   = var.theme

  registration_allowed           = true
  registration_email_as_username = true
  edit_username_allowed          = true
  reset_password_allowed         = true
  remember_me                    = true
  login_with_email_allowed       = true
  verify_email                   = true
  duplicate_emails_allowed       = false
  ssl_required                   = "all"

  access_code_lifespan = "3h"

  smtp_server {
    host = "smtp"
    from = "donotreply@${var.realm_name}"

    port     = 25
    starttls = false
  }
}

resource "keycloak_role" "mfa_role" {
  realm_id    = keycloak_realm.dev_realm.id
  name        = "ROLE_MEMBERSHIP"
  description = "Users with this role have access to the Membership Database and require 2FA"

  depends_on = [
    keycloak_realm.dev_realm,
  ]
}

resource "keycloak_group" "mfa_group" {
  realm_id = keycloak_realm.dev_realm.id
  name     = "ROLE_MEMBERSHIP"

  depends_on = [keycloak_realm.dev_realm]
}

resource "keycloak_group_roles" "mfa_group_roles" {
  realm_id = keycloak_realm.dev_realm.id
  group_id = keycloak_group.mfa_group.id

  role_ids = [
    keycloak_role.mfa_role.id,
  ]

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_group.mfa_group,
    keycloak_role.mfa_role,
  ]
}

resource "keycloak_authentication_flow" "browser_mfa_flow" {
  realm_id = keycloak_realm.dev_realm.id
  alias    = "browser-mfa-flow"

  depends_on = [
    keycloak_realm.dev_realm
  ]
}

resource "keycloak_authentication_execution" "cookies_execution" {
  realm_id          = keycloak_realm.dev_realm.id
  parent_flow_alias = keycloak_authentication_flow.browser_mfa_flow.alias
  authenticator     = "auth-cookie"
  requirement       = "ALTERNATIVE"

  depends_on = [
    keycloak_authentication_flow.browser_mfa_flow
  ]
}

resource "keycloak_authentication_execution" "idp_execution" {
  realm_id          = keycloak_realm.dev_realm.id
  parent_flow_alias = keycloak_authentication_flow.browser_mfa_flow.alias
  authenticator     = "identity-provider-redirector"
  requirement       = "ALTERNATIVE"

  depends_on = [
    keycloak_authentication_flow.browser_mfa_flow,
    keycloak_authentication_execution.cookies_execution,
  ]
}

resource "keycloak_authentication_subflow" "otp_browser_flow" {
  realm_id          = keycloak_realm.dev_realm.id
  parent_flow_alias = keycloak_authentication_flow.browser_mfa_flow.alias
  alias             = "otp-browser-flow"
  requirement       = "ALTERNATIVE"

  depends_on = [
    keycloak_authentication_flow.browser_mfa_flow,
    keycloak_authentication_execution.idp_execution,
  ]
}


resource "keycloak_authentication_execution" "login_form_execution" {
  realm_id          = keycloak_realm.dev_realm.id
  parent_flow_alias = keycloak_authentication_subflow.otp_browser_flow.alias
  authenticator     = "auth-username-password-form"
  requirement       = "REQUIRED"

  depends_on = [
    keycloak_authentication_flow.browser_mfa_flow,
    keycloak_authentication_subflow.otp_browser_flow,
  ]
}

resource "keycloak_authentication_execution" "conditional_otp_execution" {
  realm_id          = keycloak_realm.dev_realm.id
  parent_flow_alias = keycloak_authentication_subflow.otp_browser_flow.alias
  authenticator     = "auth-conditional-otp-form"
  requirement       = "REQUIRED"

  depends_on = [
    keycloak_authentication_flow.browser_mfa_flow,
    keycloak_authentication_subflow.otp_browser_flow,
    keycloak_authentication_execution.login_form_execution,
  ]
}

resource "keycloak_authentication_execution_config" "conditional_otp_config" {
  realm_id     = keycloak_realm.dev_realm.id
  execution_id = keycloak_authentication_execution.conditional_otp_execution.id
  alias        = "MFA for ROLE_MEMBERSHIP"
  config = {
    forceOtpRole      = "ROLE_MEMBERSHIP",
    defaultOtpOutcome = "skip"
  }

  depends_on = [
    keycloak_authentication_flow.browser_mfa_flow,
    keycloak_authentication_execution.conditional_otp_execution,
  ]
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

  authentication_flow_binding_overrides {
    browser_id = keycloak_authentication_flow.browser_mfa_flow.id
  }

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
  realm_id       = keycloak_realm.dev_realm.id
  username       = var.keycloak_email_address
  enabled        = true
  email_verified = true

  email      = var.keycloak_email_address
  first_name = var.keycloak_given_name
  last_name  = var.keycloak_family_name

  initial_password {
    value     = random_password.user_password.result
    temporary = true
  }

  depends_on = [keycloak_realm.dev_realm]
}

resource "keycloak_group_memberships" "mfa_group_members" {
  realm_id = keycloak_realm.dev_realm.id
  group_id = keycloak_group.mfa_group.id

  members = [
    keycloak_user.keycloak_user_dev.username,
  ]

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_group.mfa_group,
    keycloak_user.keycloak_user_dev,
  ]
}

resource "keycloak_group_memberships" "test_group_members" {
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

resource "keycloak_openid_user_realm_role_protocol_mapper" "dev_realm_role_mapper" {
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


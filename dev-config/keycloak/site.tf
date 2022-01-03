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
  ssl_required                   = "none"

  sso_session_idle_timeout = "6h"
//  sso_session_idle_timeout_remember_me = "12w"
  access_token_lifespan = "24h"
  access_code_lifespan = "1m"


  smtp_server {
    host = "smtp"
    from = "donotreply@${var.realm_name}"

    port     = 25
    starttls = false
  }
}

resource "keycloak_role" "role_admin" {
  realm_id    = keycloak_realm.dev_realm.id
  name        = "ROLE_ADMIN"
  description = "Users with this role have access to the admin area"

  depends_on = [
    keycloak_realm.dev_realm,
  ]
}

resource "keycloak_role" "role_membership" {
  realm_id    = keycloak_realm.dev_realm.id
  name        = "ROLE_MEMBERSHIP"
  description = "Users with this role have access to the Membership Database and require 2FA"

  depends_on = [
    keycloak_realm.dev_realm,
  ]
}

resource "keycloak_role" "role_news" {
  realm_id    = keycloak_realm.dev_realm.id
  name        = "ROLE_NEWS"
  description = "Users with this role to manage new stories"

  depends_on = [
    keycloak_realm.dev_realm,
  ]
}

resource "keycloak_role" "role_trusted_application" {
  realm_id    = keycloak_realm.dev_realm.id
  name        = "TRUSTED_APPLICATION"
  description = "Role assigned to trusted clients to perform sensitive operations"

  depends_on = [
    keycloak_realm.dev_realm,
  ]
}


resource "keycloak_group" "role_membership_group" {
  realm_id = keycloak_realm.dev_realm.id
  name     = "ROLE_MEMBERSHIP"

  depends_on = [keycloak_realm.dev_realm]
}

resource "keycloak_group_roles" "role_membership_group_roles" {
  realm_id = keycloak_realm.dev_realm.id
  group_id = keycloak_group.role_membership_group.id

  role_ids = [
    keycloak_role.role_admin.id,
    keycloak_role.role_membership.id,
  ]

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_group.role_membership_group,
    keycloak_role.role_admin,
    keycloak_role.role_membership,
  ]
}

resource "keycloak_group" "role_news_group" {
  realm_id = keycloak_realm.dev_realm.id
  name     = "ROLE_NEWS"

  depends_on = [keycloak_realm.dev_realm]
}

resource "keycloak_group_roles" "role_news_group_roles" {
  realm_id = keycloak_realm.dev_realm.id
  group_id = keycloak_group.role_news_group.id

  role_ids = [
    keycloak_role.role_admin.id,
    keycloak_role.role_news.id,
  ]

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_group.role_news_group,
    keycloak_role.role_admin,
    keycloak_role.role_news,
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
    keycloak_realm.dev_realm,
    keycloak_authentication_flow.browser_mfa_flow
  ]
}

resource "keycloak_authentication_execution" "idp_execution" {
  realm_id          = keycloak_realm.dev_realm.id
  parent_flow_alias = keycloak_authentication_flow.browser_mfa_flow.alias
  authenticator     = "identity-provider-redirector"
  requirement       = "ALTERNATIVE"

  depends_on = [
    keycloak_realm.dev_realm,
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
    keycloak_realm.dev_realm,
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
    keycloak_realm.dev_realm,
    keycloak_authentication_flow.browser_mfa_flow,
    keycloak_authentication_subflow.otp_browser_flow,
  ]
}

resource "keycloak_authentication_execution" "conditional_otp_execution" {
  realm_id          = keycloak_realm.dev_realm.id
  parent_flow_alias = keycloak_authentication_subflow.otp_browser_flow.alias
  authenticator     = "mcc-two-factor-authentication"
  requirement       = "REQUIRED"

  depends_on = [
    keycloak_realm.dev_realm,
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
  }

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_authentication_flow.browser_mfa_flow,
    keycloak_authentication_execution.conditional_otp_execution,
  ]
}

resource "keycloak_required_action" "required_action" {
  realm_id		= keycloak_realm.dev_realm.id
  alias			= "mcc-configure-otp-sms"
  name			= "Configure SMS for OTP"
  priority		= 500

  default_action 	= false
  enabled			= true
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
  service_accounts_enabled     = true

  authentication_flow_binding_overrides {
    browser_id = keycloak_authentication_flow.browser_mfa_flow.id
  }

  valid_redirect_uris = [
    "${var.base_url}/sso/login",
    "http://localhost:8080/sso/login"
  ]

  web_origins = [
    var.base_url,
  ]

  depends_on = [keycloak_realm.dev_realm]
}

resource "keycloak_openid_client" "graphql_client" {
  realm_id    = keycloak_realm.dev_realm.id
  client_id   = "graphql"
  name        = "graphql"
  description = "graphql"
  enabled     = true

  access_type   = "CONFIDENTIAL"
  client_secret = var.website_client_secret

  standard_flow_enabled        = true
  implicit_flow_enabled        = false
  direct_access_grants_enabled = false
  service_accounts_enabled     = false

  valid_redirect_uris = [
    "${var.graphql_base_url}/graphql"
  ]

  web_origins = [
    var.graphql_base_url,
  ]

  depends_on = [keycloak_realm.dev_realm]
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

resource "keycloak_group_memberships" "role_membership_members" {
  realm_id = keycloak_realm.dev_realm.id
  group_id = keycloak_group.role_membership_group.id

  members = [
    keycloak_user.keycloak_user_dev.username,
  ]

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_group.role_membership_group,
    keycloak_user.keycloak_user_dev,
  ]
}

resource "keycloak_group_memberships" "role_news_members" {
  realm_id = keycloak_realm.dev_realm.id
  group_id = keycloak_group.role_news_group.id

  members = [
    keycloak_user.keycloak_user_dev.username,
  ]

  depends_on = [
    keycloak_realm.dev_realm,
    keycloak_group.role_membership_group,
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

resource "keycloak_openid_client_service_account_realm_role" "website_realm_roles" {
  realm_id 				  = keycloak_realm.dev_realm.id
  service_account_user_id = keycloak_openid_client.website_client.service_account_user_id
  role 					  = keycloak_role.role_trusted_application.name
}

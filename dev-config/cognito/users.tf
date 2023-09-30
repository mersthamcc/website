resource "random_password" "developer_password" {
  length = 12

  min_lower   = 1
  min_numeric = 1
  min_upper   = 1
  min_special = 1
}

resource "aws_cognito_user" "developer" {
  user_pool_id       = aws_cognito_user_pool.dev_pool.id
  username           = var.developer_email
  temporary_password = random_password.developer_password.result
  enabled            = true

  desired_delivery_mediums = ["EMAIL"]

  attributes = {
    "email"                 = var.developer_email
    "email_verified"        = true
    "phone_number"          = var.developer_phone
    "phone_number_verified" = true
    "preferred_username"    = var.developer_email
    "given_name"            = var.developer_given_name
    "family_name"           = var.developer_family_name
  }
}

resource "aws_cognito_user_group" "admin" {
  name         = "ADMIN"
  user_pool_id = aws_cognito_user_pool.dev_pool.id
}

resource "aws_cognito_user_in_group" "developer_in_admin" {
  user_pool_id = aws_cognito_user_pool.dev_pool.id
  group_name   = aws_cognito_user_group.admin.name
  username     = aws_cognito_user.developer.username
}

resource "aws_cognito_user_group" "membership" {
  name         = "MEMBERSHIP"
  user_pool_id = aws_cognito_user_pool.dev_pool.id
}

resource "aws_cognito_user_in_group" "developer_in_membership" {
  user_pool_id = aws_cognito_user_pool.dev_pool.id
  group_name   = aws_cognito_user_group.membership.name
  username     = aws_cognito_user.developer.username
}

resource "aws_cognito_user_group" "news" {
  name         = "NEWS"
  user_pool_id = aws_cognito_user_pool.dev_pool.id
}

resource "aws_cognito_user_in_group" "developer_in_news" {
  user_pool_id = aws_cognito_user_pool.dev_pool.id
  group_name   = aws_cognito_user_group.news.name
  username     = aws_cognito_user.developer.username
}

resource "aws_cognito_user_group" "events" {
  name         = "EVENTS"
  user_pool_id = aws_cognito_user_pool.dev_pool.id
}

resource "aws_cognito_user_in_group" "developer_in_events" {
  user_pool_id = aws_cognito_user_pool.dev_pool.id
  group_name   = aws_cognito_user_group.events.name
  username     = aws_cognito_user.developer.username
}

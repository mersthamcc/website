resource "aws_dynamodb_table" "migrated_users" {
  name         = "${var.developer_name}-migrated-user"
  billing_mode = "PAY_PER_REQUEST"

  hash_key = "email"

  attribute {
    name = "email"
    type = "S"
  }
}
resource "vault_kv_secret_v2" "twitter-credentials" {
  mount = "secret"
  name  = "twitter-credentials"
  data_json = jsonencode({
    client_id     = var.twitter_client_id
    client_secret = var.twitter_client_secret
  })
}

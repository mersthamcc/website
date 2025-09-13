resource "vault_mount" "secrets" {
  path        = "secrets"
  type        = "kv"
  options     = { version = "2" }
  description = "KV Version 2 secret engine mount"
}

resource "vault_kv_secret_v2" "twitter-credentials" {
  mount = "secret"
  name  = "twitter-credentials"
  data_json = jsonencode({
    client_id     = var.twitter_client_id
    client_secret = var.twitter_client_secret
  })
}

resource "vault_kv_secret_v2" "cloud-creds" {
  mount     = vault_mount.secrets.path
  name      = "test"
  data_json = jsonencode({
    apple_signing_key = file("/keys/test.apple.pass.key")
    apple_signing_cert = file("/certs/test.apple.pass.pem")
    apple_intermediates = file("/certs/AppleWWDRCAG4.pem")
    google_credentials = file("/keys/google-credentials.json")
  })
}
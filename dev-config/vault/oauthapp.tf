resource "vault_generic_secret" "twitter-oauth" {
  data_json = jsonencode({
    client_id = var.twitter_client_id
    client_secret = var.twitter_client_secret

    provider  = "custom"
    provider_options = {
      auth_code_url = "https://twitter.com/i/oauth2/authorize"
      token_url = "https://api.x.com/2/oauth2/token"
    }
  })
  path      = "${vault_mount.oauthapp.path}/servers/twitter"

  depends_on = [
    vault_mount.oauthapp
  ]
}
resource "vault_plugin" "oauthapp" {
  type    = "secret"
  name    = "oauthapp"
  command = "oauthapp-v3.1.1"
  version = "v3.1.1"
  sha256  = filesha256("/vault/plugins/oauthapp-v3.1.1")
}

resource "vault_plugin_pinned_version" "oauthapp" {
  type    = vault_plugin.oauthapp.type
  name    = vault_plugin.oauthapp.name
  version = vault_plugin.oauthapp.version
}

resource "vault_mount" "oauthapp" {
  type = vault_plugin_pinned_version.oauthapp.name
  path = vault_plugin.oauthapp.name
}

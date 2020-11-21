package uk.co.mersthamcc.keycloak.credentials;

import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.CredentialTypeMetadata;
import org.keycloak.credential.CredentialTypeMetadataContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class TwoFactorSmsCredentialsProvider implements CredentialProvider {

    @Override
    public String getType() {
        return null;
    }

    @Override
    public CredentialModel createCredential(RealmModel realm, UserModel user, CredentialModel credentialModel) {
        return null;
    }

    @Override
    public boolean deleteCredential(RealmModel realm, UserModel user, String credentialId) {
        return false;
    }

    @Override
    public CredentialModel getCredentialFromModel(CredentialModel model) {
        return null;
    }

    @Override
    public CredentialTypeMetadata getCredentialTypeMetadata(CredentialTypeMetadataContext metadataContext) {
        return null;
    }
}

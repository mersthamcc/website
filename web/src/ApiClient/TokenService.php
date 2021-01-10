<?php

namespace App\ApiClient;

use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use League\OAuth2\Client\Token\AccessToken;
use Symfony\Component\HttpFoundation\Session\SessionInterface;

class TokenService
{
    /**
     * @var $session SessionInterface
     */
    private $session;

    /**
     * @var $cachedToken AccessToken
     */
    private $cachedToken = null;

    /**
     * @var $clientRegistry ClientRegistry
     */
    private $clientRegistry;

    public function __construct(
        \Symfony\Component\HttpFoundation\Session\SessionInterface $session,
        \KnpU\OAuth2ClientBundle\Client\ClientRegistry $clientRegistry
    ) {
        $this->session = $session;
        $this->clientRegistry = $clientRegistry;
    }

    public function getAccessToken(bool $trustedToken): AccessToken
    {
        $accessToken = $this->getServiceAccountToken();
        if (!$trustedToken) {
            $userToken = $this->session->get("access_token", $accessToken);
            if ($userToken != null) {
                if ($userToken->hasExpired()) {
                    $userToken = $this->refreshToken($userToken);
                    $this->session->set("access_token", $userToken);
                }
                return $userToken;
            }
        }
        return $accessToken;
    }

    public function getServiceAccountToken(): AccessToken
    {
        if ($this->cachedToken == null || $this->cachedToken->hasExpired()) {
            $this->cachedToken = $this->clientRegistry
                ->getClient("keycloak")
                ->getOAuth2Provider()
                ->getAccessToken("client_credentials");
        }
        return $this->cachedToken;
    }

    public function refreshToken(AccessToken $accessToken): AccessToken
    {
        return $this->clientRegistry
            ->getClient("keycloak")
            ->refreshAccessToken($accessToken);
    }
}

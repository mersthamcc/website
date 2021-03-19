<?php

namespace App\ApiClient;

use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use League\OAuth2\Client\Token\AccessToken;
use Psr\Log\LoggerInterface;
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

    /**
     * @var $logger LoggerInterface
     */
    private $logger;

    public function __construct(
        SessionInterface $session,
        ClientRegistry $clientRegistry,
        LoggerInterface $logger
    ) {
        $this->session = $session;
        $this->clientRegistry = $clientRegistry;
        $this->logger = $logger;
    }

    public function getAccessToken(bool $trustedToken): AccessToken
    {
        $accessToken = $this->getServiceAccountToken();
        if (!$trustedToken) {
            $this->logger->debug("Using user token");
            $userToken = $this->session->get("access_token");
            if ($userToken != null) {
                if ($userToken->hasExpired()) {
                    $userToken = $this->refreshToken($userToken);
                    $this->session->set("access_token", $userToken);
                }
                $this->logger->debug("Got access token from session", [
                    "access_token" => $userToken,
                ]);
                return $userToken;
            }
            $this->logger->debug("User token not found!");
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

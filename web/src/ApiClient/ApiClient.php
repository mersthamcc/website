<?php

namespace App\ApiClient;

use GraphQL\Client;
use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use League\OAuth2\Client\Grant\ClientCredentials;
use League\OAuth2\Client\Token\AccessToken;
use Symfony\Component\HttpFoundation\Session\SessionInterface;

class ApiClient
{
    /**
     * @var $tokenService TokenService
     */
    private $tokenService;

    /**
     * @var $apiBaseUrl string
     */
    private $apiBaseUrl;

    public function __construct(TokenService $tokenService, string $apiBaseUrl)
    {
        $this->tokenService = $tokenService;
        $this->apiBaseUrl = $apiBaseUrl;
    }

    public function getClient(bool $trustedToken = false): Client
    {
        $accessToken = $this->tokenService->getAccessToken($trustedToken);
        return new Client($this->apiBaseUrl, [
            "Authorization" => "Bearer " . $accessToken->getToken(),
        ]);
    }
}

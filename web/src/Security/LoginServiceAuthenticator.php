<?php

namespace App\Security;

use App\ApiClient\UserService;
use App\Entity\User;
use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use KnpU\OAuth2ClientBundle\Client\Provider\KeycloakClient;
use KnpU\OAuth2ClientBundle\Security\Authenticator\SocialAuthenticator;
use League\OAuth2\Client\Token\AccessToken;
use Psr\Log\LoggerInterface;
use Stevenmaguire\OAuth2\Client\Provider\KeycloakResourceOwner;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Routing\RouterInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\Exception\AuthenticationServiceException;
use Symfony\Component\Security\Core\Exception\UsernameNotFoundException;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\User\UserProviderInterface;

class LoginServiceAuthenticator extends SocialAuthenticator
{
    private $clientRegistry;
    private $router;
    private $logger;
    private $userService;
    private $session;

    public function __construct(
        ClientRegistry $clientRegistry,
        RouterInterface $router,
        LoggerInterface $logger,
        UserService $userService,
        SessionInterface $session
    ) {
        $this->clientRegistry = $clientRegistry;
        $this->router = $router;
        $this->logger = $logger;
        $this->userService = $userService;
        $this->session = $session;
    }

    public function supports(Request $request): bool
    {
        // continue ONLY if the current ROUTE matches the check ROUTE
        return $request->attributes->get("_route") === "login_check";
    }

    public function getCredentials(Request $request): AccessToken
    {
        return $this->fetchAccessToken($this->getKeycloakClient());
    }

    public function getUser(
        $credentials,
        UserProviderInterface $userProvider
    ): User {
        if (!$userProvider instanceof ApiUserProvider) {
            throw new AuthenticationServiceException("Invalid user provider!");
        }

        $client = $this->getKeycloakClient();
        $keycloakUser = $client->fetchUserFromToken($credentials);
        $id = $keycloakUser->getId();
        $this->logger->debug("Access token = $credentials");
        $this->logger->debug("Keycloak User = $id", $keycloakUser->toArray());

        $user = $this->matchOrCreateUser($keycloakUser, $userProvider);
        $this->logger->debug("Returning User", [
            "user" => $user,
        ]);
        if ($user == null) {
            $this->logger->error("Could not create user", [
                "keycloakUser" => $keycloakUser,
            ]);
            throw new AuthenticationServiceException("Could not create user");
        }
        $user = $this->updateCachedDetails($user, $keycloakUser);
        $this->session->set("access_token", $credentials);
        return $user;
    }

    /**
     * @return KeycloakClient
     */
    private function getKeycloakClient(): KeycloakClient
    {
        return $this->clientRegistry->getClient("keycloak");
    }

    public function onAuthenticationSuccess(
        Request $request,
        TokenInterface $token,
        $providerKey
    ): RedirectResponse {
        $targetUrl = $this->router->generate("home");

        return new RedirectResponse($targetUrl);
    }

    public function onAuthenticationFailure(
        Request $request,
        AuthenticationException $exception
    ): Response {
        $message = strtr(
            $exception->getMessageKey(),
            $exception->getMessageData()
        );

        return new Response($message, Response::HTTP_FORBIDDEN);
    }

    /**
     * Called when authentication is needed, but it's not sent.
     * This redirects to the 'login'.
     * @param Request $request
     * @param AuthenticationException|null $authException
     * @return RedirectResponse
     */
    public function start(
        Request $request,
        AuthenticationException $authException = null
    ): RedirectResponse {
        return new RedirectResponse(
            "/login", // might be the site, where users choose their oauth provider
            Response::HTTP_TEMPORARY_REDIRECT
        );
    }

    /**
     * @param KeycloakResourceOwner $keycloakUser
     * @param UserProviderInterface $userProvider
     * @return User
     */
    private function matchOrCreateUser(
        KeycloakResourceOwner $keycloakUser,
        UserProviderInterface $userProvider
    ): UserInterface {
        $externalId = $keycloakUser->getId();
        $this->logger->debug("Checking for user $externalId");
        try {
            return $userProvider->loadUserByUsername($externalId);
        } catch (UsernameNotFoundException $exception) {
            $this->logger->debug(
                "User $externalId not found, creating instead!"
            );
            $attributes = $keycloakUser->toArray();
            return $this->userService->createUser(
                $keycloakUser->getId(),
                $keycloakUser->getEmail(),
                $attributes["family_name"],
                $attributes["given_name"]
            );
        }
    }

    private function updateCachedDetails(
        User $user,
        KeycloakResourceOwner $keycloakUser
    ): User {
        $attributes = $keycloakUser->toArray();
        return $this->userService->updateUserDetails(
            $user->getId(),
            $attributes["roles"],
            $attributes["family_name"],
            $attributes["given_name"],
            $keycloakUser->getEmail()
        );
    }
}

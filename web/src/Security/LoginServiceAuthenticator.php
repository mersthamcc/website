<?php

namespace App\Security;

use App\Entity\User;
use Doctrine\ORM\EntityManagerInterface;
use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use KnpU\OAuth2ClientBundle\Client\Provider\KeycloakClient;
use KnpU\OAuth2ClientBundle\Security\Authenticator\SocialAuthenticator;
use Psr\Log\LoggerInterface;
use Stevenmaguire\OAuth2\Client\Provider\KeycloakResourceOwner;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\RouterInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\User\UserProviderInterface;

class LoginServiceAuthenticator extends SocialAuthenticator
{
    private $clientRegistry;
    private $em;
    private $router;
    private $logger;

    public function __construct(ClientRegistry $clientRegistry, EntityManagerInterface $em, RouterInterface $router, LoggerInterface $logger)
    {
        $this->clientRegistry = $clientRegistry;
        $this->em = $em;
        $this->router = $router;
        $this->logger = $logger;
    }

    public function supports(Request $request)
    {
        // continue ONLY if the current ROUTE matches the check ROUTE
        return $request->attributes->get('_route') === 'login_check';
    }

    public function getCredentials(Request $request)
    {
        return $this->fetchAccessToken($this->getKeycloakClient());
    }

    public function getUser($credentials, UserProviderInterface $userProvider)
    {
        /**
         * @var KeycloakResourceOwner $keycloakUser
         */
        $keycloakUser = $this->getKeycloakClient()
            ->fetchUserFromToken($credentials);
        $id = $keycloakUser->getId();
        $this->logger->debug("Access token = $credentials");
        $this->logger->debug("Keycloak User = $id", $keycloakUser->toArray());

        $user = $this->matchOrCreateUser($keycloakUser);
        $this->em->persist($this->updateCachedDetails($user, $keycloakUser));
        $this->em->flush();

        return $user;
    }

    /**
     * @return KeycloakClient
     */
    private function getKeycloakClient()
    {
        return $this->clientRegistry
            ->getClient('keycloak');
    }

    public function onAuthenticationSuccess(Request $request, TokenInterface $token, $providerKey)
    {
        $targetUrl = $this->router->generate('home');

        return new RedirectResponse($targetUrl);
    }

    public function onAuthenticationFailure(Request $request, AuthenticationException $exception)
    {
        $message = strtr($exception->getMessageKey(), $exception->getMessageData());

        return new Response($message, Response::HTTP_FORBIDDEN);
    }

    /**
     * Called when authentication is needed, but it's not sent.
     * This redirects to the 'login'.
     * @param Request $request
     * @param AuthenticationException|null $authException
     * @return RedirectResponse
     */
    public function start(Request $request, AuthenticationException $authException = null)
    {
        return new RedirectResponse(
            '/login', // might be the site, where users choose their oauth provider
            Response::HTTP_TEMPORARY_REDIRECT
        );
    }

    /**
     * @param KeycloakResourceOwner $keycloakUser
     * @return User
     */
    private function matchOrCreateUser(KeycloakResourceOwner $keycloakUser) {
        $externalId = $keycloakUser->getId();
        $existingUser = $this->em->getRepository(User::class)
            ->findOneBy(['external_id' => $externalId]);
        if ($existingUser) {
            return $existingUser;
        }

        $email = $keycloakUser
            ->getEmail();
        $existingUser = $this->em->getRepository(User::class)
            ->findOneBy(['email' => $email]);
        if ($existingUser) {
            return $existingUser;
        }

        $user = new User();
        return $user->setExternalId($externalId)->setEmail($email);
    }

    private function updateCachedDetails(User $user, KeycloakResourceOwner $keycloakUser) {
        $attributes = $keycloakUser->toArray();
        return $user->setEmail($keycloakUser->getEmail())->setFamilyName($attributes['family_name'])->setGivenName($attributes['given_name']);
    }
}

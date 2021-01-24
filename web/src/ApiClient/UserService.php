<?php

namespace App\ApiClient;

use App\Entity\User;
use Psr\Log\LoggerInterface;

class UserService
{
    /**
     * @var ApiClient
     */
    private $client;

    /**
     * @var LoggerInterface
     */
    private $logger;

    public function __construct(ApiClient $client, LoggerInterface $logger)
    {
        $this->client = $client;
        $this->logger = $logger;
    }

    public function getUserByEmailAddress(string $emailAddress)
    {
        $this->logger->debug("Looking up user $emailAddress");
        return $this->client->userByEmail(User::class, [
            "emailAddress" => $emailAddress,
        ]);
    }

    public function getUserByExternalId(string $externalId)
    {
        $this->logger->debug("Looking up user $externalId");
        return $this->client->userByExternalId(User::class, [
            "externalId" => $externalId,
        ]);
    }

    public function createUser(
        string $externalId,
        string $emailAddress,
        string $familyName,
        string $givenName
    ) {
        $this->logger->debug("Creating user $emailAddress");
        return $this->client->signupUser(User::class, [
            "externalId" => $externalId,
            "emailAddress" => $emailAddress,
            "familyName" => $familyName,
            "givenName" => $givenName,
        ]);
    }

    public function updateUserDetails(User $user): User
    {
        return $this->client->updateUserDetails(User::class, [
            "id" => $user->getId(),
            "roles" => $user->getRoles(),
            "familyName" => $user->getFamilyName(),
            "givenName" => $user->getGivenName(),
            "email" => $user->getEmail(),
        ]);
    }
}

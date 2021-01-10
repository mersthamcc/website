<?php

namespace App\ApiClient;

use App\Entity\User;

class UserService extends BaseService
{
    public function getUserByEmailAddress(string $emailAddress)
    {
        $this->logger->debug("Looking up user $emailAddress");

        $gql = $this->loadGraphQL("userByEmail.graphql");

        $result = $this->client->getClient(true)->runRawQuery($gql, false, [
            "emailAddress" => $emailAddress,
        ]);
        return $this->deserializeResult($result, "userByEmail", User::class);
    }

    public function getUserByExternalId(string $externalId)
    {
        $this->logger->debug("Looking up user $externalId");

        $gql = $this->loadGraphQL("userByExternalId.graphql");

        $result = $this->client->getClient(true)->runRawQuery($gql, false, [
            "externalId" => $externalId,
        ]);
        return $this->deserializeResult(
            $result,
            "userByExternalId",
            User::class
        );
    }

    public function createUser(
        string $externalId,
        string $emailAddress,
        string $familyName,
        string $givenName
    ) {
        $this->logger->debug("Creating user $emailAddress");

        $gql = $this->loadGraphQL("signupUser.graphql");
        $result = $this->client->getClient(true)->runRawQuery($gql, false, [
            "externalId" => $externalId,
            "emailAddress" => $emailAddress,
            "familyName" => $familyName,
            "givenName" => $givenName,
        ]);
        return $this->deserializeResult($result, "signupUser", User::class);
    }

    public function updateUserDetails(User $user): User
    {
        $gql = $this->loadGraphQL("updateUserDetails.graphql");

        $result = $this->client->getClient(true)->runRawQuery($gql, false, [
            "id" => $user,
            "roles" => $user->getRoles(),
            "familyName" => $user->getFamilyName(),
            "givenName" => $user->getGivenName(),
            "email" => $user->getEmail(),
        ]);
        return $this->deserializeResult(
            $result,
            "updateUserDetails",
            User::class
        );
    }
}

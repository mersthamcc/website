<?php

namespace App\ApiClient;

use App\Entity\MemberCategory;
use App\Entity\User;

class MethodResponse
{
    /**
     * @var User
     */
    private $me;

    /**
     * @var MemberCategory[]
     */
    private $membershipCategories;

    /**
     * @var User|null
     */
    private $signupUser;

    /**
     * @var User|null
     */
    private $updateUserDetails;

    /**
     * @var User|null
     */
    private $userByEmail;

    /**
     * @var User|null
     */
    private $userByExternalId;

    /**
     * @param string $method
     * @return mixed
     */
    public function get(string $method)
    {
        return $this->{$method};
    }

    /**
     * @return User
     */
    public function getMe(): User
    {
        return $this->me;
    }

    /**
     * @param User $me
     * @return MethodResponse
     */
    public function setMe(User $me): MethodResponse
    {
        $this->me = $me;
        return $this;
    }

    /**
     * @return MemberCategory[]
     */
    public function getMembershipCategories(): array
    {
        return $this->membershipCategories;
    }

    /**
     * @param MemberCategory[] $membershipCategories
     * @return $this
     */
    public function setMembershipCategories(
        array $membershipCategories
    ): MethodResponse {
        $this->membershipCategories = $membershipCategories;
        return $this;
    }

    /**
     * @return User|null
     */
    public function getSignupUser(): ?User
    {
        return $this->signupUser;
    }

    /**
     * @param User|null $signupUser
     * @return MethodResponse
     */
    public function setSignupUser(?User $signupUser): MethodResponse
    {
        $this->signupUser = $signupUser;
        return $this;
    }

    /**
     * @return User|null
     */
    public function getUpdateUserDetails(): ?User
    {
        return $this->updateUserDetails;
    }

    /**
     * @param User|null $updateUserDetails
     * @return MethodResponse
     */
    public function setUpdateUserDetails(
        ?User $updateUserDetails
    ): MethodResponse {
        $this->updateUserDetails = $updateUserDetails;
        return $this;
    }

    /**
     * @return User|null
     */
    public function getUserByEmail(): ?User
    {
        return $this->userByEmail;
    }

    /**
     * @param User|null $userByEmail
     * @return MethodResponse
     */
    public function setUserByEmail(?User $userByEmail): MethodResponse
    {
        $this->userByEmail = $userByEmail;
        return $this;
    }

    /**
     * @return User|null
     */
    public function getUserByExternalId(): ?User
    {
        return $this->userByExternalId;
    }

    /**
     * @param User|null $userByExternalId
     * @return MethodResponse
     */
    public function setUserByExternalId(?User $userByExternalId): MethodResponse
    {
        $this->userByExternalId = $userByExternalId;
        return $this;
    }
}

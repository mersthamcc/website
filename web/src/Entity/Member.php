<?php

namespace App\Entity;

class Member
{
    /**
     * @var $id int
     */
    private $id;

    /**
     * @var $ownerId int
     */
    private $ownerId;

    /**
     * @var $givenName string
     */
    private $givenName;

    /**
     * @var $familyName string
     */
    private $familyName;

    /**
     * @var $email string
     */
    private $email;

    /**
     * @var $attributes MemberAttribute[]
     */
    private $attributes;

    /**
     * @return int
     */
    public function getId(): int
    {
        return $this->id;
    }

    /**
     * @param int $id
     * @return Member
     */
    public function setId(int $id): Member
    {
        $this->id = $id;
        return $this;
    }

    /**
     * @return int
     */
    public function getOwnerId(): int
    {
        return $this->ownerId;
    }

    /**
     * @param int $ownerId
     * @return Member
     */
    public function setOwnerId(int $ownerId): Member
    {
        $this->ownerId = $ownerId;
        return $this;
    }

    /**
     * @return string
     */
    public function getGivenName(): string
    {
        return $this->givenName;
    }

    /**
     * @param string $givenName
     * @return Member
     */
    public function setGivenName(string $givenName): Member
    {
        $this->givenName = $givenName;
        return $this;
    }

    /**
     * @return string
     */
    public function getFamilyName(): string
    {
        return $this->familyName;
    }

    /**
     * @param string $familyName
     * @return Member
     */
    public function setFamilyName(string $familyName): Member
    {
        $this->familyName = $familyName;
        return $this;
    }

    /**
     * @return string
     */
    public function getEmail(): string
    {
        return $this->email;
    }

    /**
     * @param string $email
     * @return Member
     */
    public function setEmail(string $email): Member
    {
        $this->email = $email;
        return $this;
    }

    /**
     * @return MemberAttribute[]
     */
    public function getAttributes(): array
    {
        return $this->attributes;
    }

    /**
     * @param MemberAttribute[] $attributes
     * @return Member
     */
    public function setAttributes(array $attributes): Member
    {
        $this->attributes = $attributes;
        return $this;
    }
}

<?php

namespace App\Entity;

use ArrayAccess;

class Member implements ArrayAccess
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

    public function __construct()
    {
        $this->attributes = [];
    }

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

    /**
     * @param mixed $offset
     * @return bool
     */
    public function offsetExists($offset)
    {
        foreach ($this->getAttributes() as $attribute) {
            if ($attribute->getKey() === $offset) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param mixed $offset
     * @return mixed|null
     */
    public function offsetGet($offset)
    {
        foreach ($this->getAttributes() as $attribute) {
            if ($attribute->getKey() === $offset) {
                return $attribute->getValue();
            }
        }
        return null;
    }

    /**
     * @param mixed $offset
     * @param mixed $value
     */
    public function offsetSet($offset, $value)
    {
        foreach ($this->getAttributes() as $attribute) {
            if ($attribute->getKey() === $offset) {
                $attribute->setValue($value);
                return;
            }
        }
        $this->attributes[] = (new MemberAttribute())
            ->setKey($offset)
            ->setValue($value);
    }

    /**
     * @param mixed $offset
     */
    public function offsetUnset($offset)
    {
        $new = [];
        foreach ($this->getAttributes() as $attribute) {
            if ($attribute->getKey() != $offset) {
                $new[] = $attribute;
            }
        }
        $this->setAttributes($new);
    }
}

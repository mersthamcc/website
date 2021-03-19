<?php

namespace App\Entity;

class MemberFormSection
{
    /**
     * @var string $key
     */
    private $key;

    /**
     * @var MemberFormSectionAttribute[] $attribute
     */
    private $attribute;

    /**
     * @return string
     */
    public function getKey(): string
    {
        return $this->key;
    }

    /**
     * @return MemberFormSectionAttribute[]
     */
    public function getAttribute(): array
    {
        return $this->attribute;
    }

    /**
     * @param string $key
     * @return MemberFormSection
     */
    public function setKey(string $key): MemberFormSection
    {
        $this->key = $key;
        return $this;
    }

    /**
     * @param MemberFormSectionAttribute[] $attribute
     * @return MemberFormSection
     */
    public function setAttribute(array $attribute): MemberFormSection
    {
        $this->attribute = $attribute;
        return $this;
    }
}

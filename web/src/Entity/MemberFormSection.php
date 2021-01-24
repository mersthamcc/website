<?php

namespace App\Entity;

class MemberFormSection
{
    /**
     * @var $key string
     */
    private $key;

    /**
     * @var $attributes MemberFormSectionAttribute[]
     */
    private $attributes;

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
    public function getAttributes(): array
    {
        return $this->attributes;
    }
}

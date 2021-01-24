<?php

namespace App\Entity;

class MemberCategory
{
    /**
     * @var $key string
     */
    private $key;

    /**
     * @var $registrationCode string
     */
    private $registrationCode;

    /**
     * @var $form MemberCategoryFormSection[]
     */
    private $formSections;

    /**
     * @return string
     */
    public function getKey(): string
    {
        return $this->key;
    }

    /**
     * @return string
     */
    public function getRegistrationCode(): string
    {
        return $this->registrationCode;
    }

    /**
     * @return MemberCategoryFormSection[]
     */
    public function getFormSections(): array
    {
        return $this->formSections;
    }
}

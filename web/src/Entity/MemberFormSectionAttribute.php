<?php

namespace App\Entity;

class MemberFormSectionAttribute
{
    /**
     * @var $sortOrder int
     */
    private $sortOrder;

    /**
     * @var $mandatory bool
     */
    private $mandatory;

    /**
     * @var $definition AttributeDefinition
     */
    private $definition;

    /**
     * @return int
     */
    public function getSortOrder(): int
    {
        return $this->sortOrder;
    }

    /**
     * @return bool
     */
    public function isMandatory(): bool
    {
        return $this->mandatory;
    }

    /**
     * @return AttributeDefinition
     */
    public function getDefinition(): AttributeDefinition
    {
        return $this->definition;
    }
}

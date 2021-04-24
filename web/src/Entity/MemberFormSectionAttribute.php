<?php

namespace App\Entity;

class MemberFormSectionAttribute
{
    /**
     * @var int
     */
    private $sortOrder;

    /**
     * @var bool
     */
    private $mandatory;

    /**
     * @var AttributeDefinition
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

    /**
     * @param int $sortOrder
     * @return MemberFormSectionAttribute
     */
    public function setSortOrder(int $sortOrder): MemberFormSectionAttribute
    {
        $this->sortOrder = $sortOrder;
        return $this;
    }

    /**
     * @param bool $mandatory
     * @return MemberFormSectionAttribute
     */
    public function setMandatory(bool $mandatory): MemberFormSectionAttribute
    {
        $this->mandatory = $mandatory;
        return $this;
    }

    /**
     * @param AttributeDefinition $definition
     * @return MemberFormSectionAttribute
     */
    public function setDefinition(
        AttributeDefinition $definition
    ): MemberFormSectionAttribute {
        $this->definition = $definition;
        return $this;
    }
}

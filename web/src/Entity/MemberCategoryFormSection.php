<?php

namespace App\Entity;

class MemberCategoryFormSection
{
    /**
     * @var int
     */
    private $sortOrder;

    /**
     * @var MemberFormSection
     */
    private $section;

    /**
     * @return int
     */
    public function getSortOrder(): int
    {
        return $this->sortOrder;
    }

    /**
     * @return MemberFormSection
     */
    public function getSection(): MemberFormSection
    {
        return $this->section;
    }

    /**
     * @param int $sortOrder
     * @return MemberCategoryFormSection
     */
    public function setSortOrder(int $sortOrder): MemberCategoryFormSection
    {
        $this->sortOrder = $sortOrder;
        return $this;
    }

    /**
     * @param MemberFormSection $section
     * @return MemberCategoryFormSection
     */
    public function setSection(
        MemberFormSection $section
    ): MemberCategoryFormSection {
        $this->section = $section;
        return $this;
    }
}

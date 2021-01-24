<?php

namespace App\Entity;

class MemberCategoryFormSection
{
    /**
     * @var $sortOrder int
     */
    private $sortOrder;

    /**
     * @var $section MemberFormSection
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
}

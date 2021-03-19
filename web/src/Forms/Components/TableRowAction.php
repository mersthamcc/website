<?php

namespace App\Forms\Components;

class TableRowAction
{
    /**
     * @var $name string
     */
    private $name;

    /**
     * @var $label string
     */
    private $label;

    /**
     * @var $icon string | null
     */
    private $icon;

    /**
     * @var $disposition string | null
     */
    private $disposition;

    /**
     * TableRowAction constructor.
     * @param string $name
     * @param string $label
     * @param string|null $icon
     * @param string|null $disposition
     */
    public function __construct(
        string $name,
        string $label,
        ?string $icon,
        ?string $disposition
    ) {
        $this->name = $name;
        $this->label = $label;
        $this->icon = $icon;
        $this->disposition = $disposition;
    }

    /**
     * @return string
     */
    public function getName(): string
    {
        return $this->name;
    }

    /**
     * @return string
     */
    public function getLabel(): string
    {
        return $this->label;
    }

    /**
     * @return string|null
     */
    public function getIcon(): ?string
    {
        return $this->icon;
    }

    /**
     * @return string|null
     */
    public function getDisposition(): ?string
    {
        return $this->disposition;
    }
}

<?php
namespace App\Data;


class MenuEntry
{
    /**
     * @var $title string
     */
    private $title;

    /**
     * @var $icon string
     */
    private $icon;

    /**
     * @var $sortorder integer
     */
    private $sortorder = 0;

    /**
     * @var $route string|null
     */
    private $route = "home";

    /**
     * @var $label string|null
     */
    private $label;

    /**
     * @var $labelClass string;
     */
    private $labelClass = "primary";

    /**
     * @var $children MenuEntry[]
     */
    private $children = array();

    /**
     * @var $parent MenuEntry
     */
    private $parent = null;

    /**
     * @param $title string
     * @return static MenuEntry
     */
    public static function createMenuEntry($title) {
        $menuEntry = new MenuEntry();
        return $menuEntry->setTitle($title);
    }


    /**
     * @return string
     */
    public function getTitle(): string
    {
        return $this->title;
    }

    /**
     * @param string $title
     * @return MenuEntry
     */
    public function setTitle(string $title): MenuEntry
    {
        $this->title = $title;
        return $this;
    }

    /**
     * @return string
     */
    public function getIcon(): string
    {
        return $this->icon;
    }

    /**
     * @param string $icon
     * @return MenuEntry
     */
    public function setIcon(string $icon): MenuEntry
    {
        $this->icon = $icon;
        return $this;
    }

    /**
     * @return int
     */
    public function getSortorder(): int
    {
        return $this->sortorder;
    }

    /**
     * @param int $sortorder
     * @return MenuEntry
     */
    public function setSortorder(int $sortorder): MenuEntry
    {
        $this->sortorder = $sortorder;
        return $this;
    }

    /**
     * @return string|null
     */
    public function getRoute(): ?string
    {
        return $this->route;
    }

    /**
     * @param string|null $route
     * @return MenuEntry
     */
    public function setRoute(?string $route): MenuEntry
    {
        $this->route = $route;
        return $this;
    }

    /**
     * @return MenuEntry[]
     */
    public function getChildren(): array
    {
        return $this->children;
    }

    /**
     * @param MenuEntry[] $children
     * @return MenuEntry
     */
    public function setChildren(array $children): MenuEntry
    {
        $this->children = $children;
        return $this;
    }

    /**
     * @return MenuEntry
     */
    public function getParent(): MenuEntry
    {
        return $this->parent;
    }

    /**
     * @param MenuEntry $parent
     * @return MenuEntry
     */
    public function setParent(MenuEntry $parent): MenuEntry
    {
        $this->parent = $parent;
        return $this;
    }

    /**
     * @return string|null
     */
    public function getLabel(): ?string
    {
        return $this->label;
    }

    /**
     * @param string|null $label
     * @return MenuEntry
     */
    public function setLabel(?string $label): MenuEntry
    {
        $this->label = $label;
        return $this;
    }

    /**
     * @return string
     */
    public function getLabelClass(): string
    {
        return $this->labelClass;
    }

    /**
     * @param string $labelClass
     * @return MenuEntry
     */
    public function setLabelClass(string $labelClass): MenuEntry
    {
        $this->labelClass = $labelClass;
        return $this;
    }
}
<?php

namespace App\Entity;

class AttributeDefinition
{
    /**
     * @var $key string
     */
    private $key;

    /**
     * @var $type string
     */
    private $type;

    /**
     * @var $choices array
     */
    private $choices;

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
    public function getType(): string
    {
        return $this->type;
    }

    /**
     * @return array
     */
    public function getChoices(): array
    {
        return $this->choices;
    }
}

<?php

namespace App\Entity;

class RegistrationBasket implements \ArrayAccess
{
    /**
     * @var Subscription[]
     */
    private $items = [];

    /**
     * @return Subscription[]
     */
    public function getItems(): array
    {
        return $this->items;
    }

    /**
     * @inheritDoc
     */
    public function offsetExists($offset)
    {
        return isset($this->items[$offset]);
    }

    /**
     * @inheritDoc
     */
    public function offsetGet($offset)
    {
        if ($this->offsetExists($offset)) {
            return $this->items[$offset];
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public function offsetSet($offset, $value)
    {
        $this->items[$offset] = $value;
    }

    /**
     * @inheritDoc
     */
    public function offsetUnset($offset)
    {
        unset($this->items[$offset]);
    }
}

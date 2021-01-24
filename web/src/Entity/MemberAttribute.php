<?php

namespace App\Entity;

class MemberAttribute
{
    /**
     * @var $key string
     */
    private $key;

    /**
     * @var $value mixed
     */
    private $value;

    /**
     * @return string
     */
    public function getKey(): string
    {
        return $this->key;
    }

    /**
     * @param string $key
     * @return MemberAttribute
     */
    public function setKey(string $key): MemberAttribute
    {
        $this->key = $key;
        return $this;
    }

    /**
     * @return mixed
     */
    public function getValue()
    {
        return $this->value;
    }

    /**
     * @param mixed $value
     * @return MemberAttribute
     */
    public function setValue($value)
    {
        $this->value = $value;
        return $this;
    }
}

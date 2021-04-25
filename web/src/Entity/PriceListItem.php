<?php

namespace App\Entity;

class PriceListItem
{
    /**
     * @var int $id
     */
    private $id;

    /**
     * @var string $description
     */
    private $description;

    /**
     * @var int $minAge
     */
    private $minAge;

    /**
     * @var int|null $maxAge
     */
    private $maxAge;

    /**
     * @var float $currentPrice
     */
    private $currentPrice;

    /**
     * @var boolean|null $includesMatchFee
     */
    private $includesMatchFee;

    /**
     * @return int
     */
    public function getId(): int
    {
        return $this->id;
    }

    /**
     * @param int $id
     * @return PriceListItem
     */
    public function setId(int $id): PriceListItem
    {
        $this->id = $id;
        return $this;
    }

    /**
     * @return string
     */
    public function getDescription(): string
    {
        return $this->description;
    }

    /**
     * @param string $description
     * @return PriceListItem
     */
    public function setDescription(string $description): PriceListItem
    {
        $this->description = $description;
        return $this;
    }

    /**
     * @return int
     */
    public function getMinAge(): int
    {
        return $this->minAge;
    }

    /**
     * @param int $minAge
     * @return PriceListItem
     */
    public function setMinAge(int $minAge): PriceListItem
    {
        $this->minAge = $minAge;
        return $this;
    }

    /**
     * @return int|null
     */
    public function getMaxAge(): ?int
    {
        return $this->maxAge;
    }

    /**
     * @param int|null $maxAge
     * @return PriceListItem
     */
    public function setMaxAge(?int $maxAge): PriceListItem
    {
        $this->maxAge = $maxAge;
        return $this;
    }

    /**
     * @return float
     */
    public function getCurrentPrice(): float
    {
        return $this->currentPrice;
    }

    /**
     * @param float $currentPrice
     * @return PriceListItem
     */
    public function setCurrentPrice(float $currentPrice): PriceListItem
    {
        $this->currentPrice = $currentPrice;
        return $this;
    }

    /**
     * @return bool|null
     */
    public function getIncludesMatchFee(): ?bool
    {
        return $this->includesMatchFee;
    }

    /**
     * @param bool|null $includesMatchFee
     * @return PriceListItem
     */
    public function setIncludesMatchFee(?bool $includesMatchFee): PriceListItem
    {
        $this->includesMatchFee = $includesMatchFee;
        return $this;
    }
}

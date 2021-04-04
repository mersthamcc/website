<?php

namespace App\Entity;

use DateTime;

class PriceListItem
{
    /**
     * @var int $id
     */
    private $id;

    /**
     * @var DateTime|null $dateFrom
     */
    private $dateFrom;

    /**
     * @var DateTime|null $dateTo
     */
    private $dateTo;

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
     * @var float $price
     */
    private $price;

    /**
     * @var float|null $additionalUnitPrice
     */
    private $additionalUnitPrice;

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
     * @return DateTime|null
     */
    public function getDateFrom(): ?DateTime
    {
        return $this->dateFrom;
    }

    /**
     * @param DateTime|null $dateFrom
     * @return PriceListItem
     */
    public function setDateFrom(?DateTime $dateFrom): PriceListItem
    {
        $this->dateFrom = $dateFrom;
        return $this;
    }

    /**
     * @return DateTime|null
     */
    public function getDateTo(): ?DateTime
    {
        return $this->dateTo;
    }

    /**
     * @param DateTime|null $dateTo
     * @return PriceListItem
     */
    public function setDateTo(?DateTime $dateTo): PriceListItem
    {
        $this->dateTo = $dateTo;
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
     * @return float|int|string
     */
    public function getPrice()
    {
        return $this->price;
    }

    /**
     * @param float|int|string $price
     * @return PriceListItem
     */
    public function setPrice($price)
    {
        $this->price = $price;
        return $this;
    }

    /**
     * @return float|int|string|null
     */
    public function getAdditionalUnitPrice()
    {
        return $this->additionalUnitPrice;
    }

    /**
     * @param float|int|string|null $additionalUnitPrice
     * @return PriceListItem
     */
    public function setAdditionalUnitPrice($additionalUnitPrice)
    {
        $this->additionalUnitPrice = $additionalUnitPrice;
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

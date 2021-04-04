<?php

namespace App\Entity;

class MemberCategory
{
    /**
     * @var string
     */
    private $key;

    /**
     * @var string|null
     */
    private $registrationCode;

    /**
     * @var MemberCategoryFormSection[]
     */
    private $form;

    /**
     * @var PriceListItem[]|null $priceList
     */
    private $priceList;

    /**
     * @return string
     */
    public function getKey(): string
    {
        return $this->key;
    }

    /**
     * @return string|null
     */
    public function getRegistrationCode(): ?string
    {
        return $this->registrationCode;
    }

    /**
     * @return MemberCategoryFormSection[]
     */
    public function getForm(): array
    {
        return $this->form;
    }

    /**
     * @param string $key
     * @return MemberCategory
     */
    public function setKey(string $key): MemberCategory
    {
        $this->key = $key;
        return $this;
    }

    /**
     * @param string|null $registrationCode
     * @return MemberCategory
     */
    public function setRegistrationCode(
        ?string $registrationCode
    ): MemberCategory {
        $this->registrationCode = $registrationCode;
        return $this;
    }

    /**
     * @param MemberCategoryFormSection[] $form
     * @return MemberCategory
     */
    public function setForm(array $form): MemberCategory
    {
        $this->form = $form;
        return $this;
    }

    /**
     * @return PriceListItem[]|null
     */
    public function getPriceList(): ?array
    {
        return $this->priceList;
    }

    /**
     * @param PriceListItem[]|null $priceList
     * @return MemberCategory
     */
    public function setPriceList(?array $priceList): MemberCategory
    {
        $this->priceList = $priceList;
        return $this;
    }
}

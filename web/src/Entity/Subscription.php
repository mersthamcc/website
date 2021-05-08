<?php

namespace App\Entity;

class Subscription implements \ArrayAccess
{
    /**
     * @var int $id
     */
    private $id;

    /**
     * @var string $registrationId
     */
    private $registrationId;

    /**
     * @var \DateTime $date
     */
    private $date;

    /**
     * @var integer|null $priceListItemId
     */
    private $priceListItemId = 0;

    /**
     * @var PriceListItem|null $priceListItem
     */
    private $priceListItem;

    /**
     * @var MemberCategory|null $membershipCategory
     */
    private $membershipCategory;

    /**
     * @var Member|null $member
     */
    private $member;

    /**
     * Subscription constructor.
     */
    public function __construct()
    {
        $this->member = new Member();
    }

    /**
     * @return int
     */
    public function getId(): int
    {
        return $this->id;
    }

    /**
     * @param int $id
     * @return Subscription
     */
    public function setId(int $id): Subscription
    {
        $this->id = $id;
        return $this;
    }

    /**
     * @return string
     */
    public function getRegistrationId(): string
    {
        return $this->registrationId;
    }

    /**
     * @param string $registrationId
     * @return Subscription
     */
    public function setRegistrationId(string $registrationId): Subscription
    {
        $this->registrationId = $registrationId;
        return $this;
    }

    /**
     * @return \DateTime
     */
    public function getDate(): \DateTime
    {
        return $this->date;
    }

    /**
     * @param \DateTime $date
     * @return Subscription
     */
    public function setDate(\DateTime $date): Subscription
    {
        $this->date = $date;
        return $this;
    }

    /**
     * @return int|null
     */
    public function getPriceListItemId(): ?int
    {
        return $this->priceListItemId;
    }

    /**
     * @param int|null $priceListItemId
     * @return Subscription
     */
    public function setPriceListItemId(?int $priceListItemId): Subscription
    {
        $this->priceListItemId = $priceListItemId;
        return $this;
    }

    /**
     * @return PriceListItem|null
     */
    public function getPriceListItem(): ?PriceListItem
    {
        return $this->priceListItem;
    }

    /**
     * @param PriceListItem|null $priceListItem
     * @return Subscription
     */
    public function setPriceListItem(
        ?PriceListItem $priceListItem
    ): Subscription {
        $this->priceListItem = $priceListItem;
        $this->priceListItemId = $priceListItem->getId();
        return $this;
    }

    /**
     * @return MemberCategory|null
     */
    public function getMembershipCategory(): ?MemberCategory
    {
        return $this->membershipCategory;
    }

    /**
     * @param MemberCategory|null $membershipCategory
     * @return Subscription
     */
    public function setMembershipCategory(
        ?MemberCategory $membershipCategory
    ): Subscription {
        $this->membershipCategory = $membershipCategory;
        return $this;
    }

    /**
     * @return Member|null
     */
    public function getMember(): ?Member
    {
        return $this->member;
    }

    /**
     * @param Member|null $member
     * @return Subscription
     */
    public function setMember(?Member $member): Subscription
    {
        $this->member = $member;
        return $this;
    }

    /**
     * @inheritDoc
     */
    public function offsetExists($offset)
    {
        return isset($this->member[$offset]);
    }

    /**
     * @inheritDoc
     */
    public function offsetGet($offset)
    {
        if ($this->offsetExists($offset)) {
            return $this->member[$offset];
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public function offsetSet($offset, $value)
    {
        $this->member[$offset] = $value;
    }

    /**
     * @inheritDoc
     */
    public function offsetUnset($offset)
    {
        unset($this->member[$offset]);
    }
}

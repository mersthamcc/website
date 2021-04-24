<?php

namespace App\Entity;

class Subscription
{
    /**
     * @var integer|null $priceListItemId
     */
    public $priceListItemId = 0;

    /**
     * @var Member|null $member
     */
    public $member;
}

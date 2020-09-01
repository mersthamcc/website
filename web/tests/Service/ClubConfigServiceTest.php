<?php

namespace App\Tests\Service;

use App\Service\ClubConfigService;
use PHPUnit\Framework\TestCase;

class ClubConfigServiceTest extends TestCase
{
    private $clubConfigService;

    public function setUp()
    {
        parent::setUp();
        $this->clubConfigService = new ClubConfigService("kfkfkf");
    }

    public function clubNameReturnsCorrectValue()
    {
        $this->assertEquals($this->clubConfigService->getClubName());
    }
}

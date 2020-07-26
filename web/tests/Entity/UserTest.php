<?php

namespace Entity;

use App\Entity\User;
use PHPUnit\Framework\TestCase;

class UserTest extends TestCase
{
    private const TEST_ROLE = "ROLE_TEST";

    private $user;

    protected function setUp()
    {
        $this->user = new User();
        $this->user->setGivenName("Test")
            ->setFamilyName("User")
            ->setExternalId(uniqid())
            ->setEmail("test@example.com");
    }

    public function testAddRoles()
    {
        $this->user->addRoles([self::TEST_ROLE]);

        $this->assertContains(self::TEST_ROLE, $this->user->getRoles(), sprintf("User roles doesn't contain the role %s", self::TEST_ROLE));
    }

    public function testHasValidRole()
    {
        $this->user->addRoles([self::TEST_ROLE]);

        $this->assertTrue($this->user->hasValidRole([self::TEST_ROLE]));
    }

    public function testGetGravatarHash()
    {
        $this->assertEquals("55502f40dc8b7c769880b10874abc9d0", $this->user->getGravatarHash());
    }
}

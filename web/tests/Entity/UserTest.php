<?php

namespace Entity;

use App\Entity\User;
use PHPUnit\Framework\TestCase;

class UserTest extends TestCase
{
    private const DEFAULT_ROLE = "ROLE_USER";
    private const TEST_ROLE = "ROLE_TEST";
    private const SECONDARY_TEST_ROLE = "ROLE_ANOTHER_TEST";

    private $user;

    protected function setUp(): void
    {
        $this->user = new User();
        $this->user
            ->setGivenName("Test")
            ->setFamilyName("User")
            ->setExternalId(uniqid())
            ->setEmail("test@example.com");
    }

    public function testDefaultRoles()
    {
        $this->assertCount(1, $this->user->getRoles());
        $this->assertContainsEquals(
            self::DEFAULT_ROLE,
            $this->user->getRoles()
        );
    }

    public function testAddRoles()
    {
        $this->user->addRoles([self::TEST_ROLE]);

        $this->assertCount(2, $this->user->getRoles());
        $this->assertContainsEquals(
            self::DEFAULT_ROLE,
            $this->user->getRoles()
        );
        $this->assertContainsEquals(self::TEST_ROLE, $this->user->getRoles());
    }

    public function testAddDuplicateRoles()
    {
        $this->user->addRoles([self::TEST_ROLE]);
        $this->user->addRoles([self::TEST_ROLE]);

        $this->assertCount(2, $this->user->getRoles());
        $this->assertContainsEquals(
            self::DEFAULT_ROLE,
            $this->user->getRoles()
        );
        $this->assertContainsEquals(self::TEST_ROLE, $this->user->getRoles());
    }

    public function testAddMultipleRolesOneAtATime()
    {
        $this->user->addRoles([self::TEST_ROLE]);
        $this->user->addRoles([self::SECONDARY_TEST_ROLE]);

        $this->assertCount(3, $this->user->getRoles());
        $this->assertContainsEquals(self::TEST_ROLE, $this->user->getRoles());
        $this->assertContainsEquals(
            self::SECONDARY_TEST_ROLE,
            $this->user->getRoles()
        );
    }

    public function testAddMultipleRolesInOneGo()
    {
        $this->user->addRoles([self::TEST_ROLE, self::SECONDARY_TEST_ROLE]);

        $this->assertCount(3, $this->user->getRoles());
        $this->assertContainsEquals(self::TEST_ROLE, $this->user->getRoles());
        $this->assertContainsEquals(
            self::SECONDARY_TEST_ROLE,
            $this->user->getRoles()
        );
    }

    public function testClearRoles()
    {
        $this->user->addRoles([self::TEST_ROLE, self::SECONDARY_TEST_ROLE]);

        $this->assertCount(3, $this->user->getRoles());

        $this->user->setRoles([]);
        $this->assertCount(1, $this->user->getRoles());
        $this->assertContainsEquals(
            self::DEFAULT_ROLE,
            $this->user->getRoles()
        );
    }

    public function testHasValidRoleWithSingleValidRole()
    {
        $this->user->addRoles([self::TEST_ROLE]);

        $this->assertTrue($this->user->hasValidRole([self::TEST_ROLE]));
    }

    public function testHasValidRoleWithMultipleValidRoles()
    {
        $this->user->addRoles([self::SECONDARY_TEST_ROLE]);

        $this->assertTrue(
            $this->user->hasValidRole([
                self::TEST_ROLE,
                self::SECONDARY_TEST_ROLE,
            ])
        );
    }

    public function testHasValidRoleWithEmptyValidRole()
    {
        $this->user->addRoles([self::TEST_ROLE]);

        $this->assertTrue($this->user->hasValidRole([]));
    }

    public function testHasValidRoleWhenUserDoesNotHaveRole()
    {
        $this->user->addRoles([self::TEST_ROLE]);

        $this->assertFalse(
            $this->user->hasValidRole([self::SECONDARY_TEST_ROLE])
        );
    }

    public function testGetGravatarHash()
    {
        $this->assertEquals(
            "55502f40dc8b7c769880b10874abc9d0", // pragma: allowlist secret
            $this->user->getGravatarHash()
        );
    }
}

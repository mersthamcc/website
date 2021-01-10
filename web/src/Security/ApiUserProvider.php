<?php

namespace App\Security;

use App\ApiClient\UserService;
use App\Entity\User;
use Symfony\Component\Security\Core\Exception\UnsupportedUserException;
use Symfony\Component\Security\Core\Exception\UsernameNotFoundException;
use Symfony\Component\Security\Core\User\PasswordUpgraderInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\User\UserProviderInterface;

class ApiUserProvider implements
    UserProviderInterface,
    PasswordUpgraderInterface
{
    /**
     * @var $userService UserService
     *
     */
    private $userService;
    public function __construct(UserService $userService)
    {
        $this->userService = $userService;
    }

    public function upgradePassword(
        UserInterface $user,
        string $newEncodedPassword
    ): void {
        // Do nothing .. passwords not managed here
    }

    /**
     * @param string $username
     * @return User|UserInterface
     */
    public function loadUserByUsername($username): UserInterface
    {
        $user = $this->userService->getUserByExternalId($username);
        if ($user == null) {
            throw new UsernameNotFoundException(
                "Username not found: " . $username
            );
        }
        return $user;
    }

    /**
     * @param UserInterface $user
     * @return UserInterface|void
     */
    public function refreshUser(UserInterface $user): UserInterface
    {
        if (!$user instanceof User) {
            throw new UnsupportedUserException(
                sprintf('Invalid user class "%s".', get_class($user))
            );
        }

        return $this->loadUserByUsername($user->getUsername());
    }

    public function supportsClass(string $class): bool
    {
        return User::class === $class || is_subclass_of($class, User::class);
    }
}

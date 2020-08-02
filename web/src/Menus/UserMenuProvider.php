<?php
namespace App\Menus;

use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;

interface UserMenuProvider {
    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getUserMenuItems(FactoryInterface $factory): array;
}
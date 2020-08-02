<?php
namespace App\Menus;

use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;

interface TopMenuProvider {
    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getTopMenuItems(FactoryInterface $factory): array;
}
<?php
namespace App\Menus;

use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;

interface AdministrationMenuProvider {
    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getFrontEndMenuItems(FactoryInterface $factory): array;
}
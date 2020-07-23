<?php
namespace App\Menus;

use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;

interface AdministrationMenuProvider {
    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getContentAdminMenuItems(FactoryInterface $factory): array;

    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getAdministrationMenuItems(FactoryInterface $factory): array;

    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getConfigurationAdminMenuItems(FactoryInterface $factory): array;

}
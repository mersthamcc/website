<?php


namespace App\Service;


use App\Controller\HomeController;
use App\Controller\NewsController;
use App\Data\MenuEntry;
use App\Menus\AdministrationMenuProvider;
use App\Menus\FrontEndMenuProvider;
use ReflectionClass;
use Symfony\Component\DependencyInjection\ContainerInterface;

class MenuService
{
    /**
     * @var string[]
     */
    private $controllers = [
        HomeController::class,
        NewsController::class
    ];

    /**
     * @var $container;
     */
    private $container;

    public function __construct(ContainerInterface $container)
    {
        $this->container = $container;
    }

    /**
     * @return MenuEntry[]
     * @throws \ReflectionException
     */
    public function getFrontendMenuItems() {
        return $this->getMenuItems(FrontEndMenuProvider::class, 'getFrontEndMenuItems');
    }

    /**
     * @return MenuEntry[]
     * @throws \ReflectionException
     */
    public function getAdministrationMenuItems() {
        return $this->getMenuItems(AdministrationMenuProvider::class, 'getAdminMenuItems');
    }

    /**
     * @param $interface string
     * @param $methodName string
     * @return MenuEntry[]
     * @throws \ReflectionException
     */
    private function getMenuItems($interface, $methodName) {
        $menuItems = [];
        foreach ($this->getClassesThatImplement($interface) as $class) {
            $method = $class->getMethod($methodName);
            $menuItems = array_merge($menuItems, $method->invoke(null));
        }
        $this->sortMenuItems($menuItems);
        return $menuItems;
    }

    /**
     * @param $interface string
     * @return \ReflectionClass[]
     */
    private function getClassesThatImplement($interface) {
        $implementations = array();
        foreach($this->controllers as $controller) {
            $reflect = new ReflectionClass($controller);
            if($reflect->implementsInterface($interface))
                $implementations[] = $reflect;
        }
        return $implementations;
    }

    /**
     * @param $menuItems MenuEntry[]
     */
    private function sortMenuItems($menuItems) {
        foreach ($menuItems as $menuItem) {
            $this->sortMenuItems($menuItem->getChildren());
        }
        usort($menuItems, function (MenuEntry $a, MenuEntry $b) {
            if ( $a->getSortorder() == $b->getSortorder() ) return strcmp($a->getTitle(), $b->getTitle());
            return $a->getSortorder() - $b->getSortorder();
        });
    }
}
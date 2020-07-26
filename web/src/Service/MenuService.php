<?php


namespace App\Service;

use App\Controller\ContactController;
use App\Controller\CricketController;
use App\Controller\HomeController;
use App\Controller\NewsController;
use App\Data\MenuEntry;
use App\Menus\AdministrationMenuProvider;
use App\Menus\FrontEndMenuProvider;
use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;
use ReflectionClass;
use Symfony\Component\DependencyInjection\ContainerAwareInterface;
use Symfony\Component\DependencyInjection\ContainerAwareTrait;

class MenuService implements ContainerAwareInterface
{
    use ContainerAwareTrait;

    /**
     * @var string[]
     */
    private $controllers = [
        HomeController::class,
        NewsController::class,
        CricketController::class,
        ContactController::class,
    ];

    /**
     * @var FactoryInterface
     */
    private $factory;

    public function __construct(FactoryInterface $factory)
    {
        $this->factory = $factory;
    }

    /**
     * @param array $options
     * @param array|null $topLevelOrder
     * @return \Knp\Menu\ItemInterface
     * @throws \ReflectionException
     */
    public function getFrontendMenuItems(array $options, array $topLevelOrder = null): ItemInterface {
        $menu = $this->factory->createItem('root')->setDisplay(false);
        $this->addItemsToMenu($menu, FrontEndMenuProvider::class, 'getFrontEndMenuItems');
        if ( !is_null($topLevelOrder) ) $menu->reorderChildren($topLevelOrder);
        return $this->setParents($menu);
    }

    /**
     * @param array $options
     * @param array|null $topLevelOrder
     * @return \Knp\Menu\ItemInterface
     * @throws \ReflectionException
     */
    public function getAdministrationMenuItems(array $options, array $topLevelOrder = null): ItemInterface {
        $menu = $this->factory->createItem('home', [
            'route' => 'admin_home'
        ])->setDisplay(true)->setLabel("Dashboard");

        $contentMenu = $this->factory->createItem('content')->setDisplay(true)->setLabel("Content");
        $this->addItemsToMenu($contentMenu, AdministrationMenuProvider::class, 'getContentAdminMenuItems');
        $menu->addChild($contentMenu);

        $administrationMenu = $this->factory->createItem('administration')->setDisplay(true)->setLabel("Administration");
        $this->addItemsToMenu($administrationMenu, AdministrationMenuProvider::class, 'getAdministrationMenuItems');
        $menu->addChild($administrationMenu);

        $configurationMenu = $this->factory->createItem('config')->setDisplay(true)->setLabel("Configuration");
        $this->addItemsToMenu($configurationMenu, AdministrationMenuProvider::class, 'getConfigurationAdminMenuItems');
        $menu->addChild($configurationMenu);

        if ( !is_null($topLevelOrder) ) $menu->reorderChildren($topLevelOrder);
        return $this->setParents($menu);
    }

    /**
     * @param $interface string
     * @param $methodName string
     * @return ItemInterface
     * @throws \ReflectionException
     */
    private function addItemsToMenu(ItemInterface $root, $interface, $methodName) {
        foreach ($this->getClassesThatImplement($interface) as $class) {
            $method = $class->getMethod($methodName);
            foreach ($method->invoke(null, $this->factory) as $item) {
                $root->addChild($item);
            }
        }
        return $root;
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
     * @param ItemInterface $item
     * @return ItemInterface
     */
    private function setParents(ItemInterface $item): ItemInterface {
        if ($item->hasChildren()) {
            foreach ($item->getChildren() as $child) {
                $child->setParent($item);
                if ($child->hasChildren()) $this->setParents($child);
                if ($child->getExtra("roles")) $item->setExtra('roles', array_unique(array_merge($item->getExtra('roles', []), $child->getExtra('roles')), SORT_STRING));
            }
        }
        return $item;
    }
}
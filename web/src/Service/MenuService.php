<?php

namespace App\Service;

use App\Controller\ContactController;
use App\Controller\CricketController;
use App\Controller\HomeController;
use App\Controller\NewsController;
use App\Controller\RegistrationController;
use App\Menus\AdministrationMenuProvider;
use App\Menus\FrontEndMenuProvider;
use App\Menus\TopMenuProvider;
use App\Menus\UserMenuProvider;
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
        RegistrationController::class,
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
    public function getFrontendMenuItems(array $options): ItemInterface
    {
        $translationDomain = "frontend";
        $menu = $this->factory
            ->createItem("root")
            ->setDisplay(false)
            ->setExtra("translation_domain", $translationDomain);
        $this->addItemsToMenu(
            $menu,
            FrontEndMenuProvider::class,
            "getFrontEndMenuItems"
        );
        //        if ( !is_null($topLevelOrder) ) $menu->reorderChildren($topLevelOrder);
        return $this->setDefaults($menu, $translationDomain);
    }

    /**
     * @param array $options
     * @param array|null $topLevelOrder
     * @return \Knp\Menu\ItemInterface
     * @throws \ReflectionException
     */
    public function getAdministrationMenuItems(array $options): ItemInterface
    {
        $translationDomain = "administration";
        //        $menu = $this->factory->createItem('root')->setDisplay(false)->setExtra('translation_domain', $translationDomain);

        $menu = $this->factory
            ->createItem("home", [
                "route" => "admin_home",
            ])
            ->setDisplay(true)
            ->setExtra("translation_domain", $translationDomain)
            ->setExtra("translation_domain", $translationDomain);

        $contentMenu = $this->factory
            ->createItem("content")
            ->setDisplay(true)
            ->setExtra("translation_domain", $translationDomain);
        $this->addItemsToMenu(
            $contentMenu,
            AdministrationMenuProvider::class,
            "getContentAdminMenuItems"
        );
        $menu->addChild($contentMenu);

        $administrationMenu = $this->factory
            ->createItem("administration")
            ->setDisplay(true)
            ->setExtra("translation_domain", $translationDomain);
        $this->addItemsToMenu(
            $administrationMenu,
            AdministrationMenuProvider::class,
            "getAdministrationMenuItems"
        );
        $menu->addChild($administrationMenu);

        $configurationMenu = $this->factory
            ->createItem("config")
            ->setDisplay(true)
            ->setExtra("translation_domain", $translationDomain);
        $this->addItemsToMenu(
            $configurationMenu,
            AdministrationMenuProvider::class,
            "getConfigurationAdminMenuItems"
        );
        $menu->addChild($configurationMenu);

        //        if ( !is_null($topLevelOrder) ) $menu->reorderChildren($topLevelOrder);
        return $this->setDefaults($menu, $translationDomain);
    }

    /**
     * @param array $options
     * @param array|null $topLevelOrder
     * @return \Knp\Menu\ItemInterface
     * @throws \ReflectionException
     */
    public function getTopMenuItems(array $options): ItemInterface
    {
        $translationDomain = "frontend";
        $menu = $this->factory
            ->createItem("root")
            ->setDisplay(false)
            ->setExtra("translation_domain", $translationDomain);
        $this->addItemsToMenu($menu, TopMenuProvider::class, "getTopMenuItems");
        $topLevelOrder = array_map(function ($item) {
            return [$item->getAttribute("sortorder"), $item->getName()];
        }, $menu->getChildren());
        sort($topLevelOrder);
        $topLevelOrder = array_map(function ($item) {
            return $item[1];
        }, $topLevelOrder);
        $menu->reorderChildren($topLevelOrder);
        return $this->setDefaults($menu, $translationDomain);
    }

    /**
     * @param array $options
     * @param array|null $topLevelOrder
     * @return \Knp\Menu\ItemInterface
     * @throws \ReflectionException
     */
    public function getUserMenuItems(array $options): ItemInterface
    {
        $translationDomain = "frontend";
        $menu = $this->factory
            ->createItem("root")
            ->setDisplay(false)
            ->setExtra("translation_domain", $translationDomain);
        $this->addItemsToMenu(
            $menu,
            UserMenuProvider::class,
            "getUserMenuItems"
        );

        //        if ( !is_null($topLevelOrder) ) $menu->reorderChildren($topLevelOrder);

        $menu->addChild(
            $this->factory
                ->createItem("logoutDivider")
                ->setExtra("divider", true)
        );
        $menu->addChild(
            $this->factory
                ->createItem("logout", [
                    "route" => "logout",
                ])
                ->setExtra("translation_domain", $translationDomain)
        );
        return $this->setDefaults($menu, $translationDomain);
    }

    /**
     * @param $interface string
     * @param $methodName string
     * @return ItemInterface
     * @throws \ReflectionException
     */
    private function addItemsToMenu(
        ItemInterface $root,
        $interface,
        $methodName
    ) {
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
    private function getClassesThatImplement($interface)
    {
        $implementations = [];
        foreach ($this->controllers as $controller) {
            $reflect = new ReflectionClass($controller);
            if ($reflect->implementsInterface($interface)) {
                $implementations[] = $reflect;
            }
        }
        return $implementations;
    }

    /**
     * @param ItemInterface $item
     * @param string $translationDomain
     * @return ItemInterface
     */
    private function setDefaults(
        ItemInterface $item,
        string $translationDomain
    ): ItemInterface {
        if ($item->hasChildren()) {
            foreach ($item->getChildren() as $child) {
                $child->setParent($item);
                if ($child->hasChildren()) {
                    $this->setDefaults($child, $translationDomain);
                }
                if ($child->getExtra("roles")) {
                    $item->setExtra(
                        "roles",
                        array_unique(
                            array_merge(
                                $item->getExtra("roles", []),
                                $child->getExtra("roles")
                            ),
                            SORT_STRING
                        )
                    );
                }
                if (!$child->getExtra("translation_domain", false)) {
                    $child->setExtra("translation_domain", $translationDomain);
                }
            }
        }
        return $item;
    }
}

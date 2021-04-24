<?php

namespace App\Controller;

use App\Menus\FrontEndMenuProvider;
use App\Menus\TopMenuProvider;
use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

class HomeController extends AbstractController implements
    FrontEndMenuProvider,
    TopMenuProvider
{
    /**
     * @Route("/", name="home")
     */
    public function index()
    {
        return $this->render("home/index.html.twig", [
            "controller_name" => "HomeController",
        ]);
    }

    /**
     * @Route("/administration/", name="admin_home")
     */
    public function adminIndex()
    {
        return $this->render("home/admin_index.html.twig", [
            "controller_name" => "HomeController",
        ]);
    }

    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getFrontEndMenuItems(
        FactoryInterface $factory
    ): array {
        return [
            $factory->createItem("home", [
                "route" => "home",
                "sortorder" => 10,
            ]),
        ];
    }

    public static function getTopMenuItems(FactoryInterface $factory): array
    {
        return [
            $factory
                ->createItem("administration", [
                    "route" => "admin_home",
                ])
                ->setExtra("roles", ["ROLE_ADMIN"])
                ->setAttribute("sortorder", 100),
        ];
    }
}

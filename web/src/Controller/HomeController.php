<?php

namespace App\Controller;

use App\Data\MenuEntry;
use App\Menus\FrontEndMenuProvider;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

class HomeController extends AbstractController implements FrontEndMenuProvider
{
    /**
     * @Route("/", name="home")
     */
    public function index()
    {
        return $this->render('home/index.html.twig', [
            'controller_name' => 'HomeController',
        ]);
    }

    /**
     * @Route("/administration/", name="admin_home")
     */
    public function adminIndex()
    {
        return $this->render('home/admin_index.html.twig', [
            'controller_name' => 'HomeController',
        ]);
    }

    public static function getFrontEndMenuItems()
    {
        return [
            MenuEntry::createMenuEntry("Home")->setRoute("home")->setSortorder(10)
        ];
    }
}

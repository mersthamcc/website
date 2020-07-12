<?php

namespace App\Controller;

use App\Data\MenuEntry;
use App\Menus\AdministrationMenuProvider;
use App\Menus\FrontEndMenuProvider;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

class NewsController extends AbstractController implements AdministrationMenuProvider, FrontEndMenuProvider
{
    /**
     * @Route("/news", name="news")
     */
    public function index()
    {
        return $this->render('news/index.html.twig', [
            'controller_name' => 'NewsController',
        ]);
    }

    /**
     * @Route("/administration/news", name="news_admin")
     */
    public function newsAdmin()
    {
        return $this->render('news/news_admin.html.twig', [

        ]);
    }

    /**
     * @Route("/administration/news/new", name="create_news")
     */
    public function createNews()
    {
        return $this->render('news/news_admin.html.twig', [

        ]);
    }

    public static function getAdminMenuItems()
    {
        return [
            MenuEntry::createMenuEntry("News")->setRoute(null)->setSortorder(10)->setChildren([
                MenuEntry::createMenuEntry("List News Items")->setRoute("news_admin")->setSortorder(10),
                MenuEntry::createMenuEntry("Create News Item")->setRoute("create_news")->setSortorder(20)
            ])
        ];
    }

    public static function getFrontEndMenuItems()
    {
        return [
            MenuEntry::createMenuEntry("News")->setRoute("news")->setSortorder(20)
        ];
    }
}

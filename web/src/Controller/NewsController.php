<?php

namespace App\Controller;

use App\Entity\BrowserCategory;
use App\Menus\AdministrationMenuProvider;
use App\Menus\FrontEndMenuProvider;
use App\Service\FinderService;
use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

class NewsController extends AbstractController implements AdministrationMenuProvider, FrontEndMenuProvider
{
    private $finderService;

    public function __construct(FinderService $finderService)
    {
        $this->finderService = $finderService;
    }

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
        $form = $this->createForm(\NewsForm::class, null, [
            'imageFinderConfig' => $this->finderService->createParameterString("/resources/news/111111", BrowserCategory::Image, false),
            'fileFinderConfig'  => $this->finderService->createParameterString("/resources/news/111111", BrowserCategory::File, false)

        ]);
        return $this->render('news/new.html.twig', [
            'form' => $form->createView()
        ]);
    }

    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getFrontEndMenuItems(FactoryInterface $factory): array
    {
        return [
            $factory->createItem("news", [
                'route' => 'news',
                'sortorder' => 10,
            ]),
        ];
    }

    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getContentAdminMenuItems(FactoryInterface $factory): array
    {
        return [
            $factory->createItem("news", [])->setExtra('icon', 'far fa-newspaper')
                ->setChildren([
                    $factory->createItem("news_admin", ['route' => 'news_admin'])->setExtra('roles', ['ROLE_NEWS'])
                ]),
        ];
    }

    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getAdministrationMenuItems(FactoryInterface $factory): array
    {
        return [];
    }

    /**
     * @param FactoryInterface $factory
     * @return ItemInterface[]
     */
    public static function getConfigurationAdminMenuItems(FactoryInterface $factory): array
    {
        return [];
    }
}

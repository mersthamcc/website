<?php

namespace App\Controller;

use App\Menus\FrontEndMenuProvider;
use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

class CricketController extends AbstractController implements FrontEndMenuProvider
{
    /**
     * @Route("/cricket", name="cricket")
     */
    public function index()
    {
        return $this->render('cricket/index.html.twig', [
            'controller_name' => 'CricketController',
        ]);
    }

    /**
     * @Route("/cricket/fixtures", name="fixtures")
     */
    public function fixtures()
    {
        return $this->render('cricket/index.html.twig', [
            'controller_name' => 'CricketController',
        ]);
    }

    /**
     * @Route("/cricket/selection", name="selection")
     */
    public function selection()
    {
        return $this->render('cricket/index.html.twig', [
            'controller_name' => 'CricketController',
        ]);
    }

    public static function getFrontEndMenuItems(FactoryInterface $factory): array
    {
        return [
            $factory->createItem("Cricket", [
                'route' => 'cricket',
                'sortorder' => 20,
            ])->setLabel("Cricket")->setChildren([
                $factory->createItem("Selection", [
                    'route' => 'selection',
                    'sortorder' => 10,
                ])->setLabel("Selection")->setChildren([
                    $factory->createItem('selection_saturday', [
                        'route' => 'selection',
                    ])->setLabel('Saturday'),
                    $factory->createItem('selection_sunday', [
                        'route' => 'selection',
                    ])->setLabel('Sunday'),
                ]),
                $factory->createItem("Fixtures", [
                    'route' => 'fixtures',
                    'sortorder' => 20,
                ])->setLabel("Fixtures"),
            ]),
        ];
    }
}

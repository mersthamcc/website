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
     * @Route("/cricket/selection/{day}", name="selection")
     * @param null|string $day
     * @return \Symfony\Component\HttpFoundation\Response
     */
    public function selection($day = null)
    {
        return $this->render('cricket/index.html.twig', [
            'controller_name' => 'CricketController',
        ]);
    }

    public static function getFrontEndMenuItems(FactoryInterface $factory): array
    {
        return [
            $factory->createItem("cricket", [
                'route' => 'cricket',
            ])->setChildren([
                $factory->createItem("selection", [
                    'route' => 'selection',
                ])->setChildren([
                    $factory->createItem('selection_saturday', [
                        'route' => 'selection',
                        'routeParameters' => [ 'day' => 'saturday'],
                    ]),
                    $factory->createItem('selection_sunday', [
                        'route' => 'selection',
                        'routeParameters' => [ 'day' => 'sunday'],
                    ]),
                ]),
                $factory->createItem("Fixtures", [
                    'route' => 'fixtures',
                ]),
            ]),
        ];
    }
}

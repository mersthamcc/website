<?php

namespace App\Controller;

use App\Menus\FrontEndMenuProvider;
use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

class ContactController extends AbstractController implements FrontEndMenuProvider
{
    /**
     * @Route("/contact", name="contacts")
     */
    public function index()
    {
        return $this->render('contact/index.html.twig', [
            'controller_name' => 'ContactController',
        ]);
    }

    /**
     * @Route("/contact/{category}/{slug}", name="contact")
     */
    public function contact($category, $slug)
    {
        return $this->render('contact/index.html.twig', [
            'controller_name' => 'ContactController',
        ]);
    }


    public static function getFrontEndMenuItems(FactoryInterface $factory): array
    {
        return [
            $factory->createItem("Contacts", [
                'sortorder' => 20,
            ])->setLabel("Contacts")->setChildren([
                $factory->createItem("Committee", [
                    'sortorder' => 10,
                ])->setLabel("Selection")->setChildren([
                    $factory->createItem('chairman', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'commmittee',
                            'slug' => 'chairman'
                        ]
                    ])->setLabel('Chairman'),
                    $factory->createItem('treasurer', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'commmittee',
                            'slug' => 'treasurer'
                        ]
                    ])->setLabel('Treasurer'),
                ]),
                $factory->createItem("captains", [
                    'sortorder' => 20,
                ])->setLabel("Adult Captains")->setChildren([
                    $factory->createItem('saturday_1st', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'captains',
                            'slug' => 'saturday_1st'
                        ]
                    ])->setLabel('Saturday 1st XI'),
                    $factory->createItem('saturday_2nd', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'captains',
                            'slug' => 'saturday_2nd'
                        ]
                    ])->setLabel('Saturday 2nd XI'),
                    $factory->createItem('saturday_3rd', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'captains',
                            'slug' => 'saturday_3rd'
                        ]
                    ])->setLabel('Saturday 3rd XI'),
                    $factory->createItem('saturday_4th', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'captains',
                            'slug' => 'saturday_4th'
                        ]
                    ])->setLabel('Saturday 4th XI'),
                ]),
            ])->setExtra('megaMenu', true),
        ];
    }}

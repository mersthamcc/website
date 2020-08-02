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
     * @Route("/contact/{category}", name="contacts", defaults={"category"="committee"})
     */
    public function index($category = null)
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
            $factory->createItem("contacts", [
                'route' => 'contacts',
                'sortorder' => 20,
            ])->setChildren([
                $factory->createItem("committee", [
                    'route' => 'contacts',
                    'routeParameters' => [
                        'category' => 'commmittee',
                    ],
                    'sortorder' => 10,
                ])->setChildren([
                    $factory->createItem('chairman', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'commmittee',
                            'slug' => 'chairman'
                        ]
                    ]),
                    $factory->createItem('treasurer', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'commmittee',
                            'slug' => 'treasurer'
                        ]
                    ]),
                ]),
                $factory->createItem("captains", [
                    'route' => 'contacts',
                    'routeParameters' => [
                        'category' => 'captains',
                    ],
                    'sortorder' => 20,
                ])->setChildren([
                    $factory->createItem('saturday_1st', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'captains',
                            'slug' => 'saturday_1st'
                        ]
                    ]),
                    $factory->createItem('saturday_2nd', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'captains',
                            'slug' => 'saturday_2nd'
                        ]
                    ]),
                    $factory->createItem('saturday_3rd', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'captains',
                            'slug' => 'saturday_3rd'
                        ]
                    ]),
                    $factory->createItem('saturday_4th', [
                        'route' => 'contact',
                        'routeParameters' => [
                            'category' => 'captains',
                            'slug' => 'saturday_4th'
                        ]
                    ]),
                ]),
            ])->setExtra('megaMenu', true),
        ];
    }}

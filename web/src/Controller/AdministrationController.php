<?php

namespace App\Controller;

use App\Menus\FrontEndMenuProvider;
use App\Menus\TopMenuProvider;
use Knp\Menu\FactoryInterface;
use Knp\Menu\ItemInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

class AdministrationController extends AbstractController
{
    /**
     * @Route("/administration/filebrowser", name="filebrowser")
     */
    public function ckfinder()
    {
        return $this->render('administration/ckfinder.html.twig');
    }
}

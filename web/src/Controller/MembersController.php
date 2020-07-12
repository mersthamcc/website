<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

class MembersController extends AbstractController
{
    /**
     * @Route("/members", name="members")
     */
    public function index()
    {
        return $this->render('members/index.html.twig', [
            'controller_name' => 'MembersController',
        ]);
    }
}

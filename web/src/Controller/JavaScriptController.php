<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;


class JavaScriptController extends AbstractController
{

    /**
     * @Route("/javascripts/cookie-control.js", name="cookie_control")
     *
     */
    public function cookieControl()
    {
        $response = new Response();
        $response->headers->set('Content-Type', 'text/javascript');
        return $this->render('java_script/cookie-control.js.twig', [

        ], $response);
    }
}

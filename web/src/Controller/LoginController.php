<?php

namespace App\Controller;

use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class LoginController extends AbstractController
{
    /**
     * @Route("/login", name="login")
     */
    public function login(ClientRegistry $clientRegistry, UrlGeneratorInterface $generator)
    {
        return $clientRegistry
            ->getClient('keycloak')
            ->redirect([
                'openid'
        ]);
    }

    /**
     * @Route("/login_check", name="login_check"), schemes={"https"}
     */
    public function loginCheck()
    {
    }

    /**
     * @Route("/logout", name="logout", methods={"GET"})
     */
    public function logout()
    {
        throw new \Exception('Don\'t forget to activate logout in security.yaml');
    }
}

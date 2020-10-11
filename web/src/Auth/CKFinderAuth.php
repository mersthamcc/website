<?php
namespace App\Auth;

use CKSource\Bundle\CKFinderBundle\Authentication\Authentication as AuthenticationBase;

class CKFinderAuth extends AuthenticationBase
{
    public function authenticate()
    {
        return true;
    }
}
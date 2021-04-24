<?php

namespace App\Utils;

class FormHelpers
{
    public static function arrayOf(string $className)
    {
        return $className . "[]";
    }
}

<?php

namespace App\Forms\Components;

use CricketDataBundle\Constraints\ValidRecaptchaToken;
use Symfony\Component\OptionsResolver\OptionsResolver;

class PanelType extends \Symfony\Component\Form\AbstractType
{
    public function getBlockPrefix()
    {
        return "panel";
    }

    public function buildView(
        \Symfony\Component\Form\FormView $view,
        \Symfony\Component\Form\FormInterface $form,
        array $options
    ) {
        $view->vars["title"] = $options["title"];
        $view->vars["type"] = $options["type"];
        parent::buildView($view, $form, $options);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        parent::configureOptions($resolver);
        $resolver
            ->setDefined(["title", "type"])
            ->setDefined(["title"])
            ->setDefault("type", "info")
            ->setAllowedTypes("title", "string")
            ->setAllowedTypes("type", "string");
    }
}

<?php

namespace App\Forms\Components;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormView;
use Symfony\Component\OptionsResolver\OptionsResolver;

class PanelType extends AbstractType
{
    /**
     * @inheritDoc
     */
    public function getBlockPrefix(): string
    {
        return "panel";
    }

    /**
     * @inheritDoc
     */
    public function buildView(
        FormView $view,
        FormInterface $form,
        array $options
    ) {
        $view->vars["title"] = $options["title"];
        $view->vars["type"] = $options["type"];
        parent::buildView($view, $form, $options);
    }

    /**
     * @inheritDoc
     */
    public function configureOptions(OptionsResolver $resolver)
    {
        parent::configureOptions($resolver);
        $resolver
            ->setDefined(["title", "type"])
            ->setDefined(["title"])
            ->setDefault("type", "info")
            ->setAllowedTypes("title", "string")
            ->setAllowedTypes("type", "string")
            ->setDefaults([
                "mapped" => false,
                "compound" => true,
                "inherit_data" => true,
            ]);
    }
}

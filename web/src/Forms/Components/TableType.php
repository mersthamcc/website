<?php

namespace App\Forms\Components;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormView;
use Symfony\Component\OptionsResolver\OptionsResolver;

class TableType extends AbstractType
{
    public function buildView(
        FormView $view,
        FormInterface $form,
        array $options
    ) {
        $view->vars["columns"] = $options["columns"];
        $view->vars["id"] = $options["id"];
        $view->vars["actions"] = $options["row-actions"];
        $view->vars["selectable"] = $options["selectable"];
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver
            ->setDefined(["columns", "selectable", "id", "row-actions"])
            ->setRequired(["columns"])
            ->setDefault("selectable", false)
            ->setDefault("id", "id")
            ->setAllowedTypes("columns", "iterable")
            ->setAllowedTypes("selectable", "bool")
            ->setAllowedTypes("id", "string")
            ->setAllowedTypes("row-actions", "iterable");
    }

    public function getBlockPrefix()
    {
        return "table_form";
    }
}

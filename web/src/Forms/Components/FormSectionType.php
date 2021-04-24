<?php
namespace App\Forms\Components;

use Symfony\Component\Form\AbstractType;

class FormSectionType extends AbstractType
{
    /**
     * @inheritDoc
     */
    public function configureOptions(
        \Symfony\Component\OptionsResolver\OptionsResolver $resolver
    ) {
        $resolver->setDefaults([
            "inherit_data" => true,
            "mapped" => false,
        ]);
    }

    /**
     * @inheritDoc
     */
    public function getBlockPrefix()
    {
        return "form_section";
    }
}

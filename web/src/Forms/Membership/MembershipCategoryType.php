<?php

namespace App\Forms\Membership;

use App\Entity\MemberCategory;
use App\Utils\FormHelpers;
use Symfony\Component\Form\Extension\Core\DataMapper\RadioListMapper;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormView;
use Symfony\Component\OptionsResolver\OptionsResolver;

class MembershipCategoryType extends IntegerType
{
    /**
     * @inheritDoc
     */
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        parent::buildForm($builder, $options);
        $builder->setDataMapper(new RadioListMapper());
    }

    /**
     * @inheritDoc
     */
    public function configureOptions(OptionsResolver $resolver)
    {
        parent::configureOptions($resolver);
        $resolver
            ->setDefined("choices")
            ->setRequired("choices")
            ->setAllowedTypes(
                "choices",
                FormHelpers::arrayOf(MemberCategory::class)
            )
            ->setDefault("label", "select");
    }

    /**
     * @inheritDoc
     */
    public function buildView(
        FormView $view,
        FormInterface $form,
        array $options
    ) {
        $view->vars["choices"] = $options["choices"];
    }

    /**
     * @inheritDoc
     */
    public function getBlockPrefix()
    {
        return "membershipcategory";
    }
}

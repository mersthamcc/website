<?php

namespace App\Forms\Membership;

use App\Entity\MemberCategory;
use App\Entity\Subscription;
use App\Forms\Components\PanelType;
use App\Utils\FormHelpers;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ChooseMembershipForm extends AbstractType
{
    /**
     * @inheritDoc
     */
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        parent::buildForm($builder, $options);
        $panel = $builder->create("subscription", PanelType::class, [
            "title" => "choose-membership-type",
            "translation_domain" => "membership",
        ]);

        $panel->add("priceListItemId", MembershipCategoryType::class, [
            "choices" => $options["choices"],
        ]);
        $builder->add($panel);
    }

    /**
     * @inheritDoc
     */
    public function configureOptions(OptionsResolver $resolver)
    {
        parent::configureOptions($resolver);
        $resolver
            ->setAllowedTypes("data", Subscription::class)
            ->setDefined("choices")
            ->setRequired("choices")
            ->setAllowedTypes(
                "choices",
                FormHelpers::arrayOf(MemberCategory::class)
            );
    }

    /**
     * @inheritDoc
     */
    public function getBlockPrefix(): string
    {
        return "choosemembership";
    }
}

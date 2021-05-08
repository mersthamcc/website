<?php
namespace App\Forms\Membership;

use App\Entity\PriceListItem;
use App\Entity\Subscription;
use App\Utils\FormHelpers;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormView;
use Symfony\Component\OptionsResolver\OptionsResolver;

class PricelistType extends AbstractType
{
    /**
     * @inheritDoc
     */
    public function buildView(
        FormView $view,
        FormInterface $form,
        array $options
    ) {
        parent::buildView($view, $form, $options);
        $view->vars["choices"] = $options["choices"];
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
                "data",
                FormHelpers::arrayOf(PriceListItem::class)
            )
            ->setDefault("mapped", true)
            ->setDefault("inherit_data", false)
            ->setDefault("label", "select")
            ->setDefault("data_class", Subscription::class);
    }

    /**
     * @inheritDoc
     */
    public function getBlockPrefix()
    {
        return "pricelist";
    }
}

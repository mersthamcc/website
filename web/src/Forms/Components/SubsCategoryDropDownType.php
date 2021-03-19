<?php

namespace App\Forms\Components;

use App\ApiClient\MemberRegistrationService;
use Symfony\Component\Form\ChoiceList\Factory\ChoiceListFactoryInterface;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormView;

class SubsCategoryDropDownType extends ChoiceType
{
    /**
     * @var $registrationService MemberRegistrationService
     */
    private $registrationService;

    public function __construct(
        MemberRegistrationService $registrationService,
        ChoiceListFactoryInterface $choiceListFactory = null
    ) {
        parent::__construct($choiceListFactory);
        $this->registrationService = $registrationService;
    }

    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $choices = [];
        foreach (
            $this->registrationService->getMembershipCategories()
            as $value
        ) {
            $choices[$value->getKey()] = $value->getKey();
        }
        parent::buildForm(
            $builder,
            array_merge($options, [
                "choices" => [
                    "active" => $choices,
                    "inactive" => [
                        "no-renewal" => "no-renewal",
                        "delete" => "delete",
                    ],
                ],
                "choice_translation_domain" => "membership",
            ])
        );
    }

    public function buildView(
        FormView $view,
        FormInterface $form,
        array $options
    ) {
        $view->vars["attr"] = [
            "class" => "form-control c-square c-theme input-lg",
        ];
        parent::buildView($view, $form, $options);
    }
}

<?php

namespace App\Forms\Membership;

use App\Forms\Components\ButtonSectionType;
use App\Forms\Components\FormSectionType;
use App\Forms\Components\PanelType;
use App\Forms\Components\SubsCategoryDropDownType;
use App\Forms\Components\TableColumn;
use App\Forms\Components\TableRowAction;
use App\Forms\Components\TableType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ResetType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class MemberListForm extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder->add(
            $builder
                ->create("panel", PanelType::class, [
                    "title" => "member-details",
                ])
                ->add("my-members", FormSectionType::class, [
                    "label" => "my-members",
                ])
                ->add("Members", TableType::class, [
                    "data" => $options["data"]->getItems(),
                    "selectable" => true,
                    "id" => "registrationId",
                    "mapped" => false,
                    "columns" => [
                        new TableColumn("family-name", "family-name", ""),
                        new TableColumn("given-name", "given-name", ""),
                        new TableColumn("category", null, null),
                        new TableColumn("age", null, null),
                        new TableColumn("renewalcost", null, null),
                    ],
                    "row-actions" => [
                        new TableRowAction(
                            "edit",
                            "Review Details",
                            "fa fa-edit",
                            "info"
                        ),
                    ],
                ])
                ->add("add-member", SubmitType::class, [])
                ->add(
                    $builder
                        ->create("buttons", ButtonSectionType::class)
                        ->add("reset", SubmitType::class, ["label" => "Clear"])
                        ->add("save", SubmitType::class, ["label" => "Next"])
                )
        );
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        parent::configureOptions($resolver);
        $resolver->setDefaults([
            //            "data_class" => "${Member::class}[]",
            "csrf_protection" => false,
            // the name of the hidden HTML field that stores the token
            "csrf_field_name" => "_token",
            // an arbitrary string used to generate the value of the token
            // using a different string for each form improves its security
            "csrf_token_id" => self::class,
            "allow_extra_fields" => true,
        ]);
    }

    public function getBlockPrefix()
    {
        return parent::getBlockPrefix();
    }
}

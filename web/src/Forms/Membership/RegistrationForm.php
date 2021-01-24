<?php

namespace App\Forms\Membership;

use App\Forms\Components\ButtonSectionType;
use App\Forms\Components\FormSectionType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ResetType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class RegistrationForm extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add(
                $builder
                    ->create("basics", FormSectionType::class, [])
                    ->add("givenName", TextType::class, [])
                    ->add("familyName", TextType::class, [])
            )
            ->add(
                $builder
                    ->create("buttons", ButtonSectionType::class, [])
                    ->add("save", SubmitType::class, [
                        "label" => "member-btn-register",
                    ])
                    ->add("save", ResetType::class, ["label" => "cancel"])
            );
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        parent::configureOptions($resolver);
        $resolver->setDefaults([
            "data_class" => Member::class,
            // enable/disable CSRF protection for this form
            "csrf_protection" => true,
            // the name of the hidden HTML field that stores the token
            "csrf_field_name" => "_token",
            // an arbitrary string used to generate the value of the token
            // using a different string for each form improves its security
            "csrf_token_id" => self::class,
            "allow_extra_fields" => true,
        ]);
    }
}

<?php

use App\Form\Components\ButtonSectionType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ResetType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\Extension\Core\Type\TextType;

class NewsForm extends AbstractType
{
    public function buildForm(\Symfony\Component\Form\FormBuilderInterface $builder, array $options)
    {
        $builder->add('title', TextType::class)
            ->add('body', \FOS\CKEditorBundle\Form\Type\CKEditorType::class)
            ->add($builder->create('buttons', ButtonSectionType::class)
                ->add('save', SubmitType::class, array('label' => 'Save Details'))
                ->add('reset', ResetType::class, array('label' => 'Reset')
                )
            );
    }

}
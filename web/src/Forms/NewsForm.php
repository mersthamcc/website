<?php

namespace App\Forms;

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
            ->add('body', \FOS\CKEditorBundle\Form\Type\CKEditorType::class, array(
                'config_name' => 'admin_config',
                'config' => [
                    'filebrowserBrowseRouteParameters' => [
                        'finderConfig' => $options['fileFinderConfig']
                    ],
                    'filebrowserImageBrowseRouteParameters' => [
                        'finderConfig' => $options['imageFinderConfig']
                    ],
                ]
            ))
            ->add($builder->create('buttons', ButtonSectionType::class)
                ->add('save', SubmitType::class, array('label' => 'Save Details'))
                ->add('reset', ResetType::class, array('label' => 'Reset'))
            );
    }

    /**
     * {@inheritDoc}
     * @see \Symfony\Component\Form\AbstractType::configureOptions()
     */
    public function configureOptions(\Symfony\Component\OptionsResolver\OptionsResolver $resolver)
    {
        parent::configureOptions($resolver);
//        $resolver->setDefaults(array(
//            'csrf_protection' => true,
//            'csrf_field_name' => '_token',
//            'csrf_token_id'   => self::class,
//            'allow_extra_fields' => true,
//        ));
        $resolver->setDefined(array('imageFinderConfig', 'fileFinderConfig'));
        $resolver->setRequired(array('imageFinderConfig', 'fileFinderConfig'));

    }
}
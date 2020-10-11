<?php
namespace App\Form\Components;

use Symfony\Component\Form\Extension\Core\Type\BaseType;
use CricketDataBundle\Constraints\ValidRecaptchaToken;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;

/**
 *
 * @author chris
 *
 */
class ReCaptchaType extends TextareaType
{
    /**
     * {@inheritDoc}
     * @see \Symfony\Component\Form\AbstractType::buildView()
     */
    public function buildView(\Symfony\Component\Form\FormView $view, \Symfony\Component\Form\FormInterface $form, array $options)
    {
        $view->vars['sitekey'] = $options['sitekey'];
        $options['constraints'] = array(
            new ValidRecaptchaToken($options)
        );
        parent::buildView($view, $form, $options);
    }

    /**
     * {@inheritDoc}
     * @see \Symfony\Component\Form\AbstractType::configureOptions()
     */
    public function configureOptions(\Symfony\Component\OptionsResolver\OptionsResolver $resolver)
    {
        $resolver->setDefault('mapped', true);
        $resolver->setDefined(array('sitekey', 'secret'));
        $resolver->setRequired(array('sitekey', 'secret'));

    }

    /**
     * {@inheritDoc}
     * @see \Symfony\Component\Form\AbstractType::getBlockPrefix()
     */
    public function getBlockPrefix()
    {
        return 'recaptcha';
    }

}


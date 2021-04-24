<?php

namespace App\Forms\Components;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormView;

class TableColumn
{
    /**
     * @var $header string|null
     */
    private $header;

    /**
     * @var $field string|null
     */
    private $field;

    /**
     * @var $footer string|null
     */
    private $footer;

    /**
     * @var $form FormInterface|null
     */
    private $form;

    /**
     * TableColumn constructor.
     * @param string|null $header
     * @param string|null $field
     * @param string|null $footer
     * @param FormInterface|null $form
     */
    public function __construct(
        ?string $header,
        ?string $field = null,
        ?string $footer = null,
        ?FormInterface $form = null
    ) {
        $this->header = $header;
        $this->field = $field;
        $this->footer = $footer;
        $this->form = $form;
    }

    /**
     * @return string|null
     */
    public function getHeader(): ?string
    {
        return $this->header;
    }

    /**
     * @return string|null
     */
    public function getField(): ?string
    {
        return $this->field;
    }

    /**
     * @return string|null
     */
    public function getFooter(): ?string
    {
        return $this->footer;
    }

    /**
     * @return FormInterface|null
     */
    public function getForm(): ?FormInterface
    {
        return $this->form;
    }

    /**
     * @return FormView
     */
    public function getFormView(): ?FormView
    {
        if ($this->form) {
            return $this->form->createView();
        }
        return null;
    }
}

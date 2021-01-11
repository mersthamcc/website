<?php

namespace App\Entity;

use App\Repository\NewsAttributeRepository;
use Doctrine\ORM\Mapping as ORM;

class NewsAttribute
{
    private $id;
    private $name;
    private $value;
    private $news;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getName(): ?string
    {
        return $this->name;
    }

    public function setName(string $name): self
    {
        $this->name = $name;

        return $this;
    }

    public function getValue(): ?string
    {
        return $this->value;
    }

    public function setValue(?string $value): self
    {
        $this->value = $value;

        return $this;
    }

    public function getNews(): ?News
    {
        return $this->news;
    }

    public function setNews(?News $news): self
    {
        $this->news = $news;

        return $this;
    }
}

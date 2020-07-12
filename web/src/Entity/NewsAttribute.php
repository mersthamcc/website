<?php

namespace App\Entity;

use App\Repository\NewsAttributeRepository;
use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity(repositoryClass=NewsAttributeRepository::class)
 */
class NewsAttribute
{
    /**
     * @ORM\Id()
     * @ORM\GeneratedValue(strategy="IDENTITY")
     * @ORM\Column(type="integer")
     */
    private $id;

    /**
     * @ORM\Column(type="string", length=64)
     */
    private $name;

    /**
     * @ORM\Column(type="string", length=1024, nullable=true)
     */
    private $value;

    /**
     * @ORM\ManyToOne(targetEntity=News::class, inversedBy="newsAttributes")
     * @ORM\JoinColumn(nullable=false)
     */
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

<?php

namespace App\Entity;

use App\Repository\NewsRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;

class News
{
    private $id;
    private $created_date;
    private $publish_date;
    private $title;
    private $body;
    private $newsAttributes;
    private $newsComments;

    public function __construct()
    {
        $this->newsAttributes = new ArrayCollection();
        $this->newsComments = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getCreatedDate(): ?\DateTimeInterface
    {
        return $this->created_date;
    }

    public function setCreatedDate(\DateTimeInterface $created_date): self
    {
        $this->created_date = $created_date;
    }

    public function getPublishDate(): ?\DateTimeInterface
    {
        return $this->publish_date;
    }

    public function setPublishDate(\DateTimeInterface $publish_date): self
    {
        $this->publish_date = $publish_date;

        return $this;
    }

    public function getTitle(): ?string
    {
        return $this->title;
    }

    public function setTitle(string $title): self
    {
        $this->title = $title;

        return $this;
    }

    public function getBody(): ?string
    {
        return $this->body;
    }

    public function setBody(string $body): self
    {
        $this->body = $body;

        return $this;
    }

    /**
     * @return Collection|NewsAttribute[]
     */
    public function getNewsAttributes(): Collection
    {
        return $this->newsAttributes;
    }

    public function addNewsAttribute(NewsAttribute $newsAttribute): self
    {
        if (!$this->newsAttributes->contains($newsAttribute)) {
            $this->newsAttributes[] = $newsAttribute;
            $newsAttribute->setNews($this);
        }

        return $this;
    }

    public function removeNewsAttribute(NewsAttribute $newsAttribute): self
    {
        if ($this->newsAttributes->contains($newsAttribute)) {
            $this->newsAttributes->removeElement($newsAttribute);
            // set the owning side to null (unless already changed)
            if ($newsAttribute->getNews() === $this) {
                $newsAttribute->setNews(null);
            }
        }

        return $this;
    }

    /**
     * @return Collection|NewsComment[]
     */
    public function getNewsComments(): Collection
    {
        return $this->newsComments;
    }

    public function addNewsComment(NewsComment $newsComment): self
    {
        if (!$this->newsComments->contains($newsComment)) {
            $this->newsComments[] = $newsComment;
            $newsComment->setNews($this);
        }

        return $this;
    }

    public function removeNewsComment(NewsComment $newsComment): self
    {
        if ($this->newsComments->contains($newsComment)) {
            $this->newsComments->removeElement($newsComment);
            // set the owning side to null (unless already changed)
            if ($newsComment->getNews() === $this) {
                $newsComment->setNews(null);
            }
        }

        return $this;
    }
}

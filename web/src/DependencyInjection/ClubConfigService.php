<?php


namespace App\DependencyInjection;

use App\Utils\PhoneNumber;

class ClubConfigService
{
    private $config;

    public function __construct($config)
    {
        $this->config = $config;
    }

    public function getClubName(): ?string
    {
        return $this->config['name'];
    }

    public function getLogo(): ?string
    {
        return $this->config['logo'];
    }

    public function getPhoneNumber(): ?PhoneNumber
    {
        return new PhoneNumber($this->config['phone']);
    }

    public function isPlayCricketEnabled(): ?bool
    {
        return $this->config['playcricket']['enabled'];
    }

    public function getPlayCricketSubSite(): ?string
    {
        return $this->config['playcricket']['subsitePrefix'];
    }

    public function getTwitterFeedEnabled(): bool
    {
        return $this->config['social']['twitter']['feedEnabled'];
    }

    public function getTwitterHandle(): ?string
    {
        return $this->config['social']['twitter']['handle'];
    }
    
    public function getFacebookHandle(): ?string
    {
        return $this->config['social']['facebook']['handle'];
    }

    public function getCookieConsentApiKey(): ?string
    {
        return $this->config['cookies']['apiKey'];
    }

    public function getCookieConsentProductCode(): ?string
    {
        return $this->config['cookies']['product'];
    }

    public function getGoogleAnalyticsKey(): ?string
    {
        return $this->config['analytics']['googleAnalyticsKey'];
    }
}
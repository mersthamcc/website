<?php


namespace App\Service;


use App\DependencyInjection\Configuration;
use App\Utils\PhoneNumber;
use Symfony\Component\Config\Definition\Processor;

class ClubConfigService
{
    private $config = null;

    public function __construct(array $config)
    {
        $this->config = $config;
    }

    public function getClubName(): ?string
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['name'];
    }

    public function getLogo(): ?string
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['logo'];
    }

    public function getPhoneNumber(): ?PhoneNumber
    {
        if ( $this->config == null ) $this->loadConfig();
        return new PhoneNumber($this->config['phone']);
    }

    public function isPlayCricketEnabled(): ?bool
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['playcricket']['enabled'];
    }

    public function getPlayCricketSubsite(): ?string
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['playcricket']['subsitePrefix'];
    }

    public function getTwitterFeedEnabled(): bool
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['social']['twitter']['feedEnabled'];
    }

    public function getTwitterHandle(): ?string
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['social']['twitter']['handle'];
    }
    
    public function getFacebookHandle(): ?string
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['social']['facebook']['handle'];
    }

    public function getCookieConsentApiKey(): ?string
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['cookies']['apiKey'];
    }

    public function getCookieConsentProductCode(): ?string
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['cookies']['product'];
    }

    public function getGoogleAnalyticsKey(): ?string
    {
        if ( $this->config == null ) $this->loadConfig();
        return $this->config['analytics']['googleAnalyticsKey'];
    }



    private function loadConfig()
    {
        $processor = new Processor();

        $this->config = $processor->processConfiguration(
            $this,
            [$this->configEntries]
        );
    }
}
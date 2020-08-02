<?php


namespace App\Service;


use App\Utils\PhoneNumber;
use Symfony\Component\Config\Definition\Builder\TreeBuilder;
use Symfony\Component\Config\Definition\ConfigurationInterface;
use Symfony\Component\Config\Definition\Processor;
use Symfony\Component\Yaml\Yaml;

class ClubConfigService implements ConfigurationInterface
{
    private $config = null;
    private $configFile;

    public function __construct(string $configFile)
    {
        $this->configFile = $configFile;
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


    public function getConfigTreeBuilder()
    {
        $treeBuilder = new TreeBuilder("club");

        $rootNode = $treeBuilder->getRootNode();

        $rootNode
            ->isRequired()
            ->children()
                ->scalarNode("name")
                    ->info("The name of the club")
                    ->isRequired()
                ->end()
                ->scalarNode("logo")
                    ->info("Path to the logo file")
                    ->isRequired()
                ->end()
                ->scalarNode("phone")
                    ->info("The primary contact number of the club; displayed on all pages")
                    ->isRequired()
                ->end()
                ->arrayNode("playcricket")
                    ->info("The clubs PlayCricket configuration")
                    ->children()
                        ->booleanNode("enabled")
                            ->info("Is PlayCricket integration on")
                            ->defaultTrue()
                        ->end()
                        ->scalarNode("subsitePrefix")
                            ->info("The prefix of the club's Play-Cricket website")
                        ->end()
                        ->scalarNode("siteId")
                            ->info("The prefix of the club's Play-Cricket website")
                        ->end()
                    ->end()
                ->end()
                ->arrayNode("social")
                    ->info("Details for linking to the club's social media accounts")
                    ->children()
                        ->arrayNode("twitter")
                            ->info("The twitter account details")
                            ->children()
                                ->booleanNode("feedEnabled")
                                    ->info("The Latest Tweets feed is enabled in the footer")
                                    ->defaultFalse()
                                ->end()
                                ->scalarNode("handle")
                                    ->info("The Twitter handle; used to provide the feed at the bottom of the page")
                                    ->isRequired()
                                ->end()
                                ->scalarNode("apiKey")
                                    ->info("API Key for Automatic posting to Twitter")
                                ->end()
                                ->scalarNode("apiSecret")
                                    ->info("API Secret for Automatic posting to Twitter")
                                ->end()
                            ->end()
                        ->end()
                        ->arrayNode("facebook")
                            ->info("The Facebook account details")
                            ->children()
                                ->scalarNode("handle")
                                    ->info("The Facebook handle; used to provide the link at the top of the page")
                                ->end()
                                ->scalarNode("apiKey")
                                    ->info("API Key for Automatic posting to Facebook")
                                ->end()
                                ->scalarNode("apiSecret")
                                    ->info("API Secret for Automatic posting to Facebook")
                                ->end()
                            ->end()
                        ->end()
                    ->end()
                ->end()
                ->arrayNode("cookies")
                    ->info("The cookie consent configuration")
                    ->children()
                        ->scalarNode("apiKey")
                            ->info("The Cookie Consent API key")
                        ->end()
                        ->scalarNode("product")
                            ->info("The cookie consent configuration")
                            ->defaultValue("PRO")
                        ->end()
                    ->end()
                ->end()
                ->arrayNode("analytics")
                    ->children()
                        ->scalarNode("googleAnalyticsKey")
                            ->info("The Google Analytics Key for this Property")
                        ->end()
                    ->end()
                ->end()
            ->end();
        return $treeBuilder;
    }

    private function loadConfig()
    {
        $processor = new Processor();
        $configFile = Yaml::parse(file_get_contents($this->configFile));
        $this->config = $processor->processConfiguration(
            $this,
            [$configFile]
        );
    }
}
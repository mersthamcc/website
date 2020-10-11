<?php


namespace App\Factory;


use App\Service\FinderService;
use CKSource\Bundle\CKFinderBundle\Factory\ConnectorFactory;
use Psr\Log\LoggerInterface;
use Symfony\Component\DependencyInjection\ContainerAwareInterface;
use Symfony\Component\DependencyInjection\ContainerAwareTrait;

class FinderFactory extends ConnectorFactory
{
    private $finderService;
    private $logger;

    public function __construct($connectorConfig, $authenticationService, FinderService $finderService, LoggerInterface $logger)
    {
        parent::__construct($connectorConfig, $authenticationService);
        $this->finderService = $finderService;
        $this->logger = $logger;
        $this->logger->debug("Factory config", [ "config" => $this->connectorConfig ]);
    }

    public function getConnector()
    {
        $config = $this->finderService->decodeParameterString();
        $this->connectorConfig = array_merge($this->connectorConfig, $config);
        $this->logger->debug("Creating with config", [ "config" => $this->connectorConfig ]);
        return parent::getConnector();
    }
}
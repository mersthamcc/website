<?php


namespace App\Factory;


use App\Service\FinderService;
use CKSource\Bundle\CKFinderBundle\Factory\ConnectorFactory;

class FinderFactory extends ConnectorFactory
{
    private $finderService;

    public function __construct($connectorConfig, $authenticationService, FinderService $finderService)
    {
        parent::__construct($connectorConfig, $authenticationService);
        $this->finderService = $finderService;
    }

    public function getConnector()
    {
        $config = $this->finderService->decodeParameterString();
        $this->connectorConfig = $this->array_merge_recursive_ex($this->connectorConfig, $config);
        return parent::getConnector();
    }

    private function array_merge_recursive_ex(array $array1, array $array2)
    {
        $merged = $array1;

        foreach ($array2 as $key => & $value) {
            if (is_array($value) && isset($merged[$key]) && is_array($merged[$key])) {
                $merged[$key] = $this->array_merge_recursive_ex($merged[$key], $value);
            } else if (is_numeric($key)) {
                if (!in_array($value, $merged)) {
                    $merged[] = $value;
                }
            } else {
                $merged[$key] = $value;
            }
        }

        return $merged;
    }
}
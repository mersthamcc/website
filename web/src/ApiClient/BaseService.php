<?php

namespace App\ApiClient;

use GraphQL\Results;
use Psr\Log\LoggerInterface;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\PropertyNormalizer;
use Symfony\Component\Serializer\Serializer;

abstract class BaseService
{
    /**
     * @var ApiClient
     */
    protected $client;

    /**
     * @var LoggerInterface
     */
    protected $logger;

    public function __construct(ApiClient $client, LoggerInterface $logger)
    {
        $this->client = $client;
        $this->logger = $logger;
    }

    protected function loadGraphQL(string $filename): string
    {
        return file_get_contents(__DIR__ . "/graphql/" . $filename);
    }

    protected function deserializeResult(
        Results $result,
        string $operationName,
        string $class
    ) {
        $this->logger->debug("API response received", [
            "result" => $result,
            "raw" => $result->getResponseBody(),
        ]);

        if ($result->getData()->{$operationName} == null) {
            return null;
        }

        $encoders = [new JsonEncoder()];
        $normalizers = [new PropertyNormalizer()];

        $serializer = new Serializer($normalizers, $encoders);
        $json = json_encode($result->getData()->{$operationName});

        $this->logger->debug("Serialising '$json' to $class");
        return $serializer->deserialize($json, $class, "json");
    }
}

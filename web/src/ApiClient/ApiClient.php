<?php

namespace App\ApiClient;

use GraphQL\Client;
use GraphQL\Results;
use Psr\Log\LoggerInterface;
use Symfony\Component\PropertyInfo\Extractor\PhpDocExtractor;
use Symfony\Component\PropertyInfo\Extractor\ReflectionExtractor;
use Symfony\Component\PropertyInfo\PropertyInfoExtractor;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ArrayDenormalizer;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Normalizer\PropertyNormalizer;
use Symfony\Component\Serializer\Serializer;

class ApiClient
{
    /**
     * @var TokenService $tokenService
     */
    private $tokenService;

    /**
     * @var string $apiBaseUrl
     */
    private $apiBaseUrl;

    /**
     * @var LoggerInterface $logger
     */
    private $logger;

    /**
     * @var ApiResponseSerializer
     */
    private $serializer;

    public function __construct(
        TokenService $tokenService,
        string $apiBaseUrl,
        LoggerInterface $logger,
        ApiResponseSerializer $serializer
    ) {
        $this->tokenService = $tokenService;
        $this->apiBaseUrl = $apiBaseUrl;
        $this->logger = $logger;
        $this->serializer = $serializer;
    }

    public function getClient(bool $trustedToken = false): Client
    {
        $accessToken = $this->tokenService->getAccessToken($trustedToken);
        return new Client($this->apiBaseUrl, [
            "Authorization" => "Bearer " . $accessToken->getToken(),
        ]);
    }

    public function __call($method, $args)
    {
        $argCount = count($args, COUNT_NORMAL);
        if (!($argCount == 2 || $argCount == 3)) {
            throw new \ArgumentCountError(
                "Must have exactly 2 or 3 parameters (got $argCount), a type, an array of parameters and an option flag to indicate whether trusted calls should be made."
            );
        }
        $type = $args[0];
        $params = $args[1];
        $trusted = isset($args[2]) ? $args[2] : false;
        if (!class_exists($type, true)) {
            throw new \ArgumentCountError(
                "Argument 1 does not contain a valid class name"
            );
        }
        if (!($params == null || $this->isAssociativeArray($params))) {
            throw new \ArgumentCountError(
                "Argument 2 must be null or an associative array of parameters"
            );
        }
        $gql = $this->loadGraphQL("$method.graphql");

        $result = $this->getClient($trusted)->runRawQuery($gql, false, $params);
        $this->logger->debug("Graph Result Received", [
            "method" => $method,
            "result" => $result->getResponseBody(),
        ]);
        return $this->deserializeResult($result, $method);
    }

    private function isAssociativeArray($value): bool
    {
        return is_array($value) &&
            array_keys($value) !== range(0, count($value) - 1);
    }

    private function loadGraphQL(string $filename): string
    {
        return file_get_contents(__DIR__ . "/graphql/" . $filename);
    }

    private function deserializeResult(Results $result, string $operationName)
    {
        if ($result->getData() == null) {
            return null;
        }
        $response = $this->serializer->deserialize($result->getResponseBody());
        $this->logger->debug("Deserialised the result", [
            '$data' => print_r($response, true),
        ]);
        return $response->getData()->get($operationName);
    }
}

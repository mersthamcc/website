<?php

namespace App\ApiClient;

use GraphQL\Client;
use GraphQL\Results;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\PropertyNormalizer;
use Symfony\Component\Serializer\Serializer;

class ApiClient
{
    /**
     * @var $tokenService TokenService
     */
    private $tokenService;

    /**
     * @var $apiBaseUrl string
     */
    private $apiBaseUrl;

    public function __construct(TokenService $tokenService, string $apiBaseUrl)
    {
        $this->tokenService = $tokenService;
        $this->apiBaseUrl = $apiBaseUrl;
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
        if ($argCount != 2) {
            throw new \ArgumentCountError(
                "Must have exactly 2 parameters (got $argCount), a type and an array of parameters"
            );
        }
        $type = $args[0];
        $params = $args[1];
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

        $result = $this->getClient(true)->runRawQuery($gql, false, $params);
        return $this->deserializeResult($result, $method, $type);
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

    private function deserializeResult(
        Results $result,
        string $operationName,
        string $class
    ) {
        if ($result->getData()->{$operationName} == null) {
            return null;
        }
        return $this->deserializeObject(
            $result->getData()->{$operationName},
            $class
        );
    }

    private function deserializeObject($object, string $class)
    {
        if (is_array($object)) {
            return array_map(function ($item) use ($class) {
                return $this->deserializeObject($item, $class);
            });
        }
        $encoders = [new JsonEncoder()];
        $normalizers = [new PropertyNormalizer()];

        $serializer = new Serializer($normalizers, $encoders);
        $json = json_encode($object);

        return $serializer->deserialize($json, $class, "json");
    }
}

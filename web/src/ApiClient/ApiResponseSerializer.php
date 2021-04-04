<?php

namespace App\ApiClient;

use Symfony\Component\PropertyInfo\Extractor\PhpDocExtractor;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ArrayDenormalizer;
use Symfony\Component\Serializer\Normalizer\DateTimeNormalizer;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;

class ApiResponseSerializer
{
    /**
     * @var Serializer $serializer
     */
    private $serializer;

    public function __construct()
    {
        $encoders = [new JsonEncoder()];
        $normalizers = [
            new DateTimeNormalizer(),
            new ObjectNormalizer(null, null, null, new PhpDocExtractor()),
            new ArrayDenormalizer(),
        ];

        $this->serializer = new Serializer($normalizers, $encoders);
    }

    /**
     * @param string $body
     * @return ApiResponse
     */
    public function deserialize(string $body)
    {
        return $this->serializer->deserialize(
            $body,
            ApiResponse::class,
            "json"
        );
    }
}

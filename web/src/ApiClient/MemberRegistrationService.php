<?php

namespace App\ApiClient;

use App\Entity\MemberCategory;
use Psr\Log\LoggerInterface;

class MemberRegistrationService
{
    /**
     * @var ApiClient
     */
    private $client;

    /**
     * @var LoggerInterface
     */
    private $logger;

    /**
     * MemberRegistrationService constructor.
     * @param ApiClient $client
     * @param LoggerInterface $logger
     */
    public function __construct(ApiClient $client, LoggerInterface $logger)
    {
        $this->client = $client;
        $this->logger = $logger;
    }

    /**
     * @return array
     */
    public function getMembershipCategories(): array
    {
        return $this->client->membershipCategories(MemberCategory::class, []);
    }
}

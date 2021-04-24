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
     * @return MemberCategory[]
     */
    public function getMembershipCategories(
        \DateTime $priceListEffectiveDate = null
    ): array {
        if ($priceListEffectiveDate == null) {
            $priceListEffectiveDate = new \DateTime();
        }
        return $this->client->membershipCategories(MemberCategory::class, [
            "priceListEffectiveDate" => $priceListEffectiveDate->format(
                DATE_RFC3339_EXTENDED
            ),
        ]);
    }

    /**
     * @param string $categoryName
     * @return MemberCategory
     */
    public function getCategory(string $categoryName): MemberCategory
    {
        $categories = $this->getMembershipCategories();
        foreach ($categories as $category) {
            if ($category->getKey() === $categoryName) {
                $this->logger->debug("Category selected", [
                    "category" => print_r($category, true),
                ]);
                return $category;
            }
        }
    }

    /**
     * @param int $priceListItemId
     * @return MemberCategory
     */
    public function getCategoryByPriceListItemId(
        int $priceListItemId
    ): MemberCategory {
        $categories = $this->getMembershipCategories();
        foreach ($categories as $category) {
            foreach ($category->getPriceList() as $priceListItem) {
                if ($priceListItem->getId() === $priceListItemId) {
                    return $category;
                }
            }
        }
    }
}

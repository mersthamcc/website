<?php

namespace App\Tests\DependencyInjection;

use App\DependencyInjection\ClubConfigService;
use App\Utils\PhoneNumber;
use PHPUnit\Framework\TestCase;

class ClubConfigServiceTest extends TestCase
{
    private const CLUB_NAME = 'Club Name';
    private const LOGO_PATH = '/logo.jpg';
    private const PHONE_NUMBER = '(01234) 567890';
    private const PLAY_CRICKET_SUB_SITE = 'myclub';
    private const TWITTER_HANDLE = 'tweet-tweet';
    private const FACEBOOK_HANDLE = 'friend-face';

    private const COOKIE_CONTROL_API_KEY = 'my-api-key';
    private const COOKIE_CONTROL_PRODUCT = 'my-product-name';

    private const GOOGLE_ANALYTICS_KEY = 'my-google-analytics-site-key';

    private $clubConfigService;

    private $config = [
        'name' => self::CLUB_NAME,
        'logo' => self::LOGO_PATH,
        'phone' => self::PHONE_NUMBER,
        'playcricket' => [
            'enabled' => true,
            'subsitePrefix' => self::PLAY_CRICKET_SUB_SITE
        ],
        'social' => [
            'twitter' => [
                'feedEnabled' => true,
                'handle' => self::TWITTER_HANDLE
            ],
            'facebook' => [
                'handle' => self::FACEBOOK_HANDLE
            ]
        ],
        'cookies' => [
            'apiKey' => self::COOKIE_CONTROL_API_KEY,
            'product' => self::COOKIE_CONTROL_PRODUCT
        ],
        'analytics' => [
            'googleAnalyticsKey' => self::GOOGLE_ANALYTICS_KEY
        ]
    ];

    public function setUp()
    {
        parent::setUp();
        $this->clubConfigService = new ClubConfigService($this->config);
    }

    public function testClubNameReturnsCorrectValue()
    {
        $this->assertEquals(self::CLUB_NAME, $this->clubConfigService->getClubName());
    }

    public function testLogoReturnsCorrectValue()
    {
        $this->assertEquals(self::LOGO_PATH, $this->clubConfigService->getLogo());
    }

    public function testPhoneReturnsCorrectValue()
    {
        $phone = new PhoneNumber(self::PHONE_NUMBER);
        $this->assertEquals($phone, $this->clubConfigService->getPhoneNumber());
    }

    public function testPlayCricketEnabledReturnsCorrectValue()
    {
        $this->assertTrue($this->clubConfigService->isPlayCricketEnabled());
    }

    public function testPlayCricketSubSiteReturnsCorrectValue()
    {
        $this->assertEquals(self::PLAY_CRICKET_SUB_SITE, $this->clubConfigService->getPlayCricketSubSite());
    }

    public function testTwitterHandleReturnsCorrectValue()
    {
        $this->assertEquals(self::TWITTER_HANDLE, $this->clubConfigService->getTwitterHandle());
    }

    public function testTwitterFeedEnabledReturnsCorrectValue()
    {
        $this->assertTrue($this->clubConfigService->getTwitterFeedEnabled());
    }

    public function testFacebookHandleReturnsCorrectValue()
    {
        $this->assertEquals(self::FACEBOOK_HANDLE, $this->clubConfigService->getFacebookHandle());
    }

    public function testCookieControlApiKeyReturnsCorrectValue()
    {
        $this->assertEquals(self::COOKIE_CONTROL_API_KEY, $this->clubConfigService->getCookieConsentApiKey());
    }

    public function testCookieControlProductReturnsCorrectValue()
    {
        $this->assertEquals(self::COOKIE_CONTROL_PRODUCT, $this->clubConfigService->getCookieConsentProductCode());
    }

    public function testGoogleAnalyticsKeyReturnsCorrectValue()
    {
        $this->assertEquals(self::GOOGLE_ANALYTICS_KEY, $this->clubConfigService->getGoogleAnalyticsKey());
    }
}

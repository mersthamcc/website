<?php

namespace App\Tests\Utils;

use App\Utils\PhoneNumber;
use PHPUnit\Framework\TestCase;

class PhoneNumberTest extends TestCase
{
    private const TEST_NUMBER = "020 1234 5678";
    private const TEST_NUMBER_WITH_FORMATTING = "(020)-1234 5678";
    private const TEST_NUMBER_WITH_PREFIX = "44 20 1234 5678";
    private const TEST_NUMBER_WITH_PREFIX_AND_PLUS = "+44 20 1234 5678";

    private const TEST_NUMBER_DIALABLE_RESULT = "+442012345678";
    public function testGetRaw()
    {
        $phone = new PhoneNumber(self::TEST_NUMBER);
        $this->assertEquals(self::TEST_NUMBER, $phone->getRaw());
    }

    public function testGetDialableStringWithSpacedNumber()
    {
        $phone = new PhoneNumber(self::TEST_NUMBER);
        $this->assertEquals(self::TEST_NUMBER_DIALABLE_RESULT, $phone->getDialableString());
    }

    public function testGetDialableStringWithFormattedNumber()
    {
        $phone = new PhoneNumber(self::TEST_NUMBER_WITH_FORMATTING);
        $this->assertEquals(self::TEST_NUMBER_DIALABLE_RESULT, $phone->getDialableString());
    }

    public function testGetDialableStringWithPrefixedNumber()
    {
        $phone = new PhoneNumber(self::TEST_NUMBER_WITH_PREFIX);
        $this->assertEquals(self::TEST_NUMBER_DIALABLE_RESULT, $phone->getDialableString());
    }

    public function testGetDialableStringWithDialableNumber()
    {
        $phone = new PhoneNumber(self::TEST_NUMBER_WITH_PREFIX_AND_PLUS);
        $this->assertEquals(self::TEST_NUMBER_DIALABLE_RESULT, $phone->getDialableString());
    }
}

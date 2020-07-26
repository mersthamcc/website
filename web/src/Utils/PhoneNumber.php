<?php


namespace App\Utils;


class PhoneNumber
{
    /**
     * @var string
     */
    private $raw;

    public function __construct(string $raw)
    {
        $this->raw = $raw;
    }

    /**
     * @return string
     */
    public function getRaw(): string
    {
        return $this->raw;
    }

    /**
     * @return string
     */
    public function getDialableString(): string
    {
        return '+44' . $this->cleanPhoneNumber($this->raw, true);
    }

    private function cleanPhoneNumber($phone, $stripLeadingZero = false)
    {
        $clean = preg_replace("/[^0-9]/", "", $phone);

        if ($stripLeadingZero) {
            $clean = substr($clean, 0, 1) == "0" ? substr($clean, 1) : $clean;
        }
        if (substr($clean, 0, 2) == "44" && strlen($clean) > 10) {
            $clean = substr($clean, 2, strlen($clean) - 2);
        }
        return $clean;
    }

}
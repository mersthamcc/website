<?php


namespace App\Service;


use Symfony\Component\HttpFoundation\Session\SessionInterface;

class SessionEncryptor
{
    public const CIPHER = "AES256";

    protected $session;
    private $key, $iv;

    /**
     * SessionEncryptor constructor.
     * @param $session
     */
    public function __construct(SessionInterface $session)
    {
        $this->session = $session;
        if ($session->has('encryptionKey')) {
            $this->key = $session->get('encryptionKey');
            $this->iv = $session->get('encryptionIV');
        } else {
            $ivLength = openssl_cipher_iv_length(self::CIPHER);
            $this->key = openssl_random_pseudo_bytes(256);
            $this->iv = openssl_random_pseudo_bytes($ivLength);
            $session->set('encryptionKey', $this->key);
            $session->set('encryptionIV', $this->iv);
        }
    }

    /**
     * @param string $plainText
     * @return string
     */
    public function encrypt(string $plainText): string
    {
        return openssl_encrypt($plainText, self::CIPHER, $this->key, 0, $this->iv);
    }

    /**
     * @param string $cipherText
     * @return string
     */
    public function decrypt(string $cipherText): string
    {
        return openssl_decrypt($cipherText, self::CIPHER, $this->key, 0, $this->iv);
    }
}
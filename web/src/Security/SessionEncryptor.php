<?php


namespace App\Security;


use Symfony\Component\HttpFoundation\Session\SessionInterface;

class SessionEncryptor
{
    private const CIPHER = "AES256";

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
            $this->key = openssl_random_pseudo_bytes(256);
            $ivlen = openssl_cipher_iv_length(self::CIPHER);
            $this->iv = openssl_random_pseudo_bytes($ivlen);
            $session->set('encryptionKey', $this->key);
            $session->set('encryptionIV', $this->iv);
        }
    }

    public function encrypt($plainText)
    {
        return openssl_encrypt($plainText, self::CIPHER, $this->key, 0, $this->iv);
    }

    public function decrypt($cipherText)
    {
        return openssl_decrypt($cipherText, self::CIPHER, $this->key, 0, $this->iv);
    }
}
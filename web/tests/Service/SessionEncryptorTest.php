<?php

namespace App\Tests\Service;

use App\Service\SessionEncryptor;
use PHPUnit\Framework\TestCase;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\Session\Storage\MockArraySessionStorage;

class SessionEncryptorTest extends TestCase
{
    private $session, $emptySession;
    private $key, $iv;

    protected function setUp(): void
    {
        parent::setUp();

        $ivLength = openssl_cipher_iv_length(SessionEncryptor::CIPHER);
        $this->key = openssl_random_pseudo_bytes(256);
        $this->iv = openssl_random_pseudo_bytes($ivLength);

        $this->session = new Session(new MockArraySessionStorage());
        $this->session->set("encryptionKey", $this->key);
        $this->session->set("encryptionIV", $this->iv);

        $this->emptySession = new Session(new MockArraySessionStorage());
    }

    public function testEncryptionKeyCreation()
    {
        new SessionEncryptor($this->emptySession);
        $this->assertTrue($this->emptySession->has("encryptionKey"));
        $this->assertTrue($this->emptySession->has("encryptionIV"));
        $this->assertNotEquals(
            $this->key,
            $this->emptySession->get("encryptionKey")
        );
        $this->assertNotEquals(
            $this->iv,
            $this->emptySession->get("encryptionIV")
        );
    }

    public function testEncryptionUseExistingSession()
    {
        new SessionEncryptor($this->session);
        $this->assertEquals($this->key, $this->session->get("encryptionKey"));
        $this->assertEquals($this->iv, $this->session->get("encryptionIV"));
    }
}

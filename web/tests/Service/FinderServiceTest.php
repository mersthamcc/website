<?php

namespace App\Tests\Service;

use App\Entity\BrowserCategory;
use App\Service\FinderService;
use App\Service\SessionEncryptor;
use PHPUnit\Framework\TestCase;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\RequestStack;

class FinderServiceTest extends TestCase
{
    private $passThroughEncryptor;
    private $requestStack;

    protected function setUp(): void
    {
        parent::setUp();
        $this->passThroughEncryptor = $this->createMock(
            SessionEncryptor::class
        );
        $this->requestStack = $this->createMock(RequestStack::class);

        $this->passThroughEncryptor->method("encrypt")->willReturnArgument(0);
        $this->passThroughEncryptor->method("decrypt")->willReturnArgument(0);
    }

    public function testCreateParameterStringForImages()
    {
        $finderService = new FinderService(
            $this->requestStack,
            $this->passThroughEncryptor
        );

        $configString = $finderService->createParameterString(
            "/private/news/11112222",
            BrowserCategory::Image,
            false
        );

        $config = json_decode(base64_decode($configString), true);

        $this->assertEquals(
            "/private/news/11112222",
            $config["backends"]["private"]["baseUrl"]
        );
        $this->assertEquals("local", $config["backends"]["private"]["adapter"]);
        $this->assertEquals(
            "/resources/statics/",
            $config["backends"]["global"]["baseUrl"]
        );

        $this->assertEquals("local", $config["backends"]["global"]["adapter"]);
        $this->assertEquals(
            "/resources/statics/",
            $config["backends"]["global"]["baseUrl"]
        );
        $this->assertEquals("local", $config["backends"]["global"]["adapter"]);

        $this->assertEquals(
            "local",
            $config["backends"]["globaldocs"]["adapter"]
        );
        $this->assertEquals(
            "/resources/clubdocuments/",
            $config["backends"]["globaldocs"]["baseUrl"]
        );
        $this->assertEquals(
            "local",
            $config["backends"]["globaldocs"]["adapter"]
        );

        $this->assertEquals(
            "This Items Images",
            $config["resourceTypes"]["1Images"]["label"]
        );
        $this->assertEquals(
            "images",
            $config["resourceTypes"]["1Images"]["directory"]
        );
        $this->assertEquals(
            "private",
            $config["resourceTypes"]["1Images"]["backend"]
        );

        $this->assertEquals(
            "Global Images",
            $config["resourceTypes"]["2WebGlobalImages"]["label"]
        );
        $this->assertEquals(
            "images",
            $config["resourceTypes"]["2WebGlobalImages"]["directory"]
        );
        $this->assertEquals(
            "global",
            $config["resourceTypes"]["2WebGlobalImages"]["backend"]
        );

        $this->assertFalse($config["readOnly"]);
    }

    public function testCreateParameterStringForFiles()
    {
        $finderService = new FinderService(
            $this->requestStack,
            $this->passThroughEncryptor
        );

        $configString = $finderService->createParameterString(
            "/private/news/11112222",
            BrowserCategory::File,
            true
        );

        $config = json_decode(base64_decode($configString), true);

        $this->assertEquals(
            "/private/news/11112222",
            $config["backends"]["private"]["baseUrl"]
        );
        $this->assertEquals("local", $config["backends"]["private"]["adapter"]);
        $this->assertEquals(
            "/resources/statics/",
            $config["backends"]["global"]["baseUrl"]
        );

        $this->assertEquals("local", $config["backends"]["global"]["adapter"]);
        $this->assertEquals(
            "/resources/statics/",
            $config["backends"]["global"]["baseUrl"]
        );
        $this->assertEquals("local", $config["backends"]["global"]["adapter"]);

        $this->assertEquals(
            "local",
            $config["backends"]["globaldocs"]["adapter"]
        );
        $this->assertEquals(
            "/resources/clubdocuments/",
            $config["backends"]["globaldocs"]["baseUrl"]
        );
        $this->assertEquals(
            "local",
            $config["backends"]["globaldocs"]["adapter"]
        );

        $this->assertEquals(
            "This Items Files",
            $config["resourceTypes"]["1Files"]["label"]
        );
        $this->assertEquals(
            "files",
            $config["resourceTypes"]["1Files"]["directory"]
        );
        $this->assertEquals(
            "private",
            $config["resourceTypes"]["1Files"]["backend"]
        );

        $this->assertEquals(
            "Global Files",
            $config["resourceTypes"]["2WebsiteGlobalFiles"]["label"]
        );
        $this->assertEquals(
            "files",
            $config["resourceTypes"]["2WebsiteGlobalFiles"]["directory"]
        );
        $this->assertEquals(
            "global",
            $config["resourceTypes"]["2WebsiteGlobalFiles"]["backend"]
        );

        $this->assertEquals(
            "Club Documents",
            $config["resourceTypes"]["3ClubDocuments"]["label"]
        );
        $this->assertEquals(
            "files",
            $config["resourceTypes"]["3ClubDocuments"]["directory"]
        );
        $this->assertEquals(
            "globaldocs",
            $config["resourceTypes"]["3ClubDocuments"]["backend"]
        );

        $this->assertTrue($config["readOnly"]);
    }

    public function testCreateParameterStringForAttachments()
    {
        $finderService = new FinderService(
            $this->requestStack,
            $this->passThroughEncryptor
        );

        $configString = $finderService->createParameterString(
            "/private/newsletter/11112222",
            BrowserCategory::File,
            true
        );

        $config = json_decode(base64_decode($configString), true);

        $this->assertEquals(
            "/private/newsletter/11112222",
            $config["backends"]["private"]["baseUrl"]
        );
        $this->assertEquals("local", $config["backends"]["private"]["adapter"]);
        $this->assertEquals(
            "/resources/statics/",
            $config["backends"]["global"]["baseUrl"]
        );

        $this->assertEquals("local", $config["backends"]["global"]["adapter"]);
        $this->assertEquals(
            "/resources/statics/",
            $config["backends"]["global"]["baseUrl"]
        );
        $this->assertEquals("local", $config["backends"]["global"]["adapter"]);

        $this->assertEquals(
            "local",
            $config["backends"]["globaldocs"]["adapter"]
        );
        $this->assertEquals(
            "/resources/clubdocuments/",
            $config["backends"]["globaldocs"]["baseUrl"]
        );
        $this->assertEquals(
            "local",
            $config["backends"]["globaldocs"]["adapter"]
        );

        $this->assertEquals(
            "This Items Files",
            $config["resourceTypes"]["1Files"]["label"]
        );
        $this->assertEquals(
            "files",
            $config["resourceTypes"]["1Files"]["directory"]
        );
        $this->assertEquals(
            "private",
            $config["resourceTypes"]["1Files"]["backend"]
        );

        $this->assertEquals(
            "Global Files",
            $config["resourceTypes"]["2WebsiteGlobalFiles"]["label"]
        );
        $this->assertEquals(
            "files",
            $config["resourceTypes"]["2WebsiteGlobalFiles"]["directory"]
        );
        $this->assertEquals(
            "global",
            $config["resourceTypes"]["2WebsiteGlobalFiles"]["backend"]
        );

        $this->assertEquals(
            "Club Documents",
            $config["resourceTypes"]["3ClubDocuments"]["label"]
        );
        $this->assertEquals(
            "files",
            $config["resourceTypes"]["3ClubDocuments"]["directory"]
        );
        $this->assertEquals(
            "globaldocs",
            $config["resourceTypes"]["3ClubDocuments"]["backend"]
        );

        $this->assertTrue($config["readOnly"]);
    }

    public function testCreateParameterStringForDocuments()
    {
        $finderService = new FinderService(
            $this->requestStack,
            $this->passThroughEncryptor
        );

        $configString = $finderService->createParameterString(
            null,
            BrowserCategory::Document,
            true
        );

        $config = json_decode(base64_decode($configString), true);

        $this->assertEquals(null, $config["backends"]["private"]["baseUrl"]);
        $this->assertEquals("local", $config["backends"]["private"]["adapter"]);
        $this->assertEquals(
            "/resources/statics/",
            $config["backends"]["global"]["baseUrl"]
        );

        $this->assertEquals("local", $config["backends"]["global"]["adapter"]);
        $this->assertEquals(
            "/resources/statics/",
            $config["backends"]["global"]["baseUrl"]
        );
        $this->assertEquals("local", $config["backends"]["global"]["adapter"]);

        $this->assertEquals(
            "local",
            $config["backends"]["globaldocs"]["adapter"]
        );
        $this->assertEquals(
            "/resources/clubdocuments/",
            $config["backends"]["globaldocs"]["baseUrl"]
        );
        $this->assertEquals(
            "local",
            $config["backends"]["globaldocs"]["adapter"]
        );

        $this->assertEquals(
            "Club Documents",
            $config["resourceTypes"]["1ClubDocuments"]["label"]
        );
        $this->assertEquals(
            "files",
            $config["resourceTypes"]["1ClubDocuments"]["directory"]
        );
        $this->assertEquals(
            "globaldocs",
            $config["resourceTypes"]["1ClubDocuments"]["backend"]
        );

        $this->assertTrue($config["readOnly"]);
    }

    public function testDecodeParameterString()
    {
        $config = [];
        $config["foo"] = "bar";
        $config["testrecord"]["string"] = "hello";
        $config["testrecord"]["boolean"] = true;
        $mockResult = base64_encode(json_encode($config));

        $request = $this->createMock(Request::class);
        $request
            ->method("get")
            ->with($this->equalTo("finderConfig"))
            ->willReturn($mockResult);
        $this->requestStack->method("getCurrentRequest")->willReturn($request);
        $finderService = new FinderService(
            $this->requestStack,
            $this->passThroughEncryptor
        );

        $result = $finderService->decodeParameterString();
        $this->assertEquals("bar", $result["foo"]);
    }
}

<?php
namespace App\Service;


use App\Entity\BrowserCategory;
use Psr\Log\LoggerInterface;
use Symfony\Component\HttpFoundation\RequestStack;

class FinderService
{
    private $requestStack;
    private $logger;

    public function __construct(RequestStack $requestStack, LoggerInterface $logger)
    {
        $this->requestStack = $requestStack;
        $this->logger = $logger;
    }

    public function createParameterString($privateDirectory, $category, $readOnly = false)
    {
        $config = array();
        $config['backends']["symfony_cache"] = [
            "name" => "symfony_cache",
            "adapter" => "local",
            "root" => "/var/www/var/cache/dev"
        ];
        $config['backends']["symfony_logs"] = [
            "name" => "symfony_logs",
            "adapter" => "local",
            "root" => "/var/www/var/log"
        ];

        $config['backends']['default'] = [
            'name' => 'default',
            'adapter' => 'local',
            'baseUrl' => $privateDirectory,
            //  'root'         => '', // Can be used to explicitly set the CKFinder user files directory.
            'chmodFiles' => 0777,
            'chmodFolders' => 0755,
            'filesystemEncoding' => 'UTF-8',
        ];

        $config['backends']['global'] = [
            'name' => 'global',
            'adapter' => 'local',
            'baseUrl' => '/resources/statics/',
            //  'root'         => '', // Can be used to explicitly set the CKFinder user files directory.
            'chmodFiles' => 0777,
            'chmodFolders' => 0755,
            'filesystemEncoding' => 'UTF-8',
        ];

        $config['backends']['globaldocs'] = [
            'name' => 'globaldocs',
            'adapter' => 'local',
            'baseUrl' => '/resources/clubdocuments/',
            //  'root'         => '', // Can be used to explicitly set the CKFinder user files directory.
            'chmodFiles' => 0777,
            'chmodFolders' => 0755,
            'filesystemEncoding' => 'UTF-8',
        ];

        $config['defaultResourceTypes'] = '';

        $index = 1;

        if ($category == BrowserCategory::Image) {
            $name = $index . 'Images';
            $config['resourceTypes'][$name] = [
                'name' => $name,
                'label' => 'This Items Images',
                'directory' => 'images',
                'maxSize' => 0,
                'allowedExtensions' => 'bmp,gif,jpeg,jpg,png',
                'deniedExtensions' => '',
                'backend' => 'default',
            ];
            $index++;
            $name = $index . 'WebGlobalImages';
            $config['resourceTypes'][$name] = [
                'name' => $name,
                'label' => 'Global Images',
                'directory' => 'images',
                'maxSize' => 0,
                'allowedExtensions' => 'bmp,gif,jpeg,jpg,png',
                'deniedExtensions' => '',
                'backend' => 'global',
            ];
            $index++;
        }

        if ($category == BrowserCategory::File) {
            $name = $index . 'Files';
            $config['resourceTypes'][$name] = [
                'name' => $name,
                'label' => 'This Items Files',
                'directory' => 'files',
                'maxSize' => 0,
                'allowedExtensions' =>
                    '7z,aiff,asf,avi,bmp,csv,doc,docx,fla,flv,gif,gz,gzip,jpeg,jpg,mid,mov,mp3,mp4,mpc,mpeg,mpg,ods,odt,pdf,png,ppt,pptx,pxd,qt,ram,rar,rm,rmi,rmvb,rtf,sdc,sitd,swf,sxc,sxw,tar,tgz,tif,tiff,txt,vsd,wav,wma,wmv,xls,xlsx,zip',
                'deniedExtensions' => '',
                'backend' => 'default',
            ];
            $index++;
            $name = $index . 'WebsiteGlobalFiles';
            $config['resourceTypes'][$name] = [
                'name' => $name,
                'label' => 'Global Files',
                'directory' => 'files',
                'maxSize' => 0,
                'allowedExtensions' =>
                    '7z,aiff,asf,avi,bmp,csv,doc,docx,fla,flv,gif,gz,gzip,jpeg,jpg,mid,mov,mp3,mp4,mpc,mpeg,mpg,ods,odt,pdf,png,ppt,pptx,pxd,qt,ram,rar,rm,rmi,rmvb,rtf,sdc,sitd,swf,sxc,sxw,tar,tgz,tif,tiff,txt,vsd,wav,wma,wmv,xls,xlsx,zip',
                'deniedExtensions' => '',
                'backend' => 'global',
            ];
            $index++;
        }

        if ($category == BrowserCategory::Attachment) {
            $name = $index . 'Files';
            $config['resourceTypes'][$name] = [
                'name' => $name,
                'label' => 'Attachments',
                'directory' => 'files',
                'maxSize' => 0,
                'allowedExtensions' =>
                    '7z,aiff,asf,avi,bmp,csv,doc,docx,fla,flv,gif,gz,gzip,jpeg,jpg,mid,mov,mp3,mp4,mpc,mpeg,mpg,ods,odt,pdf,png,ppt,pptx,pxd,qt,ram,rar,rm,rmi,rmvb,rtf,sdc,sitd,swf,sxc,sxw,tar,tgz,tif,tiff,txt,vsd,wav,wma,wmv,xls,xlsx,zip',
                'deniedExtensions' => '',
                'backend' => 'default',
            ];
            $index++;
        }

        if ($category == BrowserCategory::File || $category == BrowserCategory::Document) {
            $name = $index . 'ClubDocuments';
            $config['resourceTypes'][$name] = [
                'name' => $name,
                'label' => 'Club Documents',
                'directory' => 'files',
                'maxSize' => 0,
                'allowedExtensions' => 'doc,docx,ods,odt,pdf,ppt,pptx,xls,xlsx',
                'deniedExtensions' => '',
                'backend' => 'globaldocs',
            ];
        }
        $config['readOnly'] = $readOnly;

        return base64_encode(json_encode($config));
    }

    public function decodeParameterString()
    {
        $request = $this->requestStack->getCurrentRequest();
        $config = $request->get('finderConfig');
        if ($config) {
            $this->logger->debug($config);
            $base64 = urldecode($config);
            $this->logger->debug($base64);
            $json = base64_decode($base64, true);
            $this->logger->debug($json);
            return json_decode($json, true);
        }
        return [];
    }
}
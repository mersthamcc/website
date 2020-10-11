<?php
namespace App\Service;


use App\Entity\BrowserCategory;
use Symfony\Component\HttpFoundation\RequestStack;

class FinderService
{
    private $requestStack;
    private $encryptor;

    public function __construct(RequestStack $requestStack, SessionEncryptor $encryptor)
    {
        $this->requestStack = $requestStack;
        $this->encryptor = $encryptor;
    }

    public function createParameterString($privateDirectory, $category, $readOnly = false)
    {
        $config = array();

        $config['backends']['private'] = [
            'name' => 'private',
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
                'backend' => 'private',
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
                'backend' => 'private',
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
                'backend' => 'private',
            ];
            $index++;
        }

        if ($category == BrowserCategory::File || $category == BrowserCategory::Document || $category == BrowserCategory::Attachment) {
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

        return base64_encode($this->encryptor->encrypt(json_encode($config)));
    }

    public function decodeParameterString()
    {
        $request = $this->requestStack->getCurrentRequest();
        $config = $request->get('finderConfig');
        if ($config) {
            $base64 = urldecode($config);
            $json = base64_decode($base64, true);
            return json_decode($this->encryptor->decrypt($json), true);
        }
        return [];
    }
}
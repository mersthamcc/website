<?php

namespace App\ApiClient;

class ApiResponse
{
    /**
     * @var MethodResponse
     */
    private $data;

    /**
     * @return MethodResponse
     */
    public function getData(): MethodResponse
    {
        return $this->data;
    }

    /**
     * @param MethodResponse $data
     * @return ApiResponse
     */
    public function setData(MethodResponse $data): ApiResponse
    {
        $this->data = $data;
        return $this;
    }
}

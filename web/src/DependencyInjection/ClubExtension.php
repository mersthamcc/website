<?php


namespace App\DependencyInjection;


use Symfony\Component\DependencyInjection\ContainerBuilder;

class ClubExtension extends \Symfony\Component\DependencyInjection\Extension\Extension
{
    /**
     * @inheritDoc
     */
    public function load(array $configs, ContainerBuilder $container)
    {
        $configuration = new Configuration();

        $config = $this->processConfiguration($configuration, $configs);

        $definition = $container->register("config", ClubConfigService::class);
        $definition->setPublic(true);
        $definition->setArgument(0, $config);
    }

    public function getAlias()
    {
        return "club";
    }
}
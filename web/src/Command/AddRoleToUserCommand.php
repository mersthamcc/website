<?php

namespace App\Command;

use App\Entity\User;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\ORMException;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Input\InputOption;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\Console\Style\SymfonyStyle;

class AddRoleToUserCommand extends Command
{
    protected static $defaultName = 'app:add-role-to-user';

    private $entityManager;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->entityManager = $entityManager;

        parent::__construct();
    }

    protected function configure()
    {
        $this
            ->setDescription('Add a short description for your command')
            ->addOption('emailaddress', null, InputOption::VALUE_REQUIRED, 'The e-mail address for the user')
            ->addOption('role', null, InputOption::VALUE_IS_ARRAY | InputOption::VALUE_REQUIRED, 'The role to add to the user (multiple values allowed)')
        ;
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $io = new SymfonyStyle($input, $output);
        $username = $input->getOption('emailaddress');

        $io->title("Add roles to a given user");
        if ($username) {
            $repo = $this->entityManager->getRepository(User::class);
            $user = $repo->findOneBy([
                "email" => $username
            ]);

            if ($user) {
                $io->note(sprintf('Updating user: %s', $username));

                $user->addRoles($input->getOption('role'));

                try {
                    $this->entityManager->persist($user);
                    $this->entityManager->flush();
                    $io->success(sprintf('User %s successfully updated!', $username));
                } catch (ORMException $e) {
                    $io->error('Failed to update user');
                    $io->error($e->getMessage());
                    return 3;
                }

                return 0;
            } else {
                $io->error(sprintf('Could not find user %s', $username));
                return 1;
            }
        }
        return 5;
    }
}

<?php

namespace App\Repository;

use App\Entity\NewsAttribute;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @method NewsAttribute|null find($id, $lockMode = null, $lockVersion = null)
 * @method NewsAttribute|null findOneBy(array $criteria, array $orderBy = null)
 * @method NewsAttribute[]    findAll()
 * @method NewsAttribute[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class NewsAttributeRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, NewsAttribute::class);
    }

    // /**
    //  * @return NewsAttribute[] Returns an array of NewsAttribute objects
    //  */
    /*
    public function findByExampleField($value)
    {
        return $this->createQueryBuilder('n')
            ->andWhere('n.exampleField = :val')
            ->setParameter('val', $value)
            ->orderBy('n.id', 'ASC')
            ->setMaxResults(10)
            ->getQuery()
            ->getResult()
        ;
    }
    */

    /*
    public function findOneBySomeField($value): ?NewsAttribute
    {
        return $this->createQueryBuilder('n')
            ->andWhere('n.exampleField = :val')
            ->setParameter('val', $value)
            ->getQuery()
            ->getOneOrNullResult()
        ;
    }
    */
}

<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20200710220215 extends AbstractMigration
{
    public function getDescription() : string
    {
        return '';
    }

    public function up(Schema $schema) : void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE SEQUENCE "user_id_seq" INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE TABLE news (id SERIAL NOT NULL, created_date TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, publish_date TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, title VARCHAR(255) NOT NULL, body TEXT NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE TABLE news_attribute (id SERIAL NOT NULL, news_id INT NOT NULL, name VARCHAR(64) NOT NULL, value VARCHAR(1024) DEFAULT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_E65FB03AB5A459A0 ON news_attribute (news_id)');
        $this->addSql('CREATE TABLE news_comment (id SERIAL NOT NULL, news_id INT NOT NULL, author VARCHAR(255) DEFAULT NULL, body TEXT NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_C3904E8AB5A459A0 ON news_comment (news_id)');
        $this->addSql('CREATE TABLE page (id SERIAL NOT NULL, title VARCHAR(255) NOT NULL, slug VARCHAR(128) NOT NULL, body TEXT NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE TABLE "user" (id INT NOT NULL, external_id VARCHAR(180) DEFAULT NULL, email VARCHAR(64) NOT NULL, given_name VARCHAR(180) DEFAULT NULL, family_name VARCHAR(180) DEFAULT NULL, roles JSON NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_8D93D6499F75D7B0 ON "user" (external_id)');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_8D93D649E7927C74 ON "user" (email)');
        $this->addSql('ALTER TABLE news_attribute ADD CONSTRAINT FK_E65FB03AB5A459A0 FOREIGN KEY (news_id) REFERENCES news (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE news_comment ADD CONSTRAINT FK_C3904E8AB5A459A0 FOREIGN KEY (news_id) REFERENCES news (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema) : void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE SCHEMA public');
        $this->addSql('ALTER TABLE news_attribute DROP CONSTRAINT FK_E65FB03AB5A459A0');
        $this->addSql('ALTER TABLE news_comment DROP CONSTRAINT FK_C3904E8AB5A459A0');
        $this->addSql('DROP SEQUENCE "user_id_seq" CASCADE');
        $this->addSql('DROP TABLE news');
        $this->addSql('DROP TABLE news_attribute');
        $this->addSql('DROP TABLE news_comment');
        $this->addSql('DROP TABLE page');
        $this->addSql('DROP TABLE "user"');
    }
}

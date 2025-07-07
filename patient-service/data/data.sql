-- =====================================================================
-- Script de création de la table Patient et d'insertion des données de test
-- Compatible avec MySQL 8+
-- =====================================================================

-- Utiliser votre base de données (remplacez 'diabete_patient_db' si nécessaire)
CREATE DATABASE IF NOT EXISTS diabete_patient_db;
USE diabete_patient_db;

-- -----------------------------------------------------
-- Table `patient`
-- -----------------------------------------------------
-- On utilise `IF NOT EXISTS` pour que le script puisse être relancé sans erreur.
CREATE TABLE IF NOT EXISTS `patient` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `prenom` VARCHAR(100) NOT NULL,
  `nom` VARCHAR(100) NOT NULL,
  `date_de_naissance` DATE NOT NULL,
  `genre` VARCHAR(1) NOT NULL COMMENT 'M pour Masculin, F pour Féminin',
  `adresse_postale` VARCHAR(255) NULL,
  `numero_de_telephone` VARCHAR(20) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- -----------------------------------------------------
-- Insertion des données de test pour les patients
-- -----------------------------------------------------
-- On vide la table avant d'insérer pour éviter les doublons lors des tests
-- TRUNCATE TABLE `patient`;

INSERT INTO `patient` (`prenom`, `nom`, `date_de_naissance`, `genre`, `adresse_postale`, `numero_de_telephone`) VALUES
('TestNone', 'Test', '1966-12-31', 'F', '1 Brookside St', '100-222-3333'),
('TestBorderline', 'Test', '1945-06-24', 'M', '2 High St', '200-333-4444'),
('TestInDanger', 'Test', '2004-06-18', 'M', '3 Club Road', '300-444-5555'),
('TestEarlyOnset', 'Test', '2002-06-28', 'F', '4 Valley Dr', '400-555-6666');
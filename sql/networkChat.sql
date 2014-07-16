SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema networkChat
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `networkChat` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `networkChat` ;

-- -----------------------------------------------------
-- Table `networkChat`.`Users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `networkChat`.`Users` (
  `nickname` VARCHAR(20) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  `city` VARCHAR(20) NOT NULL,
  `dateOfBirth` DATE NOT NULL,
  `info` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`nickname`),
  UNIQUE INDEX `nickname_UNIQUE` (`nickname` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `networkChat`.`OnlineTimeLog`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `networkChat`.`OnlineTimeLog` (
  `socketId` VARCHAR(36) NOT NULL,
  `logIn` TIMESTAMP NOT NULL,
  `logOut` TIMESTAMP NULL,
  `ip` VARCHAR(16) NOT NULL,
  `nickname` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`socketId`),
  INDEX `USER_SOCKETID_ONLINETIME_idx` (`nickname` ASC),
  CONSTRAINT `USER_NICKNAME_ONLINETIME`
    FOREIGN KEY (`nickname`)
    REFERENCES `networkChat`.`Users` (`nickname`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
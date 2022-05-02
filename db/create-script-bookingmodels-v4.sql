-- MySQL Script generated by MySQL Workbench
-- Sun Apr 24 22:42:27 2022
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema bookingmodels
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `bookingmodels` ;

-- -----------------------------------------------------
-- Schema bookingmodels
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `bookingmodels` DEFAULT CHARACTER SET utf8 ;
USE `bookingmodels` ;

-- -----------------------------------------------------
-- Table `bookingmodels`.`eventCategories`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `bookingmodels`.`eventCategories` ;

CREATE TABLE IF NOT EXISTS `bookingmodels`.`eventCategories` (
  `eventCategoryId` INT NOT NULL,
  `eventCategoryName` VARCHAR(100) NOT NULL,
  `eventCategoryDescription` TEXT(500) NULL,
  `eventCategoryDuration` INT NOT NULL,
  PRIMARY KEY (`eventCategoryId`),
  UNIQUE INDEX `eventCategoryName_UNIQUE` (`eventCategoryName` ASC) VISIBLE , 
  CONSTRAINT time_duration CHECK (eventCategoryDuration BETWEEN 1 AND 480)
)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bookingmodels`.`events`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `bookingmodels`.`events` ;

CREATE TABLE IF NOT EXISTS `bookingmodels`.`events` (
  `eventId` INT NOT NULL,
  `bookingName` VARCHAR(100) NOT NULL,
  `bookingEmail` VARCHAR(50) NOT NULL,
  `eventStartTime` DATETIME NOT NULL,
  `eventDuration` INT NOT NULL ,
  `eventNotes` TEXT(500) NULL,
  `eventCategoryId` INT NOT NULL,
  PRIMARY KEY (`eventId`),
  INDEX `fk_event_eventCategory_idx` (`eventCategoryId` ASC) VISIBLE,
  CONSTRAINT `fk_event_eventCategory`
    FOREIGN KEY (`eventCategoryId`)
    REFERENCES `bookingmodels`.`eventCategories` (`eventCategoryId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

use bookingmodels;
set names utf8;
insert  into eventCategories(eventCategoryId,eventCategoryName,eventCategoryDescription,eventCategoryDuration) 
values 
(1,'Project Management Clinic','ตารางนัดหมายนี้ใช้สำหรับนัดหมาย project management clinic ในวิชา INT221 integrated project I ให้นักศึกษาเตรียมเอกสารที่เกี่ยวข้อง เพื่อแสดงระหว่างขอคำปรึกษา',30),
(2,'DevOps/Infra Clinic','Use this event category for DevOps/Infra clinic.',30),
(3,'Database Clinic','ตารางนัดหมายนี้ใช้สำหรับนัดหมาย database clinic ในวิชา INT221 integrated project I',15),
(4,'Client-side Clinic','ตารางนัดหมายนี้ใช้สำหรับนัดหมาย client-side clinic ในวิชา INT221 integrated project I',30),
(5,'Server-side Clinic',null,30);


insert  into events(eventId,bookingName,bookingEmail,eventStartTime,eventDuration,eventNotes,eventCategoryId) 
values 
(1,'Somchai Jaidee (OR-7)','somchai.jai@mail.kmutt.ac.th','2022-05-23 13:30:00',(select eventCategoryDuration from eventCategories where eventCategoryId=2),null,2),
(2,'Somsri Rakdee (SJ-3)','somsri.rak@mail.kmutt.ac.th','2022-04-27 09:30:00',(select eventCategoryDuration from eventCategories where eventCategoryId=1),'ขอปรึกษาปัญหาเพื่อนไม่ช่วยงาน',1),
(3,'สมเกียรติ ขยันเรียน กลุ่ม TT-4','somkiat.kay@kmutt.ac.th','2022-05-23 16:30:00',(select eventCategoryDuration from eventCategories where eventCategoryId=3),null,3);

update eventCategories set eventCategoryDuration = 20 where eventCategoryId=2;
select * from eventCategories;
select * from events;

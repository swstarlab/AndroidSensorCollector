-- --------------------------------------------------------
-- 호스트:                          hanockka.cafe24.com
-- 서버 버전:                        5.5.30-cll - MySQL Community Server (GPL) by Atomicorp
-- 서버 OS:                        Linux
-- HeidiSQL 버전:                  9.2.0.4947
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- lifome 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `lifome` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `lifome`;


-- 테이블 lifome.Activity 구조 내보내기
CREATE TABLE IF NOT EXISTS `Activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL DEFAULT '0',
  `time` datetime NOT NULL,
  `activity` varchar(2048) NOT NULL DEFAULT '0',
  `venue` varchar(2048) NOT NULL DEFAULT '0',
  `latitude` double NOT NULL DEFAULT '0',
  `longitude` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 내보낼 데이터가 선택되어 있지 않습니다.


-- 테이블 lifome.Location 구조 내보내기
CREATE TABLE IF NOT EXISTS `Location` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL DEFAULT '0',
  `time` datetime NOT NULL,
  `gpsLat` double NOT NULL DEFAULT '0',
  `gpsLng` double NOT NULL DEFAULT '0',
  `gpsOn` double NOT NULL DEFAULT '0',
  `netLat` double NOT NULL DEFAULT '0',
  `netLng` double NOT NULL DEFAULT '0',
  `netOn` double NOT NULL DEFAULT '0',
  `cellId` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 내보낼 데이터가 선택되어 있지 않습니다.


-- 테이블 lifome.User 구조 내보내기
CREATE TABLE IF NOT EXISTS `User` (
  `userId` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` varchar(128) NOT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 내보낼 데이터가 선택되어 있지 않습니다.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

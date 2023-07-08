-- phpMyAdmin SQL Dump
-- version 4.6.6deb5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 28, 2019 at 05:19 PM
-- Server version: 5.7.25-0ubuntu0.18.04.2
-- PHP Version: 7.2.10-0ubuntu0.18.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `wireless_stats`
--
CREATE DATABASE IF NOT EXISTS `wireless_stats` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `wireless_stats`;

-- --------------------------------------------------------

--
-- Table structure for table `observed_device`
--

DROP TABLE IF EXISTS `observed_device`;
CREATE TABLE IF NOT EXISTS `observed_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reporting_device_id` int(11) NOT NULL,
  `mac_address` bigint(20) NOT NULL,
  `mac_type` tinyint(4) NOT NULL,
  `network_name` varchar(255) NOT NULL,
  `signal_strength` int(11) NOT NULL,
  `frequency` int(11) NOT NULL,
  `channel_width` int(11) NOT NULL,
  `security` varchar(254) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `reporting_device_id` (`reporting_device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `reporting_device`
--

DROP TABLE IF EXISTS `reporting_device`;
CREATE TABLE IF NOT EXISTS `reporting_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `bt_mac_address` bigint(20) NOT NULL,
  `wifi_mac_address` bigint(20) NOT NULL,
  `ipv4_address` int(11) NOT NULL,
  `ipv6_address` varbinary(16) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `longitude` float NOT NULL,
  `latitude` float NOT NULL,
  `OS` varchar(255) NOT NULL,
  `battery_life` float NOT NULL,
  `has_cellular_internet` tinyint(1) NOT NULL,
  `has_wifi_internet` tinyint(1) NOT NULL,
  `cellular_throughput` float NOT NULL,
  `wifi_throughput` float NOT NULL,
  `cellular_ping` int(11) NOT NULL,
  `wifi_ping` int(11) NOT NULL,
  `cellular_operator` varchar(255) NOT NULL,
  `cellular_network_type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `observed_device`
--
ALTER TABLE `observed_device`
  ADD CONSTRAINT `reporting_device_id` FOREIGN KEY (`reporting_device_id`) REFERENCES `reporting_device` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

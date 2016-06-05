-- phpMyAdmin SQL Dump
-- version 4.0.10.14
-- http://www.phpmyadmin.net
--
-- Host: localhost:3306
-- Generation Time: Jun 05, 2016 at 06:23 PM
-- Server version: 5.6.30
-- PHP Version: 5.4.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `grapotco_ddd`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `utk` varchar(255) NOT NULL,
  `fid` varchar(256) NOT NULL,
  `ftk` varchar(255) NOT NULL,
  `rep` int(11) NOT NULL DEFAULT '0' COMMENT 'reputation',
  PRIMARY KEY (`id`),
  KEY `utk` (`utk`),
  KEY `fid` (`fid`,`ftk`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `utk`, `fid`, `ftk`, `rep`) VALUES
(2, 'cff8354cfea0940804d61f88afb5257e', '10207905168321590', 'EAAPYCOVNfocBAE8SMltyf9WlRGGNFRwbKtkv2ZAtjns05U4MzcVZC1DcSrwy07n2bcfCPu7fsLMMvREvX2QNyMPTq5KOKWZATUN7AEfhP682aDNoA9K3k4X3YIkZAzrtY7PNbGYnkPdyvhIxeZC6ZBwFqw7DazfZAuiE20UZB5ZBrKgZDZD', 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

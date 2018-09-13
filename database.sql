CREATE SCHEMA tms_observer;


CREATE TABLE `tms_observer`.`changeable_properties` (

  `key_prop` varchar(255) NOT NULL,

  `value_prop` varchar(255) NOT NULL,

  PRIMARY KEY (`key_prop`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tms_observer`.`currencyguardian` (

  `idCG` int(11) NOT NULL AUTO_INCREMENT,

  `symbolCG` varchar(45) NOT NULL,

  `valueCG` double NOT NULL,

  `setting_dateCG` datetime NOT NULL,

  `refers_to_bid` tinyint(4) NOT NULL,

  `was_value_greater_than_scrapped` tinyint(4) NOT NULL,

  PRIMARY KEY (`idCG`),

  UNIQUE KEY `idCG_UNIQUE` (`idCG`)

) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;




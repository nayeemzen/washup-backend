CREATE TABLE `washup_employees` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varbinary(50) NOT NULL,
  `first_name` tinyint(1) NOT NULL,
  `last_name` tinyint(1) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `unq_token` (`token`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
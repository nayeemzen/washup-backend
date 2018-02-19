CREATE TABLE `washup_employees` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varbinary(50) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `unq_token` (`token`),
  UNIQUE KEY `unq_email` (`email`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
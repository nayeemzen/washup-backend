CREATE TABLE `preferences` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `scented` tinyint(1) DEFAULT 0,
  `fabric_softener` tinyint(1) DEFAULT 1,
  `one_day_delivery` tinyint(1) DEFAULT 0,
  `laundry_reminder` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `unq_user_id` (`user_id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
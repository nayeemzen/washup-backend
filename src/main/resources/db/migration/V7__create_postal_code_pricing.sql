CREATE TABLE `postal_codes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `postal_code` varchar(6) NOT NULL,
  `rule` varchar(255) NOT NULL,
  `pricing_bucket_id` bigint(20) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_postal_code` (`postal_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
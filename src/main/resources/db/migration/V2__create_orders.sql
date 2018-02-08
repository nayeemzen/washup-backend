CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varbinary(50) NOT NULL,
  `idempotence_token` varbinary(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `order_type` varchar(50) NOT NULL,
  `status` varchar(255) NOT NULL,
  `total_cost_cents` bigint(20) DEFAULT NULL,
  `pickup_date` datetime NOT NULL,
  `delivery_date` datetime NOT NULL,
  `rush_service` bit DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `unq_token` (`token`),
  UNIQUE KEY `unq_idempotence_token` (`idempotence_token`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
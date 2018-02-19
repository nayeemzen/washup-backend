CREATE TABLE `payment_cards` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varbinary(50) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `stripe_customer_token` varchar(255) NOT NULL,
  `last_four` varchar(4) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `unq_token` (`token`),
  UNIQUE KEY `unq_user_id` (`user_id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
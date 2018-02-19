CREATE TABLE `item_pricings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item` varchar(255) NOT NULL,
  `price_cents` int NOT NULL,
  `bucket_id` bigint(20) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_item_bucket_id` (`item`, `bucket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
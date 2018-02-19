CREATE TABLE `pricing_bucket` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item` varchar(255) NOT NULL,
  `price_cents` int NOT NULL,
  `bucket_name` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_item_bucket_name` (`item`, `bucket_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
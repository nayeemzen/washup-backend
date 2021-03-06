CREATE TABLE `pricing_buckets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bucket_name` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_bucket_name` (`bucket_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
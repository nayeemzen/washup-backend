CREATE TABLE `receipt_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL,
  `item_name` varchar(255) NOT NULL,
  `item_amount_cents` int NOT NULL,
  `item_quantity` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_order_id_item_name` (`order_id`, `item_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
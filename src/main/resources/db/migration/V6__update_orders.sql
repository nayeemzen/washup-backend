ALTER TABLE `orders`
  ADD COLUMN `billed_at` datetime DEFAULT NULL after `pickup_date`,
  ADD KEY `idx_pickup_date` (`pickup_date`),
  ADD KEY `idx_delivery_date` (`delivery_date`);
-- 为尚未包含审核人、审核状态字段的单据表增加 auditorId、checked

ALTER TABLE `fc_collection` ADD COLUMN `auditorId` VARCHAR(20) NULL COMMENT '审核人ID' AFTER `listerId`;
ALTER TABLE `fc_collection` ADD COLUMN `checked` BIT(1) DEFAULT b'0' COMMENT '是否已审核' AFTER `auditorId`;

ALTER TABLE `fc_payment` ADD COLUMN `auditorId` VARCHAR(20) NULL COMMENT '审核人ID' AFTER `listerId`;
ALTER TABLE `fc_payment` ADD COLUMN `checked` BIT(1) DEFAULT b'0' COMMENT '是否已审核' AFTER `auditorId`;

ALTER TABLE `fc_income` ADD COLUMN `auditorId` VARCHAR(20) NULL COMMENT '审核人ID' AFTER `listerId`;
ALTER TABLE `fc_income` ADD COLUMN `checked` BIT(1) DEFAULT b'0' COMMENT '是否已审核' AFTER `auditorId`;

ALTER TABLE `fc_expense` ADD COLUMN `auditorId` VARCHAR(20) NULL COMMENT '审核人ID' AFTER `listerId`;
ALTER TABLE `fc_expense` ADD COLUMN `checked` BIT(1) DEFAULT b'0' COMMENT '是否已审核' AFTER `auditorId`;

ALTER TABLE `wc_store` ADD COLUMN `auditorId` VARCHAR(20) NULL COMMENT '审核人ID' AFTER `listerId`;
ALTER TABLE `wc_store` ADD COLUMN `checked` BIT(1) DEFAULT b'0' COMMENT '是否已审核' AFTER `auditorId`;

ALTER TABLE `wc_checkout` ADD COLUMN `auditorId` VARCHAR(20) NULL COMMENT '审核人ID' AFTER `listerId`;
ALTER TABLE `wc_checkout` ADD COLUMN `checked` BIT(1) DEFAULT b'0' COMMENT '是否已审核' AFTER `auditorId`;




ALTER TABLE `fc_receivable` ADD COLUMN `currentAmount` DOUBLE DEFAULT NULL COMMENT '应收款余额' AFTER `paidAmount`;

ALTER TABLE `fc_payable` ADD COLUMN `currentAmount` DOUBLE DEFAULT NULL COMMENT '应付款余额' AFTER `paidAmount`;









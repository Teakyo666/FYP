/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 8.0.34 : Database - fyp
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`fyp` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `fyp`;

/*Table structure for table `ai_generate` */

DROP TABLE IF EXISTS `ai_generate`;

CREATE TABLE `ai_generate` (
  `id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '垃圾名称',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '垃圾类型',
  `title` varchar(100) DEFAULT NULL COMMENT '环保标语',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '分类原因/说明',
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='垃圾信息表';

/*Data for the table `ai_generate` */

insert  into `ai_generate`(`id`,`name`,`category`,`title`,`reason`,`status`) values ('9044b2f3-d244-48bb-9f35-eb6ec2f41163','纸箱','Recyclable Waste','Keep it green, recycle paper boxes!','Paper boxes are made of recyclable materials, such as cardboard, which can be processed and reused. They do not contain harmful substances and are not organic waste.','approved'),('c8894cb7-525c-4d5c-b16d-e4f0d63cc399','塑料袋','Non-recyclable waste','Keep plastic bags out of landfills!','Plastic bags are not biodegradable and can cause pollution in the environment.','approved');

/*Table structure for table `apply` */

DROP TABLE IF EXISTS `apply`;

CREATE TABLE `apply` (
  `id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'id',
  `user_id` varchar(100) NOT NULL COMMENT '用户uuid',
  `to_apply` varchar(30) NOT NULL COMMENT '申请？权限',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请原因',
  `status` varchar(10) NOT NULL COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `apply` */

insert  into `apply`(`id`,`user_id`,`to_apply`,`reason`,`status`) values ('43e878c1-84dc-45b7-aa61-eab1cb31acaa','52ed5403-b222-4be6-a45a-fcd4c430bf6b','volunteer','123','拒绝');

/*Table structure for table `garbage` */

DROP TABLE IF EXISTS `garbage`;

CREATE TABLE `garbage` (
  `id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '垃圾名称',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '垃圾类型',
  `title` varchar(100) DEFAULT NULL COMMENT '环保标语',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '分类原因/说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='垃圾信息表';

/*Data for the table `garbage` */

insert  into `garbage`(`id`,`name`,`category`,`title`,`reason`) values ('9044b2f3-d244-48bb-9f35-eb6ec2f41163','纸箱','Recyclable Waste','Keep it green, recycle paper boxes!','Paper boxes are made of recyclable materials, such as cardboard, which can be processed and reused. They do not contain harmful substances and are not organic waste.'),('c8894cb7-525c-4d5c-b16d-e4f0d63cc399','塑料袋','Non-recyclable waste','Keep plastic bags out of landfills!','Plastic bags are not biodegradable and can cause pollution in the environment.');

/*Table structure for table `password_reset` */

DROP TABLE IF EXISTS `password_reset`;

CREATE TABLE `password_reset` (
  `id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '关联的用户名',
  `reset_token` varchar(100) NOT NULL COMMENT '临时重置令牌（唯一）',
  `expire_time` datetime NOT NULL COMMENT '令牌过期时间（默认15分钟）',
  `is_used` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已使用（0-未使用，1-已使用）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reset_token` (`reset_token`),
  KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `password_reset` */

/*Table structure for table `recycle_place` */

DROP TABLE IF EXISTS `recycle_place`;

CREATE TABLE `recycle_place` (
  `id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'uuid',
  `country` varchar(50) NOT NULL COMMENT '国家',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地址',
  `name` varchar(50) NOT NULL COMMENT '回收站名称',
  `user_id` varchar(50) NOT NULL COMMENT '回收人id',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '介绍',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `recycle_place` */

insert  into `recycle_place`(`id`,`country`,`city`,`address`,`name`,`user_id`,`description`) values ('1b0f32bb-0003-4543-b02f-b55453689820','country1','city1','country1 city1 road1','垃圾回收站1','398615ad-fa15-4bd5-871b-50576d54a261','123'),('e87b9b21-c19a-4e06-8ee3-e9d4debda253','国家1','城市2','国家1城市2路1','垃圾回收站2','52ed5403-b222-4be6-a45a-fcd4c430bf6b','回收站2');

/*Table structure for table `sentence` */

DROP TABLE IF EXISTS `sentence`;

CREATE TABLE `sentence` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sentence` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '句子',
  `create_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_date` timestamp NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `sentence` */

insert  into `sentence`(`id`,`sentence`,`create_by`,`create_date`) values (1,'垃圾分类一小步，地球减负一大步','398615ad-fa15-4bd5-871b-50576d54a261','2025-12-01 00:00:00'),(2,'垃圾有归属，环境不添堵','398615ad-fa15-4bd5-871b-50576d54a261','2025-12-01 00:00:00'),(3,'分好每一类，守护一方绿','398615ad-fa15-4bd5-871b-50576d54a261','2025-12-01 00:00:00'),(4,'垃圾有归属，环境不添堵','52ed5403-b222-4be6-a45a-fcd4c430bf6b','2025-12-13 22:46:54');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录id',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `role` varchar(10) DEFAULT NULL COMMENT '权限',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`role`) values ('0740e4de-f99c-4e02-8250-fcc62e0e94c6','volunteer','$2a$10$1wTDAVHGDJ.k4au32BcEOO6m/fyHj663dmIi7MxhY8LWDxXlo7/HS','volunteer'),('398615ad-fa15-4bd5-871b-50576d54a261','recycler','$2a$10$Ufap75TtxZmjkxaUOPkqdejNY4eq8iPoRiZCft7Zju/eruJfeNqNO','recycler'),('52ed5403-b222-4be6-a45a-fcd4c430bf6b','admin','$2a$10$Ufap75TtxZmjkxaUOPkqdejNY4eq8iPoRiZCft7Zju/eruJfeNqNO','admin'),('8d51b168-94c3-4a88-83d1-9224f33d9436','customer','$2a$10$Mj2P83Nr118YUJpLx3zElOFQ3TmYVTnwKouGEQbx4vjbQYrQ73kCe','customer');

/*Table structure for table `user_info` */

DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
  `id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'id',
  `name` varchar(100) DEFAULT NULL COMMENT '自定义名称',
  `user_id` varchar(100) NOT NULL COMMENT '用户uuid',
  `city` varchar(100) DEFAULT NULL COMMENT '城市',
  `country` varchar(100) DEFAULT NULL COMMENT '国家',
  `avatar` text COMMENT '头像',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `user_info` */

insert  into `user_info`(`id`,`name`,`user_id`,`city`,`country`,`avatar`) values ('002e3ecb-8469-45f8-93b0-6de78f5b1121','User002e3ecb37','0740e4de-f99c-4e02-8250-fcc62e0e94c6',NULL,NULL,'http://localhost:8080/assets/2025/12/avatar_524d83a5496f.png'),('33c8e330-6164-4524-8301-2a4ffc25ad02','User33c8e33019','8d51b168-94c3-4a88-83d1-9224f33d9436',NULL,NULL,'http://localhost:8080/assets/2025/12/avatar_524d83a5496f.png'),('5d515076-e742-4a5b-b6fc-4772942bb3f7','Customer1','398615ad-fa15-4bd5-871b-50576d54a261','city','country',NULL),('7098a6fa-1519-4302-8cce-83420e4f815b','User7098a6fa1','52ed5403-b222-4be6-a45a-fcd4c430bf6b',NULL,NULL,'http://localhost:8080/assets/2025/12/avatar_fe7accc90f74.gif');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- V1: baseline 스키마 (순수)
-- 테이블 + PK + UNIQUE + FK(및 FK 필수 인덱스)만 포함.
-- FK 의존성 순서로 생성: users/product → diary/store_product → likes/payment_history → fcm_token/user_wallet

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `character` enum('DODO','MIMI','RERE') DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `provider` enum('APPLE','KAKAO') DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `is_notification_enabled` bit(1) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_provider_info` (`provider`,`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `note_amount` int NOT NULL,
  `price` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- diary: 성능 인덱스 제외. user_id FK를 위한 plain 인덱스만 유지.
CREATE TABLE `diary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `lyrics` longtext,
  `mood` int NOT NULL,
  `music_url` varchar(255) DEFAULT NULL,
  `photo_url` varchar(255) DEFAULT NULL,
  `text` longtext,
  `weather` enum('CLOUDY','RAIN','SNOW','SUNNY') DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `like_count` bigint NOT NULL,
  `is_public` bit(1) NOT NULL,
  `status` enum('PENDING','COMPLETED','FAILED','DELETED') NOT NULL,
  `type` enum('PHOTO','TEXT') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_diary_user_id` (`user_id`),
  CONSTRAINT `FK74rd0bn5raxejw2ukenelbdmt` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `store_product` (
  `store_product_id` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `store_type` enum('APPLE','GOOGLE','ONE_STORE') DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  PRIMARY KEY (`store_product_id`),
  KEY `FKd91qk8cbmboomritdwn351tak` (`product_id`),
  CONSTRAINT `FKd91qk8cbmboomritdwn351tak` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','DELETED') NOT NULL,
  `diary_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_like_user_diary` (`user_id`,`diary_id`),
  KEY `FKae96sp8cw9jhmj7op5lemhdov` (`diary_id`),
  CONSTRAINT `FKae96sp8cw9jhmj7op5lemhdov` FOREIGN KEY (`diary_id`) REFERENCES `diary` (`id`),
  CONSTRAINT `FKnvx9seeqqyy71bij291pwiwrg` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `payment_history` (
  `purchase_token` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `status` enum('COMPLETED','FAILED','IN_PROGRESS','READY','REFUNDED') DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `store_product_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`purchase_token`),
  KEY `FKdlxblnvif9cwf3o7uuxiiq81w` (`store_product_id`),
  CONSTRAINT `FKdlxblnvif9cwf3o7uuxiiq81w` FOREIGN KEY (`store_product_id`) REFERENCES `store_product` (`store_product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `fcm_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `last_used_at` datetime(6) DEFAULT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKd83f58imj8mrd7j9ddrg9d7pd` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_wallet` (
  `user_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `note_count` int NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

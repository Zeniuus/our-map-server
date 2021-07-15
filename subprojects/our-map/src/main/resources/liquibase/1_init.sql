--liquibase formatted sql
--changeset swann:1
 CREATE TABLE `user` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `encrypted_password` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `instagram_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nickname` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `building` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `eup_myeon_dong` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `li` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `main_building_number` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `road_name` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  `si_do` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `si_gun_gu` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sub_building_number` varchar(4) COLLATE utf8mb4_unicode_ci NOT NULL,
  `lat` double NOT NULL,
  `lng` double NOT NULL,
  `name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `place` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `lat` double NOT NULL,
  `lng` double NOT NULL,
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `building_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT fk_place_building FOREIGN KEY (`building_id`) REFERENCES `building` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `building_accessibility` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `building_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `has_elevator` bit(1) NOT NULL,
  `has_obstacle_to_elevator` bit(1) NOT NULL,
  `stair_info` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT fk_building_accessibility_building FOREIGN KEY (`building_id`) REFERENCES `building` (`id`),
  CONSTRAINT fk_building_accessibility_user FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `place_accessibility` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `has_stair` bit(1) NOT NULL,
  `is_first_floor` bit(1) NOT NULL,
  `is_wheelchair_accessible` bit(1) NOT NULL,
  `place_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT fk_place_accessibility_place FOREIGN KEY (`place_id`) REFERENCES `place` (`id`),
  CONSTRAINT fk_place_accessibility_user FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

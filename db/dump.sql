SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `login` char(16) DEFAULT NULL,
  `password_hash` char(32) DEFAULT NULL,
  `email` char(255) DEFAULT NULL,
  `passkey` char(32) DEFAULT NULL,
  `display_name` char(32) DEFAULT NULL,
  `registration_date` datetime DEFAULT NULL,
  `last_activity_date` datetime DEFAULT NULL,
  `profile_data` mediumtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `passkey` (`passkey`),
  KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `account_torrents`;
CREATE TABLE `account_torrents` (
  `account_id` bigint(10) unsigned NOT NULL,
  `torrent_id` bigint(10) unsigned NOT NULL DEFAULT '0',
  `uploaded` bigint(20) unsigned NOT NULL DEFAULT '0',
  `downloaded` bigint(20) unsigned NOT NULL DEFAULT '0',
  `left` bigint(20) unsigned NOT NULL DEFAULT '0',
  `last_update` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`account_id`,`torrent_id`),
  CONSTRAINT `account_torrents` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `banned_accounts`;
CREATE TABLE `banned_accounts` (
  `account_id` bigint(10) unsigned NOT NULL,
  `issuer_id` bigint(11) unsigned DEFAULT '0',
  `creation_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `expiration_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `reason` text,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `banned_cookies`;
CREATE TABLE `banned_cookies` (
  `cookie_key` char(32) NOT NULL,
  `cookie_value` char(32) NOT NULL,
  `issuer_id` bigint(20) DEFAULT NULL,
  `permanent` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `creation_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `expiration_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `reason` text,
  PRIMARY KEY (`cookie_key`,`cookie_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `description` text,
  `template` char(255) NOT NULL DEFAULT 'default',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;


INSERT INTO `categories` VALUES ('1', '0', 'Категория 1', 'Описание первой категории', 'default');

DROP TABLE IF EXISTS `client_codes`;
CREATE TABLE `client_codes` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `code` char(2) DEFAULT NULL,
  `code_style` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

INSERT INTO `client_codes` VALUES ('1', 'AG', '0', 'Ares');
INSERT INTO `client_codes` VALUES ('2', 'A~', '0', 'Ares');
INSERT INTO `client_codes` VALUES ('3', 'AR', '0', 'Arctic');
INSERT INTO `client_codes` VALUES ('4', 'AT', '0', 'Artemis');
INSERT INTO `client_codes` VALUES ('5', 'AX', '0', 'BitPump');
INSERT INTO `client_codes` VALUES ('6', 'AZ', '0', 'Azureus');
INSERT INTO `client_codes` VALUES ('7', 'BB', '0', 'BitBuddy');
INSERT INTO `client_codes` VALUES ('8', 'BC', '0', 'BitComet');
INSERT INTO `client_codes` VALUES ('9', 'BF', '0', 'Bitflu');
INSERT INTO `client_codes` VALUES ('10', 'BG', '0', 'BTG (uses Rasterbar libtorrent)');
INSERT INTO `client_codes` VALUES ('11', 'BL', '0', 'BitBlinder');
INSERT INTO `client_codes` VALUES ('12', 'BP', '0', 'BitTorrent Pro (Azureus + spyware)');
INSERT INTO `client_codes` VALUES ('13', 'BR', '0', 'BitRocket');
INSERT INTO `client_codes` VALUES ('14', 'BS', '0', 'BTSlave');
INSERT INTO `client_codes` VALUES ('15', 'BW', '0', 'BitWombat');
INSERT INTO `client_codes` VALUES ('16', 'BX', '0', '~Bittorrent X');
INSERT INTO `client_codes` VALUES ('17', 'CD', '0', 'Enhanced CTorrent');
INSERT INTO `client_codes` VALUES ('18', 'CT', '0', 'CTorrent');
INSERT INTO `client_codes` VALUES ('19', 'DE', '0', 'DelugeTorrent');
INSERT INTO `client_codes` VALUES ('20', 'DP', '0', 'Propagate Data Client');
INSERT INTO `client_codes` VALUES ('21', 'EB', '0', 'EBit');
INSERT INTO `client_codes` VALUES ('22', 'ES', '0', 'electric sheep');
INSERT INTO `client_codes` VALUES ('23', 'FC', '0', 'FileCroc');
INSERT INTO `client_codes` VALUES ('24', 'FT', '0', 'FoxTorrent');
INSERT INTO `client_codes` VALUES ('25', 'GS', '0', 'GSTorrent');
INSERT INTO `client_codes` VALUES ('26', 'HK', '0', 'Hekate');
INSERT INTO `client_codes` VALUES ('27', 'HL', '0', 'Halite');
INSERT INTO `client_codes` VALUES ('28', 'HN', '0', 'Hydranode');
INSERT INTO `client_codes` VALUES ('29', 'KG', '0', 'KGet');
INSERT INTO `client_codes` VALUES ('30', 'KT', '0', 'KTorrent');
INSERT INTO `client_codes` VALUES ('31', 'LC', '0', 'LeechCraft');
INSERT INTO `client_codes` VALUES ('32', 'LH', '0', 'LH-ABC');
INSERT INTO `client_codes` VALUES ('33', 'LP', '0', 'Lphant');
INSERT INTO `client_codes` VALUES ('34', 'LT', '0', 'libtorrent');
INSERT INTO `client_codes` VALUES ('35', 'lt', '0', 'libTorrent');
INSERT INTO `client_codes` VALUES ('36', 'LW', '0', 'LimeWire');
INSERT INTO `client_codes` VALUES ('37', 'MK', '0', 'Meerkat');
INSERT INTO `client_codes` VALUES ('38', 'MO', '0', 'MonoTorrent');
INSERT INTO `client_codes` VALUES ('39', 'MP', '0', 'MooPolice');
INSERT INTO `client_codes` VALUES ('40', 'MR', '0', 'Miro');
INSERT INTO `client_codes` VALUES ('41', 'MT', '0', 'MoonlightTorrent');
INSERT INTO `client_codes` VALUES ('42', 'NX', '0', 'Net Transport');
INSERT INTO `client_codes` VALUES ('43', 'OS', '0', 'OneSwarm');
INSERT INTO `client_codes` VALUES ('44', 'OT', '0', 'OmegaTorrent');
INSERT INTO `client_codes` VALUES ('45', 'PD', '0', 'Pando');
INSERT INTO `client_codes` VALUES ('46', 'qB', '0', 'qBittorrent');
INSERT INTO `client_codes` VALUES ('47', 'QD', '0', 'QQDownload');
INSERT INTO `client_codes` VALUES ('48', 'QT', '0', 'Qt 4 Torrent example');
INSERT INTO `client_codes` VALUES ('49', 'RT', '0', 'Retriever');
INSERT INTO `client_codes` VALUES ('50', 'RZ', '0', 'RezTorrent');
INSERT INTO `client_codes` VALUES ('51', 'S~', '0', 'Shareaza alpha/beta');
INSERT INTO `client_codes` VALUES ('52', 'SB', '0', '~Swiftbit');
INSERT INTO `client_codes` VALUES ('53', 'SD', '0', 'Thunder (aka XùnLéi)');
INSERT INTO `client_codes` VALUES ('54', 'SM', '0', 'SoMud');
INSERT INTO `client_codes` VALUES ('55', 'SS', '0', 'SwarmScope');
INSERT INTO `client_codes` VALUES ('56', 'ST', '0', 'SymTorrent');
INSERT INTO `client_codes` VALUES ('57', 'st', '0', 'sharktorrent');
INSERT INTO `client_codes` VALUES ('58', 'SZ', '0', 'Shareaza');
INSERT INTO `client_codes` VALUES ('59', 'TN', '0', 'TorrentDotNET');
INSERT INTO `client_codes` VALUES ('60', 'TR', '0', 'Transmission');
INSERT INTO `client_codes` VALUES ('61', 'TS', '0', 'Torrentstorm');
INSERT INTO `client_codes` VALUES ('62', 'TT', '0', 'TuoTu');
INSERT INTO `client_codes` VALUES ('63', 'UL', '0', 'uLeecher!');
INSERT INTO `client_codes` VALUES ('64', 'UM', '0', 'µTorrent for Mac');
INSERT INTO `client_codes` VALUES ('65', 'UT', '0', 'µTorrent');
INSERT INTO `client_codes` VALUES ('66', 'VG', '0', 'Vagaa');
INSERT INTO `client_codes` VALUES ('67', 'WT', '0', 'BitLet');
INSERT INTO `client_codes` VALUES ('68', 'WY', '0', 'FireTorrent');
INSERT INTO `client_codes` VALUES ('69', 'XL', '0', 'Xunlei');
INSERT INTO `client_codes` VALUES ('70', 'XS', '0', 'XSwifter');
INSERT INTO `client_codes` VALUES ('71', 'XT', '0', 'XanTorrent');
INSERT INTO `client_codes` VALUES ('72', 'XX', '0', 'Xtorrent');
INSERT INTO `client_codes` VALUES ('73', 'ZT', '0', 'ZipTorrent');
INSERT INTO `client_codes` VALUES ('74', 'A', '1', 'ABC');
INSERT INTO `client_codes` VALUES ('75', 'O', '1', 'Osprey Permaseed');
INSERT INTO `client_codes` VALUES ('76', 'Q', '1', 'BTQueue');
INSERT INTO `client_codes` VALUES ('77', 'R', '1', 'Tribler');
INSERT INTO `client_codes` VALUES ('78', 'S', '1', 'Shadow\'s client');
INSERT INTO `client_codes` VALUES ('79', 'T', '1', 'BitTornado');
INSERT INTO `client_codes` VALUES ('80', 'U', '1', 'UPnP NAT Bit Torrent');

DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `id` int(10) unsigned NOT NULL,
  `title` char(255) NOT NULL DEFAULT '',
  `prefix` char(255) NOT NULL DEFAULT '',
  `postfix` char(255) NOT NULL DEFAULT '',
  `privileges` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `groups` VALUES ('1', 'Администратор', '<span style=\"color: red;\">', '</span>', '{\"torrent_upload\": true, \"torrent_download\": true}');
INSERT INTO `groups` VALUES ('2', 'Модератор', '<span style=\"color: green;\">', '</span>', '{\"torrent_upload\": true, \"torrent_download\": true}');
INSERT INTO `groups` VALUES ('3', 'Пользователь', '', '', '{\"torrent_upload\": true, \"torrent_download\": true}');

DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` int(10) unsigned NOT NULL,
  `key` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

INSERT INTO `tags` VALUES ('1', '2', 'tv', 'TV');
INSERT INTO `tags` VALUES ('2', '2', 'dvd', 'DVD');
INSERT INTO `tags` VALUES ('3', '2', 'hd', 'HD');
INSERT INTO `tags` VALUES ('4', '2', 'camrip', 'CamRip');
INSERT INTO `tags` VALUES ('5', '2', 'ts', 'Telesync (TS)');
INSERT INTO `tags` VALUES ('6', '2', 'scr', 'Screener');
INSERT INTO `tags` VALUES ('7', '3', '480p', '480p');
INSERT INTO `tags` VALUES ('8', '3', '720p', '720p');
INSERT INTO `tags` VALUES ('9', '3', '1080p', '1080p');

DROP TABLE IF EXISTS `torrents`;
CREATE TABLE `torrents` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` int(10) unsigned NOT NULL DEFAULT '0',
  `author_id` bigint(10) unsigned NOT NULL DEFAULT '0',
  `title` char(255) DEFAULT '',
  `description` mediumtext,
  `description_html` mediumtext,
  `creation_date` datetime DEFAULT NULL,
  `edit_date` datetime DEFAULT NULL,
  `info_hash` binary(20) DEFAULT NULL,
  `views` int(10) unsigned NOT NULL DEFAULT '0',
  `hits` int(10) unsigned NOT NULL DEFAULT '0',
  `visible` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `torrent_size` bigint(20) DEFAULT NULL,
  `torrent_creation_date` datetime DEFAULT NULL,
  `torrent_created_by` char(255) DEFAULT NULL,
  `state` enum('PREPARING','CONFIRMED','MODERATED','CLOSED') NOT NULL DEFAULT 'PREPARING',
  `state_change_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `info_hash` (`info_hash`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `torrent_comments`;
CREATE TABLE `torrent_comments` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `torrent_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `content` mediumtext,
  `content_html` mediumtext,
  `visible` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `post_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `torrents_comments` (`torrent_id`),
  CONSTRAINT `torrents_comments` FOREIGN KEY (`torrent_id`) REFERENCES `torrents` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `torrent_peers`;
CREATE TABLE `torrent_peers` (
  `account_id` bigint(10) unsigned NOT NULL DEFAULT '0',
  `torrent_id` bigint(10) unsigned NOT NULL DEFAULT '0',
  `peer_id` binary(20) DEFAULT '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',
  `ip` bigint(20) NOT NULL DEFAULT '0',
  `port` smallint(5) unsigned NOT NULL DEFAULT '0',
  `user_agent` char(255) DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  `uploaded` bigint(20) unsigned NOT NULL DEFAULT '0',
  `downloaded` bigint(20) unsigned NOT NULL DEFAULT '0',
  `left` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_id`,`torrent_id`,`ip`,`port`),
  KEY `torrent_id` (`torrent_id`),
  CONSTRAINT `torrent_peers` FOREIGN KEY (`torrent_id`) REFERENCES `torrents` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `torrent_tags`;
CREATE TABLE `torrent_tags` (
  `torrent_id` bigint(20) NOT NULL,
  `tag` char(32) NOT NULL,
  PRIMARY KEY (`torrent_id`,`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `torrent_uploads`;
CREATE TABLE `torrent_uploads` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `upload_date` datetime DEFAULT NULL,
  `info_hash` binary(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

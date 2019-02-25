/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : dingshen

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-04-09 11:44:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `system_dic`
-- ----------------------------
DROP TABLE IF EXISTS `system_dic`;
CREATE TABLE `system_dic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of system_dic
-- ----------------------------
INSERT INTO `system_dic` VALUES ('1', '2016-12-29 15:58:11', '2016-12-29 15:58:08', '直营', '1', '1000', '1');

-- ----------------------------
-- Table structure for `system_menu`
-- ----------------------------
DROP TABLE IF EXISTS `system_menu`;
CREATE TABLE `system_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `link` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of system_menu
-- ----------------------------
INSERT INTO `system_menu` VALUES ('1', '2016-12-14 17:24:33', '2016-12-14 17:24:36', 'xxx', '系统设置', null, '0');
INSERT INTO `system_menu` VALUES ('2', '2016-12-14 17:24:54', '2016-12-14 17:24:57', 'views/sys/user/user.html', '用户管理', '1', '1');
INSERT INTO `system_menu` VALUES ('3', '2016-12-15 15:51:59', '2016-12-15 15:52:01', 'views/sys/role/role.html', '角色管理', '1', '2');
INSERT INTO `system_menu` VALUES ('4', '2016-12-15 15:52:24', '2016-12-15 15:52:28', 'views/sys/menu/menu.html', '菜单管理', '1', '3');
INSERT INTO `system_menu` VALUES ('5', '2016-12-29 15:33:47', '2016-12-29 15:33:49', '', '门店管理', null, '4');
INSERT INTO `system_menu` VALUES ('6', '2016-12-29 15:35:05', '2016-12-29 15:35:08', 'views/rs/restaurant/restaurant.html', '门店管理', '5', '5');
INSERT INTO `system_menu` VALUES ('7', '2016-12-30 17:25:52', '2016-12-30 17:25:55', '', '营销管理', null, '6');
INSERT INTO `system_menu` VALUES ('8', '2016-12-30 17:28:07', '2016-12-30 17:28:04', 'views/mktg/coupon/coupon.html', '优惠券管理', '7', '7');
INSERT INTO `system_menu` VALUES ('9', '2017-01-03 17:10:09', '2017-01-03 17:10:12', null, '餐品管理', null, '8');
INSERT INTO `system_menu` VALUES ('10', '2017-01-03 17:10:40', '2017-01-03 17:10:42', 'views/dish/dish/dish.html', '餐品管理', '9', '9');
INSERT INTO `system_menu` VALUES ('11', '2017-01-04 13:42:54', '2017-01-04 13:42:57', null, '会员管理', null, '10');
INSERT INTO `system_menu` VALUES ('12', '2017-01-04 13:43:31', '2017-01-04 13:43:34', 'views/member/member/member.html', '会员管理', '11', '11');
INSERT INTO `system_menu` VALUES ('13', '2017-01-04 17:28:34', '2017-01-04 17:28:37', null, '订单管理', null, '12');
INSERT INTO `system_menu` VALUES ('14', '2017-01-04 17:29:21', '2017-01-04 17:29:23', 'views/trade/dishorder/dishorder.html', '订单管理', '13', '13');
INSERT INTO `system_menu` VALUES ('15', '2017-01-05 14:21:00', '2017-01-05 14:21:02', null, '微信管理', null, '14');
INSERT INTO `system_menu` VALUES ('16', '2017-01-05 14:21:10', '2017-01-05 14:21:13', 'views/weixin/wxmenu/wxmenu.html', '微信菜单', '15', '15');
INSERT INTO `system_menu` VALUES ('17', '2017-01-05 16:25:34', '2017-01-05 16:25:37', 'views/rs/clerk/clerk.html', '店员管理', '5', '16');
INSERT INTO `system_menu` VALUES ('18', '2017-02-15 17:11:58', '2017-03-07 09:21:37', 'views/dish/dish/material.html', '加码管理', '9', '18');
INSERT INTO `system_menu` VALUES ('19', '2017-02-22 11:14:26', '2017-02-22 11:14:26', null, '广告图管理', null, '16');
INSERT INTO `system_menu` VALUES ('20', '2017-02-22 11:14:58', '2017-02-22 11:16:11', 'views/ad/ad/ad.html', '广告图管理', '19', '17');
INSERT INTO `system_menu` VALUES ('22', '2017-03-03 09:38:42', '2017-03-03 09:38:45', null, '意见箱', null, '21');
INSERT INTO `system_menu` VALUES ('23', '2017-03-03 09:39:20', '2017-03-03 09:39:24', 'views/advice/advice/advice.html', '意见反馈', '22', '22');
INSERT INTO `system_menu` VALUES ('24', '2017-03-03 15:39:05', '2017-03-03 15:39:08', 'views/mktg/activity/activity.html', '活动管理', '7', '23');
INSERT INTO `system_menu` VALUES ('25', '2017-03-10 16:13:22', '2017-03-17 16:38:14', 'views/stock/stock/stock.html', '原料管理', '5', '20');
INSERT INTO `system_menu` VALUES ('26', '2017-03-10 16:16:10', '2017-03-24 09:41:24', 'views/stock/stockRestaurant/stockrestaurant.html', '门店库存', '5', '25');
INSERT INTO `system_menu` VALUES ('27', '2017-03-13 19:43:17', '2017-03-13 19:43:46', 'views/rs/rsdish/rsdish.html', '门店餐品', '5', '100');
INSERT INTO `system_menu` VALUES ('28', '2017-03-17 15:55:15', '2017-03-17 16:03:23', 'views/stock/purchaseTemplate/purchaseTemplate.html', '采购模板', '5', '20');
INSERT INTO `system_menu` VALUES ('29', '2017-03-17 16:43:52', '2017-03-17 16:44:47', 'views/stock/stockCate/stockCate.html', '原料分类', '5', '10');
INSERT INTO `system_menu` VALUES ('30', '2017-03-21 14:34:44', '2017-03-21 14:34:44', 'views/stock/purchase/purchase.html', '采购单', '5', '5');
INSERT INTO `system_menu` VALUES ('31', '2017-03-22 11:21:47', '2017-03-22 11:21:47', 'views/stock/storage/storage.html', '入库单', '5', '20');

-- ----------------------------
-- Table structure for `system_role`
-- ----------------------------
DROP TABLE IF EXISTS `system_role`;
CREATE TABLE `system_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `use_flag` bit(1) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of system_role
-- ----------------------------
INSERT INTO `system_role` VALUES ('1', '2016-12-16 14:40:36', '2016-12-29 09:38:48', '超级管理拥有所有权限', '超级管理员', '', null);
INSERT INTO `system_role` VALUES ('2', '2016-12-20 14:56:00', '2016-12-29 09:38:39', '普通用户', '普通用户', '', null);
INSERT INTO `system_role` VALUES ('3', '2016-12-20 15:46:44', '2017-01-05 14:12:32', '放松放松大放送', '侧二十', '', null);
INSERT INTO `system_role` VALUES ('4', null, '2016-12-28 15:53:30', '3123123', '213123', null, null);
INSERT INTO `system_role` VALUES ('5', null, '2016-12-28 15:50:09', 'admin1', 'admin1', '', null);
INSERT INTO `system_role` VALUES ('6', '2016-12-20 16:30:33', '2016-12-20 16:30:33', null, null, null, null);
INSERT INTO `system_role` VALUES ('7', null, '2016-12-28 15:52:34', '嘻嘻嘻发现 ', '测试', '', null);
INSERT INTO `system_role` VALUES ('8', '2016-12-20 17:27:53', '2016-12-20 17:27:53', null, null, null, null);
INSERT INTO `system_role` VALUES ('9', '2016-12-20 17:30:45', '2016-12-20 17:30:45', null, null, null, null);
INSERT INTO `system_role` VALUES ('10', '2016-12-28 13:35:06', '2016-12-29 11:23:15', '休息休息', '测试', null, null);
INSERT INTO `system_role` VALUES ('11', '2016-12-28 13:37:47', '2016-12-29 11:26:40', '信息', '嘻嘻嘻', '', null);
INSERT INTO `system_role` VALUES ('12', '2016-12-28 13:40:51', '2016-12-28 13:40:51', '2323', '嘻嘻嘻', '', null);
INSERT INTO `system_role` VALUES ('13', '2017-02-08 09:00:43', '2017-02-08 13:45:36', null, '', '', '111121213');

-- ----------------------------
-- Table structure for `system_role_menu_rel`
-- ----------------------------
DROP TABLE IF EXISTS `system_role_menu_rel`;
CREATE TABLE `system_role_menu_rel` (
  `role_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`,`menu_id`),
  KEY `FK_denyk3dapp7kvdhmq5fo5p6jh` (`menu_id`),
  CONSTRAINT `FK_cptcdc3hc9wr3b7xy6mciqo0` FOREIGN KEY (`role_id`) REFERENCES `system_role` (`id`),
  CONSTRAINT `FK_denyk3dapp7kvdhmq5fo5p6jh` FOREIGN KEY (`menu_id`) REFERENCES `system_menu` (`id`),
  CONSTRAINT `FKdjcepa2ppjuev9x9ad9qrb6ek` FOREIGN KEY (`role_id`) REFERENCES `system_role` (`id`),
  CONSTRAINT `FKdsj2y82dxqwdlop7e1ay4wmkn` FOREIGN KEY (`menu_id`) REFERENCES `system_menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of system_role_menu_rel
-- ----------------------------
INSERT INTO `system_role_menu_rel` VALUES ('13', '1');
INSERT INTO `system_role_menu_rel` VALUES ('3', '2');
INSERT INTO `system_role_menu_rel` VALUES ('11', '2');
INSERT INTO `system_role_menu_rel` VALUES ('13', '2');
INSERT INTO `system_role_menu_rel` VALUES ('13', '3');
INSERT INTO `system_role_menu_rel` VALUES ('13', '4');
INSERT INTO `system_role_menu_rel` VALUES ('13', '5');
INSERT INTO `system_role_menu_rel` VALUES ('13', '6');
INSERT INTO `system_role_menu_rel` VALUES ('13', '7');
INSERT INTO `system_role_menu_rel` VALUES ('13', '8');
INSERT INTO `system_role_menu_rel` VALUES ('13', '9');
INSERT INTO `system_role_menu_rel` VALUES ('13', '10');
INSERT INTO `system_role_menu_rel` VALUES ('13', '11');
INSERT INTO `system_role_menu_rel` VALUES ('13', '12');
INSERT INTO `system_role_menu_rel` VALUES ('13', '13');
INSERT INTO `system_role_menu_rel` VALUES ('13', '14');
INSERT INTO `system_role_menu_rel` VALUES ('13', '15');
INSERT INTO `system_role_menu_rel` VALUES ('13', '16');
INSERT INTO `system_role_menu_rel` VALUES ('13', '17');

-- ----------------------------
-- Table structure for `system_user`
-- ----------------------------
DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `account` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `use_flag` bit(1) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of system_user
-- ----------------------------
INSERT INTO `system_user` VALUES ('5', '2016-12-13 17:32:26', '2016-12-13 17:32:26', 'admin123', null, 'admin', '09d092a6b57981c59d8a6d2cd964b3f6b2720e76', null, 'e6c42fb8d4537185', '0', null, null);
INSERT INTO `system_user` VALUES ('6', '2016-12-13 17:36:08', '2016-12-13 17:36:08', 'admin1', null, 'admin2', '390587d7e145a4384582ce6e46043a5dc62d62d8', null, '0e40dac260ccb35e', '0', null, null);
INSERT INTO `system_user` VALUES ('7', '2016-12-30 15:12:36', '2016-12-30 16:21:39', '123', 'ds', 'abc', 'b2c231cc073d3f84e77440f388d7f99bc5732d4d', '1356', '39830ca918c05f86', '1', '', '111');
INSERT INTO `system_user` VALUES ('8', '2017-02-08 09:07:37', '2017-02-10 11:38:41', 'he', '', 'he', '3b330437c8a70f23bb6a26b366cdd788c5a616ab', '', '1e5a7a8377f5f51d', '1', '', '');

-- ----------------------------
-- Table structure for `system_user_role_rel`
-- ----------------------------
DROP TABLE IF EXISTS `system_user_role_rel`;
CREATE TABLE `system_user_role_rel` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK_i0q2q80bv73shxccmpae1m3ti` (`role_id`),
  CONSTRAINT `FK1mvtjdjktec47utb8xq0tfjl1` FOREIGN KEY (`role_id`) REFERENCES `system_role` (`id`),
  CONSTRAINT `FK_9njiolxoq7vtkmj4jaxp9r3xv` FOREIGN KEY (`user_id`) REFERENCES `system_user` (`id`),
  CONSTRAINT `FK_i0q2q80bv73shxccmpae1m3ti` FOREIGN KEY (`role_id`) REFERENCES `system_role` (`id`),
  CONSTRAINT `FKf8l3tt8pnmh9equ4wrv5iip7f` FOREIGN KEY (`user_id`) REFERENCES `system_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of system_user_role_rel
-- ----------------------------
INSERT INTO `system_user_role_rel` VALUES ('8', '1');
INSERT INTO `system_user_role_rel` VALUES ('6', '2');
INSERT INTO `system_user_role_rel` VALUES ('7', '2');
INSERT INTO `system_user_role_rel` VALUES ('8', '13');

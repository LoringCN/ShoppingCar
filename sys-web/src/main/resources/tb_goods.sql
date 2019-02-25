/*
Navicat MySQL Data Transfer

Source Server         : local_mysql
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : shopcar

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-05-16 12:41:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_goods
-- ----------------------------
DROP TABLE IF EXISTS `tb_goods`;
CREATE TABLE `tb_goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) DEFAULT NULL COMMENT '购物车所属超市id',
  `barcode` varchar(32) DEFAULT NULL COMMENT '商品条形码',
  `title` varchar(100) DEFAULT NULL COMMENT '商品名',
  `promotion_price` int(11) DEFAULT NULL COMMENT '商品折扣价',
  `normal_price` int(11) DEFAULT NULL COMMENT '商品原价',
  `cover_img_url` varchar(255) DEFAULT NULL,
  `detail_img_url` varchar(255) DEFAULT NULL COMMENT '商品详情图',
  `sid` varchar(255) DEFAULT NULL COMMENT '商品所在位置信息（基于地图粗粒度位置）',
  `location` varchar(255) DEFAULT NULL COMMENT '商品所在货架信息',
  `descr` text COMMENT '商品详细信息介绍信息',
  `type` varchar(20) DEFAULT 'normal' COMMENT '商品类型（normal - 正常，promotion - 特价）',
  `flag` char(2) DEFAULT '1' COMMENT '状态标记（''-1'' - 删除，1 - 正常）',
  `sort_no` int(11) DEFAULT NULL COMMENT '显示顺序',
  `created_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_goods
-- ----------------------------
INSERT INTO `tb_goods` VALUES ('1', '11', null, '巧克力', null, '1000', 'http://localhost:8080/a.png', 'http://localhost:8080/b.png', '111', '第三排货架A面', '法国进口巧克力', ' normal', '1', '1', '2016-10-22 09:10:00', '2016-10-22 09:10:00', '该巧克力保质期为一年');
INSERT INTO `tb_goods` VALUES ('2', '11', null, '巧克力', '200', '1000', 'http://localhost:8080/a.png', 'http://localhost:8080/b.png', '222', '第三排货架A面', '法国进口巧克力', 'normal', '1', '3', '2016-10-22 09:10:00', '2016-10-22 09:10:00', '该巧克力保质期为一年');
INSERT INTO `tb_goods` VALUES ('3', '12', null, '巧克力', '300', '1000', 'http://localhost:8080/a.png', 'http://localhost:8080/b.png', '1111111', '第三排货架A面', '法国进口巧克力', 'normal', '1', '2', '2016-10-22 09:10:00', '2016-10-22 09:10:00', '该巧克力保质期为一年');
INSERT INTO `tb_goods` VALUES ('4', '12', null, '巧克力', '400', '1000', 'http://localhost:8080/a.png', 'http://localhost:8080/b.png', '2681688', '第三排货架A面', '法国进口巧克力', 'promotion', '1', '4', '2016-10-22 09:10:00', '2016-10-22 09:10:00', '该巧克力保质期为一年');
INSERT INTO `tb_goods` VALUES ('5', '13', null, '巧克力', '500', '1000', 'http://localhost:8080/a.png', 'http://localhost:8080/b.png', '1111', '第三排货架A面', '法国进口巧克力', 'promotion', '1', '7', '2016-10-22 09:10:00', '2016-10-22 09:10:00', '该巧克力保质期为一年');
INSERT INTO `tb_goods` VALUES ('6', '13', null, '巧克力', '800', '1000', 'http://localhost:8080/a.png', 'http://localhost:8080/b.png', '33333', '第三排货架A面', '法国进口巧克力', 'promotion', '1', '6', '2016-10-22 09:10:00', '2016-10-22 09:10:00', '该巧克力保质期为一年');
INSERT INTO `tb_goods` VALUES ('7', '14', 'hgahaha', '奶粉', '10000', '20000', 'http://localhost:8080/img/z.png', null, '11111', '第三个货架第二层B面', '颜色:红色,绿色，尺寸:大,中,小', 'normal', '1', '11', '2017-05-12 15:24:51', '2017-05-12 15:56:38', null);

/*
Navicat MySQL Data Transfer

Source Server         : 2.174
Source Server Version : 50636
Source Host           : 192.168.2.174:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50636
File Encoding         : 65001

Date: 2018-01-26 17:41:28
*/

SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE ZMBean;
-- ----------------------------
-- Table structure for zqz_jmx_jvm
-- ----------------------------
DROP TABLE IF EXISTS `zqz_jmx_jvm`;
CREATE TABLE `zqz_jmx_jvm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL DEFAULT '' COMMENT 'jvm别名',
  `ip` varchar(15) NOT NULL COMMENT 'jvm服务器ip',
  `port` int(11) NOT NULL COMMENT 'jmx端口',
  `jmxUserName` varchar(20) NOT NULL DEFAULT '' COMMENT 'jmx用户名',
  `jmxPassword` varchar(20) NOT NULL DEFAULT '' COMMENT 'jmx密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of zqz_jmx_jvm
-- ----------------------------
INSERT INTO `zqz_jmx_jvm` VALUES ('4', '74-9999', '192.168.2.74', '9999', '', '');
INSERT INTO `zqz_jmx_jvm` VALUES ('5', '74-8888', '192.168.2.74', '8888', '', '');

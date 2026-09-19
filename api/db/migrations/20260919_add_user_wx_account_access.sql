-- Run once against an existing wx database before deploying the new console.
CREATE TABLE IF NOT EXISTS `sys_user_wx_account` (
  `user_id` bigint(20) NOT NULL COMMENT '系统用户 ID',
  `appid` char(20) NOT NULL COMMENT '公众号 appid',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `appid`),
  KEY `idx_appid` (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户可操作公众号';

-- Preserve the existing administrator behavior: the super administrator can access every account.
INSERT IGNORE INTO `sys_user_wx_account` (`user_id`, `appid`)
SELECT 1, `appid` FROM `wx_account`;

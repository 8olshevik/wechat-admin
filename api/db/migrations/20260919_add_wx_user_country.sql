-- Add country column for OAuth snsapi_userinfo 补全字段。
-- nickname/headimgurl/sex/city/province 已存在，OAuth 授权后回填。
ALTER TABLE `wx_user` ADD COLUMN `country` varchar(20) NULL DEFAULT NULL COMMENT '国家' AFTER `province`;

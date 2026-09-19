# 微信公众号运营前端

## 本地开发

1. 启动 API：默认地址为 `http://127.0.0.1:8088/wx`。
2. 在本目录执行 `npm install` 与 `npm run dev`。
3. 浏览器访问 `http://127.0.0.1:3001`。

首次上线前需执行 `api/db/migrations/20260919_add_user_wx_account_access.sql`。
非超级管理员必须在 `sys_user_wx_account` 中被授予对应 `appid`，才可在控制台中选择及操作该公众号。

## 生产部署

`deploy/nginx.conf` 将静态站点与 `/wx/**` API 代理在同一域名下。复制
`deploy/.env.production.example` 为部署环境的 `.env`，再根据实际 API 镜像和数据库配置执行
`docker compose -f deploy/docker-compose.production.yml up -d --build`。

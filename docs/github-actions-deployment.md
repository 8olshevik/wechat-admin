# GitHub Actions Deployment

The workflow in `.github/workflows/deploy.yml` builds the Vue application and the Java 8 API
on GitHub-hosted runners. Pull requests run the build only. A push to `main` uploads a release
to the server, switches the active frontend and JAR symlinks, then restarts `wechat-admin`.

## Server prerequisites

Configure these once on the production server:

```text
/opt/wechat-admin/
  releases/
  current-web -> releases/<commit>/web
  current-api.jar -> releases/<commit>/api/wx-api.jar
```

The systemd unit must execute `/opt/wechat-admin/current-api.jar`. Nginx should serve
`/opt/wechat-admin/current-web` and proxy `/wx/` to the API service's loopback port.

Install `api/deploy/wechat-admin.service` at `/etc/systemd/system/wechat-admin.service`.
Create `/etc/wechat-admin/wechat-admin.env` from
`api/deploy/wechat-admin.env.example`, fill in the existing MySQL and Redis connection
settings, then run:

```text
mkdir -p /opt/wechat-admin/releases /etc/wechat-admin
chown -R www-data:www-data /opt/wechat-admin
systemctl daemon-reload
systemctl enable wechat-admin
```

The deployment account must be able to write `/opt/wechat-admin` and restart only this service:

```text
systemctl restart wechat-admin
systemctl is-active wechat-admin
```

Do not use the root account for the GitHub Actions key in production.

## GitHub environment

Create a repository environment named `production`, then add the following environment secrets:

| Secret | Value |
| --- | --- |
| `DEPLOY_HOST` | Production server host name or IP |
| `DEPLOY_PORT` | SSH port, for example `2232` |
| `DEPLOY_USER` | Dedicated deployment user |
| `DEPLOY_PATH` | `/opt/wechat-admin` |
| `DEPLOY_SSH_PRIVATE_KEY` | Private key for the deployment user |
| `DEPLOY_KNOWN_HOSTS` | Output from `ssh-keyscan -p <port> <host>` |

The public half of `DEPLOY_SSH_PRIVATE_KEY` must be added to the deployment user's
`~/.ssh/authorized_keys` on the server.

## Release behavior

Each deployed commit is uploaded into `releases/<commit-sha>`. The frontend and API are
activated independently by symlink, so rolling back is a symlink switch followed by:

```text
systemctl restart wechat-admin
```

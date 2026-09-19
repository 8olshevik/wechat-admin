# GitHub Actions Deployment

The workflow in `.github/workflows/deploy.yml` builds the Vue application and the Java 8 API
on GitHub-hosted runners. Pull requests run the build only. A push to `main` uploads a release
to the server, switches the active frontend and JAR symlinks, then restarts `wechat-admin`.

## Server prerequisites

Configure these once on the production server:

```text
/opt/wechat-admin/
  web/
  wechat-admin.jar
  web.previous/
  wechat-admin.previous.jar
  .deploy/
  logs/
```

Nginx should always serve `/opt/wechat-admin/web` and proxy `/wx/` to the API service's
loopback port. The systemd unit executes `/opt/wechat-admin/wechat-admin.jar`.

Install `api/deploy/wechat-admin.service` at `/etc/systemd/system/wechat-admin.service`.
Create `/etc/wechat-admin/wechat-admin.env` from
`api/deploy/wechat-admin.env.example`, fill in the existing MySQL and Redis connection
settings, then run:

```text
mkdir -p /opt/wechat-admin/.deploy /etc/wechat-admin
systemctl daemon-reload
systemctl enable wechat-admin
```

The systemd unit creates `/opt/wechat-admin/logs` with the required `www-data` ownership
before each application start.

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

Each deployed commit is uploaded into `/opt/wechat-admin/.deploy/<commit-sha>` first.
After the complete upload is verified, the workflow replaces the stable `/opt/wechat-admin/web`
directory and `/opt/wechat-admin/wechat-admin.jar` file. The immediately previous frontend and
JAR are retained for rollback:

```text
/opt/wechat-admin/web.previous
/opt/wechat-admin/wechat-admin.previous.jar
```

To roll back, replace the stable paths with their `.previous` copies, then restart
`wechat-admin`.

const TOKEN_KEY = 'wechat-console-token'
const ACCOUNT_KEY = 'wechat-console-account'

export const session = {
  get token() {
    return sessionStorage.getItem(TOKEN_KEY) || ''
  },
  set token(value) {
    value ? sessionStorage.setItem(TOKEN_KEY, value) : sessionStorage.removeItem(TOKEN_KEY)
  },
  get account() {
    const raw = sessionStorage.getItem(ACCOUNT_KEY)
    return raw ? JSON.parse(raw) : null
  },
  set account(value) {
    if (value) {
      sessionStorage.setItem(ACCOUNT_KEY, JSON.stringify(value))
      document.cookie = `appid=${encodeURIComponent(value.appid)}; Path=/; SameSite=Lax`
    } else {
      sessionStorage.removeItem(ACCOUNT_KEY)
      document.cookie = 'appid=; Path=/; Max-Age=0; SameSite=Lax'
    }
  },
  clear() {
    this.token = ''
    this.account = null
  },
}

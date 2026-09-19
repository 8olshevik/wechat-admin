<template>
  <div class="accounts">
    <header>
      <div>
        <h1>选择工作公众号</h1>
        <p>选择后即可进入对应账号的运营工作台</p>
      </div>
      <div class="header-actions">
        <el-button :icon="Plus" type="primary" @click="openCreate">接入公众号</el-button>
        <el-button text @click="logout">退出登录</el-button>
      </div>
    </header>

    <div v-loading="loading" class="account-grid">
      <article v-for="item in list" :key="item.appid" :class="{ active: selected?.appid === item.appid }" class="account-card">
        <button class="account-select" @click="selected = item">
          <span class="avatar">{{ item.name.slice(0, 1) }}</span>
          <span class="account-copy">
            <b>{{ item.name }}</b>
            <small>{{ item.appid }}</small>
          </span>
          <el-icon v-if="selected?.appid === item.appid"><CircleCheckFilled /></el-icon>
        </button>
        <div class="account-meta">
          <el-tag :type="item.secretConfigured && item.tokenConfigured ? 'success' : 'warning'" size="small">
            {{ item.secretConfigured && item.tokenConfigured ? '已配置' : '待完善' }}
          </el-tag>
          <el-button :icon="EditPen" text @click="openEdit(item)">配置</el-button>
        </div>
      </article>
    </div>

    <el-empty v-if="!loading && !list.length" description="尚未接入公众号">
      <el-button :icon="Plus" type="primary" @click="openCreate">接入公众号</el-button>
    </el-empty>

    <footer>
      <el-button type="primary" :disabled="!selected" @click="confirm">进入工作台</el-button>
    </footer>
  </div>

  <el-drawer v-model="drawerOpen" :title="editing ? '编辑公众号配置' : '接入公众号'" direction="rtl" size="560px" destroy-on-close>
    <el-form :model="form" label-position="top" @submit.prevent>
      <el-alert type="info" :closable="false" show-icon>
        <template #title>密钥仅在保存时提交，不会在页面再次显示。</template>
      </el-alert>

      <div class="form-grid">
        <el-form-item label="公众号名称" required>
          <el-input v-model.trim="form.name" placeholder="例如：美利善服务号" maxlength="50" />
        </el-form-item>
        <el-form-item label="账号类型" required>
          <el-select v-model="form.type">
            <el-option label="服务号" :value="1" />
            <el-option label="订阅号" :value="2" />
          </el-select>
        </el-form-item>
      </div>

      <el-form-item label="AppID" required>
        <el-input v-model.trim="form.appid" :disabled="editing" placeholder="wx 开头的公众号 AppID" maxlength="20" />
      </el-form-item>

      <el-form-item label="AppSecret" required>
        <el-input v-model="form.secret" type="password" show-password :placeholder="editing ? '留空则保持现有 AppSecret 不变' : '从微信开发者平台复制'" />
      </el-form-item>

      <el-form-item label="消息推送 Token" required>
        <el-input v-model.trim="form.token" :placeholder="editing ? '留空则保持现有 Token 不变' : '3 至 32 位自定义字符串'" />
      </el-form-item>

      <el-form-item label="消息加密方式">
        <el-radio-group v-model="encryptionMode">
          <el-radio-button label="plain">明文模式</el-radio-button>
          <el-radio-button label="aes">安全模式</el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="encryptionMode === 'aes'" label="EncodingAESKey" :required="!editing">
        <el-input v-model.trim="form.aesKey" type="password" show-password :placeholder="editing ? '留空则保持现有 AES Key 不变' : '微信开发者平台提供的 43 位密钥'" maxlength="43" />
      </el-form-item>

      <el-form-item label="认证状态">
        <el-switch v-model="form.verified" active-text="已认证" inactive-text="未认证" />
      </el-form-item>

      <section class="callback">
        <div>
          <span>消息推送 URL</span>
          <code>{{ callbackUrl }}</code>
        </div>
        <el-button :icon="DocumentCopy" circle title="复制回调地址" @click="copyCallback" />
      </section>
    </el-form>

    <template #footer>
      <div class="drawer-actions">
        <el-button @click="drawerOpen = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存配置</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { CircleCheckFilled, DocumentCopy, EditPen, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { api } from '@/api/console'
import { session } from '@/lib/session'
import router from '@/router'

const list = ref([])
const selected = ref(null)
const loading = ref(true)
const drawerOpen = ref(false)
const editing = ref(false)
const saving = ref(false)
const encryptionMode = ref('aes')
const form = reactive(emptyForm())

const callbackUrl = computed(() => {
  const appid = form.appid || '你的AppID'
  return `${window.location.origin}/wx/wx/msg/${appid}`
})

function emptyForm() {
  return {
    appid: '',
    name: '',
    type: 1,
    verified: true,
    secret: '',
    token: '',
    aesKey: '',
  }
}

async function load() {
  loading.value = true
  try {
    list.value = (await api.accounts()).list || []
    selected.value = list.value.find((item) => item.appid === selected.value?.appid) || list.value[0] || null
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = false
  encryptionMode.value = 'aes'
  Object.assign(form, emptyForm())
  drawerOpen.value = true
}

async function openEdit(item) {
  const { account } = await api.accountConfig(item.appid)
  editing.value = true
  encryptionMode.value = account.aesKeyConfigured ? 'aes' : 'plain'
  Object.assign(form, {
    appid: account.appid,
    name: account.name,
    type: account.type,
    verified: account.verified,
    secret: '',
    token: '',
    aesKey: '',
  })
  drawerOpen.value = true
}

async function copyCallback() {
  try {
    await navigator.clipboard.writeText(callbackUrl.value)
    ElMessage.success('回调地址已复制')
  } catch {
    ElMessage.warning('复制失败，请手动复制回调地址')
  }
}

async function save() {
  if (!form.name || !form.appid || (!editing.value && (!form.secret || !form.token))) {
    ElMessage.warning('请填写公众号名称、AppID、AppSecret 和 Token')
    return
  }
  if (encryptionMode.value === 'aes' && !editing.value && form.aesKey.length !== 43) {
    ElMessage.warning('安全模式需要填写 43 位 EncodingAESKey')
    return
  }

  saving.value = true
  try {
    const { account } = await api.saveAccountConfig({
      ...form,
      clearAesKey: encryptionMode.value === 'plain',
    })
    await load()
    selected.value = list.value.find((item) => item.appid === account.appid) || selected.value
    drawerOpen.value = false
    ElMessage.success('公众号配置已保存，请到微信开发者平台提交消息推送配置')
  } finally {
    saving.value = false
  }
}

async function confirm() {
  session.account = selected.value
  await api.context()
  router.replace('/')
}

function logout() {
  session.clear()
  router.replace('/login')
}

onMounted(load)
</script>

<style scoped>
.accounts { max-width: 1050px; margin: 0 auto; padding: 72px 28px; }
.accounts header, .header-actions, footer, .drawer-actions { display: flex; align-items: center; }
.accounts header { justify-content: space-between; }
.header-actions { gap: 8px; }
.accounts h1 { margin: 0; font-size: 30px; }
.accounts p { color: #73809a; }
.account-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; margin-top: 36px; }
.account-card { background: #fff; border: 1px solid #e6ebf2; border-radius: 8px; overflow: hidden; }
.account-card.active { border-color: #16a36a; box-shadow: 0 0 0 2px #d9f3e8; }
.account-select { width: 100%; display: flex; align-items: center; gap: 14px; text-align: left; background: transparent; border: 0; padding: 20px; cursor: pointer; }
.avatar { display: grid; place-items: center; width: 44px; height: 44px; border-radius: 50%; background: #dff5eb; color: #16875a; font-weight: 700; }
.account-copy { min-width: 0; flex: 1; }
.account-copy b, .account-copy small { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.account-copy small { margin-top: 6px; color: #8793a9; }
.account-select .el-icon { color: #16a36a; }
.account-meta { display: flex; align-items: center; justify-content: space-between; min-height: 42px; padding: 0 12px; border-top: 1px solid #edf0f5; }
footer { justify-content: flex-end; margin-top: 24px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.callback { display: flex; align-items: center; gap: 12px; padding: 14px; border: 1px solid #dfe7e3; background: #f7fbf8; border-radius: 6px; }
.callback > div { min-width: 0; flex: 1; }
.callback span, .callback code { display: block; }
.callback span { margin-bottom: 6px; color: #66748d; font-size: 12px; }
.callback code { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: #195e40; font-size: 12px; }
.drawer-actions { justify-content: flex-end; gap: 10px; }
</style>

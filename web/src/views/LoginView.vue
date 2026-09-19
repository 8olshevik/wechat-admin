<template>
  <div class="login"><section><div class="login-mark">W</div><h1>微信公众号管理系统</h1><p>运营、内容与用户触达的一体化工作台</p></section>
    <el-card class="login-card" shadow="never"><h2>欢迎回来</h2><p>使用管理员账号登录工作台</p>
      <el-form :model="form" @submit.prevent="submit">
        <el-form-item><el-input v-model="form.username" placeholder="用户名" size="large" /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" show-password placeholder="密码" size="large" /></el-form-item>
        <el-form-item><div class="captcha"><el-input v-model="form.captcha" placeholder="验证码" size="large" /><img :src="captchaUrl" @click="refreshCaptcha" /></div></el-form-item>
        <el-button type="primary" size="large" :loading="loading" native-type="submit" style="width:100%">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '@/api/console'
import { session } from '@/lib/session'
const router = useRouter(); const loading = ref(false); const uuid = ref(''); const captchaUrl = ref('')
const form = reactive({ username: '', password: '', captcha: '', uuid: '' })
function refreshCaptcha() { uuid.value = crypto.randomUUID(); form.uuid = uuid.value; captchaUrl.value = `${api.captcha(uuid.value)}&t=${Date.now()}` }
refreshCaptcha()
async function submit() { if (!form.username || !form.password || !form.captcha) return ElMessage.warning('请填写完整登录信息'); loading.value=true
  try { const data=await api.login(form); session.token=data.token; router.replace('/accounts') } finally { loading.value=false; refreshCaptcha() } }
</script>
<style scoped>
.login { min-height:100vh; display:grid; grid-template-columns:1.25fr .75fr; background:#101829; }
section { padding: 15vh 14%; color:#fff; background:linear-gradient(135deg,#101829,#173b47); }.login-mark{display:grid;place-items:center;width:48px;height:48px;border-radius:10px;background:#16a36a;font-weight:700;font-size:24px}.login h1{font-size:42px;margin:28px 0 12px}.login p{color:#9fadc5}
.login-card{align-self:center;margin:0 16%;border-radius:8px}.login-card h2{margin:8px 0}.login-card>p{margin:0 0 26px}.captcha{display:flex;gap:10px;width:100%}.captcha img{width:120px;height:40px;cursor:pointer;border:1px solid #e5eaf2}
</style>

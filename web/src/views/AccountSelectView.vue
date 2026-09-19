<template><div class="accounts"><header><div><h1>选择工作公众号</h1><p>选择后即可进入对应账号的运营工作台</p></div><el-button text @click="logout">退出登录</el-button></header>
  <div v-loading="loading" class="account-grid"><button v-for="item in list" :key="item.appid" :class="{active:selected?.appid===item.appid}" @click="selected=item">
    <span class="avatar">{{ item.name.slice(0,1) }}</span><span><b>{{ item.name }}</b><small>{{ item.appid }}</small></span><el-icon v-if="selected?.appid===item.appid"><CircleCheckFilled /></el-icon></button></div>
  <el-empty v-if="!loading&&!list.length" description="当前账号尚未获分配公众号权限" /><footer><el-button type="primary" :disabled="!selected" @click="confirm">进入工作台</el-button></footer>
</div></template>
<script setup>
import { onMounted, ref } from 'vue'; import { CircleCheckFilled } from '@element-plus/icons-vue'; import { api } from '@/api/console'; import { session } from '@/lib/session'; import router from '@/router'
const list=ref([]), selected=ref(null), loading=ref(true)
onMounted(async()=>{try{list.value=(await api.accounts()).list||[];selected.value=list.value[0]||null}finally{loading.value=false}})
async function confirm(){session.account=selected.value; await api.context(); router.replace('/')}
function logout(){session.clear();router.replace('/login')}
</script>
<style scoped>.accounts{max-width:1050px;margin:0 auto;padding:72px 28px}.accounts header{display:flex;justify-content:space-between;align-items:start}.accounts h1{margin:0;font-size:30px}.accounts p{color:#73809a}.account-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:14px;margin-top:36px}.account-grid button{position:relative;display:flex;align-items:center;gap:14px;text-align:left;background:#fff;border:1px solid #e6ebf2;border-radius:8px;padding:20px;cursor:pointer}.account-grid button.active{border-color:#16a36a;box-shadow:0 0 0 2px #d9f3e8}.avatar{display:grid;place-items:center;width:44px;height:44px;border-radius:50%;background:#dff5eb;color:#16875a;font-weight:700}.account-grid b,.account-grid small{display:block}.account-grid small{margin-top:6px;color:#8793a9}.account-grid .el-icon{position:absolute;right:14px;color:#16a36a}footer{text-align:right;margin-top:24px}</style>

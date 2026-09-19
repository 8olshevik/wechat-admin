<template>
  <div class="shell">
    <aside class="sidebar">
      <div class="brand"><span class="brand-mark">W</span><span>微信运营台</span></div>
      <nav>
        <RouterLink v-for="item in nav" :key="item.to" :to="item.to" class="nav-item">
          <component :is="item.icon" /><span>{{ item.label }}</span>
        </RouterLink>
      </nav>
    </aside>
    <main>
      <header class="topbar">
        <div class="account-pill"><span class="account-dot"></span>{{ account?.name || '未选择公众号' }}</div>
        <div class="top-actions">
          <el-button text @click="$router.push('/accounts')">切换公众号</el-button>
          <el-dropdown @command="handleCommand">
            <span class="user-menu"><el-icon><User /></el-icon>{{ user?.username || '管理员' }}<el-icon><ArrowDown /></el-icon></span>
            <template #dropdown><el-dropdown-menu><el-dropdown-item command="logout">退出登录</el-dropdown-item></el-dropdown-menu></template>
          </el-dropdown>
        </div>
      </header>
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ArrowDown, ChatDotRound, Grid, Picture, Promotion, PriceTag, Tickets, User, UserFilled } from '@element-plus/icons-vue'
import { api } from '@/api/console'
import { session } from '@/lib/session'

const user = ref(null)
const account = computed(() => session.account)
const nav = [
  { label: '运营概览', to: '/', icon: Grid },
  { label: '粉丝管理', to: '/followers', icon: UserFilled },
  { label: '用户标签', to: '/tags', icon: PriceTag },
  { label: '素材中心', to: '/materials', icon: Picture },
  { label: '自定义菜单', to: '/menu', icon: Promotion },
  { label: '自动回复', to: '/replies', icon: ChatDotRound },
  { label: '渠道二维码', to: '/qrcodes', icon: Tickets },
]
onMounted(async () => { user.value = (await api.me()).user })
async function handleCommand(command) {
  if (command === 'logout') {
    try { await api.logout() } finally { session.clear(); location.href = '/login' }
  }
}
</script>

<style scoped>
.shell { display: grid; grid-template-columns: 224px 1fr; min-height: 100vh; background: #f5f7fb; }
.sidebar { background: #101829; color: #c9d2e5; padding: 18px 12px; }
.brand { height: 52px; display: flex; align-items: center; gap: 10px; color: white; font-size: 17px; font-weight: 650; padding: 0 10px 18px; border-bottom: 1px solid #28344b; }
.brand-mark { display:grid; place-items:center; width:28px; height:28px; background:#16a36a; border-radius:6px; }
nav { padding-top: 18px; display: grid; gap: 4px; }
.nav-item { height: 42px; display:flex; align-items:center; gap:12px; padding:0 12px; border-radius:6px; font-size:14px; }
.nav-item:hover, .nav-item.router-link-exact-active { color:#fff; background:#243047; }
main { min-width: 0; }
.topbar { height: 68px; display:flex; justify-content:space-between; align-items:center; padding:0 28px; background:#fff; border-bottom:1px solid #e8ecf3; }
.account-pill { font-size: 14px; color:#3e4b63; display:flex; align-items:center; gap:8px; }
.account-dot { width:8px; height:8px; border-radius:50%; background:#17a36c; }
.top-actions, .user-menu { display:flex; align-items:center; gap:10px; }.user-menu { cursor:pointer; color:#506079; font-size:14px; }
</style>

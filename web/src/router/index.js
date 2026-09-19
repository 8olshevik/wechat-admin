import { createRouter, createWebHistory } from 'vue-router'
import { session } from '@/lib/session'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    { path: '/accounts', name: 'accounts', component: () => import('@/views/AccountSelectView.vue') },
    {
      path: '/',
      component: () => import('@/layouts/ConsoleLayout.vue'),
      children: [
        { path: '', name: 'dashboard', component: () => import('@/views/DashboardView.vue') },
        { path: 'followers', name: 'followers', component: () => import('@/views/FollowersView.vue') },
        { path: 'tags', name: 'tags', component: () => import('@/views/TagsView.vue') },
        { path: 'materials', name: 'materials', component: () => import('@/views/MaterialsView.vue') },
        { path: 'menu', name: 'menu', component: () => import('@/views/MenuView.vue') },
        { path: 'replies', name: 'replies', component: () => import('@/views/RepliesView.vue') },
        { path: 'qrcodes', name: 'qrcodes', component: () => import('@/views/QrcodesView.vue') },
      ],
    },
  ],
})

router.beforeEach((to) => {
  if (to.name !== 'login' && !session.token) return { name: 'login' }
  if (to.name !== 'login' && to.name !== 'accounts' && !session.account) return { name: 'accounts' }
})

export default router

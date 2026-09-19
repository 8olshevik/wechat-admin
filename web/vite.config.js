import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    plugins: [vue()],
    resolve: { alias: { '@': new URL('./src', import.meta.url).pathname } },
    server: {
      port: 3001,
      proxy: {
        '/wx': {
          target: env.VITE_PROXY_TARGET,
          changeOrigin: true,
        },
      },
    },
  }
})

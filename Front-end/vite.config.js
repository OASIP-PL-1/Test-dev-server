import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()]
  // ,
  // server: {
  //   proxy: {
  //     '/api': {
  //       target: 'http://10.4.84.106:8080',
  //       changeOrigin: true,
  //       secure: false
  //     }
  //   }, port : 80
  // }
})

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
    base: '/pl1/'
    ,
    server: {
        // proxy: {
        //     "^/api": {
        //         target: "http://localhost:8080/api",
        //         changeOrigin: true,
        //         secure: false,
        //         rewrite: (path) => path.replace(/^\/api/, ''),
        //         ws: true,
        //     }
        // },
        port : 80
    }
})

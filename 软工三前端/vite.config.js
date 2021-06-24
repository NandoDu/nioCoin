import {defineConfig} from "vite";
import vue from "@vitejs/plugin-vue";

const path = require("path");

export default defineConfig({
    server: {
        hmr: {
            overlay: false
        },
        port: 8000
    },
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src")
        }
    },
    plugins: [vue()]
});

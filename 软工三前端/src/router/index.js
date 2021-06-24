import {createRouter, createWebHashHistory, createWebHistory} from "vue-router";
import Home from "../view/home";
import Login from "../view/login";
import Register from "../view/register";
import Forget from "../view/forget";
import Mine from "../view/mine";
import MineHistory from "../view/mine/mine-history";
import MineItem from "../view/mine/mine-item";
import Graph from "../view/graph";
import Guidance from "../view/guidance"
import store from "../store";
import Story from "../view/story"

const authGuard = (from, to) => {
    if (!store.getters.logged) {
        console.log("[route] auth failed");
        return {path: "/login"};
    }
};

const noAuthGuard = (from, to) => {
    if (store.getters.logged) {
        console.log("[route] already logged");
        return {path: "/mine/item"};
    }
};
const routes = [
    {
        path: "/",
        name: "Home",
        component: Home
    },
    {
        path: "/login",
        name: "Login",
        component: Login,
        beforeEnter: noAuthGuard
    },
    {
        path: "/register",
        name: "Register",
        component: Register
    },
    {
        path: "/forget",
        name: "Forget",
        component: Forget
    },
    {
        path: "/mine/",
        name: "Mine",
        component: Mine,
        beforeEnter: authGuard,
        children: [
            {
                path: "history",
                component: MineHistory
            },
            {
                path: "item",
                component: MineItem
            }
        ]
    },
    {
        path: "/graph/:id",
        name: "Graph",
        component: Graph
    },
    {
        path: "/guide",
        name: "guidance",
        component: Guidance
    },
    {
        path: "/story",
        name: "story",
        component: Story
    }
];

export default createRouter({
    history: createWebHashHistory(),
    routes
});

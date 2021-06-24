import {createStore} from "vuex";
import graph from "./graph";
import user from "./user";
import trigger from "./trigger.js";
import Persist from "vuex-persistedstate";

export default createStore({
    modules: {
        graph,
        user,
        trigger
    },
    plugins: [
        Persist()
    ]
});

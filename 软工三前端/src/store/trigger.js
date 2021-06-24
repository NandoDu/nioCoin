const trigger = {
    state: {
        sync: false
    },
    mutation: {
        mutSync(state, payload) {
            state.sync = false;
        }
    },
    getter: {
        sync: state => state.sync
    }
};
export default trigger;

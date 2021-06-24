const defaultCallback = mutation => (ctx, res) => {
    console.log("[default callback] response: ");
    console.log(res);
    if (mutation) {
        ctx.commit(mutation, res);
    }
    return res;
};

const actionCon = (api, mutation, callback = defaultCallback(mutation)) => async (ctx, payload) => {
    let params = {};
    if ("__params" in payload && "__payload" in payload) {
        const {__params, __payload} = payload;
        payload = __payload;
        params = __params;
    }
    console.log(`[ACTION] payload[${JSON.stringify(payload)}] params[${JSON.stringify(params)}]`);
    return api(payload, params).then(res => callback(ctx, res));
};

const withParams = (payload, params) => ({
    __payload: payload,
    __params: params
});

export default actionCon;

export {
    withParams
};

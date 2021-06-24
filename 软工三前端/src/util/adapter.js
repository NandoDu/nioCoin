const nodeAdaptor = n => {
    let r = {
        key: n.id,
        text: n.label
    };
    Object.assign(r, n.userAttributes);
    return r;
};

const edgeAdaptor = e => {
    let r = {
        from: e.source,
        to: e.target,
        text: e.label
    };
    Object.assign(r, e.userAttributes);
    return r;
};

const revNodeAdaptor = n => {
    const {key, text, ...attributes} = n;
    return {
        id: key,
        label: text,
        userAttributes: attributes,
        sysAttributes: {}
    };
};

const revEdgeAdaptor = e => {
    const {from, to, text, ...attributes} = e;
    delete attributes.points;
    return {
        source: from,
        target: to,
        label: text,
        userAttributes: attributes,
        sysAttributes: {}
    };
};

const adaptor = data => ({
    nodeDataArray: data.nodes.map(nodeAdaptor),
    linkDataArray: data.edges.map(edgeAdaptor)
});

const revAdaptor = data => ({
    nodes: data.nodeDataArray.map(revNodeAdaptor),
    edges: data.linkDataArray.map(revEdgeAdaptor)
});

export {
    adaptor, revAdaptor
};

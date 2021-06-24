const HighlightMode = {Search: 0, Filter: 1};
let mode = HighlightMode.Search;

const search = (diagram, query) => {
    console.log("[query] option:");
    console.log(query);
    mode = HighlightMode.Search;

    diagram.commit(d => {
        const re = new RegExp(query.nodeName + (query.fuzzy ? "" : "$"));
        const res = d.findNodesByExample({
            text: t => re.test(t)
        });
        d.highlightCollection(res);
    }, "search");

    console.log("[search] hightlight result");
};

const filter = (diagram, query) => {
    console.log("[query] option:");
    console.log(query);
    mode = HighlightMode.Filter;

    diagram.commit(d => {
        const re = new RegExp(query.nodeName + (query.fuzzy ? "" : "$"));
        const res = d.findNodesByExample({
            text: t => !re.test(t)
        });
        d.highlightCollection(res);
        res.each(n => {
            n.findLinksOutOf().each(l => l.isHighlighted = true);
            n.findLinksInto().each(l => l.isHighlighted = true);
        });
    }, "search");

    console.log("[filter] hidden unmatched");
};
const noHighlight = (diagram) => {
    console.log("[highlight] cancel");
    diagram.commit(d => d.clearHighlighteds(), "no highlight");
    mode = HighlightMode.Search;
};

const highlightFill = node =>
    mode === HighlightMode.Search ?
        (node.isHighlighted ? "yellow" : (node.data.fill || "white")) :
        (node.data.fill || "white");

const highlightVisible = node =>
    mode === HighlightMode.Filter ?
        !node.isHighlighted :
        true;

export {
    search, filter, noHighlight,
    highlightFill, highlightVisible
};

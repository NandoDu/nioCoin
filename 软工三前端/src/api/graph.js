import {ContentType, Method} from "./apiCon";
import apiCon from "./apiCon";

const getGraphDataAPI = apiCon(Method.Get, "/graph/getGraph");
const updateGraphAPI = apiCon(Method.Post, "/graph/graph");
const getGraphNameAPI = apiCon(Method.Get, "/graph/getGraphName");
const setThumbAPI = apiCon(Method.Post, "/graph/thumb");

const modifyEntityAPI = apiCon(Method.Post, "/modify/node");
const modifyRelationAPI = apiCon(Method.Post, "/modify/relation");
const modifyNodesAPI = apiCon(Method.Post, "/modify/modifyNodes");
const modifyRelationsAPI = apiCon(Method.Post, "/modify/modifyRelations");

const setGraphStarredAPI = apiCon(Method.Get, "/user/setGraphStarred");
const getThumbnailAPI = apiCon(Method.Get, "/user/thumbnail");
const generateNewGraphAPI = apiCon(Method.Get, "/user/newGraphId");

const sendPartTxtAPI = apiCon(Method.Post, "/nre/first");
const sendFullTxtAPI = apiCon(Method.Post, "/nre/second");
const getStatusAPI = apiCon(Method.Get, "/nre/status");

export {
    getGraphDataAPI,
    updateGraphAPI,
    generateNewGraphAPI,
    getGraphNameAPI,
    setGraphStarredAPI,
    modifyEntityAPI,
    modifyRelationAPI,
    getThumbnailAPI,
    sendPartTxtAPI,
    sendFullTxtAPI,
    getStatusAPI,
    setThumbAPI,
    modifyNodesAPI,
    modifyRelationsAPI
};

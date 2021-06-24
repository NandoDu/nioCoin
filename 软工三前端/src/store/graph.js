import {
	generateNewGraphAPI,
	modifyEntityAPI,
	modifyRelationAPI,
	getGraphDataAPI,
	getGraphNameAPI,
	getThumbnailAPI,
	setGraphStarredAPI,
	updateGraphAPI,
	sendPartTxtAPI,
	sendFullTxtAPI,
	getStatusAPI,
	setThumbAPI,
	modifyNodesAPI,
	modifyRelationsAPI
} from "../api/graph";
import actionCon, {withParams} from "./actionCon";
import {adaptor, revAdaptor} from "../util/adapter.js";

const getCon = stateName => state => graphId => state[graphId][stateName];
const deepCopy = copiable => JSON.parse(JSON.stringify(copiable));

const graph = {
	state: {
		fullText: "",
		partText: ""
	},
	getters: {
		fullText: state => state.fullText,
		partText: state => state.partText,
		model: getCon("model"),
		draft: getCon("draft"),
		textHide: getCon("textHide"),
		curNodeAttr: getCon("curNodeAttr"),
		attrChanged: getCon("attrChanged"),
		needLayout: getCon("needLayout"),
		diagramFn: getCon("diagramFn"),
		allData: state => graphId => state[graphId]
	},
	mutations: {
		createGraph: function (state, payload) {
			state[payload.graphId] = payload.allData ?? {
				model: [],
				draft: []
			};
		},
		setPartText: function (state, payload) {
			state.partText = payload.partText;
			console.log("set part text");
			console.log(state.partText);
		},
		setFullText: function (state, payload) {
			state.fullText = payload.fullText;
			console.log("set full text");
			console.log(state.fullText.length);
		},
		setGraphData: function (state, {graphId, model}) {
			state = state[graphId];
			let _data = adaptor(model);
			state.model = deepCopy(_data);
			state.draft = deepCopy(_data);
		},
		setModelData: function (state, {graphId, model}) {
			state = state[graphId];
			state.model = model;
		},
		setDraftModelData: function (state, payload) {
			state = state[payload.graphId];
			state.draft = payload.model;
		},
		setTextHide: function (state, data) {
			state[data.graphId].textHide = data.textHide;
		},
		setAttrs: (state, data) => {
			state[data.graphId].curNodeAttr = data.attrs;
		},
		setAttr: (state, payload) => {
			state = state[payload.graphId];
			const attr = payload.attr;
			console.log("[addAttr] ", attr.name, ", ", attr.value);

			let name = "NIOCOIN__" + attr.name;
			if (attr.value === undefined) {
				delete state.curNodeAttr[name];
			} else {
				state.curNodeAttr[name] = attr.value;
			}
			state.attrChanged = true;
		},
		setAttrChanged: (state, payload) => {
			state[payload.graphId].attrChanged = payload.value;
		},
		setNeedLayout: (state, data) => {
			state[data.graphId].needLayout = data.value;
		},
		diagramFn: (state, {graphId, diagram}) => {
			state[graphId].diagramFn = () => diagram;
		},
		// Draft -> Internal
		toInternal: (state, {graphId}) => {
			state = state[graphId];
			const data = deepCopy(state.draft);
			let model = new go.GraphLinksModel();
			Object.assign(model, data);
			state.diagramFn().model = model;
		},
		// Model -> Draft
		toDraft: (state, {graphId}) => {
			state = state[graphId];
			state.draft = state.model;
		},
		// Draft <- Internal
		backDraft: (state, {graphId}) => {
			state = state[graphId];
			let model = JSON.parse(state.diagramFn().model.toJson());
			delete model.class;
			state.draft = model;
		}
	},
	actions: {
		getGraphData: actionCon(getGraphDataAPI),
		updateGraph: actionCon(updateGraphAPI),
		generateNewGraph: actionCon(generateNewGraphAPI),
		getGraphName: actionCon(getGraphNameAPI),
		setGraphStarred: actionCon(setGraphStarredAPI),
		modifyEntity: actionCon(modifyEntityAPI),
		modifyRelation: actionCon(modifyRelationAPI),
		getThumbnail: actionCon(getThumbnailAPI),
		sendPartTxt: actionCon(sendPartTxtAPI),
		sendFullTxt: actionCon(sendFullTxtAPI),
		getStatus: actionCon(getStatusAPI),
		modifyNodes: actionCon(modifyNodesAPI),
		modifyRelations: actionCon(modifyRelationsAPI),
		// Server <- Model <- Draft
		save: async ({commit, dispatch, getters}, {graphId}) => {
			commit("setModelData", {
				graphId,
				model: getters.draft(graphId)
			});
			const t = revAdaptor(getters.model(graphId));
			await dispatch("updateGraph", {
				nodes: t.nodes,
				edges: t.edges,
				kgId: graphId
			});

			const _setThumb = (blob) => {
				console.log(graphId);
				let formData = new FormData();
				let json = JSON.stringify({
					graphId,
					name: `thumb-of-${graphId}.png`
				});
				let data = new Blob([json], {type: "application/json"});
				formData.append("data", data);
				formData.append("image", blob);
				setThumbAPI(formData);
			};
			const diagram = getters.diagramFn(graphId)();
			diagram.makeImageData({
				background: "white",
				returnType: "blob",
				callback: _setThumb
			});
		}

	}
};

export default graph;

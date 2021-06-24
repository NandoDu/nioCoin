<template>
	<div class="header-container">
		<div class="header-all header-flex header-center header-padding">
			<i class="el-icon-arrow-left returnButton" style=""  @click="$router.push('/mine/item')"></i>
			<LogoOfDog class="logoInGraph" style="margin-top: 13px"/>
			<div class="avatar">
				<el-dropdown trigger="click">
					<img :src="imgUrl" class="avatarStyle" alt="avatar">
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item class="only-burger only-burger-visible" @click="save">保存</el-dropdown-item>
							<el-dropdown-item id="download-in-side-bar" class="only-burger only-burger-visible">下载</el-dropdown-item>
							<el-dropdown-item class="only-burger only-burger-visible" divided></el-dropdown-item>
							<el-dropdown-item @click="$router.push('/mine/item')">
								<div style="display: flex; flex-direction: row; align-items: center;">
									<img src="../../asset/homePage.png" style="width: 21px;margin-right: 5px; margin-top: -2px">
									<div>
										我的主页
									</div>
								</div>
							</el-dropdown-item>
							<el-dropdown-item @click="logout">
								<div style="display: flex; flex-direction: row; align-items: center;">
									<img src="../../asset/exit.png" style="width: 20px;margin-right: 5px; margin-top: -2px">
									<div>
										退出登录
									</div>
								</div>
							</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
			</div>
			<div class="ops-block ops-block-visible">
				<el-button class="elButtonStyle" size="medium" style="border-radius: 8px!important;" @click="save">保存
				</el-button>
				<el-button id="download" class="elButtonStyle" size="medium" style="border-radius: 8px!important;">下载
				</el-button>
			</div>
		</div>
	</div>
	<div class="toolbar">
		<el-button circle class="elButtonStyle" icon="el-icon-menu" size="mini" @click="justOpen"></el-button>
		<el-button id="hide" class="elButtonStyle" round size="mini" @click="hideText">隐藏标签</el-button>
		<el-button class="elButtonStyle" round size="mini" @click="setGraphInfoVisible">统计数据</el-button>
		<el-button class="elButtonStyle" round size="mini" @click="undoAll">一键撤回</el-button>
		<el-button class="elButtonStyle" round size="mini" @click="showImport">导入数据</el-button>
		<el-button class="elButtonStyle" round size="mini" @click="showRenameModal">重新命名</el-button>
		<el-button class="elButtonStyle" round size="mini" @click="layout">快速布局</el-button>
		<el-dialog
			v-model="newDialogVisible"
			title="导入项目"
			width="30%"
		>
			<div style="display: flex; flex-direction: column; align-items: center; justify-content: space-around">
				<el-upload
					ref="upload"
					:auto-upload="false"
					:limit="1"
					:multiple="false"
					:on-change="handleImportChange"
					accept=".csv"
					action="https://jsonplaceholder.typicode.com/posts/"
					class="upload-demo"
					style="margin-top: 20px"
				>
					<el-button class="elButtonStyle" round type="primary">上传csv文件</el-button>
				</el-upload>
			</div>
		</el-dialog>
	</div>
	<div id="main" class="main-container">
		<div id="aside" class="aside-visible">
			<div id="close-aside">
				<div class="icon-wrap"><a @click="closeAside"><i class="el-icon-close"></i></a></div>
			</div>
			<div class="tabs" style="overflow: auto">
				<el-tabs v-model="activeName">
					
					<el-tab-pane name="first">
						<template #label>
							<i class="el-icon-picture-outline"/><span class="spanStyle">样式</span>
						</template>
						<el-menu
							class="el-menu-vertical-demo"
							default-active="1"
						>
							<el-submenu class="submenuStyle" index="1" title="节点">
								<template #title>
									<span>节点</span>
								</template>
								<GoPalette/>
							</el-submenu>
							<el-submenu class="submenuStyle" index="2">
								<template #title>
									<span>边</span>
								</template>
								<EdgePalette/>
							</el-submenu>
							<el-submenu class="submenuStyleOther" index="3">
								<template #title>
									<span>快捷键指南</span>
								</template>
								<div class="other-style" style="font-size: 10px;height: 200px;padding-top: 10px;text-align: center">
									<p>CTRL-C:复制</p>
									<p>CTRL-V:粘贴</p>
									<p>CTRL-Z:撤回</p>
									<p>F2:编辑关系文本</p>
									<p>DELETE:删除</p>
								</div>
							</el-submenu>
						</el-menu>
					</el-tab-pane>
					
					<el-tab-pane name="second">
						<template #label>
							<i class="el-icon-search"/><span class="spanStyle">搜索</span>
						</template>
						<SearchOption @filter="filter" @reset="reset" @search="search"></SearchOption>
					</el-tab-pane>
					
					<el-tab-pane name="third">
						<template #label>
							<i class="el-icon-sort"/><span class="spanStyle">属性</span>
						</template>
						<AttrManagement :graph-id="graphId"/>
					</el-tab-pane>
				
				</el-tabs>
			</div>
		</div>
		<GoGraph ref="refGraph" :graph-id="graphId"/>
	</div>
	<el-dialog
		v-model="graphInfoVisible"
		width="30%"
		@close="setGraphInfoUnVisible"
		@open="getGraphInfo">
		<h3 style="text-align: center;margin-top: -20px">统计信息</h3>
		<el-divider style="margin: 0"></el-divider>
		<div style="display: flex;flex-direction: column;align-items: center;margin-top: 15px">
			<span>有 <b>{{ nodeNum }}</b> 个节点</span>
			<span>有 <b>{{ relationNum }}</b> 组关系</span>
			<el-divider>其中</el-divider>
			<span>拥有最多连接的节点是 <b>{{ maxNodeName }}</b></span>
			<span><b>{{ maxNodeName }}</b> 节点拥有 <b>{{ maxNodeLinkNum }}</b> 条连接</span>
			<span><b>{{ maxNodeName }}</b> 节点覆盖了 <b>{{ maxNodeLinkRate }}</b> 的连接</span>
		</div>
		<template #footer>
    <span class="dialog-footer">
      <el-button type="primary" @click="graphInfoVisible = false">确 定</el-button>
    </span>
		</template>
	</el-dialog>
	<el-dialog
		v-model="graphNameVisible"
		title="自定义图谱名称"
		width="30%"
		@close="setGraphNameUnVisible">
		<el-input
			v-model="graphName"
			clearable
			placeholder="请输入新的图谱名称"
			@keyup.enter="handleRename">
		</el-input>
		<template #footer>
    <span class="dialog-footer">
      <el-button @click="graphNameVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleRename">确 定</el-button>
    </span>
		</template>
	</el-dialog>
	<el-dialog
		v-model="guideVisible"
		width="60%"
		:close-on-click-modal=false
		:show-close=false
		center
	>
		<div class="guideContent" v-if="guideData === 0" style="display: flex;flex-direction: column;align-items: center;">
			<h2>第一轮预热抽取</h2>
			<div
				style="display: flex;flex-direction: column;font-size: 20px;text-align: left;justify-content: center;width: 80%">
				<ol>
					<li style="padding-top: 10px">我们将对您之前上传的txt文件内容的前<strong>10%</strong>的内容进行第一轮预处理和实体关系抽取</li>
					<li style="padding-top: 30px">我们会要求您对预提取的实体和关系进行修改和完善，这些完善的结果对完整文本抽取具有一定的反馈作用</li>
					<li style="padding-top: 30px">请点击下一步，对前10%的内容进行提交和预处理，并耐心等待几分钟，在此期间，请不要关闭页面。</li>
				</ol>
			</div>
		</div>
		<div class="guideContent" v-else-if="guideData === 1" v-loading.lock="isLoading">
			<div v-for="(item,index) in tempNodes" style="display: inline-block;">
				<button class="allNodes" v-if="item.isDeleted===false"
				        style="width: 70px;height: 70px;border-radius:50%;border: 1px solid orange;margin: 8px;background-color: white"
				        @dblclick="editNode(index)" @click="nodeDeleteOrCancel(index)">
					{{ item.newLabel }}
				</button>
				<button class="allNodes" v-else
				        style="width: 70px;height: 70px;border-radius:50%;border: 1px solid orange;margin: 8px;background-color: white;opacity:0.15"
				        @dblclick="editNode(index)" @click="nodeDeleteOrCancel(index)">
					{{ item.newLabel }}
				</button>
			</div>
			<div style="display: inline-block;">
				<button class="plusNode"
				        style="width: 70px;height: 70px;border-radius:50%;border: 2px dashed darkgray;margin: 8px;background-color: white"
				        @click="addNode">
					<i class="el-icon-plus"></i></button>
			</div>
		</div>
		<div class="guideContent" v-else-if="guideData === 2">
			<div>
				<el-table
					:data="tempEdges"
					border
					stripe
					:row-class-name="rowClassName"
				>
					<el-table-column
						label="节点1"
						prop="node1Label"
						width="200px">
					</el-table-column>
					<el-table-column
						label="关系"
						prop="newLabel"
						width="200px">
					</el-table-column>
					<el-table-column
						label="节点2"
						prop="node2Label"
						width="200px">
					</el-table-column>
					<el-table-column
						label="操作"
						width="200px"
					>
						<template v-slot="action">
							<el-button type="primary" size="mini" @click="editEdge(action.row.id-1)">修改</el-button>
							<el-button type="danger" size="mini" v-if="tempEdges[action.row.id-1].isDeleted===false"
							           @click="edgeDeleteOrCancel(action.row.id-1)">删除
							</el-button>
							<el-button size="mini" v-if="tempEdges[action.row.id-1].isDeleted===true"
							           @click="edgeDeleteOrCancel(action.row.id-1)">取消删除
							</el-button>
						</template>
					</el-table-column>
				</el-table>
			</div>
			<!--      <div v-for="(item,index) in tempEdges" style="display: inline-block;">-->
			<!--        <button class="allEdges" v-if="item.isDeleted===false" style="width: 70px;height: 45px;border: 1px solid orange;border-radius:13px;margin: 8px;background-color: white" @dblclick="editEdge(index)" @click="edgeDeleteOrCancel(index)">-->
			<!--          {{ item.newLabel }}-->
			<!--        </button>-->
			<!--        <button class="allEdges" v-else style="width: 70px;height: 45px;border: 1px solid orange;border-radius:13px;margin: 8px;background-color: white;opacity:0.15" @dblclick="editEdge(index)" @click="edgeDeleteOrCancel(index)">-->
			<!--          {{ item.newLabel }}-->
			<!--        </button>-->
			<!--      </div>-->
		</div>
		<div class="guideContent" v-else-if="guideData === 3" v-loading.lock="isLoading"
		     style="display: flex;flex-direction: column;align-items: center;">
			<h2>第二轮完整抽取</h2>
			<div
				style="display: flex;flex-direction: column;font-size: 20px;text-align: left;justify-content: center;width: 80%">
				<ol>
					<li>至此，您已经将第一轮的数据导入并将暂时生成的实体和关系进行了进一步的修改和完善，感谢您此前的耐心等待和配合</li>
					<li>请点击提交按钮，我们将会结合此前上传的txt和您前两步的修改反馈结果对实体和关系进行更加详细的抽取，此过程会耗费几分钟到几十分钟不等。</li>
					<li>生成完成后，此导航将自动关闭，并直接显示相关的人物关系图谱，请继续耐心等待，在此期间，请不要关闭页面。</li>
				</ol>
			</div>
		</div>
		<el-progress :percentage="guideProgress" :show-text=false></el-progress>
		<template #footer>
    <span class="dialog-footer">
      <el-button v-if="guideData <= 2" @click="changeGuide" type="primary" :disabled="isDisabled">下一步</el-button>
      <el-button v-else-if="guideData === 3" @click="closeGuide" type="primary" :disabled="isDisabled">提交</el-button>
    </span>
		</template>
	</el-dialog>
	<el-dialog
		v-model="editNodeVisible"
		width="30%"
		title="修改节点内容"
	>
		<div style="display: flex;flex-direction: column;align-items: center;margin-top: 15px">
			<el-input v-model="editNodeContent.text"></el-input>
		</div>
		<template #footer>
            <span class="dialog-footer">
              <el-button type="primary" @click="confirmEdit">确定</el-button>
               <el-button @click="cancelEdit">取消</el-button>
            </span>
		</template>
	</el-dialog>
	<el-dialog
		v-model="editEdgeVisible"
		width="30%"
		title="修改关系内容"
	>
		<div style="display: flex;flex-direction: column;align-items: center;margin-top: 15px">
			<el-input v-model="editEdgeContent.text"></el-input>
		</div>
		<template #footer>
            <span class="dialog-footer">
              <el-button type="primary" @click="confirmEditEdge">确定</el-button>
               <el-button @click="cancelEditEdge">取消</el-button>
            </span>
		</template>
	</el-dialog>
	<el-dialog
		v-model="addNodeVisible"
		width="30%"
		title="增加节点"
	>
		<div style="display: flex;flex-direction: column;align-items: center;margin-top: 15px">
			<el-input v-model="editNodeContent.text"></el-input>
		</div>
		<template #footer>
            <span class="dialog-footer">
              <el-button type="primary" @click="confirmAdd">确定</el-button>
               <el-button @click="cancelAdd">取消</el-button>
            </span>
		</template>
	</el-dialog>
</template>

<script>
import {ref, unref, onBeforeMount, watch, reactive} from "vue";
import GoPalette from "../../components/GoPallete";
import EdgePalette from "../../components/GoPallete/EdgePalette";
import {LogoOfDog} from "../../components/util";
import {useStore} from "vuex";
import GoGraph from "../../components/GoGraph/GoGraph.vue";
import {useRoute, useRouter} from "vue-router";
import SearchOption from "../../components/SearchOption/SearchOption.vue";
import Papa from "papaparse";
import * as jschardet from "jschardet";
import AttrManagement from "../../components/AttrManagement/AttrManagement.vue";
import {ElMessage} from "element-plus";
import {withParams} from "../../store/actionCon.js";

export default {
	name: "Graph",
	components: {AttrManagement, SearchOption, GoPalette, LogoOfDog, GoGraph, EdgePalette},
	setup() {
		let imgUrl = ref("");
		let switchAside = ref(false);
		let newDialogVisible = ref(false);
		let graphNameVisible = ref(false);
		let graphInfoVisible = ref(false);
		let nodeNum = ref(0);
		let relationNum = ref(0);
		let maxNodeName = ref("");
		let maxNodeLinkNum = ref(0);
		let maxNodeLinkRate = ref("");
		let currentGraphName = ref("");
		const store = useStore();
		const router = useRouter();
		const route = useRoute();
		const refGraph = ref();
		const graphName = ref("");
		const drawerShow = ref(false);
		const graphId = route.params.id;
		let guideData = ref(0);
		let guideVisible = ref(false);
		let nodeLength = ref(0);
		let guideProgress = ref(25);
		let tempNodes = ref([]);
		let tempEdges = ref([]);
		let modifyNodesList = [];
		let modifyEdgeList = [];
		let addNodeVisible = ref(false);
		let editNodeVisible = ref(false);
		//let addEdgeVisible = ref(false);
		let editEdgeVisible = ref(false);
		let editNodeContent = reactive({id: -1, text: ""});
		let editEdgeContent = reactive({id: -1, text: ""});
		let isLoading = ref(false);
		let isDisabled = ref(false);
		const getImg = () => {
			imgUrl.value = store.getters.userInfo.imgUrl + '?v=' + Math.random();
			console.log(imgUrl.value)
		}
		getImg();
		const rowClassName = ({row, rowIndex}) => {
			//把每一行的索引放进row.id
			row.id = rowIndex + 1;
		}
		const addNode = () => {
			addNodeVisible.value = true;
			editNodeContent.id = nodeLength.value;
		};
		const cancelAdd = () => {
			addNodeVisible.value = false;
			editNodeContent.text = "";
			editNodeContent.id = -1;
		};
		const confirmAdd = () => {
			addNodeVisible.value = false;
			let tn = {
				actionType: 2,
				graphId: graphId,
				nodeId: editNodeContent.id,
				newLabel: editNodeContent.text,
				oldLabel: editNodeContent.text,
				isDeleted: false,
				isModified: false
			};
			tempNodes.value.push(tn);
			nodeLength.value = nodeLength.value + 1;
			editNodeContent.text = "";
			editNodeContent.id = -1;
		};
		
		const nodeDeleteOrCancel = target => {
			tempNodes.value[target].isDeleted = !tempNodes.value[target].isDeleted;
			if (tempNodes.value[target].isDeleted) {
				tempNodes.value[target].actionType = 1;
			} else {
				tempNodes.value[target].actionType = 0;
			}
		};
		const editNode = target => {
			editNodeVisible.value = true;
			editNodeContent.text = tempNodes.value[target].newLabel;
			editNodeContent.id = target;
			//tempNodes.value[target].isModified = 1;
		};
		const cancelEdit = () => {
			editNodeVisible.value = false;
			tempNodes.value[editNodeContent.id].isModified = false;
			editNodeContent.text = "";
			editNodeContent.id = -1;
		};
		const confirmEdit = () => {
			editNodeVisible.value = false;
			tempNodes.value[editNodeContent.id].isModified = true;
			tempNodes.value[editNodeContent.id].newLabel = editNodeContent.text;
			editNodeContent.text = "";
			editNodeContent.id = -1;
		};
		const edgeDeleteOrCancel = index => {
			console.log("边的id:");
			console.log(index);
			tempEdges.value[index].isDeleted = !tempEdges.value[index].isDeleted;
			if (tempEdges.value[index].isDeleted) {
				tempEdges.value[index].actionType = 1;
			} else {
				tempEdges.value[index].actionType = 0;
			}
		};
		const editEdge = target => {
			console.log("边的id:");
			console.log(target);
			editEdgeVisible.value = true;
			editEdgeContent.text = tempEdges.value[target].newLabel;
			editEdgeContent.id = target;
		};
		const cancelEditEdge = () => {
			editEdgeVisible.value = false;
			tempEdges.value[editEdgeContent.id].isModified = false;
			editEdgeContent.text = "";
			editEdgeContent.id = -1;
		};
		const confirmEditEdge = () => {
			editEdgeVisible.value = false;
			tempEdges.value[editEdgeContent.id].isModified = true;
			tempEdges.value[editEdgeContent.id].newLabel = editEdgeContent.text;
			editEdgeContent.text = "";
			editEdgeContent.id = -1;
		};
		//const addEdge=()=>{};
		//const cancelAddEdge =()=> {};
		//const confirmAddEdge =()=> {};
		onBeforeMount(() => {
			if (store.getters.userGuideVisible) {
				guideVisible.value = true;
			}
			// tempEdges = store.getters.model(graphId).linkDataArray;
		});
		
		window.onresize = function () {
			let main = document.getElementById("main");
			if (main.clientWidth <= 1012) {
				closeAside();
			} else {
				openAside();
			}
		};
		// const getLabels = target => {
		//   let edgeObj = {
		//     graphId: graphId,
		//     newLabel: "",
		//     oldLabel: "",
		//     actionType: 0,
		//     node1Label: "",
		//     node2Label: ""
		//   };
		//   edgeObj.newLabel = target.newLabel;
		//   edgeObj.oldLabel = target.oldLabel;
		//   edgeObj.actionType = target.actionType;
		//   let n = store.getters.model(graphId).nodeDataArray;
		//   let count = 0;
		//   for (let i = 0; i < n.length; i++) {
		//     if (count === 2) break;
		//     if (target.node1Id === Number(n[i].key)) {
		//       edgeObj.node1Label = n[i].text;
		//       count = count + 1;
		//     }
		//     if (target.node2Id === Number(n[i].key)) {
		//       edgeObj.node2Label = n[i].text;
		//       count = count + 1;
		//     }
		//   }
		//   return edgeObj;
		// };
		const checkModifiedNodes = () => {
			for (let i = 0; i < tempNodes.value.length; i++) {
				if (tempNodes.value[i].isModified || tempNodes.value[i].isDeleted || tempNodes.value[i].actionType === 2) {
					let nodeObj = {
						actionType: 0,
						graphId: graphId,
						nodeId: 0,
						newLabel: "",
						oldLabel: ""
					};
					nodeObj.actionType = tempNodes.value[i].actionType;
					nodeObj.nodeId = tempNodes.value[i].nodeId;
					nodeObj.newLabel = tempNodes.value[i].newLabel;
					nodeObj.oldLabel = tempNodes.value[i].oldLabel;
					modifyNodesList.push(nodeObj);
				}
			}
		};
		const checkModifiedEdges = () => {
			for (let i = 0; i < tempEdges.value.length; i++) {
				if (tempEdges.value[i].isModified || tempEdges.value[i].isDeleted || tempEdges.value[i].actionType === 2) {
					let edgeObj = {
						graphId: graphId,
						newLabel: "",
						oldLabel: "",
						actionType: 0,
						node1Label: "",
						node2Label: ""
					};
					edgeObj.newLabel = tempEdges.value[i].newLabel;
					edgeObj.oldLabel = tempEdges.value[i].oldLabel;
					edgeObj.actionType = tempEdges.value[i].actionType;
					edgeObj.node1Label = tempEdges.value[i].node1Label;
					edgeObj.node2Label = tempEdges.value[i].node2Label;
					modifyEdgeList.push(edgeObj);
				}
			}
		};
		
		const doOthers = async () => {
			let tempModel = await store.dispatch("getGraphData", {kgId: graphId});
			console.log(tempModel);
			store.commit("setGraphData", {graphId, model: tempModel});
			//model的数据请求到了
			let n = store.getters.model(graphId).nodeDataArray;
			let l = store.getters.model(graphId).linkDataArray;
			console.log(l);
			n.forEach(item => {
				let tempNodeObj = {
					actionType: 0,
					graphId: graphId,
					nodeId: 0,
					newLabel: "",
					oldLabel: "",
					isDeleted: false,
					isModified: false
				};
				tempNodeObj.nodeId = Number(item.key);
				tempNodeObj.oldLabel = item.text;
				tempNodeObj.newLabel = item.text;
				tempNodes.value.push(tempNodeObj);
			});
			nodeLength.value = n.length;
			l.forEach(item => {
				let tempEdgeObj = {
					graphId: graphId,
					newLabel: "",
					oldLabel: "",
					actionType: 0,
					node1Label: "",
					node2Label: "",
					node1Id: 0,
					node2Id: 0,
					isDeleted: false,
					isModified: false
				};
				tempEdgeObj.oldLabel = item.text;
				tempEdgeObj.newLabel = item.text;
				tempEdgeObj.node1Id = Number(item.from);
				tempEdgeObj.node2Id = Number(item.to);
				let count = 0;
				for (let i = 0; i < n.length; i++) {
					if (count === 2) break;
					if (tempEdgeObj.node1Id === Number(n[i].key)) {
						tempEdgeObj.node1Label = n[i].text;
						count = count + 1;
					}
					if (tempEdgeObj.node2Id === Number(n[i].key)) {
						tempEdgeObj.node2Label = n[i].text;
						count = count + 1;
					}
				}
				tempEdges.value.push(tempEdgeObj);
			});
			isLoading.value = false;
			isDisabled.value = false;
		};
		const changeGuide = async () => {
			if (guideData.value === 0) {
				isLoading.value = true;
				isDisabled.value = true;
				guideProgress.value = 50;
				guideData.value = (guideData.value + 1) % 4;
				let flag = true;
				let partTxt = store.getters.partText;
				await store.dispatch("sendPartTxt", {text: partTxt, graphId: Number(graphId)});
				const id = setInterval(async () => {
					const status = await store.dispatch("getStatus", {graphId});
					flag = status !== "FirstNREReceived";
					if (!flag) {
						console.log("Finished!");
						clearInterval(id);
						doOthers().then();
					}
				}, 1000);
				
				//先用假数据
				// let tempModel = {
				//   nodeDataArray: [
				//     {key: "1", text: "贾母", color: "orange", location: "-242.65967937392963 -60.75545025068665"},
				//     {key: "6", text: "老姨奶奶", color: "orange", location: "-47.07222422468533 -239.19570838749354"},
				//     {key: "7", text: "娄氏", color: "orange", location: "194.12557075165114 181.87396082962462"},
				//     {key: "2", text: "贾代善", color: "orange", location: "-56.361822495518624 -89.89427547687342"},
				//     {key: "3", text: "贾源", color: "orange", location: "73.85908370644063 51.57249383690937"},
				//     {key: "4", text: "你好", color: "orange", location: "-299.1119241615417 277.89797660843186"},
				//     {key: "5", text: "他好", color: "orange", location: "-177.52643962537587 394.7871515616145"}
				//   ],
				//   linkDataArray: [
				//     {from: "1", to: "2", text: "妻"},
				//     {from: "6", to: "2", text: "妾"},
				//     {from: "7", to: "3", text: "重孙媳妇"},
				//     {from: "2", to: "3", text: "子"},
				//     {from: "4", to: "5", text: "爹"}
				//   ]
				// };
				
				// let tempModel = await store.dispatch("getGraphData", {kgId: graphId});
				// console.log(tempModel);
				// store.commit("setModelData", {graphId, model: tempModel});
				// //model的数据请求到了
				// let n = store.getters.model(graphId).nodeDataArray;
				// let l = store.getters.model(graphId).linkDataArray;
				// console.log(l);
				// n.forEach(item => {
				//   let tempNodeObj = {
				//     actionType: 0,
				//     graphId: graphId,
				//     nodeId: 0,
				//     newLabel: "",
				//     oldLabel: "",
				//     isDeleted: false,
				//     isModified: false
				//   };
				//   tempNodeObj.nodeId = Number(item.key);
				//   tempNodeObj.oldLabel = item.text;
				//   tempNodeObj.newLabel = item.text;
				//   tempNodes.value.push(tempNodeObj);
				// });
				// nodeLength.value = n.length;
				// l.forEach(item => {
				//   let tempEdgeObj = {
				//     graphId: graphId,
				//     newLabel: "",
				//     oldLabel: "",
				//     actionType: 0,
				//     node1Label: "",
				//     node2Label: "",
				//     node1Id: 0,
				//     node2Id: 0,
				//     isDeleted: false,
				//     isModified: false
				//   };
				//   tempEdgeObj.oldLabel = item.text;
				//   tempEdgeObj.newLabel = item.text;
				//   tempEdgeObj.node1Id = Number(item.from);
				//   tempEdgeObj.node2Id = Number(item.to);
				//   tempEdges.value.push(tempEdgeObj);
				// });
				//数据装载完成
				// isLoading.value = false;
				// isDisabled.value = false;
				
				// setInterval(()=>{
				//   isLoading.value=false;
				//   isDisabled.value=false
				// },5000)
			} else if (guideData.value === 1) {
				guideProgress.value = 75;
				checkModifiedNodes();
				console.log(modifyNodesList);
				let sendData = {
					graphId: graphId,
					data: modifyNodesList
				};
				store.dispatch("modifyNodes", sendData).then(() => {
					for(let i = 0;i<tempEdges.value.length;i++){
            			for(let j =0;j<modifyNodesList.length;j++){
              				if(modifyNodesList[j].actionType===1){
              					if(modifyNodesList[j].newLabel===tempEdges.value[i].node1Label||modifyNodesList[j].newLabel===tempEdges.value[i].node2Label){
		              				tempEdges.value.splice(i,1);
		              				i = i - 1;
	              				}
              				}
            			}
          			}
					guideData.value = (guideData.value + 1) % 4;
				});
			} else if (guideData.value === 2) {
				guideProgress.value = 100;
				checkModifiedEdges();
				console.log(modifyEdgeList);
				let sendData = {
					graphId: graphId,
					data: modifyEdgeList
				};
				store.dispatch("modifyRelations", sendData).then(() => {
					guideData.value = (guideData.value + 1) % 4;
				});
			}
			//guideData.value = (guideData.value + 1) % 4;
		};
		const closeGuide = async () => {
			isLoading.value = true;
			isDisabled.value = true;
			let flag = true;
			let fullTxt = store.getters.fullText;
			await store.dispatch("sendFullTxt", {text: fullTxt, graphId});
			const id = setInterval(async () => {
				const status = await store.dispatch("getStatus", {graphId});
				flag = status !== "Created";
				if (!flag) {
					console.log("Final Finished!");
					clearInterval(id);
					//doOthers().then();
					let tempModel = await store.dispatch("getGraphData", {kgId: graphId});
					console.log(tempModel);
					store.commit("setGraphData", {graphId, model: tempModel});
					guideVisible.value = false;
					store.commit("setUserGuideVisible", false);
					guideData.value = (guideData.value + 1) % 4;
					guideProgress.value = 25;
					isLoading.value = false;
					isDisabled.value = false;
					store.commit("toDraft", {graphId});
					store.commit("toInternal", {graphId});
					unref(refGraph).layout();
					const model = store.getters.model(graphId);
					console.log("<S>D<A>S</A>");
					console.log(model);
					store.dispatch("save", {graphId}).then();
				}
			}, 1000);
			
			// let fullTxt = store.getters.fullText;
			// store.dispatch("sendFullTxt", withParams(fullTxt, {graphId}))
			//     .then(res => {
			//       console.log(res);
			//       guideVisible.value = false;
			//       store.commit("setUserGuideVisible", false);
			//       guideData.value = (guideData.value + 1) % 4;
			//       guideProgress.value = 25;
			//       isLoading.value = false;
			//     });
			
		};
		
		const justOpen = () => {
			if (!switchAside.value) {
				closeAside();
			} else {
				openAside();
			}
		};
		
		const closeAside = () => {
			document.getElementById("aside").style.display = "none";
			document.getElementById("viewport").style.width = "100vw";
			switchAside.value = !switchAside.value;
		};
		
		const openAside = () => {
			document.getElementById("aside").style.display = "flex";
			document.getElementById("viewport").style.width = "calc(100vw - 240px)";
			switchAside.value = !switchAside.value;
		};
		
		const hideText = () => {
			store.commit("setTextHide", {graphId, textHide: !store.getters.textHide(graphId)});
			console.log("set hide");
		};
		
		const setGraphInfoVisible = () => {
			graphInfoVisible.value = true;
		};
		
		const setGraphInfoUnVisible = () => {
			graphInfoVisible.value = false;
		};
		
		const showRenameModal = () => {
			newDialogVisible.value = false;
			graphNameVisible.value = true;
			graphName.value = currentGraphName.value;
		};
		
		const getGraphInfo = async () => {
			//console.log(store.getters.draft(graphId));
			//console.log(store.getters.model(graphId));
			let nodeArray = store.getters.draft(graphId).nodeDataArray;
			let linkArray = store.getters.draft(graphId).linkDataArray;
			nodeNum.value = nodeArray.length;
			relationNum.value = linkArray.length;
			let n = 0, l = 0;
			let maxLinkIndex = 0;
			let maxLink = 0;
			let maxLinkRate = 0.0;
			for (n = 0; n < nodeArray.length; n++) {
				let linkCount = 0;
				for (l = 0; l < linkArray.length; l++) {
					if (nodeArray[n].key === linkArray[l].from || nodeArray[n].key === linkArray[l].to) {
						
						linkCount++;
					}
				}
				if (linkCount > maxLink) {
					maxLink = linkCount;
					maxLinkIndex = n;
					maxLinkRate = maxLink / linkArray.length;
				}
			}
			maxNodeName.value = nodeArray[maxLinkIndex].text;
			maxNodeLinkNum.value = maxLink;
			maxNodeLinkRate.value = String(maxLinkRate * 100) + "%";
		};
		
		const showImport = () => {
			newDialogVisible.value = true;
		};
		
		const handleImportChange = (file) => {
			let fileObj = file.raw; // 相当于input里取得的files
			console.log(fileObj);
			let fReader = new FileReader();
			fReader.readAsDataURL(fileObj);
			fReader.onload = evt => {
				// 检查编码
				let encoding = checkEncoding(evt.target.result);
				console.log(encoding);
				// 将csv转换成二维数组
				Papa.parse(fileObj, {
					encoding,
					complete: async res => {
						// UTF8 \r\n与\n混用时有可能会出问题
						let data = res.data;
						if (data[data.length - 1] === "") {
							//去除最后的空行
							data.pop();
						}
						store.commit("setModelData", {
							graphId,
							model: convertJSON(data)
						});
						store.commit("toDraft", {graphId});
						store.commit("toInternal", {graphId});
						unref(refGraph).layout();
						store.dispatch("save", {graphId}).then();
						newDialogVisible.value = false;
					}
				});
			};
		};
		
		const convertJSON = (data) => {
			let nodeDataArray = [];
			let linkDataArray = [];
			for (let i = 1; i < data.length; i++) {
				if (data[i].length < data[0].length) {
					continue;
				}
				let exist_node1 = false;
				let exist_node2 = false;
				let node1_key = 0;
				let node2_key = 0;
				let nodeJson1 = {};
				let nodeJson2 = {};
				let linkJson = {};
				for (let k = 0; k < nodeDataArray.length; k++) {
					if (nodeDataArray[k]["text"] === data[i][0]) {
						exist_node1 = true;
						node1_key = nodeDataArray[k]["key"];
						break;
					}
					if (nodeDataArray[k]["text"] === data[i][1]) {
						exist_node2 = true;
						node2_key = nodeDataArray[k]["key"];
						break;
					}
				}
				if (!exist_node1) {
					nodeJson1.key = String(i * 2);
					node1_key = nodeJson1.key;
					nodeJson1.text = data[i][0];
					nodeJson1.color = "orange";
					nodeJson1.location = "0 0";
					nodeDataArray.push(nodeJson1);
				}
				if (!exist_node2) {
					nodeJson2.key = String(i * 2 + 1);
					node2_key = nodeJson2.key;
					nodeJson2.text = data[i][1];
					nodeJson2.color = "orange";
					nodeJson2.location = "0 0";
					nodeDataArray.push(nodeJson2);
				}
				linkJson.from = node1_key;
				linkJson.to = node2_key;
				linkJson.text = data[i][3];
				linkDataArray.push(linkJson);
			}
			return {nodeDataArray, linkDataArray};
		};
		
		const checkEncoding = (base64Str) => {
			// 这种方式得到的是一种二进制串
			let str = atob(base64Str.split(";base64,")[1]);
			// 要用二进制格式
			let encoding = jschardet.detect(str);
			encoding = encoding.encoding;
			if (encoding === "windows-1252") {
				// 有时会识别错误（如UTF8的中文二字）
				encoding = "ANSI";
			}
			return encoding;
		};
		
		const save = async () => {
			try {
				await store.dispatch("save", {graphId});
				ElMessage.success("保存成功！");
			} catch (e) {
				console.log(e);
				ElMessage.error("保存失败！");
			}
		};
		
		const getGraphName = async () => {
			await store.dispatch("getGraphName", {
				userId: store.getters.userInfo.id,
				graphId: route.params.id
			}).then((res) => {
				currentGraphName.value = res;
			});
		};
		
		const undoAll = () => {
			store.commit("toDraft", {graphId});
			store.commit("toInternal", {graphId});
		};
		
		const handleRename = () => {
			if (graphName.value.length === 0) {
				ElMessage.error("图谱名称不能为空");
				return;
			}
			let data;
			data = {
				graphId: Number(route.params.id),
				graphNewName: String(graphName.value)
			};
			store.dispatch("renameGraph", data).then(() => {
					ElMessage.success("图谱重命名成功");
					graphNameVisible.value = false;
					router.go(0);
				}
			);
		};
		
		const setGraphNameUnVisible = () => {
			graphNameVisible.value = false;
		};
		
		getGraphName();
		//guide()
		
		return {
			drawerShow,
			direction: ref("rtl"),
			activeName: ref("first"),
			graphInfoVisible,
			nodeNum,
			relationNum,
			maxNodeName,
			maxNodeLinkNum,
			maxNodeLinkRate,
			closeAside,
			openAside,
			setGraphInfoUnVisible,
			setGraphInfoVisible,
			save,
			getGraphInfo,
			undoAll,
			refGraph,
			logout: () => {
				store.commit("setLoginSuccess");
				localStorage.clear();
				router.push("/");
			},
			search: query => unref(refGraph).search(query),
			filter: query => unref(refGraph).filter(query),
			reset: () => unref(refGraph).noHighlight(),
			hideText,
			showImport,
			handleImportChange,
			newDialogVisible,
			handleRename,
			setGraphNameUnVisible,
			showRenameModal,
			graphName,
			graphNameVisible,
			justOpen,
			getGraphName,
			currentGraphName,
			graphId,
			layout: () => {
				unref(refGraph).layout();
			},
			guideData,
			guideVisible,
			guideProgress,
			changeGuide,
			closeGuide,
			tempNodes,
			tempEdges,
			nodeLength,
			nodeDeleteOrCancel,
			editNode,
			cancelEdit,
			confirmEdit,
			addNodeVisible,
			editNodeVisible,
			editNodeContent,
			addNode,
			cancelAdd,
			confirmAdd,
			edgeDeleteOrCancel,
			editEdge,
			cancelEditEdge,
			confirmEditEdge,
			//addEdge,
			//cancelAddEdge,
			//confirmAddEdge,
			editEdgeVisible,
			editEdgeContent,
			isLoading,
			isDisabled,
			rowClassName,
			imgUrl
		};
	}
};
</script>

<style scoped src="./style.less"/>

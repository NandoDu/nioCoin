<template>
	<div ref="screenSize">
		<ElMenu default-active="1" class="sub-menu" @select="switchShowMode">
			<div style="height: fit-content; margin-left: 10px; margin-right: 10px; margin-top: 10px">
				<el-input v-model="text" class="searchBox" placeholder="搜索">
					<template #prepend>
						<ElIcon class="el-icon-search"></ElIcon>
					</template>
				</el-input>
			</div>
			
			<ElMenuItem class="elMenuItemStyle" index="1" style="height: fit-content; margin-top: 10px;">
				<div class="iconItem">
					<div>
						<img v-if="isShow === '1'" class="menuIcon" src="../../../asset/project-blue.png">
						<img v-else class="menuIcon" src="../../../asset/project.png">
					</div>
					<div class="iconText">全部项目</div>
				</div>
			</ElMenuItem>
			
			<ElMenuItem class="elMenuItemStyle" index="2" style="height: fit-content">
				<div class="iconItem">
					<div>
						<img v-if="isShow === '2'" alt="menu-icon" class="menuIcon" src="../../../asset/star-blue.png">
						<img v-else alt="menu-icon-box" class="menuIcon" src="../../../asset/star.png">
					</div>
					<div class="iconText">星标项目</div>
				</div>
			</ElMenuItem>
		</ElMenu>
	</div>
	
	<div
		style="width: 100%; display: flex; flex-direction: column; align-items: center; margin-left: 1%; margin-right: 1%; -webkit-overflow-scrolling: touch; overflow-y: scroll; white-space: nowrap; overflow-scrolling: touch">
		<el-row>
			<el-col v-for="(kgItem,index) in kgList" v-if="isShow === '1' && kgList.length > 0" :key="index" :lg="5" :md="6"
			        :offset="1"
			        :sm="8" :xl="3" :xs="24" style="margin-top: 50px;">
				<div class="grid-content bg-purple">
					<kg-card :kg="kgItem" style="margin-left: 8px" @refreshFatherData="refreshPageData"
					         @showFatherInfoDialog="fatherGraphInfo"
					         @showFatherNameDialog="fatherGraphName"
					         @showFatherShare="fatherShare"></kg-card>
				</div>
			</el-col>
			<el-col v-for="(kgItem,index) in starKgList" v-if="isShow === '2' && starKgList.length > 0" :key="index" :lg="5"
			        :md="6" :offset="1" :sm="8"
			        :xl="3"
			        :xs="24" style="margin-top: 50px;">
				<div class="grid-content bg-purple">
					<kg-card :kg="kgItem" style="margin-left: 8px" @refreshFatherData="refreshPageData"
					         @showFatherInfoDialog="fatherGraphInfo"
					         @showFatherNameDialog="fatherGraphName"
					         @showFatherShare="fatherShare"></kg-card>
				</div>
			</el-col>
		</el-row>
		<div v-if="(isShow === '1' && kgList.length === 0) || (isShow === '2' && starKgList.length === 0)"
		     style="position: relative; width: 100%; height: 100%">
			<div class="emptyDiv">
				<img class="emptyImg" src="../../../asset/empty.png" alt="空">
				<h5 class="emptyText">暂无项目</h5>
			</div>
		</div>
	</div>
	<el-dialog
		v-model="graphInfoVisible"
		width="30%"
		@close="setGraphInfoUnVisible"
		@open="getGraphInfo">
		<h3 style="text-align: center;margin-top: -20px">《{{ graphName }}》的统计信息</h3>
		<el-divider style="margin: 0"></el-divider>
		<div style="display: flex;flex-direction: column;align-items: center;margin-top: 15px">
			<span>有 <b>{{ nodeNum }}</b> 个节点</span>
			<span>有 <b>{{ relationNum }}</b> 组关系</span>
			<span style="margin-top: 20px">更多信息请进入图谱后查看统计数据</span>
		</div>
		<template #footer>
    <span class="dialog-footer">
      <el-button type="primary" @click="graphInfoVisible = false">确 定</el-button>
    </span>
		</template>
	</el-dialog>
	<el-dialog
		v-model="graphNameVisible"
		title="重命名图谱"
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
		v-model="shareVisible"
		title="分享图谱"
		width="30%"
		@close="setShareUnVisible">
		
		<template #footer>
    <span class="dialog-footer">
      <el-button @click="graphNameVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleRename">确 定</el-button>
    </span>
		</template>
	</el-dialog>
</template>

<script>
import {ref, onBeforeMount, reactive} from "vue";
import kgCard from "../../../components/KgCard/kgCard.vue";
import {useStore} from "vuex";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import {withParams} from "../../../store/actionCon.js";

export default {
	name: "MineItem",
	components: {
		kgCard
	},
	setup() {
		const router = useRouter();
		let text = ref("");
		let shareVisible = ref(false);
		let graphInfoVisible = ref(false);
		let graphNameVisible = ref(false);
		let graphId = ref(0);
		let graphName = ref("");
		let store = useStore();
		let isShow = ref("1");
		let properSize = ref("");
		let kgList = ref([]);
		let nodeNum = ref(0);
		let relationNum = ref(0);
		let starKgList = ref([]);
		const getAllGraph = () => {
			store.dispatch("getAllGraph", {
				userId: store.getters.userInfo.id
			}).then((res) => {
				let item;
				console.log(res);
				for (item = 0; item < res.length; item++) {
					res[item].time = res[item].time.substring(0, 10);
					res[item].imgURL = res[item].imgURL + '?v=' + Math.random();
				}
				kgList.value = res;
			});
		};
		const setShareUnVisible = () => {
			shareVisible.value = false;
		};
		const getStarredGraph = () => {
			store.dispatch("getStarredGraph", {
				userId: store.getters.userInfo.id
			}).then((res) => {
				let item;
				for (item = 0; item < res.length; item++) {
					res[item].time = res[item].time.substring(0, 10);
					res[item].imgURL = res[item].imgURL + '?v=' + Math.random();
				}
				starKgList.value = res;
			});
		};
		const refreshPageData = () => {
			getStarredGraph();
			getAllGraph();
		};
		
		getAllGraph();
		getStarredGraph();
		
		onBeforeMount(() => {
			window.addEventListener("resize", getScreenSize);
			getScreenSize();
		});
		
		const fatherGraphInfo = (id, name) => {
			graphInfoVisible.value = true;
			graphId.value = id;
			graphName.value = name;
		};
		
		const fatherGraphName = (id, name) => {
			graphNameVisible.value = true;
			graphId.value = id;
			graphName.value = name;
		};
		const fatherShare = (id, name) => {
			shareVisible.value = true;
		};
		const getGraphInfo = async () => {
			await store.dispatch("getGraphData", {kgId: graphId.value})
				.then((res) => {
					nodeNum.value = res.nodes.length;
					relationNum.value = res.edges.length;
					console.log(res)
				});
		};
		const setGraphInfoUnVisible = () => {
			graphInfoVisible.value = false;
		};
		
		const setGraphNameUnVisible = () => {
			graphNameVisible.value = false;
		};
		
		const switchShowMode = (key) => {
			isShow.value = key;
		};
		
		const getScreenSize = () => {
			properSize.value = window.innerHeight * 0.89 + "px";
		};
		
		const handleRename = () => {
			if (graphName.value.length === 0) {
				ElMessage.error("图谱名称不能为空");
				return;
			}
			let data;
			data = {
				graphId: graphId.value,
				graphNewName: String(graphName.value)
			};
			store.dispatch("renameGraph", data).then(() => {
					ElMessage.success("图谱重命名成功");
					graphNameVisible.value = false;
					router.go(0);
				}
			);
		};
		
		return {
			text,
			graphId,
			graphName,
			graphNameVisible,
			graphInfoVisible,
			isShow,
			kgList,
			starKgList,
			properSize,
			nodeNum,
			relationNum,
			shareVisible,
			getGraphInfo,
			setShareUnVisible,
			fatherShare,
			setGraphInfoUnVisible,
			setGraphNameUnVisible,
			fatherGraphName,
			switchShowMode,
			fatherGraphInfo,
			refreshPageData,
			handleRename
		};
	}
};
</script>

<style scoped src="./MineItem.less"/>

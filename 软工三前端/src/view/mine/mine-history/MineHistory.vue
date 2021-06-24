<template>
	<div
		style="width: 100%; display: flex; flex-direction: column; align-items: center; margin-left: 1%; margin-right: 1%; -webkit-overflow-scrolling: touch; overflow-y: scroll; white-space: nowrap; overflow-scrolling: touch">
		<el-row>
			<el-col v-for="(kgItem,index) in kgList" v-if=" kgList.length > 0" :key="index" :lg="6" :md="6"
			        :offset="1"
			        :sm="8" :xl="3" :xs="24" style="margin-top: 50px;">
				<div class="grid-content bg-purple">
					<kg-card :kg="kgItem" style="margin-left: 8px" @refreshFatherData="refreshPageData"
					         @showFatherInfoDialog="fatherGraphInfo"
					         @showFatherNameDialog="fatherGraphName"
					         @showFatherShare="fatherShare"></kg-card>
				</div>
			</el-col>
		</el-row>
	</div>
	<el-dialog
		v-model="graphInfoVisible"
		width="30%"
		@close="graphInfoVisible = false"
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
		@close="graphNameVisible = false">
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
		@close="shareVisible=false">
		
		<template #footer>
    <span class="dialog-footer">
      <el-button @click="graphNameVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleRename">确 定</el-button>
    </span>
		</template>
	</el-dialog>
</template>

<script>
import kgCard from "../../../components/KgCard/kgCard.vue";
import {ref} from "vue";
import {ElMessage} from "element-plus";
import {withParams} from "../../../store/actionCon";
import {useStore} from "vuex";
import {useRouter} from "vue-router";

export default {
	components: {
		kgCard
	},
	name: "MineHistory",
	setup() {
		let router = useRouter();
		let store = useStore();
		let kgList = ref([]);
		let graphInfoVisible = ref(false);
		let graphId = ref(0);
		let graphName = ref("");
		let graphNameVisible = ref(false);
		let shareVisible = ref(false);
		let nodeNum = ref(0);
		let relationNum = ref(0);
		const getAllGraphByOrder = () => {
			store.dispatch("getAllGraphByOrder", {
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
		const refreshPageData = () => {
			getAllGraphByOrder();
			console.log("动态页面加载")
		};
		refreshPageData();
		const getGraphInfo = async () => {
			await store.dispatch("getGraphData", {kgId: graphId.value});
			console.log("所有的节点有：");
			console.log(store.getters.model.nodeDataArray);
			nodeNum.value = store.getters.model.nodeDataArray.length;
			relationNum.value = store.getters.model.linkDataArray.length;
		};
		
		
		const fatherShare = (id, name) => {
			shareVisible.value = true;
		};
		const fatherGraphInfo = (id, name) => {
			graphInfoVisible.value = true;
			graphId.value = id;
			graphName.value = name;
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
		const fatherGraphName = (id, name) => {
			graphNameVisible.value = true;
			graphId.value = id;
			graphName.value = name;
		};
		return {
			kgList,
			graphInfoVisible,
			graphNameVisible,
			shareVisible,
			nodeNum,
			relationNum,
			graphName,
			getAllGraphByOrder,
			refreshPageData,
			fatherGraphInfo,
			fatherShare,
			fatherGraphName,
			getGraphInfo,
			handleRename
		}
	}
};
</script>

<style scoped src="./MineHistory.less"/>

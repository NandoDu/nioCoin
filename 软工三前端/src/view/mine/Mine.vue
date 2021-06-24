<template>
  <NewHeader />
  <ElContainer style="padding: 4px; height: auto">
    <ElMain style="padding: 5px; display: flex">
      <ElMenu default-active="2" style="height: 100%; width: 80px; box-shadow: 0 0 5px 2px #b7babd" @select="switchShowMode">
        <div style="padding-bottom: 20px;padding-top: 10px; display: flex;flex-direction: row;justify-content: center">
          <span class="nav-header-text" style="line-height: 2;">图谱</span>
        </div>

        <ElMenuItem class="elMenuItemStyle" index="0" style="height: fit-content; margin-top: -20px" @click="newDialogVisible = true">
          <div class="ver-flex perf-center">
            <div class="icon-box">
              <img src="../../asset/create.png" style="width: 35px; height: 35px">
            </div>
            <div class="iconText">新建</div>
          </div>
        </ElMenuItem>

        <el-dialog
            v-model="newDialogVisible"
            title="新建项目"
            width="30%"
        >
          <div style="display: flex; flex-direction: column; align-items: center; justify-content: space-around">
            <el-button class="elButtonStyle" round type="primary" @click="generateNewGraph">新建空项目</el-button>
            <el-divider style="margin-top: 50px">或</el-divider>
            <el-upload
                ref="upload"
                :auto-upload="false"
                :limit="1"
                :multiple="false"
                :on-change="handleChange"
                accept=".csv"
                action="https://jsonplaceholder.typicode.com/posts/"
                class="upload-demo"
                style="margin-top: 20px"
            >
              <el-button class="elButtonStyle" round type="primary">上传csv文件</el-button>
            </el-upload>
            <el-divider style="margin-top: 50px">或</el-divider>
            <el-upload
                ref="upload"
                :auto-upload="false"
                :limit="1"
                :multiple="false"
                :on-change="handleImportChangeTXT"
                accept=".txt"
                action="https://jsonplaceholder.typicode.com/posts/"
                class="upload-demo"
                style="margin-bottom: 20px"
            >
              <el-button class="elButtonStyle" round type="primary">上传txt文件</el-button>
            </el-upload>
          </div>
          <template #footer>
                <span class="dialog-footer">
                <el-button class="cancelButtonStyle" type="danger" @click="newDialogVisible = false">取 消</el-button>
              </span>
          </template>
        </el-dialog>

        <ElMenuItem class="elMenuItemStyle" index="1" style="height: fit-content" @click="$router.push('/mine/history')">
          <div class="ver-flex perf-center">
            <div class="icon-box">
              <img v-if="isShow === '1'" class="menuIcon" src="../../asset/clock-blue.png" alt="logo">
              <img v-else class="menuIcon" src="../../asset/clock.png" alt="logo">
            </div>
            <div class="iconText">动态</div>
          </div>
        </ElMenuItem>

        <ElMenuItem class="elMenuItemStyle" index="2" style="height: fit-content" @click="$router.push('/mine/item')">
          <div class="ver-flex perf-center">
            <div class="icon-box">
              <img v-if="isShow === '2'" class="menuIcon" src="../../asset/folder-blue.png" alt="logo">
              <img v-else class="menuIcon" src="../../asset/folder.png" alt="logo">
            </div>
            <div class="iconText">项目</div>
          </div>
        </ElMenuItem>
      </ElMenu>
      <router-view v-if="isShow === '2' || isShow === '0' || isShow === '1'"></router-view>
    </ElMain>
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
  </ElContainer>
</template>

<script>
import {NewHeader} from "../../components/NewHeader";
import {ref} from "@vue/reactivity";
import {useRouter} from "vue-router";
import {useStore} from "vuex";
import * as jschardet from "jschardet";
import Papa from "papaparse";
import {ElMessage} from "element-plus";
import {revAdaptor} from "../../util/adapter";
import {withParams} from "../../store/actionCon.js";

export default {
  components: {
    NewHeader
  },
  setup() {
    // let txtRes = "";
    let isImportTxt = false;
    let isShow = ref("2");
    let newDialogVisible = ref(false);
    let graphNameVisible = ref(false);
    const router = useRouter();
    const store = useStore();
    const refGraph = ref();
    let fileLoaded = ref(0);
    let graphName = ref("");
    const handleImportChangeTXT = (file, fileList) => {
      isImportTxt = true;
      let fileObj = file.raw;
      let fReader = new FileReader();
      fReader.readAsText(fileObj);
      fReader.onload = evt => {
        let txtRes = evt.target.result;
        console.log(txtRes);
        // txtRes = textData.split(/[(\r\n)\r\n]+/);
        // //删除空行和每一段开头的空格
        // txtRes.forEach((item, index) => {
        //   if (!item) {
        //     txtRes.splice(index, 1);// 删除空项
        //   }
        //   if (item[0] === " ") {//删除空格
        //     txtRes[index] = txtRes[index].replace(/^\s*/, "");
        //   }
        // });
        store.commit("setFullText", {fullText: txtRes});
        let txtPart = txtRes.slice(0, Math.ceil(txtRes.length / 10));//截取前百分之十的内容
        store.commit("setPartText", {partText: txtPart});
        graphNameVisible.value = true;
        newDialogVisible.value = false;
        fileLoaded = 0;
        //console.log(res.join("|"));//用｜分句
      };
    };
    const switchShowMode = (key) => {
      isShow.value = key;
    };
    const setGraphNameUnVisible = () => {
      graphNameVisible.value = false;
    };
    const handleChange = (file, fileList) => {
      let fileObj = file.raw; // 相当于input里取得的files
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
            store.commit("createGraph", {graphId: -1});
            store.commit("setModelData", {graphId: -1, model: convertJSON(data)});
            store.commit("setDraftModelData", {graphId: -1, model: convertJSON(data)});
            store.commit("setNeedLayout", {graphId: -1, value: true});
            graphNameVisible.value = true;
            newDialogVisible.value = false;
            fileLoaded = 1;
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
      console.log(linkDataArray);
      console.log(nodeDataArray);
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
    const parseCSV = (params) => {
      console.log(params);
    };
    const generateNewGraph = () => {
      graphNameVisible.value = true;
      newDialogVisible.value = false;
      fileLoaded = 0;
    };
    const handleRename = async () => {
      let graphId = await store.dispatch("generateNewGraph", {
        userId: store.getters.userInfo.id,
        graphName: "newGraph"
      });
      graphId = String(graphId);

      if (graphName.value.length === 0) {
        ElMessage.error("图谱名称不能为空");
        return;
      }
      let data = {
        graphId: Number(graphId),
        graphNewName: String(graphName.value)
      };
      await store.dispatch("renameGraph", data);
      ElMessage.success("图谱新建成功");
      graphNameVisible.value = false;
      if (fileLoaded === 1) {
        store.commit("createGraph", {graphId, allData: store.getters.allData(-1)});
        store.commit("createGraph", {graphId: -1});
        store.commit("setNeedLayout", {graphId, value: true});
      } else {
        store.commit("createGraph", {graphId});
        console.log("New File");
      }
      if (isImportTxt) {
        // let partTxt = store.getters.partText;
        // let partTxtArray = txtRes.slice(0, Math.ceil(txtRes.length / 10));//取前百分之十进行传输
        // let partTxt = partTxtArray.join("|");//txt用｜进行分句，方便split获取单独的句子
        // let fullTxt = txtRes.join("|");
        // store.commit("setFullText", {fullText: fullTxt});
        //发送部分数据
        // await store.dispatch("sendPartTxt", {
        //   params: {graphId: graphId},
        //   partTxt
        // });
        // let flag = true;
        // while(flag){
        //   setInterval(flag = requestData,1000);
        // }
        store.commit("setUserGuideVisible", true);
      }
      await router.push(`/graph/${graphId}`);
    };
    return {
      isShow,
      newDialogVisible,
      switchShowMode,
      generateNewGraph,
      handleChange,
      checkEncoding,
      parseCSV,
      refGraph,
      setGraphNameUnVisible,
      graphNameVisible,
      graphName,
      newOrLoad: fileLoaded,
      handleRename,
      handleImportChangeTXT
    };
  }
};
</script>

<style scoped src="./Mine.less" />

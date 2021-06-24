<template>
  <el-tooltip :content=kg.graphName class="item" effect="dark" placement="top">
    <div :class="[kg.starred==='0' ? 'block-container' : 'block-container-star']">
      <div class="pic-container" @click="jumpInto">
        <img v-show="kgUrl" alt="logo" :src="kgUrl" style="max-width: 100%; max-height: 100%">
      </div>
      <div style="text-align: center">
        <h4 id="kgTitle" style="margin: 0" @click="jumpInto">{{ kgName }}</h4>
        <span style="font-size: 10px;opacity: 0.3;float: left;margin-top: 30px;margin-left: 7px"><i
            class="el-icon-edit"></i>{{ kg.time }}</span>
        <el-dropdown class="dropMenu" placement="bottom"
                     style="float: right;margin-top:26px;margin-right: 15px; z-index: 2" trigger="click">
          <img alt="trigger" src="/src/asset/point.png"
               style="width: 24px;height: 24px;opacity: 0.3;">
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item class="elDropdownItemStyle" disabled>导出</el-dropdown-item>
              <el-dropdown-item class="elDropdownItemStyle" @click="downloadPic">下载图片</el-dropdown-item>
              <el-dropdown-item v-if="kg.starred==='0'" class="elDropdownItemStyle" @click="confirmSetStarred()">设置为星标
              </el-dropdown-item>
              <el-dropdown-item v-if="kg.starred==='1'" class="elDropdownItemStyle" @click="confirmUnStarred()">取消星标
              </el-dropdown-item>
              <el-dropdown-item :disabled="kg.owned !== '1'" class="elDropdownItemStyle"
                                divided
                                @click="showFatherNameDialog(kg.graphId,kg.graphName)">
                重命名
              </el-dropdown-item>
              <el-dropdown-item class="elDropdownItemStyle" @click="showFatherInfoDialog(kg.graphId,kg.graphName)">
                详细信息
              </el-dropdown-item>
              <el-dropdown-item v-if="kg.owned === '1'" class="elDropdownItemStyle" divided @click="handleDelete">
                <span style="color: red">删除</span></el-dropdown-item>
              <el-dropdown-item v-else class="elDropdownItemStyle" disabled divided>
                <span style="color: red;opacity: 0.4">删除</span></el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <div class="added-card-footer" style="margin-top: 10px">
        </div>
      </div>
    </div>
  </el-tooltip>
</template>

<script>
import {useRouter} from "vue-router";
import {useStore} from "vuex";
import {ref, onBeforeMount, getCurrentInstance} from "vue";
import {ElMessage} from "element-plus";

export default {
  name: "kg-card",
  props: {
    kg: {}
  },
  emits: ["showFatherInfoDialog", "showFatherNameDialog", "refreshFatherData", "showFatherShare"],
  setup(props, context) {
    const graphId = props.kg.graphId;

    const {ctx} = getCurrentInstance();
    const router = useRouter();
    const store = useStore();
    let kgName = ref("");
    let kgUrl = ref(props.kg.imgURL);
    onBeforeMount(() => {
      if (props.kg.graphName.length > 8) {
        kgName.value = props.kg.graphName.substring(0, 8) + "…";
      } else {
        kgName.value = props.kg.graphName;
      }
    });
    const showFatherInfoDialog = (id, name) => {
      context.emit("showFatherInfoDialog", id, name);
    };
    const showFatherNameDialog = (id, name) => {
      console.log("emit" + name);
      context.emit("showFatherNameDialog", id, name);
    };
    const showFatherShare = (id, name) => {
      console.log("emit" + name);
      context.emit("showFatherShare", id, name);
    };
    const refreshFatherData = () => {
      console.log("refresh star page");
      context.emit("refreshFatherData");
    };
    const confirmSetStarred = () => {
      ctx.$confirm("确认设置为星标?", "星标确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
          .then(() => {
            store.dispatch("setGraphStarred", {
              userId: store.getters.userInfo.id,
              graphId,
              starFlag: 1
            })
                .then(() => {
                  ElMessage.success("设置星标成功");
                  //router.go(0);
                  refreshFatherData();
                })
                .catch((e) => {
                  console.log(e);
                });
          })
          .catch(() => {
            console.log("取消星标");
          });
    };
    const confirmUnStarred = () => {
      ctx.$confirm("确认取消星标?", "星标取消确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
          .then(() => {
            store.dispatch("setGraphStarred", {
              userId: store.getters.userInfo.id,
              graphId,
              starFlag: 0
            })
                .then(() => {
                  ElMessage.success("取消星标成功");
                  // router.go(0);
                  refreshFatherData();
                })
                .catch((e) => {
                  console.log(e);
                });
          })
          .catch(() => {
            console.log("取消星标");
          });
    };
    const handleDelete = () => {
      ctx.$confirm("此操作将永久删除该图谱的所有数据, 是否继续?", "删除确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
          .then(() => {
            store.dispatch("deleteGraph", {userId: store.getters.userInfo.id, kgId: graphId})
                .then(() => {
                  ElMessage.success("删除成功");
                  // refreshFatherData();
                  router.go(0);
                })
                .catch((e) => {
                  console.log(e);
                });
          })
          .catch(() => {
            console.log("取消删除");
          });
    };
    const downloadPic = () => {
      window.location.href = props.kg.imgURL;
    };
    const jumpInto = async () => {
      let graphData = await store.dispatch("getGraphData", {kgId: graphId});
      store.commit("createGraph", {graphId});
      store.commit("setGraphData", {graphId, model: graphData});
      await router.push(`/graph/${graphId}`);
    };
    return {
      kgName,
      kgUrl,
      showFatherInfoDialog,
      showFatherNameDialog,
      showFatherShare,
      confirmSetStarred,
      confirmUnStarred,
      jumpInto,
      handleDelete,
      downloadPic
    };
  }
};
</script>

<style scoped>
.block-container {
  transition: 0.5s all;
  width: fit-content;
  height: fit-content;
  background: #FFFFFF;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16);
  opacity: 1;
  border-top: 6px solid #46a0fc;
}

.block-container-star {
  transition: 0.5s all;
  width: fit-content;
  height: fit-content;
  background: #FFFFFF;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16);
  opacity: 1;
  border-top: 6px solid gold;
}

.block-container-star:hover {
  box-shadow: 5px 5px 10px #A2A2A2;
  cursor: pointer;
}

.block-container:hover {
  box-shadow: 5px 5px 10px #A2A2A2;
  cursor: pointer;
}

.pic-container {
  width: 140px;
  height: 140px;
  margin: 10px;
  /*padding-left: 10px;*/
  /*padding-top: 10px;*/
  /*padding-right: 10px;*/
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: lightgray 2px solid;
  border-radius: 4px;
}

.dropMenu:hover {
  cursor: pointer !important;
}

.added-card-footer {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 48px;
  border-top: 1px solid #EEEEEE;
}

.right-circle {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #FFFFFF;
  position: absolute;
  top: -10px;
  right: -10px;
}

.right-circletop {
  width: 10px;
  height: 20px;
  border-radius: 10px 0 0 10px;
  background: #FFFFFF;
  box-shadow: 2px 0 3px rgba(0, 0, 0, 0.12) inset;
  position: absolute;
  top: -10px;
  right: 0;
}

.left-circle {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #FFFFFF;
  position: absolute;
  top: -10px;
  left: -10px;
}

.left-circletop {
  width: 10px;
  height: 20px;
  border-radius: 10px 0 0 10px;
  background: #FFFFFF;
  box-shadow: 2px 0 3px rgba(0, 0, 0, 0.12) inset;
  position: absolute;
  top: -10px;
  transform: rotate(180deg);
  left: 0;
}

</style>

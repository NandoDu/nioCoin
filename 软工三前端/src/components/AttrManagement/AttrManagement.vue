<template>
  <div v-if="attrs">
    <div v-for="attr in attrs" :key="attr.name" style="display: flex;flex-direction: column;align-items: center;justify-content: center">
      <div class="attrWrapper" style="display: block;margin: 10px">
        <div style="margin: 3px;display: inline-block">{{ attr.name }}</div>
        <span> : </span>
        <div style="margin: 3px;display: inline-block">{{ attr.value }}</div>
      </div>
    </div>
    <div style="display: flex;justify-content: center;margin-top: 10px">
      <ElButton type="primary" @click="modifyAttrVisible = true"><span style="letter-spacing: 2px">编辑属性</span>
      </ElButton>
    </div>
    <el-dialog
        v-model="modifyAttrVisible"
        title="编辑节点属性"
        width="30%"
    >
      <div v-if="attrs" style="display: flex;flex-direction: column;align-items: center;justify-content: center">
        <div v-for="attr in attrs" :key="attr.name" style="display: flex;flex-direction: column;align-items: center;justify-content: center;">
          <div class="attrWrapper" style="display: block;margin: 10px">
            <div style="margin: 3px;display: inline-block;">{{ attr.name }}</div>
            <span>:</span>
            <div class="inputWrapper" style="margin: 4px;display: inline-block">
              <el-input :model-value="attr.value" @input="v=>setAttr(attr.name, v)" />
            </div>
            <el-button circle icon="el-icon-minus" size="mini" style="margin-left: 5px" type="danger" @click="delAttr(attr.name)"></el-button>
          </div>
        </div>
        <div style="margin-top: 10px;display: flex;flex-direction: row;align-items: center;justify-content: center">
          <div style="display: inline-block;width: 28%;margin:4px">
            <el-input v-model="newName" placeholder="属性名"></el-input>
          </div>
          <span>:</span>
          <div style="display: inline-block;width: 28%;margin:4px">
            <el-input v-model="newVal" placeholder="属性值"></el-input>
          </div>
          <el-button circle icon="el-icon-plus" size="mini" style="margin-left: 5px" type="success" @click="addAttr(newName, newVal)"></el-button>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="modifyAttrVisible = false">完 成</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
  <div v-else style="display: flex;justify-content: center">
    <span>No Node Selected</span>
  </div>
</template>

<script>
import {computed, ref} from "vue";
import {useStore} from "vuex";

export default {
  name: "AttrManagement",
  props: {
    graphId: {type: String}
  },
  setup(props) {
    const graphId = props.graphId;
    const store = useStore();
    const newName = ref("");
    const newVal = ref("");

    const attrValue = {};
    const attrs = computed(() => {
      const attrs = store.getters.curNodeAttr(graphId);
      if (attrs === null) return null;
      const res = [];
      for (const name in attrs) {
        if (attrs.hasOwnProperty(name) && name.startsWith("NIOCOIN__")) {
          res.push({name: name.substring(9), value: attrs[name]});
        }
      }
      return res;
    });

    const setAttr = (name, value) => {
      store.commit("setAttr", {
        graphId,
        attr: {name, value}
      });
      console.log(`[attr] set ${name} to ${value}`);
    };
    const delAttr = name => {
      store.commit("setAttr", {
        graphId,
        attr: {name, value: undefined}
      });
    };

    const addAttr = (name, value) => {
      store.commit("setAttr", {
        graphId,
        attr: {name, value}
      });
      newName.value = "";
      newVal.value = "";
    };

    return {
      attrValue,
      attrs,
      setAttr,
      delAttr,
      addAttr,
      newName,
      modifyAttrVisible: ref(false),
      newVal,
      isAdd: ref(true)
    };
  }
};
</script>

<style scoped>

</style>

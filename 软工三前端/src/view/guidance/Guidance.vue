<template>
  <div>
    <NewHeader width-limited></NewHeader>
    <div class="main-container">
	    <h2>用户使用指南</h2>
      <div class="content">
        <p class="para">在使用本系统前，请先进行注册和登陆</p>
        <p class="para">登陆之后，系统将自动跳转到首页，此刻首页左上角将出现登陆的用户状态</p>
        <p class="para">首页上方的用户引导——即本页面、组员、项目背景故事，均可供用户点击查看</p>
        <p class="para">点击首页的start按钮，即可进入图谱的操作管理界面</p>
        <p class="para">操作管理页面内侧边栏包括新建、收藏、操作历史，点击新建即可创建新的图谱，此时会供用户选择创建空图谱、导入csv和导入txt，选择相关的内容并上传所需文件（空图谱不需要）将会跳转至图谱编辑界面，此时图谱创建完成。在创建完图谱后，操作管理页面会生成对应的图谱卡片，点击图谱卡片即可进入到图谱编辑界面，图谱卡片右下角可进行收藏、删除、重命名等，图谱卡片默认蓝色，收藏以后会变成金色。点击收藏即可显示所有已收藏的图谱，点击编辑历史即可查看所有操作过的图谱</p>
        <p class="para">直接创建或者点击管理界面的卡片跳转至编辑界面，侧边栏提供了相关的节点和边的样式，并提供了详细的操作快捷键指南，用户可直接查看并遵循相关规则进行编辑，顶栏也提供了相关的便捷操作供用户选择</p>
        <p class="para">至此，本系统的介绍基本结束，感谢使用本系统</p>
      </div>
    </div>
  </div>
</template>

<script>
import {NewHeader} from "../../components/NewHeader";

export default {
  components: {NewHeader}
};
</script>

<style scoped src="./style.less" />

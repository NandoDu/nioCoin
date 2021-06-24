<template>
  <div>
    <NewHeader width-limited></NewHeader>
    <div class="main-container">
      <div class="content">
        <img alt="postman" class="img-zoom" src="../../asset/home-ele.png">
        <div style="margin-left: 40px;margin-top: 10px">
          <h1>Where the world builds knowledge graph</h1>
          <p>Millions of developers and companies build, ship, and maintain their knowledge graph on nioCoin—the
             largest and most advanced KG platform in the world.</p>
          <div class="paragraph-button-block">
            <el-button class="elButtonStyle" round
                       style="width: 176px;height: 43px;font-size: 18px;font-weight: 600;letter-spacing: 3px"
                       type="primary" @click="$router.push('/mine/item')">Start
            </el-button>
            <span class="learn-more-style">Learn More</span>
          </div>
        </div>
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

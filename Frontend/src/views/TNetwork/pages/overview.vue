<template>
  <div class="loop">
    <el-card class="box">
      <div slot="header" class="clearfix">
                
        <!-- <div class="color color1"></div>
        <div class="color color2"></div>
        <div class="color color3"></div>
        <div class="color color4"></div>
        <div class="color color5"></div>
        <div class="color color6"></div> -->
        <!-- <snap style="letter-spacing:37px">123456</snap> -->
<snap>Transction</snap>
<!-- 对比界面 -->
         <!-- <div class="color color1"></div>
        <div class="color color2"></div>
        <div class="color color3"></div>
        <div class="color color4"></div>
        <snap style="word-spacing:22px">normal abnormal remove delete</snap> -->
        <!-- <snap>Transction</snap> -->
      </div>
      <Trans :nodes="nodes" :links="links" :scope="scope" @parent="getP" />
    </el-card>
  </div>
</template>

<script>
import global from "../global";
import Trans from "../Trans";
import data from "../data/qd.json";

import store from "@/store.js";
export default {
  components: {
    Trans
  },
  props: {
    nodes: Array,
    links: Array,
    scope: {
      x: Number,
      y: Number
    }
  },
  methods: {
    getP() {}
  }
};
</script>

<style>
.text {
  float: left;
}
/* .color {
  width: 45px;
  height: 15px;
  float: left;
} */
/* .color1 {
  background: #fdae61;
}
.color2 {
  background: #fee08b;
}
.color3 {
  background: #ffffbf;
}
.color4 {
  background: #e6f598;
} */
.color {
  width: 75px;
  height: 20px;
  float: left;
}
.color1 {
  background: #dcfaf3;
}
.color2 {
  background: yellow;
}
.color3 {
  background: blue;
}
.color4 {
  background: black;
}
.color5 {
  background: #abdda4;
}
.color6 {
  background: #66c2a5;
}
</style>
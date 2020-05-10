<template>
  <div class="loop">
    <el-card class="box">
      <div slot="header" class="clearfix">
        <div class="color color1"></div>
        <div class="color color2"></div>
        <div class="color color3"></div>
        <div class="color color4"></div>
        <snap style="word-spacing:36px">add abnormal move delete</snap>
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
.color {
  width: 75px;
  height: 20px;
  float: left;
}
.color1 {
  background:green;
}
.color2 {
  background:red;
}
.color3 {
  background:blue;
}
.color4 {
  background:black;
}
</style>
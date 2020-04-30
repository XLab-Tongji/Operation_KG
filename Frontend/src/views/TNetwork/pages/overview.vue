<template>
  
    <div class="loop">
      <el-card class="box">
        <div slot="header" class="clearfix">
         
       

        <div class="color color1"></div>
        <div class="color color2"></div>
        <div class="color color3"></div>
        <div class="color color4"></div>
        <div class="color color5"></div>
        <div class="color color6"></div>

        <snap style="letter-spacing:37px">123456</snap>
    </div>
         <Trans :nodes="this.T.nodes" :links="this.T.links" :scope="this.scope" @parent="getP" />
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
  data() {
    return {
      T: {
        nodes: [],
        links: []
      },
      scope: {
        x: window.innerHeight * 0.35,
        y: window.innerWidth * 0.25
      }
    };
  },
  created() {
    global.tpe = data;
    if (global.tpe) {
      this.T.nodes = global.tpe.nodes;
      this.T.links = global.tpe.links;
    }
    store.commit("setData", data);
  },
  methods: {
    getP() {}
  }
};
</script>

<style>
.box {
  /* position: fixed;
  right: 0; */
  /* width: 310px; */
 
}
.text {
  float: left;
}
.color {
  width: 45px;
  height: 15px;
  float: left;
}
.color1 {
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
}
.color5 {
  background: #abdda4;
}
.color6 {
  background: #66c2a5;
}
</style>
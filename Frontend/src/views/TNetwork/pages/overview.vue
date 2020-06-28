<template>
  <div class="ok">
    <el-card class="box">
      <div slot="header" class="clearfix">
        <!-- <div class="color color1"></div>
        <div class="color color2"></div>
        <div class="color color3"></div>
        <div class="color color4"></div>
        <div class="color color5"></div>
        <div class="color color6"></div>-->
        <!-- <snap style="letter-spacing:37px">123456</snap> -->
        Transction
        <!-- 对比界面 -->
        <!-- <div class="color color1"></div>
        <div class="color color2"></div>
        <div class="color color3"></div>
        <div class="color color4"></div>
        <snap style="word-spacing:22px">normal abnormal remove delete</snap>-->
        <!-- <snap>Transction</snap> -->
      </div>
      <div class="total" >
        <div>
      <Trans :nodes="nodes" :links="links" :scope="scope" @parent="getP" @db="ifdb"/>
        </div>
      <div class="infor" style="min-width:40%">
      <el-card class="information" style="min-height=100%"> 
        {{this.info}} 
      </el-card>
      </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import global from "../global";
import Trans from "../Trans";
import data from "../data/qd.json";

import store from "@/store.js";

var times=1;

export default {
  components: {
    Trans
  },
  data(){
    return{
      info:"",
    }
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
    getP() {},
    ifdb(node){
      console.log('双击trans',node)
      if(times==1){
        this.oldnode=node;
         node._svgAttrs = {
        "stroke-width": 7,
       };
        times=2;
      }
      else{
        this.oldnode._svgAttrs = {
        "stroke-width": 3,
       };
       this.oldnode=node;
       node._svgAttrs = {
        "stroke-width": 7,
       };
      }
       node._svgAttrs = {
        "stroke-width": 7,
       }
      this.info="03:10.6 / INFO / [carts,,,] / 6 /  [           main] / o.s.j.e.a.AnnotationMBeanExporter	Bean with name 'configurationPropertiesRebinder' has been autodetected for JMX exposure";
    }
  }
};
</script>

<style >
.text {
  float: left;
}
/* .infor{
  color:white;
} */
/* .color {
  width: 75px;
  height: 20px;
  float: left;
} */
.information {
    border: 1px solid #EBEEF5;
     background-color: #f3f0e9;
    color: white;
    transition: .3s;
    overflow: auto;
     min-height: calc(78vh);
    max-height: calc(78vh)
}

</style>
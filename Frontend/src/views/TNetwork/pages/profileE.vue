<template>
  <div>
    <el-select v-model="selectE" filterable placeholder="请选择">
      <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
    </el-select>
    <Net :nodes="this.E.nodes" :links="this.E.links" @parent="click" />
  </div>
</template>

<script>
import global from "../global";
import Net from "../network1";
import data from "../data/trans.json";

import store from '@/store.js'
var value=0;
export default {
  components: {
    Net
  },
  data() {
   //  this.value=1;
    return {
      E: {
        nodes: [],
        links: []
      },
      options: [
        {
          value: 0,
          label: "node1"
        },
        {
          value: 1,
          label: "node2"
        }
      ],
      selectE: ""
    };
  },
  created() {
     

    global.tpe = data;
    if (global.tpe) {
      this.E.nodes = global.tpe.nodes;
      this.E.links = global.tpe.links;
    }
  },
  watch: {
   // selectR(newVal) {this.name=newVal},
    selectE(newVal,value) {
      // if(!newVal){
      //   this.E.nodes[newVal]._color="#ffffbf";
      //   console.log( newVal)
      //   value=newval;
      //   console.log( newVal)
       
      // }
      // else{
      // this.E.nodes[value]._color="#dcfaf3";
      // this.E.nodes[newVal]._color="#ffffbf";
      // //this.E.nodes._color="#dcfaf3";
      // value=newval;
      //  console.log(newVal)
      // }
     // console.log(value)
      this.E.nodes[value]._color="#dcfaf3";
      this.E.nodes[newVal]._color="#ffffbf";
    //  this.E.nodes._color="#dcfaf3";
      value=newval;
      console.log(value,newVal)
    }
  },
  methods: {
    click(parent) {
      console.log("node-info", parent);
    }
  }
};
</script>

<style>
</style>
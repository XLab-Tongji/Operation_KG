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
import Net from "../network";
import data from "../data/trans.json";

import store from '@/store.js'
export default {
  components: {
    Net
  },
  data() {
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
    selectE(newVal) {
      console.log(this.E.nodes[newVal].name,this.E.nodes[newVal])
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
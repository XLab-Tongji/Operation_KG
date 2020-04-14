<template>
  <div>
    <el-select v-model="selectT" filterable placeholder="请选择">
      <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
    </el-select>
    {{this.selectT}}
    <Net :nodes="this.P.nodes" :links="this.P.links" @parent="getP" />
  </div>
</template>

<script>
import global from "../global";
import Net from "../network";

import data from "../data/trans.json";
export default {
  components: {
    Net
  },
  data() {
    return {
      P: {
        nodes: [],
        links: []
      },
      E: {
        nodes: [],
        links: []
      },
      options: [
        {
          value: 2,
          label: "trans2"
        }
      ],
      selectT: ""
    };
  },
  watch: {
    selectT(newVal) {
      console.log(newVal);
      // vm.items.splice(newLength);

      this.P.nodes = data.nodes[newVal].nodes;
      this.P.links = data.nodes[newVal].links;
    }
  },
  created() {
    this.selectT = 0;
  },
  methods: {
    getP() {}
  }
};
</script>

<style>
</style>
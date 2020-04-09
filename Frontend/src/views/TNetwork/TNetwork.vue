<template>
  <div class="container">
    <el-row>
      <el-col :span="8">
        <graph :nodes="this.dataT.nodes" :links="this.dataT.links" @parent="getP" />
      </el-col>
      <el-col :span="8">
        <graph :nodes="this.dataP.nodes" :links="this.dataP.links" />
      </el-col>
      <el-col :span="8">
        <graph :nodes="this.dataE.nodes" :links="this.dataE.links" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import trans from "./data/trans.json";
import pat1 from "./data/pat1.json";
import pat2 from "./data/pat2.json";
import ent11 from "./data/ent11.json";

import graph from "./Trans";

export default {
  components: {
    graph
  },
  data() {
    return {
      dataT: {},
      dataP: {},
      dataE: {},
      hasP: false,
      hasE: false
    };
  },
  methods: {
    getData() {
      this.dataT = trans;
    },
    getP(parent){
        // this.dataP = 
    }
  },
  created() {
    this.getData();
  }
};
</script>
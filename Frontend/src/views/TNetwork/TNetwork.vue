<template>
  <div class="container">
    <el-row>
      <el-col :span="8">
        <graph :nodes="this.T.nodes" :links="this.T.links" @parent="getP" />
      </el-col>
      <el-col :span="8">
        <graph :nodes="this.P.nodes" :links="this.P.links" @parent="getE" />
      </el-col>
      <el-col :span="8">
        <graph v-if="render" :nodes="this.E.nodes" :links="this.E.links" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import trans from "./data/trans.json";
import graph from "./Trans";

export default {
  components: {
    graph
  },
  data() {
    return {
      T: {},
      P: {},
      E: {},
      render: false,
      focus: -1
    };
  },
  methods: {
    getData() {
      this.T = trans;
    },
    getP(parent) {
      let tmp = {
        nodes: this.T.nodes[parent - 1].nodes,
        links: this.T.nodes[parent - 1].links
      };
      if (tmp && parent != this.focus) {
        console.log("trans", parent);
        this.P = tmp;
        this.render = false;
        this.focus = parent;
      }
    },
    getE(parent) {
      let tmp = {
        nodes: this.P.nodes[parent - 1].nodes,
        links: this.P.nodes[parent - 1].links
      };
      if (tmp) {
        console.log("pat", parent);
        this.E = tmp;
        this.render = true;
      }
    }
  },
  created() {
    this.getData();
  }
};
</script>
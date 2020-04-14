<template>
  <div>
    <el-select v-model="selectT" filterable placeholder="请选择">
      <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
    </el-select>
    <Trans :nodes="this.P.nodes" :links="this.P.links" :scope="this.scope" @parent="getP" />
    <p>{{this.tran_name}} -> {{this.pat_name}}</p>
    <Trans v-if="render" :nodes="this.E.nodes" :links="this.E.links" :scope="this.scope" />
  </div>
</template>

<script>
import global from "../global";
import Net from "../network";
import Trans from "../Trans";

import store from "@/store.js";

export default {
  components: {
    Trans
  },
  data() {
    return {
      P: {
        nodes: [],
        links: []
      },
      E: {},
      options: [
        {
          value: 0,
          label: "trans1"
        },
        {
          value: 1,
          label: "trans2"
        }
      ],
      selectT: "",
      scope: {
        x: window.innerHeight / 3,
        y: window.innerWidth / 3
      },
      render: false,
      tran_name: "",
      pat_name: ""
    };
  },
  watch: {
    selectT(newVal) {
      // pattern-network
      let tmpP = {
        nodes: store.state.data.nodes[newVal].nodes,
        links: store.state.data.nodes[newVal].links
      };
      this.P = tmpP;
      this.tran_name = store.state.data.nodes[newVal].name;

      // entity-network
      let tmpE = {
        nodes: this.P.nodes[0].nodes,
        links: this.P.nodes[0].links
      };
      if (tmpE.nodes) {
        this.render = true;
        this.E = tmpE;
        this.pat_name = this.P.nodes[0].name;
      } else {
        this.render = false;
      }
    }
  },
  created() {
    this.selectT = 0;
  },
  methods: {
    getP(parent) {
      let id = parent.id;
      let tmp = {
        nodes: this.P.nodes[id - 1].nodes,
        links: this.P.nodes[id - 1].links
      };
      this.pat_name = this.P.nodes[id - 1].name;
      if (tmp.nodes) {
        this.render = true;
        this.E = tmp;
      } else {
        this.render = false;
      }
    }
  }
};
</script>

<style>
</style>
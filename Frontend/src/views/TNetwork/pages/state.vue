<template>
  <div>
    <div class="container">
      <div class="patten">
        <Trans :nodes="this.P.nodes" :links="this.P.links" :scope="this.scope" @parent="getP" />
      </div>

      <div class="overview">
        <Overview />
      </div>

      <div class="en">
        <el-card class="box1">
          <div slot="header" class="clearfix">
            <span>{{this.tran_name}} -> {{this.pat_name}}</span>
          </div>
          <div>
            <Trans v-if="render" :nodes="this.E.nodes" :links="this.E.links" :scope="this.scope1" />
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import global from "../global";
import Net from "../network";
import Trans from "../Trans";
import Overview from "./overview";
import Time from "../timepick";

import store from "@/store.js";
var c = 2;
var tra = 1;
export default {
  components: {
    Trans,
    Overview,
    Time
  },
  data() {
    return {
      P: {
        nodes: [],
        links: []
      },
      E: {},
      options: [],
      selectT: "",
      scope: {
        x: window.innerHeight,
        y: window.innerWidth * 0.6
      },
      scope1: {
        x: window.innerHeight * 0.45,
        y: window.innerWidth * 0.25
      },
      render: false,
      tran_name: "",
      pat_name: ""
    };
  },
  watch: {
    selectT(newVal) {
      store.state.data.nodes[tra]._color = "#dcfaf3";
      store.state.data.nodes[newVal]._color = "#abdda4";
      tra = newVal;
      // this.T.nodes[newVal]._color="#ffffbf"
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
    let raw = store.state.data.nodes;
    let test = raw.map(i => {
      return {
        value: i.id,
        label: i.name
      };
    });
    this.options = test;
    this.selectT = 0;
  },
  methods: {
    getP(parent) {
      this.P.nodes[c - 1]._color = "#dcfaf3";
      this.P.nodes[parent.id - 1]._color = "#ffffbf";

      c = parent.id;
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
.container {
  display: grid;
  grid-template-columns: 0.8fr 0.55fr;
  grid-template-rows: calc(100vh -30px / 4) calc(100vh -30px / 4 * 3);
  /* grid-template-rows:0.5fr 0.5fr; */
  grid-auto-flow: column;
  grid-column-gap: 20px;
  grid-row-gap: 5px;
}

.patten {
  grid-row-start: 1;
  grid-row-end: 3;
}
.overview {
  grid-row-start: 1;
  grid-row-end: 2;
}

/* .title {
  background-color: #d3d7d8;
  grid-row-start: 1;
  grid-row-end: 2;
} */
.en {
  background-color: #fffdfd;
  grid-row-start: 2;
  grid-row-end: 3;
}
/* .box {
  position: fixed;
  right: 0;
} */
</style>
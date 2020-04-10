<template>
  <div class="container">
    <div class="item Tras">
      <el-card class="box-card">
        <graph :nodes="this.T.nodes" :links="this.T.links" :scope="this.scope1" @parent="getP" />
      </el-card>
    </div>

    <div class="item Pat">
      <graph :nodes="this.P.nodes" :links="this.P.links" :scope="this.scope2" @parent="getE" />
    </div>
    <div class="item Ent">
      <graph v-if="render" :nodes="this.E.nodes" :links="this.E.links" :scope="this.scope2" />
    </div>
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
      focus: -1,
      scope1: {
        x: window.innerHeight,
        y: window.innerWidth*0.55,
      },
       scope2: {
        x: window.innerHeight/2,
        y: window.innerWidth*0.4,
      }
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
<style>
.container {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  grid-template-rows: calc(100vh / 2) calc(100vh / 2);
  /* grid-template-rows:0.5fr 0.5fr; */
  grid-auto-flow: column;
  grid-column-gap: 20px;
  grid-row-gap: 15px;
}

.Tras {
  background-color: #4ba946;
  grid-row-start: 1;
  grid-row-end: 3;
}
/* 
.Pat {
  background-color: #0376c2;
} */
/* .item {
  font-size: 4em;
  text-align: center;
  border: 1px solid #e5e4e9;
} */

/* .Ent{
  background-color: #c077af;
} */
</style>
<template>
  <div class="container">
    <div class="patten">
    <el-select v-model="selectT" filterable placeholder="请选择">
      <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
    </el-select>
    <Trans :nodes="this.P.nodes" :links="this.P.links" :scope="this.scope" @parent="getP" />
    </div>
    <el-card>
       <div slot="header" class="clearfix">
        <p style="fronted-size:10px; height:5px">{{this.tran_name}} -> {{this.pat_name}}</p>
       </div>
 
    
    
   
  
    <div class="en">
      <div>
    <Trans v-if="render" :nodes="this.E.nodes" :links="this.E.links" :scope="this.scope1" />
    </div>
    
    </div>
     </el-card>
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
        x: window.innerHeight ,
        y: window.innerWidth *0.6
      },
       scope1: {
        x: window.innerHeight*0.45 ,
        y: window.innerWidth *0.25
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
.container {
  display: grid;
  grid-template-columns: 1.3fr 0.5fr;
  grid-template-rows: calc(100vh -10px/3*0.4) calc(100vh -10px/3*0.2) calc(100vh -10px / 3) calc(100vh -10px /3);
  /* grid-template-rows:0.5fr 0.5fr; */
  grid-auto-flow: column;
  grid-column-gap: 20px;
  grid-row-gap: 15px;
}

.patten {

  grid-row-start: 1;
  grid-row-end: 5;
}

.title {
  background-color: #d3d7d8;
  grid-row-start: 1;
  grid-row-end:2;
}
.en {
  background-color: #fffdfd;
  grid-row-start: 2;
  grid-row-end:4;
  font-size: 4em
}
</style>
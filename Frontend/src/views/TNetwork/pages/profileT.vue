<template>
  <div>
    <div>
      <diagnose/>
    </div>
    <div class="container">
      <Time />
    </div>
    <el-select v-model="selectT" filterable placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>

    <div class="container1">
      <div class="patten">
        
         <edit v-if="render" :nodes="this.E.nodes" :links="this.E.links" :scope="this.scope" />
       
        <!-- <Trans :nodes="this.P.nodes" :links="this.P.links" :scope="this.scope" @parent="getP" /> -->
        
      </div>


<!-- Trans -->
      <div class="overview">
        <Overview :nodes="this.T.nodes" :links="this.T.links" :scope="this.scope2" />
      </div>
<!-- pattern -->
      <div class="en">
        <el-card class="box1">
          <div slot="header" class="clearfix">
            <span>{{this.tran_name}} -> {{this.pat_name}}</span>
          </div>
          <div>
            <Trans :nodes="this.P.nodes" :links="this.P.links" :scope="this.scope1" @parent="getP" />
          </div>
        </el-card>
      </div>

    </div>
    <state />
  </div>
</template>

<script>
import diagnose from '../diagnose/diagnose'
import state from "./state";
import edit from "../edit";

import global from "../global";
import Net from "../network";
import Trans from "../Trans";
import Overview from "./overview";
import Time from "../timepick";
import store from "@/store.js";
// import data1 from "../data/compare.json";

import data from "../data/trans.json";
import fir from "../data/qd.json";
var c = 2;
var tra = 1;
var col = "white";
var kk = 1;
var tr = 1;
export default {
  components: {
    Trans,
    Overview,
    Time,
    state,
    edit,
    diagnose
  },
  data() {
    return {
      P: {
        nodes: [],
        links: []
      },
      E: {},
      T: {},
      options: [],
      selectT: "",
      scope: {
        x: window.innerHeight*0.5,
        y: window.innerWidth *0.5
      },
      scope1: {
        x: window.innerHeight*0.65 ,
        y: window.innerWidth * 0.5
      },
      scope2: {
        x: window.innerHeight * 0.7,
        y: window.innerWidth * 0.37
      },
      render: false,
      tran_name: "",
      pat_name: "",
      data: []
    };
  },
  watch: {
    selectT(newVal) {
      store.commit("setTrans", newVal);
      console.log(store.state.trans);
      //  console.log(parent,kk)
      if (tr == 1) {
        // console.log("tr是1",tr)
        this.data.nodes[newVal]._color = "#abdda4";
        tra = newVal;
        tr = tr + 3;
      } else {
        console.log("tr不是1", tr);
        this.data.nodes[tra]._color = "#dcfaf3";
        this.data.nodes[newVal]._color = "#abdda4";
        tra = newVal;
      }
      let tmpP = {
        nodes: this.data.nodes[newVal].nodes,
        links: this.data.nodes[newVal].links
      };

      // if tmp.nodes
      this.P = tmpP;
      // var col=this.P.nodes[0]._color
      // console.log(col)
      this.tran_name = this.data.nodes[newVal].name;

      store.commit("setTrans", 0);
      console.log(store.state.trans);
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
    // ##2.在这里调用一个接口，获取fir.json的数据
    // 参数：待定
    // 返回：fir
    store.commit("setData", data);
    store.commit("setFir", fir);
    this.data = store.state.fir;
    console.log(this.data.links);
    let tmpT = {
      nodes: this.data.nodes,
      links: this.data.links
    };
    this.T = tmpT;
    let raw = this.data.nodes;
    let test = raw.map(i => {
      return {
        value: i.id,
        label: i.name
      };
    });
    this.options = test;
    this.selectT = this.data.nodes[0].id;
    store.commit("setTrans", this.selectT);
    console.log(store.state.trans);
  },
  methods: {
    getP(parent) {
      store.commit("setPat", parent.id);
      console.log(store.state.pat);
      //  console.log(parent,kk)
      if (kk == 1) {
        console.log("kk是1", kk);
        col = this.P.nodes[parent.id]._color;
        this.P.nodes[parent.id]._color = "#a78cb7";
        c = parent.id;
        kk = kk + 3;
      } else {
        console.log("kk不是1", kk);
        this.P.nodes[c]._color = col;
        col = this.P.nodes[parent.id]._color;
        this.P.nodes[parent.id]._color = "#a78cb7";
        c = parent.id;
      }

      // col=this.P.nodes[parent.id-1]._color
      // this.P.nodes[c-1]._color=col;
      // this.P.nodes[parent.id-1]._color="red";
      // c=parent.id;

      let id = parent.id;
      let tmp = {
        nodes: this.P.nodes[id].nodes,
        links: this.P.nodes[id].links
      };
      this.pat_name = this.P.nodes[id].name;
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
.container1 {
  display: grid;
  grid-template-columns: 2fr 1fr;
  grid-template-rows: calc(70vh)   calc(10ch)  calc(90vh);
  /* grid-template-rows:0.5fr 0.5fr; */
  grid-auto-flow: row;
  grid-column-gap: 20px;
  grid-row-gap: 5px;
  padding-top:1.5%
}

.patten {
 
  grid-column-end: 2;
  grid-column-start: 1;
  grid-row:1/3;
 
}
.overview {
  grid-row-start: 1;
  grid-row-end: 4;
  grid-column: 2/3;
}

/* .title {
  background-color: #d3d7d8;
  grid-row-start: 1;
  grid-row-end: 2;
} */
.en {
  
  grid-row-start: 3;
  grid-row-end: 4;
  grid-column:1/2;
  
}
/* .box {
  position: fixed;
  right: 0;
} */
</style>
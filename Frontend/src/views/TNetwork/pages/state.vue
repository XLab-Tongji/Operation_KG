<template>
  <div>
    <div>
      <p>请选择想进行比较的system state</p>
      <el-select v-model="id" @change="change" filterable placeholder="请选择想进行比较的system state">
      <el-option v-for="item in sso" :key="item.id" :label="item.id" :value="item.id"></el-option>
      </el-select>
    </div>
    <div class="container">
      <div class="patten">
         <p>请选择transction</p>
        <el-select v-model="selectT" filterable placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        <Trans :nodes="this.P.nodes" :links="this.P.links" :scope="this.scope" @parent="getP" />
      </div>

      <div class="overview">
        <Overview :nodes="this.T.nodes" :links="this.T.links" :scope="this.scope2" />
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
     <div class="json">
      <el-card class="json-card">
  <div slot="header" class="clearfix">
    <span>json</span>
  </div>
</el-card>
     </div>
  </div>
</template>

<script>
import state from "./state";

import global from "../global";
import Net from "../network";
import Trans from "../Trans_compare";
import Overview from "./overview1";
import Time from "../timepick";
import store from "@/store.js";

import data from "../data/compare.json";
var c1=2
var tra1=1
var col1="white"
var kk1=1
var tr1=1
export default {
  components: {
    Trans,
    Overview,
    Time,
    state
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
        x: window.innerHeight,
        y: window.innerWidth * 0.6
      },
      scope1: {
        x: window.innerHeight * 0.45,
        y: window.innerWidth * 0.25
      },
      scope2: {
        x: window.innerHeight * 0.35,
        y: window.innerWidth * 0.25
      },
      render: false,
      tran_name: "",
      pat_name: "",
      data: [],
      id: -1,
      sso: store.state.date
    };
  },
  watch: {
    selectT(newVal) {
    console.log(newVal)
     if(tr1==1)
    {
      console.log("tr是1",tr1)
      this.data.nodes[newVal-1]._color="#abdda4";
      tra1=newVal;
      tr1=tr1+3
    }
    else{
      console.log("tr不是1",tr1)
     this.data.nodes[tra1-1]._color="#dcfaf3";
      this.data.nodes[newVal-1]._color="#abdda4";
      tra1=newVal;
    }
   
      // this.T.nodes[newVal]._color="#ffffbf"
      // pattern-network
      let tmpP = {
        nodes: this.data.nodes[newVal-1].nodes,
        links: this.data.nodes[newVal-1].links
      };
      this.P = tmpP;
      console.log(this.P)
      this.tran_name = this.data.nodes[newVal-1].name;

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
    },
    data(newVal) {
      newVal=newVal-1
      let tmpP = {
        nodes: newVal.nodes[1].nodes,
        links: newVal.nodes[1].links
      };
      this.P = tmpP;
      this.tran_name = newVal.nodes[1].name;

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
  created() {},
  methods: {
    change() {
      this.data = store.state.data[this.id];
      if (this.data == undefined) console.log("undefune");
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
      this.selectT = 1;
    },
    getP(parent) {
     
     if(kk1==1)
    {
      // console.log("kk1是1",kk1)
      // console.log("当前点到的点",this.P.nodes[parent.id-1].name)
      // console.log("当前点到的点的原来颜色是",this.P.nodes[parent.id-1]._color)
      col1=this.P.nodes[parent.id-1]._color
      // console.log("当前存储的颜色是",col1)
      this.P.nodes[parent.id-1]._color="#a78cb7";
      // console.log("当前点到的点的颜色改变为",this.P.nodes[parent.id-1]._color)
      c1=parent.id;
      kk1=kk1+3
    }
    else{
      // console.log("kk1不是1",kk1)
      // console.log("当前点到的点",this.P.nodes[parent.id-1].name)
      // console.log("当前点到的点的原来颜色是",this.P.nodes[parent.id-1]._color)
      // console.log("之前点到的点的原来颜色是",col1)
      // console.log("之前点到的点的名字是",this.P.nodes[c1-1].name)
      this.P.nodes[c1-1]._color=col1;
      col1=this.P.nodes[parent.id-1]._color
      // console.log("现在存储的点的原来颜色是",col1)
      this.P.nodes[parent.id-1]._color="#a78cb7";
      c1=parent.id;
    }
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
  grid-template-rows: calc(100vh -10px / 4)  calc(100vh -10px / 4 * 3) ;
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
.json-card .el-card__body {
   background:lightgrey
}
</style>
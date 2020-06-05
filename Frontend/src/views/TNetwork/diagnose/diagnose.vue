<template>
  <div>
    <div>
      <p>请选择想进行比较的system state</p>
      <el-select v-model="id" @change="change" filterable placeholder="请选择想进行比较的system state">
        <el-option v-for="item in sso" :key="item.id" :label="item.id" :value="item.id"></el-option>
      </el-select>
      <p>请选择transction</p>
      <el-select v-model="selectT" filterable placeholder="请选择">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
      <el-button class="dialog_button" round  style="margin-left: 3% ;width:7%">diagnose</el-button>

      <el-card class="result-card" >
          <span>result:</span>
      </el-card>
    </div>
  </div>
</template>

<script>
import global from "../global";
import axios from "axios";

let url = global.url;
export default {
  data() {
    return {
      sso: [
        {
          date: "2020-06-02 03:17",
          id: "2020-06-02 03:17:25",
          state: "unknow"
        },
        {
          date: "2020-06-02 03:17",
          id: "2020-06-02 03:17:45",
          state: "normal"
        }
      ],
      options: [],
      id: "",
      selectT: "",
      nodes: {}
    };
  },
  methods: {
    change(val) {
      axios
        .get(url + "/api/getTransctionData?stateId=" + val)
        .then(res => res.data.nodes)
        .then(nodes => {
          return nodes.map(i => {
            return {
              value: i.id,
              label: i.name
            };
          });
        })
        .then(i => {
          this.options = i;
        });
    }
  }
};
</script>

<style>
.result-card {
  margin-top:20px;
   max-width: 26%
}
 .el-button .dialog_button{
    width: 70px;
}
</style>
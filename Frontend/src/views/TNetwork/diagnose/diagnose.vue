<template>
  <div>
    <div>
      <p>system_state_id</p>
      <el-select v-model="id" @change="change" filterable placeholder="请选择">
        <el-option v-for="item in sso" :key="item.id" :label="item.id" :value="item.id"></el-option>
      </el-select>
      <p>transction</p>
      <el-select v-model="selectT" filterable placeholder="请选择">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
      <el-button round>diagnose</el-button>
      <el-card class="json-card">
        <div slot="header" class="clearfix">
          <span>result</span>
        </div>
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
</style>
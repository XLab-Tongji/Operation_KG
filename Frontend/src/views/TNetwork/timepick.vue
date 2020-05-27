<template>
  <div>
    <div class="block">
      <el-date-picker
        v-model="value"
        type="daterange"
        align="right"
        unlink-panels
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        @change="onpick"
      ></el-date-picker>
    </div>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="id" label="system_state_id"></el-table-column>
      <el-table-column prop="state" label="system_state"></el-table-column>
    </el-table>
  </div>
</template>

<script>
import store from "@/store.js";
import axios from "axios";
import global from "./global";
const url = global.url;
// import date from "./data/date.json";

export default {
  data() {
    return {
      value: "",
      tableData: []
    };
  },
  watch: {
    value(newVal) {
      this.tableData = [];
      for (let i in date) {
        let tmp = new Date(date[i].date);
        if (tmp <= newVal[1] && tmp >= newVal[0]) {
          this.tableData.push(date[i]);
        }
      }
    }
  },
  created() {
    // ##1.在这里调用一个接口，获取date.json的数据
    // 参数：无
    // 返回：date
    let formData = new FormData();
    formData.append("start", "2020-04-07");
    formData.append("end", "2020-06-07");

    axios.post(url + "/api/getSystemStates", formData).then(res => {
      store.commit("setDate", res.data);
    });
    // store.commit("setDate", date);
  },
  mounted() {
    let d1 = new Date("2020-4-3");
    let d2 = new Date("2020-4-5");
    this.value = [d1, d2];
  },
  methods: {
    onpick() {
      this.tableData = [];
      for (let i in date) {
        let tmp = new Date(date[i].date);
        if (tmp <= this.value[1] && tmp >= this.value[0]) {
          this.tableData.push(date[i]);
        }
      }
    }
  }
};
</script>

<style>
</style>
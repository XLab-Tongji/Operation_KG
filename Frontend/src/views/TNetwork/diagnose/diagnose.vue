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
    </div>
  </div>
</template>

<script>
export default {};
</script>

<style>
</style>
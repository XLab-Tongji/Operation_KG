<template>
  <el-container>
    <el-main>
      <el-row type="flex" align="middle">
        <el-col>
          <i class="el-icon-menu" @click="handleCollapse"></i>
        </el-col>
      </el-row>
      <el-row type="flex">
        <el-col :style="{width:this.sideBarWidth()}" class="nav">
          <el-menu
            default-active="1-4-1"
            class="el-menu-vertical-demo"
            @open="handleOpen"
            @close="handleClose"
            @select="handleSelect"
            :collapse="isCollapse"
          >
            <el-menu-item index="overview">
              <i class="el-icon-view"></i>
              <span slot="title">Transactions Overview</span>
            </el-menu-item>

            <el-menu-item index="profileT">
              <i class="el-icon-share"></i>
              <span slot="title">Profile of Transactions</span>
            </el-menu-item>

            <el-menu-item index="profileE">
              <i class="el-icon-share"></i>
              <span slot="title">Profile of Entity with Attributes</span>
            </el-menu-item>

            <el-menu-item index="compare">
              <i class="el-icon-share"></i>
              <span slot="title">Comparison of Profiles by Timestamp</span>
            </el-menu-item>
          </el-menu>
        </el-col>

        <el-col style="width:100%;height:100%">
          <keep-alive>
            <router-view></router-view>
          </keep-alive>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>


<style scoped>
svg {
  left: 50%;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 250px;
  min-height: 700px;
}

.el-main {
  padding: 5px;
}

.el-header {
  box-shadow: 0px 1px 10px lightgray;
  /* opacity: 60; */
}
.nav {
  margin-top: 40px;
}
.el-icon-menu {
  color: white;
  position: fixed;
  z-index: 10;
  width: 16px;
  height: 16px;
  border-radius: 20px;
  padding: 10px;
  background-color: rgba(95, 95, 95, 0.705);
  cursor: pointer;
  top: 7px;
  left: 18px;
}
</style>

<script>
export default {
  name: "mainpage",
  data() {
    return {
      sidebar: 5,
      isCollapse: true
    };
  },
  methods: {
    sideBarWidth() {
      if (!this.isCollapse) {
        return "350px";
      } else {
        return "75px";
      }
    },
    handleSelect(key, keyPath) {
      this.$router.push({
        name: key
      });
    },
    handleOpen(key, keyPath) {
      // console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      // console.log(key, keyPath);
    },
    handleCollapse() {
      this.isCollapse = !this.isCollapse;
    }
  }
};
</script>
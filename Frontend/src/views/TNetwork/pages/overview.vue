<template>
  <div>
    <Net :nodes="this.T.nodes" :links="this.T.links" @parent="getP" />
  </div>
</template>

<script>
import global from "../global";
import Net from "../network";
import data from "../data/trans.json";
export default {
  components: {
    Net
  },
  data() {
    return {
      T: {
        nodes: [],
        links: []
      }
    };
  },
  created() {
    global.tpe = data;
    if (global.tpe) {
      this.T.nodes = global.tpe.nodes;
      this.T.links = global.tpe.links;
    }
  },
  methods: {
    getP() {}
  }
};
</script>

<style>
</style>
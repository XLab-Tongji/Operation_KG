<template>
  <div>
    <el-button @click="on">correct</el-button>
    <el-button @click="off">confirm</el-button>

    <el-button v-if="edit" @click="addEntity">add entity</el-button>
    <el-button v-if="edit" @click="addRelation">add relation</el-button>

    <el-button v-if="editEntity" @click>add attr</el-button>
    <el-button v-if="editEntity" @click>delete</el-button>

    <el-button v-if="editAttr" @click="deleteAttr">delete</el-button>

    <div v-if="name_change">
      <el-form :model="form" label-width="80px">
        <el-form-item label="new name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">ok</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <d3-network
        ref="net"
        :net-nodes="nodes"
        :net-links="links"
        :options="options"
        :selection="selection"
        @node-click="clickNode"
        @link-click="clickLink"
        :node-cb="ncb"
        :link-cb="lcb"
      />
    </div>
    <svg height="0">
      <defs>
        <!-- 普通箭头 -->
        <marker
          id="arrow"
          markerWidth="6"
          markerHeight="6"
          refX="14.3"
          refY="1.8"
          orient="auto"
          markerUnits="strokeWidth"
        >
          <path d="M0,0 L0,3.6 L3.6,1.8 z" />
        </marker>
      </defs>
    </svg>
  </div>
</template>

<script>
import D3Network from "../../components/vue-d3-network/src/d3-systemOverview.vue";
import axios from "axios";

HTMLCollection.prototype.forEach = Array.prototype.forEach;
Array.prototype.indexOf = function(val) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == val) return i;
  }
  return -1;
};
Array.prototype.remove = function(val) {
  var index = this.indexOf(val);
  if (index > -1) {
    this.splice(index, 1);
  }
};
var getCoordInDocument = function(e) {
  e = e || window.event;
  var x =
    e.pageX ||
    e.clientX +
      (document.documentElement.scrollLeft || document.body.scrollLeft);
  var y =
    e.pageY ||
    e.clientY + (document.documentElement.scrollTop || document.body.scrollTop);
  return {
    x: x,
    y: y
  };
};
function addEvent(obj, xEvent, fn) {
  if (obj.attachEvent) {
    obj.attachEvent("on" + xEvent, fn);
  } else {
    obj.addEventListener(xEvent, fn, false);
  }
}
export default {
  components: {
    D3Network
  },
  props: {
    nodes: Array,
    links: Array,
    scope: {
      x: Number,
      y: Number
    }
  },
  data() {
    return {
      delEntity: {},
      delAttr: {},
      editEntity: false,
      editAttr: false,
      count: 0,
      editR: false,
      tpnode: {},
      tpbegin: -1,
      tpend: -1,
      db: false,
      form: {
        name: ""
      },
      edit: false,
      selection: {
        links: {},
        nodes: {}
      },
      nodeSize: 40,
      fontSize: 14,
      linkWidth: 1,
      canvas: false,
      notify: {},
      sourceNodeId: 0,
      targetNodeId: 0,
      offset_X: 0,
      offset_Y: 0,
      finCoor: 0,
      staCoor: 0,
      force: 3000,
      moveable: false
    };
  },
  computed: {
    name_change() {
      return this.edit && this.db;
    },
    // 节点数量
    id() {
      return this.nodes.length;
    },
    options() {
      return {
        force: this.force,
        size: {
          // h: window.innerHeight,
          // w: window.innerWidth / 3
          h: this.scope.x,
          w: this.scope.y
        },
        offset: {
          x: this.offset_X,
          y: this.offset_Y
        },
        nodeSize: this.nodeSize,
        fontSize: this.fontSize,
        nodeLabels: true,
        linkLabels: true,
        canvas: this.canvas,
        profileLinks: []
      };
    }
  },
  methods: {
    deleteAttr() {
      let tmp_nodes=this.nodes.filter(node => node!=this.delAttr);
      this.nodes=tmp_nodes;
      let tmp_links=this.links.filter(link=> link.tid!=this.delAttr.id && link.sid!=this.delAttr.id);
      this.links=tmp_links;

      this.editAttr=false;
    },
    clickNode(e, node) {
      this.$emit("parent", node);
      this.tpnode = node;
      let timer = null;
      clearTimeout(timer);
      // 双击
      if (e.detail == 2) {
        if (this.edit) {
          this.db = true;
          console.log("需要修改名字的节点是：", node);
        }
      }
      // 单击
      timer = setTimeout(function() {}, 100);
      if (this.edit && this.editR) {
        this.count += 1;
        if (this.count > 2) {
          this.count = 0;
          this.editR = false;
        } else if (this.count == 1) {
          this.tpbegin = node.id;
        } else {
          this.tpend = node.id;
          console.log("begin", this.tpbegin);
          console.log("end", this.tpend);
          let new_link = {
            tid: this.tpend,
            sid: this.tpbegin
          };
          console.log(new_link);
          this.links.push(new_link);
          console.log(this.links);
        }
      }
      if (this.edit && node.type == "entity") {
        this.editEntity = true;
      }
      if (this.edit && node.type == "attribute") {
        this.editAttr = true;
        this.delAttr=node;
      }
    },
    addEntity() {
      // 在这儿把新节点给后端
      let _id = this.nodes[this.nodes.length - 1].id;
      let new_node = {
        id: _id + 1,
        name: "new entity"
      };
      this.nodes.push(new_node);
    },
    addRelation() {
      this.editR = true;
    },
    onSubmit() {
      // 在这儿把新名称给后端
      this.tpnode.name = this.form.name;
      this.db = false;
    },
    on() {
      this.edit = true;
    },
    off() {
      this.edit = false;
    },
    ncb(node) {
      //  对比界面
      //  if(node.state){
      //      switch(node.state){
      // case "normal":
      //   node._color="#dcfaf3";
      //   break;
      // case "abnormal":
      //   node._color="yellow";
      //   break;
      // case "remove":
      //   node._color="blue";
      //   break;
      // case "delete":
      //   node._color="black";
      //   break;
      // case "still":
      //   node._color="#dcfaf3";
      //   break;
      //   }
      //   }

      if (node.loop) {
        switch (node.loop) {
          case 1:
            node._color = "#fdae61";
            break;
          case 2:
            node._color = "#fee08b";
            break;
          case 3:
            node._color = "#ffffbf";
            break;
          case 4:
            node._color = "#e6f598";
            break;
          case 5:
            node._color = "#abdda4";
            break;
          case 6:
            node._color = "#66c2a5";
            break;
        }
      }
      return node;
    },
    lcb(link) {
      link._color = "lightgray";
      link._svgAttrs = {
        // "stroke-width": this.linkWidth,
        "stroke-width": 2,
        opacity: 1,
        "marker-end": "url(#arrow)"
      };
      return link;
    },
    clickLink(e, link) {},
    displayNodeRelation(node) {
      this.selection = {
        nodes: {},
        links: {}
      };
      // let
      this.selection.nodes[node.id] = node;
      for (let link of this.links) {
        if (link.sid === node.id) {
          this.selection.links[link.id] = link;
          for (let node of this.nodes) {
            if (node.id === link.tid) {
              this.selection.nodes[node.id] = node;
            }
          }
        }
        if (link.tid === node.id) {
          this.selection.links[link.id] = link;
          for (let node of this.nodes) {
            if (node.id === link.sid) {
              this.selection.nodes[node.id] = node;
            }
          }
        }
      }
    }
  },
  mounted() {
    var el = document.getElementsByClassName("net-svg")[0];
    el.onmousedown = e => {
      this.staCoor = getCoordInDocument(e);
    };
    el.onmouseup = e => {
      this.finCoor = getCoordInDocument(e);
      let x = this.finCoor.x - this.staCoor.x;
      let y = this.finCoor.y - this.staCoor.y;
    };
  }
};
</script>

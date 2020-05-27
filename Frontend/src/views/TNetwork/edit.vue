<template>
  <div>
    <el-alert v-if="alert" title="只可以在entity之间添加关系哦😯" type="error"></el-alert>
    <el-row>
      <el-col :span="8">
        <div class="grid-content bg-purple-dark"></div>
      </el-col>
      <el-col :span="8">
        <div class="grid-content bg-purple-dark"></div>
      </el-col>
      <el-col :span="4">
        <div class="grid-content bg-purple-dark">
          <el-button @click="on">{{button}}</el-button>
        </div>
      </el-col>
      <!-- <el-col :span="4">
        <div class="grid-content bg-purple-dark">
          <el-button @click="off">confirm</el-button>
        </div>
      </el-col>-->
    </el-row>

    <div v-if="name_change">
      <el-form :model="form" label-width="80px">
        <el-form-item label="new name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item class="ok">
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

    <el-row>
      <el-col :span="8">
        <div class="grid-content bg-purple">
          <el-button v-if="edit" @click="addEntity">add entity</el-button>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="grid-content bg-purple-light"></div>
      </el-col>
      <el-col :span="8">
        <div class="grid-content bg-purple">
          <el-button v-if="editEntity" @click="addAttr">add attr</el-button>
        </div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="8">
        <div class="grid-content bg-purple">
          <el-button v-if="edit" @click="addRelation">add relation</el-button>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="grid-content bg-purple-light"></div>
      </el-col>
      <el-col :span="8">
        <div class="grid-content bg-purple">
          <el-button v-if="editEntity" @click="deleteEntity">delete</el-button>
          <el-button v-if="editAttr" @click="deleteAttr">delete</el-button>
        </div>
      </el-col>
    </el-row>

    <!-- <el-button v-if="edit" @click="addEntity">add entity</el-button>
    <el-button v-if="edit" @click="addRelation">add relation</el-button>

    <el-button v-if="editEntity" @click="addAttr">add attr</el-button>
    <el-button v-if="editEntity" @click="deleteEntity">delete</el-button>

    <el-button v-if="editAttr" @click="deleteAttr">delete</el-button>-->
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
      button: "correct",
      tplink: {},
      flag: "",
      alert: false,
      linkLabels: true,
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
      times: 1,
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
    addAttr() {
      let _id = this.nodes[this.nodes.length - 1].id;
      let new_node = {
        id: _id + 1,
        name: "new_attr",
        type: "attribute"
      };
      this.nodes.push(new_node);
      let new_link = {
        tid: new_node.id,
        sid: this.delEntity.id
      };
      this.links.push(new_link);

      this.editEntity = false;
      this.delEntity._color = "#dcfaf3";
      console.log("添加了一个新attr", new_node);
      // ##5.在这里调用一个接口，添加新attr
      // 参数：{nodes:this.nodes,links:this.links}
      // 返回：无
    },
    clickNode(e, node) {
      this.$emit("parent", node);
      this.tpnode = node;
      let timer = null;
      clearTimeout(timer);

      // 双击

      if (e.detail == 2) {
        node._color = "red";
        if (this.edit) {
          this.db = true;
          console.log("需要修改名字的节点是：", node);
        }
      }
      // 单击

      timer = setTimeout(function() {}, 100);
      if (this.edit && this.editR) {
        if (node.type == "entity") {
          // node._color="red";
          this.count += 1;
          if (this.count > 2) {
            this.count = 0;
            this.editR = false;
          } else if (this.count == 1) {
            this.tpbegin = node.id;
            this.begin_node = node;
            node._color = "red";
          } else {
            this.tpend = node.id;
            let new_link = {
              tid: this.tpend,
              sid: this.tpbegin
            };
            this.links.push(new_link);
            this.begin_node._color = "#dcfaf3";

            console.log("添加了一个新的relation", this.tpend, this.tpbegin);
            // ##8.在这里调用一个接口，添加一个relation
            // 参数：{nodes:this.nodes,links:this.links}
            // 返回：无
          }
        } else {
          this.alert = true;
        }
      } else {
        if (this.edit && node.type == "entity") {
          this.editEntity = true;

          if (this.times == 1) {
            this.change_node = node;
            this.change_node._color = "red";
            this.last_node = this.change_node;
            this.times = 2;
          } else {
            this.last_node._color = "#dcfaf3";
            this.change_node = node;
            this.change_node._color = "red";
            this.last_node = this.change_node;
          }
          console.log("当前修改的节点是", this.change_node);
          this.delEntity = node;
        }
        if (this.edit && node.type == "attribute") {
          this.editAttr = true;
          if (this.times == 1) {
            this.change_node = node;
            this.change_node._color = "red";
            this.last_node = this.change_node;
            this.times = 2;
          } else {
            this.last_node._color = "#dcfaf3";
            this.change_node = node;
            this.change_node._color = "red";
            this.last_node = this.change_node;
          }

          this.delAttr = node;
        }
      }
    },
    deleteAttr() {
      let tmp_nodes = this.nodes.filter(node => node != this.delAttr);
      this.nodes = tmp_nodes;
      let tmp_links = this.links.filter(
        link => link.tid != this.delAttr.id && link.sid != this.delAttr.id
      );
      this.links = tmp_links;

      this.editAttr = false;

      console.log("删除了一个delatrr", this.delAttr);
      // ##7.在这里调用一个接口，删除一个attr
      // 参数：{nodes:this.nodes,links:this.links}
      // 返回：无
    },
    deleteEntity() {
      let tmp_nodes = this.nodes.filter(node => node != this.delEntity);
      this.nodes = tmp_nodes;
      let tmp_links = this.links.filter(
        link => link.tid != this.delEntity.id && link.sid != this.delEntity.id
      );

      this.links = tmp_links;

      this.editEntity = false;

      console.log("删除了一个entity", this.delEntity);
      // ##6.在这里调用一个接口，删除一个entity
      // 参数：{nodes:this.nodes,links:this.links}
      // 返回：无
    },
    addEntity() {
      // 在这儿把新节点给后端
      let _id = this.nodes[this.nodes.length - 1].id;
      let new_node = {
        id: _id + 1,
        name: "new_entity",
        type: "entity"
      };
      this.nodes.push(new_node);

      console.log("添加了一个新entity", new_node);

      // ##4.在这里调用一个接口，添加新node
      // 参数：{nodes:this.nodes,links:this.links}
      // 返回：无
    },
    addRelation() {
      this.editR = true;
    },
    onSubmit() {
      if (!this.flag) {
        this.tpnode.name = this.form.name;

        this.tpnode._color = "#dcfaf3";
        console.log("修改的是node", this.tpnode);
      } else {
        this.tplink.name = this.form.name;
        this.tplink._color = "lightgray";
        console.log("修改的是link", this.tplink);
      }
      // ##3.在这里调用一个接口，修改名称
      // 参数：{nodes:this.nodes,links:this.links}
      // 返回：无
      // let _tmp={
      //   nodes:this.nodes,
      //   links:this.links
      // }
      // let formData = new FormData();
      // formData.append("data", "_tmp");
      // axios.post(url + "/api/getSystemStates", formData).then(res => {
      //   store.commit("setDate", res.data);
      // });

      this.db = false;
    },
    on() {
      if (this.edit == false) {
        this.edit = true;
        this.button = "confirm";
      } else {
        this.edit = false;
        this.editR = false;
        this.editEntity = false;
        this.editAttr = false;

        // this.change_attr_node._color="#dcfaf3";
        this.button = "correct";
        this.change_node._color = "#dcfaf3";
      }
    },
    off() {
      this.edit = false;
      this.button = "correct";
    },
    ncb(node) {
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
      console.log(link.label);
    },
    clickLink(e, link) {
      this.tplink = link;
      let timer = null;
      clearTimeout(timer);
      // 双击
      if (e.detail == 2) {
        link._color = "red";
        if (this.edit) {
          this.db = true;
          this.flag = "link";
          console.log("需要修改名字的关系是：", link);
        }
      }
    },
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

<style>
.el-row {
  margin-bottom: 10px;
}

.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.row-bg {
  padding: 10px 0;
}
.el-button {
  width: 120px;
}

.el-button + .el-button {
  margin-left: 0px;
  margin-top: 10px;
}

.el-form-item {
  margin-bottom: 15px;
}
</style>
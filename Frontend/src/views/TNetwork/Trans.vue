<template>
  <div>
    trans
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
    links: Array
  },
  data() {
    return {
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
    // 节点数量
    id() {
      return this.nodes.length;
    },
    options() {
      return {
        force: this.force,
        size: {
          h: 500,
          w: 500
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
    getData() {
      this.nodes = this.nodes;
      this.links = this.links;
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
    },
    clickNode(e, node) {
      this.$emit("parent", node.id);
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
      // 点击空白处添加节点（因事件节点而起）
      if (
        this.radio === "2" &&
        e.target.localName !== "circle" &&
        e.target.localName !== "path"
      ) {
        this.showNewNodeInfoCard = true;
      } else {
        // 点击空白处取消高亮
        this.selection = {
          nodes: {},
          links: {}
        };

        this.finCoor = getCoordInDocument(e);
        if (this.moveable) {
          this.offset_X += this.finCoor.x - this.staCoor.x;
          this.offset_Y += this.finCoor.y - this.staCoor.y;
        } else {
          this.moveable = true;
        }
        this.staCoor = 0;
        this.finCoor = 0;
      }
    };
    el.onmousemove = e => {
      if (this.moveable) {
        if (this.staCoor) {
          this.finCoor = getCoordInDocument(e);
          this.offset_X += this.finCoor.x - this.staCoor.x;
          this.offset_Y += this.finCoor.y - this.staCoor.y;
          this.staCoor = this.finCoor;
        }
      }
    };

    let onMouseWheel = ev => {
      var ev = ev || window.event;
      var down = true; // 定义一个标志，当滚轮向下滚时，执行一些操作
      down = ev.wheelDelta ? ev.wheelDelta < 0 : ev.detail > 0;
      if (down) {
        if (this.nodeSize > 33.5) {
          this.force = Math.max(0, this.force - 80);
          this.nodeSize = Math.max(0, this.nodeSize - 0.2);
          // this.linkWidth = Math.max(0, this.linkWidth - 0.5);
          this.fontSize = Math.max(0, this.fontSize - 0.1);
        }
      } else {
        this.force = this.force + 80;
        this.nodeSize = this.nodeSize + 0.2;
        this.fontSize = this.fontSize + 0.1;
      }
      if (ev.preventDefault) {
        /*FF 和 Chrome*/
        ev.preventDefault(); // 阻止默认事件
      }
      return false;
    };

    addEvent(el, "mousewheel", onMouseWheel);
    addEvent(el, "DOMMouseScroll", onMouseWheel);

    this.getData();
  }
};
</script>

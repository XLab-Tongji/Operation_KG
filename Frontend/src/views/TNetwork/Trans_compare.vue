<template>
  <div>
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
    ncb(node) {
      if(node.state){
         switch(node.state){
    case "add":
      node._color="green";
      break;
    case "abnormal":
      node._color="red";
      break;
    case "move":
      node._color="blue";
      break;      
    case "delete":
      node._color="black";
      break;           
    // case 5:
    //   node._color="#abdda4";
    //   break;
    // case 6:
    //   node._color="#66c2a5";
    //   break;           
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
    clickNode(e, node) {
      this.$emit("parent", node);
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

<template>
  <div id="chart"></div>
</template>
<script>
import * as d3 from "d3";
import testdata from "./data.json";
import linkdata from "./link.json";
let width = window.innerWidth;
let height = window.innerHeight;
const gap = 10;
var color = d3
  .scaleLinear()
  .domain([0, 5])
  .range(["hsl(152,80%,80%)", "hsl(228,30%,40%)"])
  .interpolate(d3.interpolateHcl);
var color1 = d3
  .scaleLinear()
  .domain([0, 5])
  .range(["hsl(50,80%,80%)", "hsl(150,30%,40%)"])
  .interpolate(d3.interpolateHcl);
var pack = data =>
  d3
    .pack()
    .size([width, height])
    .padding(100)(
    d3
      .hierarchy(data)
      .sum(d => d.value)
      .sort((a, b) => b.value - a.value)
  );
//   //创建一个树状图
//     	var tree = d3.tree()
//     		.size([width-400,height-200])
//     		.separation(function(a,b){
//     			return (a.parent==b.parent?1:2)/a.depth;
//     		})
// var packData = tree(testdata);

// var nodes = pack.nodes(testdata);
// var links = pack.links(nodes)
// console.log(nodes);
// console.log(links);
const lineGenerator = d3
  .line()
  //    获取每个节点的x坐标
  .x(function(d) {
    return d.x;
  })
  //   获取每个节点的y坐标
  .y(function(d) {
    return d.y;
  });
// const pack = data =>
//   d3
//     .pack()
//     .size([width, height])
//     .padding(100)(
//     d3
//       .hierarchy(data)
//       .sum(d => d.value)
//       .sort((a, b) => b.value - a.value)
//   );
export default {
  data() {
    return {
      svg: {},
      node: {},
      label: {},
      data: {},
      root: {},
      focus: {},
      view: {},
      line: {}
    };
  },
  methods: {
    createSvg() {
      this.svg = d3
        .select("#chart")
        .append("svg")
        .attr("viewBox", `-${width / 2} -${height / 2} ${width} ${height}`)
        .style("display", "block")
        .style("margin", "0 -14px")
        .style("background", color(0))
        .style("cursor", "pointer")
        // .data(this.root.descendants().slice(1))
        //       .enter()
        //       .attr("x1", function(d) {
        //       return d.parent.x;
        //       console.log(d.children)
        //       })
        //       .attr("y1", function(d) {
        //       return d.parent.y;
        //       })
        .on("click", d => this.zoom(focus.parent));

      // .data(this.root.descendants().slice(1))
      // .enter()
      // .attr("x1", function(d) {
      // return d.parent.x;
      // console.log(d.children)
      // })
      // .attr("y1", function(d) {
      // return d.parent.y;
      // })

      this.node = this.svg
        .append("g")
        .selectAll("circle")
        .data(this.root.descendants().slice(1))
        .join("circle")
        .attr("fill", d => (d.children ? color(d.depth) : "white"))
        // .attr("fill", color(1))
        .attr("pointer-events", d => (!d.children ? "none" : null))
        .style("fill-opacity", d => (d.parent === this.root ? 1 : 0))
        .style("display", d => (d.parent === this.root ? "inline" : "none"))
        .on(
          "click",
          d => focus !== d && (this.zoom(d), d3.event.stopPropagation())
        );

      this.label = this.svg
        .append("g")
        .style("font", "18px Charlemagne Std")
        .attr("fill", "#000")
        .attr("pointer-events", "none")
        .attr("text-anchor", "middle")
        .selectAll("text")
        .data(this.root.descendants())
        .join("text")
        .style("fill-opacity", d => (d.parent === this.root ? 1 : 0))
        .style("display", d => (d.parent === this.root ? "inline" : "none"))
        .text(d => d.data.name);
      let a = this.root.descendants().slice(1);
      let b = a.map(i => {
        return {
          x: i.x,
          y: i.y,
          d: i.depth
        };
      });
      // console.log(b);
      // console.log(a);
      //pattern 线条数组
      var pat = [];
      var patstart = [];
      var patend = [];
      for (var i = 0; i <= b.length - 1; i++) {
        if (b[i].d == 2) {
          var patt = {
            x: b[i].x,
            y: b[i].y,
            d: b[i].d
          };
          pat.push(patt);
        }
      }
      for (var i = 0; i <= linkdata.length - 1; i++) {
        if (linkdata[i].depth == 2) {
          var idbegin = linkdata[i].begin;
          var idend = linkdata[i].end;
          var pattbegin = {
            x: pat[idbegin].x,
            y: pat[idbegin].y
          };
          let pattend = {
            x: pat[idend].x,
            y: pat[idend].y
          };
          patstart.push(pattbegin);
          patend.push(pattend);
        }
      }

      //entity线条数组
      var en = [];
      var enstart = [];
      var enend = [];
      for (var i = 0; i <= b.length - 1; i++) {
        if (b[i].d == 3) {
          var ent = {
            x: b[i].x,
            y: b[i].y,
            d: b[i].d
          };
          en.push(ent);
        }
      }
      // console.log(en);
      for (var i = 0; i <= linkdata.length - 1; i++) {
        if (linkdata[i].depth == 3) {
          var idbegin = linkdata[i].begin;
          var idend = linkdata[i].end;
          var entbegin = {
            x: en[idbegin].x,
            y: en[idbegin].y
          };
          let entend = {
            x: en[idend].x,
            y: en[idend].y
          };
          enstart.push(entbegin);
          enend.push(entend);
        }
      }

      // transtation 线条数组
      var tra = [];
      var trastart = [];
      var traend = [];

      for (var i = 0; i <= b.length - 1; i++) {
        if (b[i].d == 1) {
          var tras = {
            x: b[i].x,
            y: b[i].y,
            d: b[i].d,
            id: i
          };
          tra.push(tras);
        }
      }
      for (var i = 0; i <= linkdata.length - 1; i++) {
        if (linkdata[i].depth == 1) {
          var idbegin = linkdata[i].begin;
          var idend = linkdata[i].end;
          var trasbegin = {
            x: tra[idbegin].x,
            y: tra[idbegin].y
          };
          let trasend = {
            x: tra[idend].x,
            y: tra[idend].y
          };
          trastart.push(trasbegin);
          traend.push(trasend);
        }
      }

      //定义箭头
      var svg = d3
        .select("body")
        .append("svg")
        .attr("width", width)
        .attr("height", height);

      var defs = svg.append("defs");

      var arrowMarker = defs
        .append("marker")
        .attr("id", "arrow")
        .attr("markerUnits", "strokeWidth")
        .attr("markerWidth", "10")
        .attr("markerHeight", "10")
        .attr("viewBox", "0 0 12 12")
        .attr("refX", "10")
        .attr("refY", "6")
        .attr("orient", "auto");

      var arrow_path = "M2,2 L10,6 L2,10 L6,6 L2,2";

      arrowMarker
        .append("path")
        .attr("d", arrow_path)
        .attr("fill", "black");

      // //transtation 画线
      // for (let i = 0; i <= trastart.length - 1; i++) {
      //   this.line = this.svg
      //     .append("line")
      //     .attr("x1", trastart[0 + i].x - width / 2)
      //     .attr("y1", trastart[0 + i].y - height / 2)
      //     .attr("x2", traend[0 + i].x - width / 2)
      //     .attr("y2", traend[0 + i].y - height / 2)
      //     .attr("stroke", color1(2))
      //     .attr("stroke-width", 2)
      //     .attr("opacity", 1)
      //     .attr("marker-end", "url(#arrow)")
      //     .attr("class", "trans");
      // }

      // //pattern 画线
      // for (let i = 0; i <= patstart.length - 1; i++) {
      //   this.line = this.svg
      //     .append("g")
      //     .append("line")
      //     .attr("x1", patstart[0 + i].x - width / 2)
      //     .attr("y1", patstart[0 + i].y - height / 2)
      //     .attr("x2", patend[0 + i].x - width / 2)
      //     .attr("y2", patend[0 + i].y - height / 2)
      //     .attr("stroke", color1(3))
      //     .attr("stroke-width", 2)
      //     .attr("marker-end", "url(#arrow)");
      // }

      // //entity 画线
      // for (let i = 0; i <= enstart.length - 1; i++) {
      //   this.line = this.svg
      //     .append("line")
      //     .attr("x1", enstart[0 + i].x - width / 2)
      //     .attr("y1", enstart[0 + i].y - height / 2)
      //     .attr("x2", enend[0 + i].x - width / 2)
      //     .attr("y2", enend[0 + i].y - height / 2)
      //     .attr("stroke", color1(1))
      //     .attr("stroke-width", 2)
      //     .attr("marker-end", "url(#arrow)");
      // }

      // this.marker=this.svg
      // .append("g")
      // .attr("id", "resolved")
      // .attr("markerUnits", "strokeWidth")//设置为strokeWidth箭头会随着线的粗细发生变化
      // .attr("markerUnits", "userSpaceOnUse")
      // .attr("viewBox", "0 -5 10 10")//坐标系的区域
      // .attr("refX", 10)//箭头坐标
      // .attr("refY", -1)
      // .attr("markerWidth", 12)//标识的大小
      // .attr("markerHeight", 12)
      // .attr("orient", "auto")//绘制方向，可设定为：auto（自动确认方向）和 角度值
      // .attr("stroke-width", 2)//箭头宽度
      // .append("line")
      // .attr("d", "M0,-5L10,0L0,5")//箭头的路径
      // .attr('fill', '#000000');//箭头颜色
    },

    getData() {
      this.data = testdata;

      this.root = pack(this.data);
      this.focus = this.root;
    },
    zoomTo(v) {
      const k = v[0] < v[1] ? width / v[2] : height / v[2];
      this.view = v;
      this.label.attr("transform", d =>
        d.x && d.y
          ? `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`
          : "translate( 0, 0 )"
      );
      this.node.attr("transform", d =>
        d.x && d.y
          ? `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`
          : "translate( 0, 0 )"
      );
      this.node.attr("r", d => d.r * k);
      // this.node.attr("r", d => d.parent.r);
      this.node.classed("faded", false);
    },
    zoom(d) {
      const focus0 = focus;
      focus = d;
      const transition = this.svg
        .transition()
        .duration(d3.event.altKey ? 7500 : 750)
        .tween("zoom", d => {
          const i = d3.interpolateZoom(this.view, [
            focus.x,
            focus.y,
            focus.r * 2
          ]);
          return t => this.zoomTo(i(t));
        });
      this.label
        .filter(function(d) {
          return d.parent === focus || this.style.display === "inline";
        })
        .transition(transition)
        .style("fill-opacity", d => (d.parent === focus ? 1 : 0))
        .on("start", function(d) {
          if (d.parent === focus) this.style.display = "inline";
        })
        .on("end", function(d) {
          if (d.parent !== focus) this.style.display = "none";
        });
      this.node
        .filter(function(d) {
          return d.parent === focus || this.style.display === "inline";
        })
        .transition(transition)
        .style("fill-opacity", d => (d.parent === focus ? 1 : 0))
        .on("start", function(d) {
          if (d.parent === focus) this.style.display = "inline";
        })
        .on("end", function(d) {
          if (d.parent !== focus) this.style.display = "none";
        });
    }
  },

  mounted() {
    this.getData();
    this.createSvg();
    this.zoomTo([this.root.x, this.root.y, this.root.r * 2]);

    let link = d3.linkHorizontal()({
      source: this.root.descendants()[1],
      target: this.root.descendants()[2]
    });
    console.log(this.root.data.props)
    // console.log(link)
    // this.svg
    //   .append("path")
    //   .attr("d", link)
    //   .attr("stroke", "black")
    //   .attr("fill", "none");
    // console.log(this.root.descendants()[0]);
  }
};
</script>
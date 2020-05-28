let addlist = new Array();
let deletelist = new Array();
let removelist = new Array();
let abnormalNodeTimes = []
let GRAPHSTATUS = 'NORMAL'

function NodeIndex(index, node) {
	this.node = node
	this.index = index
	//两个-1，index 0表示原图谱中位置，
	this.parends = [-1, -1]
}

function NodeTime(id) {
	this.id = id
	this.time = 1
}

function initLists() {
	addlist = []
	deletelist = []
	removelist = []
}

function findSame(f_normalNode, f_abnormalGraph) {
	// console.log(f_normalNode.id)
	let index = -1
	for (let i = 0; i < abnormalNodeTimes.length; i++) {
		if (abnormalNodeTimes[i].id === f_normalNode.id) {
			index = i
			break
		}
	}
	if (index !== -1) {
		abnormalNodeTimes[index].time++

	} else {
		let newNodeTime = new NodeTime(f_normalNode.id)
		abnormalNodeTimes.push(newNodeTime)
		index = abnormalNodeTimes.length - 1

	}
	// console.log(abnormalNodeTimes)
	for (let i = 0; i < f_abnormalGraph.length; i++) {
		if (f_abnormalGraph[i].id === f_normalNode.id && f_abnormalGraph[i].id_count >= abnormalNodeTimes[index].time) {
			abnormalNodeTimes[index].time = f_abnormalGraph[i].id_count
			return i;
		}
	};
	return -1;
}

function compareCyclePart(normalGraph, abnormalGraph) {
	abnormalNodeTimes = []
	for (let index = 0; index < normalGraph.length; index++) {
		const element = normalGraph[index];
		let normalCycleNodes = normalGraph[index].cycle_children

		let findIndex = findSame(element, abnormalGraph)
		if (findIndex != -1) {
			let abnormalCycleNodes = abnormalGraph[findIndex].cycle_children
			compareCycleNodes(normalCycleNodes, abnormalCycleNodes)
			initLists()
		} else {
			if (normalCycleNodes.length !== 0 && normalCycleNodes[0] !== -1) {
				normalCycleNodes.forEach(item => {
					item.type = 'delete'
				})
			}
		}

	}
}

function bfsMatch(normalGraph, abnormalGraph) {
	abnormalNodeTimes = []
	// console.log(normalGraph, abnormalGraph)
	let normalNodeHasVisited = []; //记录节点是否被访问过(存储节点下标)
	let normalQueue = [];//访问的节点入队列(存储节点下标)

	normalNodeHasVisited.push(0);
	normalQueue.push(0);   // 根节点入队列

	while (normalQueue.length != 0) {  // 队列一未空
		let nodeIndex = normalQueue.shift();  //    排出队列一首元素
		if (nodeIndex === -1) continue
		// console.log(normalGraph,nodeIndex)
		let normalChildren = normalGraph[nodeIndex].children;   // 返回normal的子节点
		let normalCycleNodes = normalGraph[nodeIndex].cycle_children

		let findIndex = findSame(normalGraph[nodeIndex], abnormalGraph);  // 找出另一队列中存在的相同元素下标
		// console.log(findIndex)
		if (findIndex != -1) {
			let abnormalChildren = abnormalGraph[findIndex].children; // 返回abnormal的子节点
			// compareCycleNodes(normalCycleNodes, abnormalCycleNodes)
			compareChildNode(normalChildren, abnormalChildren, normalGraph, abnormalGraph, nodeIndex)
		} else {
			let node = new NodeIndex(nodeIndex, normalGraph[nodeIndex])
			deletelist.push(node)
			// 将队列一元素的子节点下标全部列为 delete
			normalChildren.forEach(element => {

				let nodeIndex = new NodeIndex(element, normalGraph[element])
				deletelist.push(nodeIndex);
			})
		}
		// 将子节点重新放入队列后端
		// 添加已访问节点
		normalChildren.forEach(element => {
			if (normalNodeHasVisited.indexOf(element) == -1) {
				normalQueue.push(element);
				normalNodeHasVisited.push(element);
			}
		});
	}
	findRest(abnormalGraph);

}

function compareChildNode(c_normalChildren, c_abnormalChildren, a_normalGraph, a_abnormalGraph, parendIndex) {
	// console.log(c_normalChildren, c_abnormalChildren)
	let normalChildren = [];
	let abnormalChildren = [];
	c_normalChildren.forEach(element => {
		normalChildren.push(a_normalGraph[element]);
	});
	c_abnormalChildren.forEach(element => {
		abnormalChildren.push(a_abnormalGraph[element]);
	});

	// console.log(normalChildren, abnormalChildren)
	// normal与abnormal的差集加入delete，另一个差集加入add
	let deletes = []
	normalChildren.forEach(function (v, index) {
		let exist = 0
		for (let i = 0; i < abnormalChildren.length; i++) {
			if (abnormalChildren[i].id === v.id) {
				exist = 1
				break
			}
		}
		if (!exist) {
			let nodeIndex = new NodeIndex(c_normalChildren[index], v)

			deletes.push(nodeIndex)
		}
	});
	let adds = []
	abnormalChildren.forEach(function (v, index) {
		let exist = 0
		for (let i = 0; i < normalChildren.length; i++) {
			if (normalChildren[i].id === v.id) {
				exist = 1
				break
			}
		}
		if (!exist) {
			let nodeIndex = new NodeIndex(c_abnormalChildren[index], v)
			nodeIndex.parends[0] = parendIndex
			adds.push(nodeIndex)
		}
	});
	// console.log(adds, deletes)
	// 将deletes、adds与deleteList、addList作补集
	deletes.forEach(element => {
		deletelist.push(element);
	})
	adds.forEach(element => {
		addlist.push(element);
	})
}

function compareCycleNodes(normalCycleNodes, abnormalCycleNodes) {
	if ((normalCycleNodes.length === 0 || normalCycleNodes[0] === -1) && (abnormalCycleNodes.length === 0 || normalCycleNodes[0] === -1)) {
		return
	} else {
		compare(normalCycleNodes, abnormalCycleNodes)
		initLists()
	}

}

function findParent(nodeIndex, graph) {
	for (let index = 0; index < graph.length; index++) {
		const element = graph[index];
		if (element.children.indexOf(nodeIndex) != -1) {
			return index
		}
	}
	return -1
}

function findRest(fr_abnormalGraph) {
	// console.log(abnormalNodeTimes)
	fr_abnormalGraph.forEach((element, index) => {
		let exist = 0
		for (let i = 0; i < abnormalNodeTimes.length; i++) {
			if (abnormalNodeTimes[i].id === element.id && element.id_count <= abnormalNodeTimes[i].time) {
				exist = 1
				break
			}
		}
		for (let i = 0; i < addlist.length; i++) {
			if (addlist[i].node.id === element.id && element.id_count === addlist[i].node.id_count) {
				exist = 1
				break
			}
		}
		if (!exist) {
			let nodeIndex = new NodeIndex(index, element)
			nodeIndex.parends[1] = findParent(index, fr_abnormalGraph)
			addlist.push(nodeIndex);
		}
	});
}

let deleteNodeTimes = []
function findRemove(addNode, deletes) {
	// console.log(addNode)
	let index = -1
	for (let i = 0; i < deleteNodeTimes.length; i++) {
		if (deleteNodeTimes[i].id === addNode.id) {
			index = i
			break
		}
	}
	if (index !== -1) {
		deleteNodeTimes[index].time++

	} else {
		let newNodeTime = new NodeTime(addNode.id)
		deleteNodeTimes.push(newNodeTime)
		index = deleteNodeTimes.length - 1

	}
	// console.log(abnormalNodeTimes)
	for (let i = 0; i < deletes.length; i++) {

		if (deletes[i].node.id === addNode.id && deletes[i].node.id_count >= deleteNodeTimes[index].time) {
			deleteNodeTimes[index].time = deletes[i].node.id_count
			return i;
		}
	};
	return -1;
}

function sortByIndex() {
	for (let i = 0; i < addlist.length; i++) {
		for (let j = 0; j < addlist.length - 1 - i; j++) {
			if (addlist[j].index > addlist[j + 1].index) {
				[addlist[j], addlist[j + 1]] = [addlist[j + 1], addlist[j]]
			}
		}
	}

	for (let i = 0; i < deletelist.length; i++) {
		for (let j = 0; j < deletelist.length - 1 - i; j++) {
			if (deletelist[j].index > deletelist[j + 1].index) {
				[deletelist[j], deletelist[j + 1]] = [deletelist[j + 1], deletelist[j]]
			}
		}
	}
}

function distinguishNode() {
	// console.log(addlist,deletelist)
	for (let i = 0; i < addlist.length; i++) {

		let index = findRemove(addlist[i].node, deletelist)
		if (index != -1) {
			removelist.push(deletelist[index])
			deletelist.splice(index, 1)
			addlist.splice(i, 1)
			i = i - 1
		}
	}

}


function generateOffsetGraph(a_normalGraph, a_abnormalGraph) {
	if (GRAPHSTATUS === 'NORMAL' && (addlist != [] || deletelist != [] || removelist != [])) {
		GRAPHSTATUS = 'ABNORMAL'
	}
	let offset = a_normalGraph;
	for (let i = 0; i < deletelist.length; i++) {
		offset[deletelist[i].index].type = 'delete'
		if (offset[deletelist[i].index].cycle_children.length !== 0 && offset[deletelist[i].index].cycle_children[0] !== -1) {
			for (let index = 0; index < offset[deletelist[i].index].cycle_children.length; index++) {
				const element = offset[deletelist[i].index].cycle_children[index];
				element.type = 'delete'

			}
		}
	}
	for (let i = 0; i < removelist.length; i++) {
		offset[removelist[i].index].type = 'remove'
	}
	for (let i = 0; i < addlist.length; i++) {
		let addNode = addlist[i]
		addNode.node.type = 'add'
		addNode.node.children = []
		if (addNode.node.cycle_children.length !== 0 && addNode.node.cycle_children[0] !== -1) {
			for (let index = 0; index < addNode.node.cycle_children.length; index++) {
				const element = addNode.node.cycle_children[index];
				element.type = 'add'
			}
		}
		offset.push(addlist[i].node)
		if (addNode.parends[0] != -1) {
			offset[addNode.parends[0]].children.push(offset.length - 1)

			for (let j = 0; j < addlist.length; j++) {
				const element = addlist[j];
				if (element.parends[1] === addlist[i].index) {
					element.parends[0] = offset.length - 1
				}
			}
		} else {
			offset[0].children.push(offset.length - 1)
			for (let j = 0; j < addlist.length; j++) {
				const element = addlist[j];
				if (element.parends[1] === addlist[i].index) {
					element.parends[0] = offset.length - 1
				}
			}
		}

	}
	return offset


}

//addlist中index对应abnormalGraph
//deletelist中index对应normalGraph
//removelist中index对应normalGraph
function compare(graphA, graphB) {
	compareCyclePart(graphA, graphB)
	bfsMatch(graphA, graphB)
	sortByIndex()
	distinguishNode()
	generateOffsetGraph(graphA, graphB)

}

let newKG = {
	nodes: [],
	links: []
}

function NodesT(name, id) {
	this.name = name
	this.id = id
	this.nodes = []
	this.links = []
}

function NodeP(name, id, state) {
	this.name = name
	this.id = id
	this.state = state
	this.nodes = []
	this.links = []
}

function Links(sid, tid) {
	this.sid = sid
	this.tid = tid
}


function compareTA(compareInfo1,compareInfo2) {
	for (let index = 0; index < compareInfo2.length; index++) {
		const element = compareInfo2[index]
		for (let i = 0; i < compareInfo1.length; i++) {
			const item = compareInfo1[i];
			if (item.name === element.name) {
				compare(item.content, element.content)
				break
			}
		}
	}
}


let existNodes = new Set()

function analysisT(resultKg) {
	for (let index = 0; index < resultKg.length; index++) {
		const element = resultKg[index];
		let newNode = new NodesT(element.name, element.id)

		existNodes.clear()
		analysisP(newNode, element.content)
		for (let i = 0; i < element.children.length; i++) {
			// let newLinks = new Links(index, element.children[i])

			let newLinks = new Links(element.id, resultKg[element.children[i]].id)
			newKG.links.push(newLinks)
		}
		newKG.nodes.push(newNode)
	}
}


function analysisP(node, patterns) {

	for (let index = 0; index < patterns.length; index++) {
		const element = patterns[index];
		if (!existNodes.has(element.id)) {
			existNodes.add(element.id)
			let newNode = new NodeP(element.name, element.id, element.type)
			node.nodes.push(newNode)
		} else if (element.type !== 'normal') {
			for (let i = 0; i < node.nodes.length; i++) {
				const item = node.nodes[i];
				if (item.id === element.id) {
					item.state = element.type
				}
			}
		}

		for (let i = 0; i < element.children.length; i++) {
			const child = element.children[i];
			let newLinks = new Links(element.id, patterns[child].id)
			node.links.push(newLinks)
		}
		if (element.cycle_children[0] === -1) {
			let newLinks = new Links(element.id, element.id)
			node.links.push(newLinks)
		} else if (element.cycle_children !== []) {
			analysisP(node, element.cycle_children)
		}
	}

}

export function compareKG(state1, state2) {
	compareTA(state1.compareInfo, state2.compareInfo)
	analysisT(state1.compareInfo)
	return newKG
}


// compareKG(a.first,a.second)
// console.log(newKG)
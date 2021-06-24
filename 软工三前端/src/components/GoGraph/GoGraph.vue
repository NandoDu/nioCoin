<template>
  <div id="viewport" style="position: relative">
    <div v-if="forceMode" style="height: 100%;width: 100%">
      <div class="nameTag" style="position: absolute;top:8px;left:12px;z-index: 3;">
        <span style="font-size: 15px">{{ currentGraphName }}</span>
      </div>
      <div id="goGraph" style="width: 100%; height: 100%;" />
    </div>
    <div v-else style="height: calc(100vh - 120px);width: 100%;-webkit-overflow-scrolling: touch; overflow-y: scroll; white-space: nowrap; overflow-scrolling: touch">
      <div
          style="display: flex;flex-direction: column;align-items: center;justify-content: center;width: inherit;margin-top: 15px">
        <div>
          <el-table
              :data="nodeList"
              border
              stripe>
            <el-table-column
                label="节点名称"
                prop="text"
                width="300px">
            </el-table-column>
            <el-table-column
                label="颜色"
                prop="color"
                width="300px">
            </el-table-column>
<!--            <el-table-column-->
<!--                label="操作"-->
<!--                width="300px"-->
<!--            >-->
<!--              <template v-slot="action">-->
<!--                <el-button size="mini" @click="modifyEntity(action.row)">修改</el-button>-->
<!--                <el-button type="danger" size="mini" @click="deleteEntity(action.row)">删除</el-button>-->
<!--                <el-dialog-->
<!--                    title="修改实体"-->
<!--                    v-model="modifyNodeVisible"-->
<!--                    width="30%"-->
<!--                >-->
<!--                  <el-input v-model="modifyNode.label"></el-input>-->
<!--                  <template #footer>-->
<!--                    <el-button type="success" small @click="confirmModifyNode">确定</el-button>-->
<!--                    <el-button @click="modifyNodeVisible = false">取消</el-button>-->
<!--                  </template>-->
<!--                </el-dialog>-->
<!--                <el-dialog-->
<!--                    title="删除实体"-->
<!--                    v-model="deleteNodeVisible"-->
<!--                    width="30%"-->
<!--                >-->
<!--                  <span>确定要删除当前实体"{{ modifyNode.label }}"吗?</span>-->
<!--                  <template #footer>-->
<!--                    <el-button type="danger" small @click="confirmDeleteNode">确定</el-button>-->
<!--                    <el-button @click="deleteNodeVisible = false">取消</el-button>-->
<!--                  </template>-->
<!--                </el-dialog>-->
<!--              </template>-->
<!--            </el-table-column>-->
          </el-table>
        </div>
<!--        <div class="addEntity" style="margin-top: 10px">-->
<!--          <el-button type="primary">增加实体</el-button>-->
<!--        </div>-->
        <div style="margin-top: 20px">
          <el-table
              :data="linkList"
              border
              stripe>
            <el-table-column
                label="起始节点"
                prop="from"
                width="225px">
            </el-table-column>
            <el-table-column
                label="连接名字"
                prop="text"
                width="250px">
            </el-table-column>
            <el-table-column
                label="终点节点"
                prop="to"
                width="225px">
            </el-table-column>
<!--            <el-table-column-->
<!--                label="操作"-->
<!--                prop="action"-->
<!--                width="225px">-->
<!--              <template v-slot="action">-->
<!--                <el-button size="mini" @click="modifyRelation(action.row)">修改</el-button>-->
<!--                <el-button type="danger" size="mini" @click="deleteRelation(action.row)">删除</el-button>-->
<!--                <el-dialog-->
<!--                    title="修改关系"-->
<!--                    v-model="modifyRelVisible"-->
<!--                    width="30%"-->
<!--                >-->
<!--                  <el-input v-model="modifyRel.newLabel"></el-input>-->
<!--                  <template #footer>-->
<!--                    <el-button type="success" small @click="confirmModifyRelation">确定</el-button>-->
<!--                    <el-button @click="modifyRelVisible = false">取消</el-button>-->
<!--                  </template>-->
<!--                </el-dialog>-->
<!--                <el-dialog-->
<!--                    title="删除关系"-->
<!--                    v-model="deleteRelVisible"-->
<!--                    width="30%"-->
<!--                >-->
<!--                  <span>确定要删除当前关系"{{ modifyRel.newLabel }}"吗?</span>-->
<!--                  <template #footer>-->
<!--                    <el-button type="danger" small @click="confirmDeleteRelation">确定</el-button>-->
<!--                    <el-button @click="deleteRelVisible = false">取消</el-button>-->
<!--                  </template>-->
<!--                </el-dialog>-->
<!--              </template>-->
<!--            </el-table-column>-->
          </el-table>
        </div>
<!--        <div class="addRelation" style="margin-top: 10px;">-->
<!--          <el-button type="primary">增加关系</el-button>-->
<!--        </div>-->
      </div>
    </div>
    <div class="switch" style="position: absolute;bottom:25px;right:25px;z-index: 2">
      <el-switch
          v-model="forceMode"
          active-text="力导向视图"
          inactive-text="排版视图"
          @change="handleSwitchChange">
      </el-switch>
    </div>
  </div>
</template>

<script>

import {useStore} from "vuex";
import {onMounted, reactive, ref, watch} from "vue";
import {getRandomInt} from "element-plus/es/utils/util.js";
import {search, filter, highlightFill, highlightVisible, noHighlight} from "./features/highlight.js";
import {download} from "./features/saver.js";
import {useRoute, useRouter} from "vue-router";
import {
  ClickFunction,
  CMButton,
  DarkColorButtons,
  FontButton,
  FontSizeButtons,
  LightFillButtons,
  makePort,
  StrokeOptionsButtons
} from "./features/template.js";

export default {
  name: "GoGraph",
  props: {
    graphId: {type: String}
  },
  setup(props) {
    const graphId = props.graphId;
    const route = useRoute();
    const router = useRouter();
    const store = useStore();
    const $ = go.GraphObject.make;

    let forceMode = ref(true);
    let nodeList = ref([]);
    let linkList = ref([]);
    let modifyNodeVisible = ref(false);
    let modifyRelVisible = ref(false);
    let deleteNodeVisible = ref(false);
    let deleteRelVisible = ref(false);

    let modifyNode = reactive({
      actionType: 0,
      graphId: 0,
      nodeId: 0,
      label: ""
    });
    let modifyRel = reactive({
      graphId: 0,
      oldLabel: "",
      newLabel: "",
      actionType: 0,
      node1Label: "",
      node2Label: ""
    });
    let diagram;
    let currentGraphName = ref("");

    const modifyEntity = data => {
      console.log(data);
      modifyNode.actionType = 0;
      modifyNode.graphId = Number(route.params.id);
      modifyNode.nodeId = Number(data.key);
      modifyNode.label = data.text;
      modifyNodeVisible.value = true;
      //console.log(modifyNode)
    };

    const deleteEntity = data => {
      console.log(data);
      modifyNode.actionType = 1;
      modifyNode.graphId = Number(route.params.id);
      modifyNode.nodeId = Number(data.key);
      modifyNode.label = data.text;
      deleteNodeVisible.value = true;
      //console.log(modifyNode)

    };

    const confirmModifyNode = async () => {
      modifyNodeVisible.value = false;
      console.log(modifyNode);
    };

    const confirmDeleteNode = async () => {
      deleteNodeVisible.value = false;
      console.log(modifyNode);
    };

    const modifyRelation = data => {
      console.log(data);
      modifyRel.graphId = Number(route.params.id);
      modifyRel.oldLabel = data.text;
      modifyRel.newLabel = data.text;
      modifyRel.actionType = 0;
      modifyRel.node1Label = data.from;
      modifyRel.node2Label = data.to;
      modifyRelVisible.value = true;
    };

    const deleteRelation = data => {
      console.log(data);
      modifyRel.graphId = Number(route.params.id);
      modifyRel.oldLabel = data.text;
      modifyRel.newLabel = data.text;
      modifyRel.actionType = 1;
      modifyRel.node1Label = data.from;
      modifyRel.node2Label = data.to;
      deleteRelVisible.value = true;
    };

    const confirmModifyRelation = async () => {
      modifyRelVisible.value = false;
      console.log(modifyRel);
    };

    const confirmDeleteRelation = async () => {
      deleteRelVisible.value = false;
    };

    const handleSwitchChange = () => {
      if (forceMode.value) {
        router.go(0);
      } else {
        let tempNodeList = store.getters.draft(graphId).nodeDataArray;
        let tempLinkList = store.getters.draft(graphId).linkDataArray;
        let l, n;
        for (l = 0; l < tempLinkList.length; l++) {
          for (n = 0; n < tempNodeList.length; n++) {
            if (tempNodeList[n].key === tempLinkList[l].from) {
              tempLinkList[l].from = tempNodeList[n].text;
            } else if (tempNodeList[n].key === tempLinkList[l].to) {
              tempLinkList[l].to = tempNodeList[n].text;
            }
          }
        }
        nodeList.value = tempNodeList;
        linkList.value = tempLinkList;
      }
    };

    // function ContinuousForceDirectedLayout() {
    //   go.ForceDirectedLayout.call(this);
    //   this._isObserving = false;
    // }
    // go.Diagram.inherit(ContinuousForceDirectedLayout, go.ForceDirectedLayout);
    //
    // ContinuousForceDirectedLayout.prototype.isFixed = function(v) {
    //   return v.node.isSelected;
    // }
    //
    // // optimization: reuse the ForceDirectedNetwork rather than re-create it each time
    // ContinuousForceDirectedLayout.prototype.doLayout = function(coll) {
    //   if (!this._isObserving) {
    //     this._isObserving = true;
    //     // cacheing the network means we need to recreate it if nodes or links have been added or removed or relinked,
    //     // so we need to track structural model changes to discard the saved network.
    //     var lay = this;
    //     this.diagram.addModelChangedListener(function(e) {
    //       // modelChanges include a few cases that we don't actually care about, such as
    //       // "nodeCategory" or "linkToPortId", but we'll go ahead and recreate the network anyway.
    //       // Also clear the network when replacing the model.
    //       if (e.modelChange !== "" ||
    //           (e.change === go.ChangedEvent.Transaction && e.propertyName === "StartingFirstTransaction")) {
    //         lay.network = null;
    //       }
    //     });
    //   }
    //   var net = this.network;
    //   if (net === null) {  // the first time, just create the network as normal
    //     this.network = net = this.makeNetwork(coll);
    //   } else {  // but on reuse we need to update the LayoutVertex.bounds for selected nodes
    //     this.diagram.nodes.each(function(n) {
    //       var v = net.findVertex(n);
    //       if (v !== null) v.bounds = n.actualBounds;
    //     });
    //   }
    //   // now perform the normal layout
    //   go.ForceDirectedLayout.prototype.doLayout.call(this, coll);
    //   // doLayout normally discards the LayoutNetwork by setting Layout.network to null;
    //   // here we remember it for next time
    //   this.network = net;
    // }
    // // end ContinuousForceDirectedLayout

    function init() {
      console.log("[init] gojs graph");
      diagram = $(go.Diagram, "goGraph",
          {
            padding: 20,
            grid: $(go.Panel, "Grid",
                $(go.Shape, "LineH", {stroke: "lightgray", strokeWidth: 0.5}),
                $(go.Shape, "LineV", {stroke: "lightgray", strokeWidth: 0.5})
            ),
            initialAutoScale: go.Diagram.Uniform,  // an initial automatic zoom-to-fit
            contentAlignment: go.Spot.Center,  // align document to the center of the viewport
            "draggingTool.isGridSnapEnabled": true,
            "draggingTool.dragsLink": true,
            "linkingTool.isUnconnectedLinkValid": true,
            "linkingTool.portGravity": 20,
            "relinkingTool.isUnconnectedLinkValid": true,
            "relinkingTool.portGravity": 20,
            "relinkingTool.fromHandleArchetype":
                $(go.Shape, "Diamond", {
                  segmentIndex: 0,
                  cursor: "pointer",
                  desiredSize: new go.Size(8, 8),
                  fill: "tomato",
                  stroke: "darkred"
                }),
            "relinkingTool.toHandleArchetype":
                $(go.Shape, "Diamond", {
                  segmentIndex: -1,
                  cursor: "pointer",
                  desiredSize: new go.Size(8, 8),
                  fill: "darkred",
                  stroke: "tomato"
                }),
            "linkReshapingTool.handleArchetype":
                $(go.Shape, "Diamond", {desiredSize: new go.Size(7, 7), fill: "lightblue", stroke: "deepskyblue"}),
            handlesDragDropForTopLevelParts: true,
            // mouseDrop: function (e) {
            //   let ok = e.diagram.commandHandler.addTopLevelParts(e.diagram.selection, true);
            //   if (!ok) e.diagram.currentTool.doCancel();
            // },
            "clickCreatingTool.archetypeNodeData": {
              text: "NEW NODE"
            },
            layout:
                $(go.ForceDirectedLayout,
                    {
                      isInitial: true,
                      defaultSpringLength: 50,
                      defaultElectricalCharge: 250,
                      isOngoing: false,
                    }
                ),
            // layout:
            //     $(ContinuousForceDirectedLayout,  // automatically spread nodes apart while dragging
                //     {
                //         isInitial: true,
                //         isOngoing: true
                //     },
                //     { defaultSpringLength: 30, defaultElectricalCharge: 100 }),
            // do an extra layout at the end of a move
            "SelectionMoved": function(e) { e.diagram.layout.invalidateLayout(); },
            "undoManager.isEnabled": true
          }
      );
      diagram.toolManager.draggingTool.doMouseMove = function() {
        go.DraggingTool.prototype.doMouseMove.call(this);
        if (this.isActive) { this.diagram.layout.invalidateLayout(); }
      }

      const nodeSelectionAdornmentTemplate =
          $(go.Adornment, "Auto",
              $(go.Shape, {fill: null, stroke: "deepskyblue", strokeWidth: 1.5, strokeDashArray: [4, 2]}),
              $(go.Placeholder)
          );

      const nodeResizeAdornmentTemplate =
          $(go.Adornment, "Spot",
              {locationSpot: go.Spot.Right},
              $(go.Placeholder),
              $(go.Shape, {
                alignment: go.Spot.TopLeft,
                cursor: "nw-resize",
                desiredSize: new go.Size(6, 6),
                fill: "lightblue",
                stroke: "deepskyblue"
              }),
              $(go.Shape, {
                alignment: go.Spot.Top,
                cursor: "n-resize",
                desiredSize: new go.Size(6, 6),
                fill: "lightblue",
                stroke: "deepskyblue"
              }),
              $(go.Shape, {
                alignment: go.Spot.TopRight,
                cursor: "ne-resize",
                desiredSize: new go.Size(6, 6),
                fill: "lightblue",
                stroke: "deepskyblue"
              }),

              $(go.Shape, {
                alignment: go.Spot.Left,
                cursor: "w-resize",
                desiredSize: new go.Size(6, 6),
                fill: "lightblue",
                stroke: "deepskyblue"
              }),
              $(go.Shape, {
                alignment: go.Spot.Right,
                cursor: "e-resize",
                desiredSize: new go.Size(6, 6),
                fill: "lightblue",
                stroke: "deepskyblue"
              }),

              $(go.Shape, {
                alignment: go.Spot.BottomLeft,
                cursor: "se-resize",
                desiredSize: new go.Size(6, 6),
                fill: "lightblue",
                stroke: "deepskyblue"
              }),
              $(go.Shape, {
                alignment: go.Spot.Bottom,
                cursor: "s-resize",
                desiredSize: new go.Size(6, 6),
                fill: "lightblue",
                stroke: "deepskyblue"
              }),
              $(go.Shape, {
                alignment: go.Spot.BottomRight,
                cursor: "sw-resize",
                desiredSize: new go.Size(6, 6),
                fill: "lightblue",
                stroke: "deepskyblue"
              })
          );

      diagram.nodeTemplate = $(go.Node, "Auto",
          {
            locationSpot: go.Spot.Center, locationObjectName: "SHAPE",
            resizable: true, resizeCellSize: new go.Size(20, 20), resizeAdornmentTemplate: nodeResizeAdornmentTemplate,
            selectable: true, selectionAdornmentTemplate: nodeSelectionAdornmentTemplate
          },
          new go.Binding("location", "location", go.Point.parse).makeTwoWay(go.Point.stringify),
          new go.Binding("desiredSize", "size", go.Size.parse).makeTwoWay(go.Size.stringify),

          $(go.Panel, "Auto",
              { name: "PANEL" },
              $(go.Shape,"Circle",
                  {
                    name: "SHAPE",
                    fill: "white",
                    stroke: "orange",
                    strokeWidth: 2,
                    portId: "", cursor: "pointer",
                    fromLinkable: true, toLinkable: true,
                    fromLinkableDuplicates: true, toLinkableDuplicates: true,
                    // fromSpot: go.Spot.AllSides, toSpot: go.Spot.AllSides
                  },
                  new go.Binding("fill", "", highlightFill).ofObject(),
                  new go.Binding("stroke", "color"),
                  new go.Binding("figure", "figure"),
                  new go.Binding("strokeWidth", "thickness"),
                  new go.Binding("strokeDashArray", "dash"),
                  new go.Binding("visible", "", highlightVisible).ofObject()
              ),
              $(go.TextBlock,
                  {
                    margin: 8,
                    editable: true,
                    font: "24px Courier New, Serif",
                    overflow: go.TextBlock.OverflowEllipsis
                  },
                  new go.Binding("text").makeTwoWay(),
                  new go.Binding("visible", "", highlightVisible).ofObject(),
                  new go.Binding("font")
              ),
              makePort("T", go.Spot.Top, false, true),
              makePort("L", go.Spot.Left, true, true),
              makePort("R", go.Spot.Right, true, true),
              makePort("B", go.Spot.Bottom, true, false)
          ),

          // $(go.Shape,
          //     {
          //       name: "SHAPE",
          //       fill: "white",
          //       strokeWidth: 2,
          //       portId: "", cursor: "pointer",
          //       fromLinkable: true, toLinkable: true,
          //       fromLinkableDuplicates: true, toLinkableDuplicates: true,
          //       fromSpot: go.Spot.AllSides, toSpot: go.Spot.AllSides
          //     },
          //     new go.Binding("fill", "", highlightFill).ofObject(),
          //     new go.Binding("stroke", "color"),
          //     new go.Binding("figure", "figure"),
          //     new go.Binding("strokeWidth", "thickness"),
          //     new go.Binding("strokeDashArray", "dash"),
          //     new go.Binding("visible", "", highlightVisible).ofObject()
          // ),
          // $(go.TextBlock,
          //     {
          //       margin: 8,
          //       editable: true,
          //       font: "24px Courier New, Serif",
          //       overflow: go.TextBlock.OverflowEllipsis
          //     },
          //     new go.Binding("text").makeTwoWay(),
          //     new go.Binding("visible", "", highlightVisible).ofObject(),
          //     new go.Binding("font")
          // ),
          // makePort("T", go.Spot.Top, false, true),
          // makePort("L", go.Spot.Left, true, true),
          // makePort("R", go.Spot.Right, true, true),
          // makePort("B", go.Spot.Bottom, true, false)
      );

      diagram.nodeTemplate.contextMenu =
          $("ContextMenu",
              LightFillButtons(),
              DarkColorButtons(),
              StrokeOptionsButtons(),
              FontSizeButtons()
          );

      diagram.linkTemplate = $(go.Link,
          {curve: go.Link.Bezier},
          {
            relinkableFrom: true,
            relinkableTo: true,
            reshapable: true,
            resegmentable: true
          },
          // new go.Binding("fromSpot", "fromSpot", go.Spot.parse),
          // new go.Binding("toSpot", "toSpot", go.Spot.parse),
          // new go.Binding("fromShortLength", "dir", function (dir) {
          //   return dir === 2 ? 4 : 0;
          // }),
          // new go.Binding("toShortLength", "dir", function (dir) {
          //   return dir >= 1 ? 4 : 0;
          // }),
          {
            mouseEnter: function(e, link) { link.elt(0).stroke = "rgba(0,90,156,0.3)"; },
            mouseLeave: function(e, link) { link.elt(0).stroke = "black"; }
          },
          $(go.Shape,
              {
                strokeWidth: 2
              },
              new go.Binding("stroke", "color"),
              new go.Binding("strokeWidth", "thickness"),
              new go.Binding("strokeDashArray", "dash")
          ),
          // $(go.Shape, {fromArrow: "Backward", strokeWidth: 0, scale: 4 / 3, visible: false},
          //     new go.Binding("visible", "dir", function (dir) {
          //       return dir === 2;
          //     }),
          //     new go.Binding("fill", "color"),
          //     new go.Binding("scale", "thickness", function (t) {
          //       return (2 + t) / 3;
          //     })),
          // $(go.Shape, {toArrow: "Standard", strokeWidth: 0, scale: 4 / 3},
          //     new go.Binding("visible", "dir", function (dir) {
          //       return dir >= 1;
          //     }),
          //     new go.Binding("fill", "color"),
          //     new go.Binding("scale", "thickness", function (t) {
          //       return (2 + t) / 3;
          //     })),
          $(go.TextBlock,
              {
                alignmentFocus: new go.Spot(0, 1, -4, 0),
                editable: true,
                visible: true,
                font: "14px Courier New, Serif"
              },
              new go.Binding("text").makeTwoWay(),
              new go.Binding("stroke", "color"),
              new go.Binding("visible"),
              new go.Binding("font")
          )
      );
      diagram.linkTemplate.selectionAdornmentTemplate =
          $(go.Adornment,
              $(go.Shape,
                  {
                    isPanelMain: true,
                    stroke: "transparent", strokeWidth: 6,
                    pathPattern: makeAdornmentPathPattern(2)
                  },
                  new go.Binding("pathPattern", "thickness", makeAdornmentPathPattern)),
              CMButton({alignmentFocus: new go.Spot(0, 0, -6, -4)})
          );

      function makeAdornmentPathPattern(w) {
        return $(go.Shape,
            {
              stroke: "dodgerblue", strokeWidth: 2, strokeCap: "square",
              geometryString: "M0 0 M4 2 H3 M4 " + (w + 4).toString() + " H3"
            });
      }

      function ArrowButton(num) {
        let geo = "M0 0 M16 16 M0 8 L16 8  M12 11 L16 8 L12 5";
        if (num === 0) {
          geo = "M0 0 M16 16 M0 8 L16 8";
        } else if (num === 2) {
          geo = "M0 0 M16 16 M0 8 L16 8  M12 11 L16 8 L12 5  M4 11 L0 8 L4 5";
        }
        return $(go.Shape,
            {
              geometryString: geo,
              margin: 2, background: "transparent",
              mouseEnter: function (e, shape) {
                shape.background = "dodgerblue";
              },
              mouseLeave: function (e, shape) {
                shape.background = "transparent";
              },
              click: ClickFunction("dir", num), contextClick: ClickFunction("dir", num)
            });
      }

      function AllSidesButton(to) {
        let setter = function (e, shape) {
          e.handled = true;
          e.diagram.model.commit(function (m) {
            let link = shape.part.adornedPart;
            m.set(link.data, (to ? "toSpot" : "fromSpot"), go.Spot.stringify(go.Spot.AllSides));
            (to ? link.toNode : link.fromNode).invalidateConnectedLinks();
          });
        };
        return $(go.Shape,
            {
              width: 12, height: 12, fill: "transparent",
              mouseEnter: function (e, shape) {
                shape.background = "dodgerblue";
              },
              mouseLeave: function (e, shape) {
                shape.background = "transparent";
              },
              click: setter, contextClick: setter
            });
      }

      function SpotButton(spot, to) {
        let ang = 0;
        let side = go.Spot.RightSide;
        if (spot.equals(go.Spot.Top)) {
          ang = 270;
          side = go.Spot.TopSide;
        } else if (spot.equals(go.Spot.Left)) {
          ang = 180;
          side = go.Spot.LeftSide;
        } else if (spot.equals(go.Spot.Bottom)) {
          ang = 90;
          side = go.Spot.BottomSide;
        }
        if (!to) ang -= 180;
        let setter = function (e, shape) {
          e.handled = true;
          e.diagram.model.commit(function (m) {
            let link = shape.part.adornedPart;
            m.set(link.data, (to ? "toSpot" : "fromSpot"), go.Spot.stringify(side));
            (to ? link.toNode : link.fromNode).invalidateConnectedLinks();
          });
        };
        return $(go.Shape,
            {
              alignment: spot, alignmentFocus: spot.opposite(),
              geometryString: "M0 0 M12 12 M12 6 L1 6 L4 4 M1 6 L4 8",
              angle: ang,
              background: "transparent",
              mouseEnter: function (e, shape) {
                shape.background = "dodgerblue";
              },
              mouseLeave: function (e, shape) {
                shape.background = "transparent";
              },
              click: setter, contextClick: setter
            });
      }

      diagram.linkTemplate.contextMenu =
          $("ContextMenu",
              DarkColorButtons(),
              StrokeOptionsButtons(),
              $("ContextMenuButton",
                  $(go.Panel, "Horizontal",
                      ArrowButton(0), ArrowButton(1), ArrowButton(2)
                  )
              ),
              $("ContextMenuButton",
                  $(go.Panel, "Horizontal",
                      $(go.Panel, "Spot",
                          AllSidesButton(false),
                          SpotButton(go.Spot.Top, false), SpotButton(go.Spot.Left, false), SpotButton(go.Spot.Right, false), SpotButton(go.Spot.Bottom, false)
                      ),
                      $(go.Panel, "Spot",
                          {margin: new go.Margin(0, 0, 0, 2)},
                          AllSidesButton(true),
                          SpotButton(go.Spot.Top, true), SpotButton(go.Spot.Left, true), SpotButton(go.Spot.Right, true), SpotButton(go.Spot.Bottom, true)
                      )
                  )
              ),
              $("ContextMenuButton",
                  $(go.Panel, "Vertical",
                      FontButton("10px", "10px Courier New, Serif"),
                      FontButton("12px", "12px Courier New, Serif"),
                      FontButton("14px", "14px Courier New, Serif"),
                      FontButton("16px", "16px Courier New, Serif"),
                      FontButton("18px", "18px Courier New, Serif"),
                      FontButton("20px", "20px Courier New, Serif")
                  )
              )
          );

      diagram.addModelChangedListener(e => {
        if (e.isTransactionFinished) {
          store.commit("backDraft", {graphId});
        }
      });

      diagram.addDiagramListener("ChangedSelection", e => {
        if (e.diagram.selection.count === 1) {
          const data = e.diagram.selection.first().data;
          if (data.hasOwnProperty("key")) {
            store.commit("setAttrs", {graphId, attrs: data});
          } else {
            store.commit("setAttrs", {graphId, attrs: null});
          }
        } else {
          store.commit("setAttrs", {graphId, attrs: null});
        }
      });

      store.commit("diagramFn", {graphId, diagram});
      store.commit("toInternal", {graphId});
      diagram.model.makeUniqueKeyFunction = (_model, _data) => {
        console.log("[hack] good luck");
        return String(getRandomInt(1000000000));
      };
      diagram.model.copiesKey = false;

      document.getElementById("download").addEventListener("click", () => {
        console.log("download");
        download(diagram)
      });
      document.getElementById("download-in-side-bar").addEventListener("click", () => download(diagram));
      if (store.getters.needLayout(graphId)) {
        layout();
        store.commit("setNeedLayout", {graphId, value: false});
        store.dispatch("save", {graphId});
      }
    }

    const getGraphName = async () => {
      await store.dispatch("getGraphName", {
        userId: store.getters.userInfo.id,
        graphId: route.params.id
      }).then((res) => {
        currentGraphName.value = res;
      });
    };

    onMounted(() => {
      getGraphName();
      init();
    });

    watch(() => store.getters.textHide(graphId), () => {
      for (let nit = diagram.links; nit.next();) {
        let link = nit.value.data;
        diagram.model.setDataProperty(link, "visible", !store.getters.textHide(graphId));
      }
    });

    watch(() => store.getters.attrChanged(graphId), (cur, _) => {
      if (cur) {
        store.commit("setAttrChanged", {graphId, value: false});
        store.commit("backDraft", {graphId});
      }
    });

    const layout = () => {
      diagram.layout.doLayout(diagram);
      store.commit("setNeedLayout", {graphId, value: false});
    };

    return {
      init,
      forceMode,
      nodeList,
      linkList,
      handleSwitchChange,
      search: query => search(diagram, query),
      filter: query => filter(diagram, query),
      noHighlight: () => noHighlight(diagram),
      layout,
      currentGraphName,
      modifyEntity,
      deleteEntity,
      confirmModifyNode,
      confirmDeleteNode,
      modifyRelation,
      deleteRelation,
      confirmModifyRelation,
      confirmDeleteRelation,
      modifyNodeVisible,
      modifyRelVisible,
      modifyNode,
      modifyRel,
      deleteNodeVisible,
      deleteRelVisible
    };
  }

};
</script>

<style scoped src="./GoGraph.less" />

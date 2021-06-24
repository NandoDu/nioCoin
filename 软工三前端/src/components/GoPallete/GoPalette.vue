<template>
  <div id="goPalette" class="node-style" style="width: 100%;height: 300px;padding-top: 10px;"></div>
</template>

<script>
import {onMounted} from "vue";

export default {
  name: "GoPalette",
  setup() {
    const $ = go.GraphObject.make;

    // onMounted(() => setTimeout(init, 1000));
    onMounted(init);
    
    function init() {
      let palette = new go.Palette("goPalette");
      const fill1 = "rgb(105,210,231)";
      const brush1 = "rgb(65,180,181)";

      const fill2 = "rgb(167,219,216)";
      const brush2 = "rgb(127,179,176)";

      const fill3 = "rgb(224,228,204)";
      const brush3 = "rgb(184,188,164)";

      const fill4 = "rgb(243,134,48)";
      const brush4 = "rgb(203,84,08)";
      palette.nodeTemplateMap.add("", $(go.Node, "Auto",
          {
            locationSpot: go.Spot.Center, locationObjectName: "SHAPE",
            resizable: true, resizeCellSize: new go.Size(20, 20)
          },
          new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
          new go.Binding("desiredSize", "size", go.Size.parse).makeTwoWay(go.Size.stringify),
          $(go.Shape,
              {
                name: "SHAPE",
                fill: "white",
                strokeWidth: 2,
                portId: "", cursor: "pointer",
                fromLinkable: true, toLinkable: true,
                fromLinkableDuplicates: true, toLinkableDuplicates: true,
                fromSpot: go.Spot.AllSides, toSpot: go.Spot.AllSides
              },
              new go.Binding("fill", "fill"),
              new go.Binding("stroke", "stroke"),
              new go.Binding("figure", "figure")
          ),
          $(go.TextBlock,
              {
                margin: 8,
                editable: true,
                font: "18px Courier New, Serif",
                overflow: go.TextBlock.OverflowEllipsis
              },
              new go.Binding("text").makeTwoWay()
          )
          )
      );
      palette.model = new go.GraphLinksModel([
        {text: "", figure: "Square"},
        {text: "", figure: "Triangle"},
        {text: "", figure: "Diamond"},
        {text: "", figure: "Circle"}
      ]);
    }

    return {
      init
    };
  }
};
</script>

<style scoped>

</style>

<template>
  <div id="edgePalette" class="edge-style" style="width: 100%;height: 150px;padding-top: 10px;margin-top: 20px"></div>
</template>

<script>
import {onMounted} from "vue";

export default {
  name: "EdgePalette",
  setup() {
    const $ = go.GraphObject.make;
    // onMounted(() => setTimeout(init, 1000));
    onMounted(init);

    function init() {
      let palette = new go.Palette("edgePalette");
      palette.linkTemplate = $(go.Link,
          {
            routing: go.Link.AvoidsNodes, corner: 10,
            relinkableFrom: true,
            relinkableTo: true,
            reshapable: true,
            resegmentable: true
          },
          new go.Binding("fromSpot", "fromSpot", go.Spot.parse),
          new go.Binding("toSpot", "toSpot", go.Spot.parse),
          new go.Binding("fromShortLength", "dir", function (dir) {
            return dir === 2 ? 4 : 0;
          }),
          new go.Binding("toShortLength", "dir", function (dir) {
            return dir >= 1 ? 4 : 0;
          }),
          new go.Binding("points").makeTwoWay(),  // TwoWay due to user reshaping with LinkReshapingTool
          $(go.Shape,
              {
                strokeWidth: 2
              },
              new go.Binding("stroke", "color"),
              new go.Binding("strokeWidth", "thickness"),
              new go.Binding("strokeDashArray", "dash")
          ),
          $(go.Shape, {fromArrow: "Backward", strokeWidth: 0, scale: 4 / 3, visible: false},
              new go.Binding("visible", "dir", function (dir) {
                return dir === 2;
              }),
              new go.Binding("fill", "color"),
              new go.Binding("scale", "thickness", function (t) {
                return (2 + t) / 3;
              })),
          $(go.Shape, {toArrow: "Standard", strokeWidth: 0, scale: 4 / 3},
              new go.Binding("visible", "dir", function (dir) {
                return dir >= 1;
              }),
              new go.Binding("fill", "color"),
              new go.Binding("scale", "thickness", function (t) {
                return (2 + t) / 3;
              })),
          $(go.TextBlock,
              {alignmentFocus: new go.Spot(0, 1, -4, 0), editable: true},
              new go.Binding("text").makeTwoWay(),  // TwoWay due to user editing with TextEditingTool
              new go.Binding("stroke", "color")
          )
      );
      palette.model = new go.GraphLinksModel([],
          [
            {points: new go.List(/*go.Point*/).addAll([new go.Point(0, 0), new go.Point(120, 0)])},
            {points: new go.List(/*go.Point*/).addAll([new go.Point(0, 0), new go.Point(120, 0)]), dash: [2, 4]},
            {points: new go.List(/*go.Point*/).addAll([new go.Point(0, 0), new go.Point(120, 0)]), dash: [4, 4]},
            {points: new go.List(/*go.Point*/).addAll([new go.Point(0, 0), new go.Point(120, 0)]), thickness: 3},
            {points: new go.List(/*go.Point*/).addAll([new go.Point(0, 0), new go.Point(120, 0)]), thickness: 4}
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

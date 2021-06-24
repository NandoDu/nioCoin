const $ = go.GraphObject.make;

// create a button that brings up the context menu
function CMButton(options) {
    return $(go.Shape,
        {
            fill: "orange", stroke: "gray", background: "transparent",
            geometryString: "F1 M0 0 M0 4h4v4h-4z M6 4h4v4h-4z M12 4h4v4h-4z M0 12",
            isActionable: true, cursor: "context-menu",
            click: function (e, shape) {
                e.diagram.commandHandler.showContextMenu(shape.part.adornedPart);
            }
        },
        options || {});
}

// Common context menu button definitions

// All buttons in context menu work on both click and contextClick,
// in case the user context-clicks on the button.
// All buttons modify the node data, not the Node, so the Bindings need not be TwoWay.

// A button-defining helper function that returns a click event handler.
// PROPNAME is the name of the data property that should be set to the given VALUE.
function ClickFunction(propname, value) {
    return function (e, obj) {
        e.handled = true;  // don't let the click bubble up
        e.diagram.model.commit(function (m) {
            m.set(obj.part.adornedPart.data, propname, value);
        });
    };
}

function FontButton(size, font, propname) {
    if (!propname) propname = "font";
    return $(go.TextBlock,
        {
            text: size,
            font: font,
            margin: 2,
            mouseEnter: function (e, shape) {
                shape.stroke = "dodgerblue";
            },
            mouseLeave: function (e, shape) {
                shape.stroke = "black";
            },
            click: ClickFunction(propname, font), contextClick: ClickFunction(propname, font)
        }
    );

}

function makePort(name, spot, output, input) {
    // the port is basically just a small transparent circle
    return $(go.Shape, "Circle",
        {
            fill: null,  // not seen, by default; set to a translucent gray by showSmallPorts, defined below
            stroke: null,
            desiredSize: new go.Size(7, 7),
            alignment: spot,  // align the port on the main Shape
            alignmentFocus: spot,  // just inside the Shape
            portId: name,  // declare this object to be a "port"
            fromSpot: spot, toSpot: spot,  // declare where links may connect at this port
            fromLinkable: output, toLinkable: input,  // declare whether the user may draw links to/from here
            cursor: "pointer"  // show a different cursor to indicate potential link point
        });
}

// Create a context menu button for setting a data property with a color value.
function ColorButton(color, propname) {
    if (!propname) propname = "color";
    return $(go.Shape,
        {
            width: 16, height: 16, stroke: "lightgray", fill: color,
            margin: 1, background: "transparent",
            mouseEnter: function (e, shape) {
                shape.stroke = "dodgerblue";
            },
            mouseLeave: function (e, shape) {
                shape.stroke = "lightgray";
            },
            click: ClickFunction(propname, color), contextClick: ClickFunction(propname, color)
        });
}

function LightFillButtons() {  // used by multiple context menus
    return [
        $("ContextMenuButton",
            $(go.Panel, "Horizontal",
                ColorButton("white", "fill"),
                ColorButton("beige", "fill"),
                ColorButton("aliceblue", "fill"),
                ColorButton("lightyellow", "fill")
            )
        ),
        $("ContextMenuButton",
            $(go.Panel, "Horizontal",
                ColorButton("lightgray", "fill"),
                ColorButton("lightgreen", "fill"),
                ColorButton("lightblue", "fill"),
                ColorButton("pink", "fill")
            )
        )
    ];
}

function DarkColorButtons() {  // used by multiple context menus
    return [
        $("ContextMenuButton",
            $(go.Panel, "Horizontal",
                ColorButton("black"), ColorButton("green"), ColorButton("blue"), ColorButton("red")
            )
        ),
        $("ContextMenuButton",
            $(go.Panel, "Horizontal",
                ColorButton("brown"), ColorButton("magenta"), ColorButton("purple"), ColorButton("orange")
            )
        )
    ];
}

function FontSizeButtons() {
    return [
        $("ContextMenuButton",
            $(go.Panel, "Vertical",
                FontButton("12px", "12px Courier New, Serif"),
                FontButton("14px", "14px Courier New, Serif"),
                FontButton("16px", "16px Courier New, Serif"),
                FontButton("18px", "18px Courier New, Serif"),
                FontButton("20px", "20px Courier New, Serif"),
                FontButton("22px", "22px Courier New, Serif"),
                FontButton("24px", "24px Courier New, Serif"),
                FontButton("26px", "26px Courier New, Serif")
            )
        )
    ];
}

// Create a context menu button for setting a data property with a stroke width value.
function ThicknessButton(sw, propname) {
    if (!propname) propname = "thickness";
    return $(go.Shape, "LineH",
        {
            width: 16, height: 16, strokeWidth: sw,
            margin: 1, background: "transparent",
            mouseEnter: function (e, shape) {
                shape.background = "dodgerblue";
            },
            mouseLeave: function (e, shape) {
                shape.background = "transparent";
            },
            click: ClickFunction(propname, sw), contextClick: ClickFunction(propname, sw)
        });
}

// Create a context menu button for setting a data property with a stroke dash Array value.
function DashButton(dash, propname) {
    if (!propname) propname = "dash";
    return $(go.Shape, "LineH",
        {
            width: 24, height: 16, strokeWidth: 2,
            strokeDashArray: dash,
            margin: 1, background: "transparent",
            mouseEnter: function (e, shape) {
                shape.background = "dodgerblue";
            },
            mouseLeave: function (e, shape) {
                shape.background = "transparent";
            },
            click: ClickFunction(propname, dash), contextClick: ClickFunction(propname, dash)
        });
}

function StrokeOptionsButtons() {  // used by multiple context menus
    return [
        $("ContextMenuButton",
            $(go.Panel, "Horizontal",
                ThicknessButton(1),
                ThicknessButton(2),
                ThicknessButton(3),
                ThicknessButton(4)
            )
        ),
        $("ContextMenuButton",
            $(go.Panel, "Horizontal",
                DashButton(null),
                DashButton([2, 4]),
                DashButton([4, 4])
            )
        )
    ];
}

export {
    CMButton,
    FontButton,
    ClickFunction,
    makePort,
    ColorButton,
    LightFillButtons,
    DarkColorButtons,
    FontSizeButtons,
    ThicknessButton,
    DashButton,
    StrokeOptionsButtons
};

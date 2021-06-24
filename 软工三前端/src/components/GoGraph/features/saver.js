function downloadPNG(blob) {
    const url = window.URL.createObjectURL(blob);
    const filename = "kg.png";

    let a = document.createElement("a");
    a.style.display = "none";
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    requestAnimationFrame(function () {
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    });
}

function download(diagram) {
    diagram.makeImageData({background: "white", returnType: "blob", callback: downloadPNG});
}

export {
    download
};

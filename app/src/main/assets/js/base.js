window.onload = function () {
    //璁剧疆瀛椾綋澶у皬
    var hTML = document.documentElement;
    var dWidth = hTML.getBoundingClientRect().width;
    hTML.style.fontSize = dWidth / 15 + "px";
    $("body").show();
}
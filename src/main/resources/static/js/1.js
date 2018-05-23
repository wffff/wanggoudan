var time;
function begin() {
    document.getElementById("btnBegin").disabled = true;
    layui.use('jquery', function(){
        var $=layui.jquery;
        $.ajaxSetup({
            async : false
        });
        var names=new Array();
        $.post({url:'mobile/findAll'}, function(d) {
            for (var i=0;i<d.data.length;i++){
                names.push(d.data[i].number);
            }
           chouqian(names);
        });
    });
}
// var _setTimeout=setTimeout;
// window.setTimeout=function(callback,time,params){
//     var param=Array.prototype.slice.call(arguments,2);
//     var _callback=function(){
//         callback.apply(null,param);
//     }
//     _setTimeout(_callback,time);
// }

function chouqian(names) {
    var index = parseInt(Math.random() * names.length);
    var name = names[index];
    document.getElementById("result").innerHTML = name;
    time=window.setTimeout(chouqian,time,names);
    document.getElementById("btnEnd").disabled = false;
}

function close() {
    document.body.style.backgroundSize = "100% 100%";
    document.body.style.backgroundImage = "url(https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1486921284912&di=34e9c8aa75d3c031a48637dd03f014a1&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F12%2F28%2F96%2F21Y58PICFrH.jpg)";
    document.getElementById("btnEnd").disabled = true;
}

function end() {
    window.clearTimeout(time);
    document.getElementById("jieguo").innerHTML += "恭喜手机号码为:" + document.getElementById("result").innerHTML + "的用户<br/>获得了本次缤纷乐园提供的99朵玫瑰花~!<br/>";
    document.getElementById("btnEnd").disabled = true;
}

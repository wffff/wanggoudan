layui.use(['jquery', 'table', 'form'], function () {
    var $ = layui.jquery;
    var table = layui.table;
    var form = layui.form;
    table.render({
        elem: '#mobile'
        , id: 'mobile'
        , url: '/mobile/findAll'
        , cols: [[
            {checkbox: true}
            , {field: 'id', title: 'ID', width: '100', align: 'center'}
            , {field: 'number', title: '手机号', width: '448', align: 'center'}
        ]]
        , width: 600
        , height: 600
    });

    var active = {
        add: function () { //获取选中数据
            var checkStatus = table.checkStatus('mobile'), data = checkStatus.data;
            $('#add input[name=number]').val("");
            layer.open({
                type: 1
                , title: ['添加手机号', 'font-size:18px;']
                , closeBtn: 1
                , area: ['450px', '300px']
                , shade: 0.8    //背景透明
                , maxmin: true  //最大最小化
                , anim: 5       //渐显
                , id: 'LAY_layui_add' //设定一个id，防止重复弹出
                , content: $('#add')
                , resize: true
                , zIndex: layer.zIndex, //重点1
                success: function (layero) {
                    layer.setTop(layero); //重点2
                }
            });
        }
        , del: function () { //获取选中数目
            var checkStatus = table.checkStatus('mobile'), data = checkStatus.data;
            if (data.length > 0) {
                var id = "";
                for (var i = 0; i < data.length; i++) {
                    id += data[i].id;
                    if (i !== data.length - 1) {
                        id += ",";
                    }
                }
                layer.confirm('是否删除?', {icon: 3, title: '警告', zIndex: layer.zIndex}, function (index) {
                    $.ajax({
                        url: '/mobile/del',
                        data: {id: id},
                        success: function (data, status) {
                            if (data.code >= 0) {
                                layer.closeAll();
                                layer.msg("删除成功",{time:400}, function () {
                                    layer.closeAll();
                                    table.reload('mobile', {});
                                });
                            } else {
                                layer.msg(data.msg, {time: 2000});
                            }
                        }
                    });
                });
            }
        }
        , chouqian: function () { //验证是否全选
           window.location.href="/chouqian";
        }
    };
    form.on('submit(add)', function (data) {
        var or = data.field;
        $.post({
            url: 'mobile/create',
            data: or,
            success: function (data, status) {
                if (data.code >= 0) {
                    layer.closeAll();
                    layer.msg("添加成功",{time:400},function () {
                        layer.closeAll();
                        table.reload('mobile', {});
                    });
                } else {
                    layer.msg(data.msg, {time: 2000});
                }
            }
        });
    });
    $('.layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

});

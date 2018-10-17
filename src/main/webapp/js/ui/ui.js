/**
 * Created by xiaogang on 2016/8/11.
 * 模块代码移植于颜老项目
 */
"use strict";
define(function (require) {
    var _this = null;
    /**
     * 每个模块对外只暴露一个函数，通过获取用户的调用函数名和参数，模块内部去调用相应的函数
     * funcName:用户调用的函数名称
     * params:用户调用的入参（只接受对象格式）
     */
    var exports = function (funcName, params) {
        _this = this;
        if (typeof funcName === "string") {
            this.loadJsCssFile({
                fileName: this.moduleUrl + "component/ui/ui.css",
                callback: function (data) {
                    _this.ui = _ui;
                    _ui[funcName](params);
                }
            });
        } else {
            return;
        }

    };

    //纯js实现的function
    var _ui = {};
    /**
     * 显示H5加载动画
     * @param msg
     */
    _ui.loading=function () {
        if(!$("#ui-loading").length){
            $('body').append('<div class="up-ui-loading" id="ui-loading"></div>');
        }
        scrollDisable();
        $("#ui-loading").show();
    }
    /**
     * 隐藏H5加载动画
     */
    _ui.hideLoading = function () {
        // $('#commonUILoading').hide();
        // $('body').removeClass('commonUI-overflow');
        scrollAble();
        $("#ui-loading").hide();
    };
    /**
     * ui-toast
     * @param params
     */
    _ui.toast = function (params) {
        if (typeof params === "string") {
            params = {msg: params};
        }
        var toastTimer = null, time = params.time || 3000;
        if (!$("#ui-toast").length) {
            $('body').append('<div class="up-ui-toast" id="ui-toast"></div>');
            scrollDisable();
        }
        if (params.mask) {
            (!$(".ui-mask").length) && $('body').append('<div class="ui-mask"></div>');
        }
        var _node = $("#ui-toast");
        if (params.title) {
            _node.html('<p class="title">' + params.title + '</p><p>' + params.msg + '</p>');
        } else {
            _node.text(params.msg);
        }

        // 动画渐显
        setTimeout(function () {
            _node.show().removeClass('fadeOut').addClass('fadeIn');
        },0);


        clearTimeout(toastTimer);
        // 动画渐隐
        toastTimer = setTimeout(function () {
            _node.removeClass('fadeIn').addClass('fadeOut');
            scrollAble();
            setTimeout(function () {
                _node.hide();
                $(".ui-mask").hide();
            }, 1000);
        }, time / 2);
    };
    /**
     *
     * @param params={
     *      title:"title",
     *      msg:"msg",
     *      btns:[{
     *          msg:"cancel",
     *          callback:function(data){
     *
     *          }
     *      },{
     *          msg:"ok",
     *          callback:function(data){
     *
     *          }
     *      }]
     *
     * }
     */
    _ui.alert = function (params) {
        //参数预处理
        if (typeof params === "string") {
            params = {
                msg: params,
                btns: [{
                    msg: "确定"
                }]
            };
        } else if (!params.btns) {
            params.btns = [{
                msg: "确定"
            }]
        }else if(!_this.isArray(params.btns)){
            params.btns=[params.btns];
        }
        //渲染组件
        if (!$("#ui-alert").length) {
            $("body").append('<div class="up-ui-alert" id="ui-alert"><div class="msg"></div><div class="buttons"></div></div>');
        }
        var _node = $("#ui-alert");
        if (params.title) {
            _node.children('.msg').html('<p class="title">' + params.title + '</p><p>' + params.msg + '</p>');
        } else {
            _node.children('.msg').text(params.msg);
        }
        if (params.btns.length > 1) {
            _node.children('.buttons').html('<div class="btn" data-code="0" style="width: 50%;">' + params.btns[0].msg + '</div><div class="btn" data-code="1" style="width: 50%;">' + params.btns[1].msg + '</div>');
        } else {
            _node.children('.buttons').html('<div class="btn" data-code="0">' + params.btns[0].msg + '</div>');
        }
        scrollDisable();
        (!$(".ui-mask").length) && $('body').append('<div class="ui-mask"></div>');
        $(".ui-mask").show();

        //延迟 600 ，以等待dom 和相关样式的渲染（因为默认是隐藏的）
        setTimeout(function () {
            _node.show().off().on('click', '.btn', function () {
                console.log($(this).data("code"));
                $(".ui-mask").hide();
                _node.hide();
                scrollAble();
                _this.successCallback(params.btns[$(this).data("code") || 0].callback);
            })
        },0);

    }
    function scrollDisable() {
        $('body').addClass("overflow")
    }
    function scrollAble() {
        $('body').removeClass("overflow")
    }
    return exports;
});

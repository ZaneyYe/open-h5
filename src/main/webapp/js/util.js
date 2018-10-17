/**
 * 界面 ui demo
 */
window.up = window.up || {};

(function () {
    var _this = this;
    this.successCallback = function (callback, data, msg) {
        this.isFunction(callback) && callback(data || {}, 100, msg || "success");
    };
    /**
     * 规范回调函数的返回code
     * @param callback
     * @param data
     * @param code ：不建议修改
     * @param msg
     */
    this.errorCallback = function (callback, data, msg, code) {
        this.isFunction(callback) && callback(data || {}, code || 0, msg || "error");
    }
    //ajax
    this.ajax = function (params) {
        var url = '';
        params.data = params.data || {};
        var _params = {
            type: params.type || "post",//默认post请求
            url: url,
            dataType: params.dataType || "json",//默认json返回
            contentType: "application/json",
            data: JSON.stringify(params.data || {}),
            success: function (data) {
                // _this.ui && _this.ui.hideLoading();
                console.log("success");
                if (data && data.code == 10000) {
                    _this.successCallback(params.callback, data);
                } else {
                    _this.errorCallback(params.callback, data || {
                            "success": false,
                            "code": 0,
                            "message": "没有返回值",
                            "result": {}
                        }, "没有返回值", 99);
                }
            },
            error: function (xhr, errorType, error) {
                if (errorType === "abort") { //无网络
                    console.log("网络已断开");
                } else if (errorType === "timeout") { //超时
                    console.log("系统连接超时");
                } else if (errorType === "error") { //服务器或者客户端错误
                    switch (xhr.status) {
                        case 403:
                            console.log("服务器禁止访问");
                            break;
                        case 404:
                            console.log("未找到服务器");
                            break;
                        case 500:
                            console.log("服务器未响应");
                            break;
                        case 503:
                            console.log("服务器不可用");
                            break;
                        case 504:
                            console.log("网关超时");
                            break;
                    }
                    _this.errorCallback(params.callback, {
                        xhr: xhr,
                        errorType: errorType,
                        error: error
                    }, error, xhr && xhr.status || -1);
                } else {
                    _this.errorCallback(params.callback, {
                        xhr: xhr,
                        errorType: errorType,
                        error: error
                    });
                }
            }
        };
        (function () {
            if (params.loading) {
                _this.ui.loading();
            }
            $.ajax(_params);
        })();
    };
}).call(window.up);

(function(){
    //纯js实现的function
    this.ui = {};
    /**
     * 显示H5加载动画
     * @param msg
     */
    this.ui.loading=function () {
        if(!$("#ui-loading").length){
            $('body').append('<div class="up-ui-loading" id="ui-loading"><div class="loading-icon"><img src="../img/loading_circle.gif"></div></div>');
        }
        $("#ui-loading").show();
    }
    /**
     * 隐藏H5加载动画
     */
    this.ui.hideLoading = function () {
        // $('#commonUILoading').hide();
        // $('body').removeClass('commonUI-overflow');
        $("#ui-loading").hide();
    };
    /**
     * ui-toast
     * @param params
     */
    this.ui.toast = function (params) {
        if (typeof params === "string") {
            params = {msg: params};
        }
        var toastTimer = null, time = params.time || 3000;
        if (!$("#ui-toast").length) {
            $('body').append('<div class="up-ui-toast" id="ui-toast"></div>');
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
            setTimeout(function () {
                _node.hide();
                $(".ui-mask").hide();
            }, 1000);
        }, time / 2);
    };
}).call(window.up);

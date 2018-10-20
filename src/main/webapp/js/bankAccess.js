$(function(){
    function getUrlParams(paraName) {
        var url = document.location.toString();
        var arrObj = url.split("?");

        if (arrObj.length > 1) {
            var arrPara = arrObj[1].split("&");
            var arr;

            for (var i = 0; i < arrPara.length; i++) {
                arr = arrPara[i].split("=");

                if (arr != null && arr[0] == paraName) {
                    return arr[1];
                }
            }
            return "";
        }
        else {
            return "";
        }
    }

    var IndUsrId = getUrlParams("userId");

    $("#plusInfo").click(function () {
        if(this.value == "show"){
            $("#requestInfo").show();
            $("#changImg").attr("src","../img/minus.png");
            this.value = "hide";
        }else{
            $("#requestInfo").hide(true);
            $("#changImg").attr("src","../img/plus.png");
            this.value = "show";
        }
    });

    var _flag = true;
    //返回icon
    $('#back,#cancelBtn').click(function(){
        window.history.go(-1);
    });


    //点击确认授权
    $('#btn').click(function(){
        var _this = this;
        //经纬度城市
        var _mock = {
            latitude: '31.233858',
            longitude: '121.663881',//经度
            cityCode: '310000',//城市code
            cityName:encodeURI('上海'),
            timestamp:'1528700527'
        };

        if($('#card-01').is(":checked") || $('#card-02').is(":checked") || $('#card-03').is(":checked") ){
            if($('#agree').is(":checked")){
                if(_flag){
                    _flag = false;
                    $(this).addClass('gray');
                    var name = $("#userName").val();
                    var certifId = $("#certifId").val();
                    var mobile = $("#mobile").val();
                    var cardNo = $("#userCard").val();
                    //调用请求
                    var _params= {
                        userId: IndUsrId,
                        name: name,
                        certifId: certifId,
                        mobile: mobile,
                        cardNo: cardNo
                    };
                    //var _apiUrl = 'http://172.20.182.221:8787/open-h5/binduser'
                    // var _apiUrl = location.origin + '/binduser';
                    var _apiUrl = location.origin + '/open-h5/binduser';
                    up.ui.loading();

                    /** 返回报文格式
                     * 正常返回
                     * {"resp":"00","msg":"","params":{"sn":"546ef50659194ec5a050c07e4396c8b7"}}
                     * 异常返回
                     * {"msg":"系统繁忙，请稍候再试","resp":"00000002"} b) {"msg":"请求报文解析错误","resp":"01"}
                     */
                    $.get(_apiUrl, _params, function(response){
                        var _response = JSON.parse(response)
                        if(response && _response.resp == '00'){
                            if(_response.params && _response.params.sn){
                                up.ui.hideLoading();
                                _flag = true;
                                $(_this).removeClass('gray');
                                console.log(_response);
                                // 跳转到银联优惠的地址
                                location.href='http://202.101.25.188:10533/s/open/outApp/react/index.html#/?sn='+_response.params.sn+'&lat='+_mock.latitude+'&lon='+_mock.longitude+'&cityName='+_mock.cityName
                            }else{
                                _flag = true;
                                $(_this).removeClass('gray');
                                up.ui.toast(_response.msg);
                            }
                        }else{
                            _flag = true;
                            $(_this).removeClass('gray');
                            up.ui.toast(_response.msg);
                        }


                    });
                }
            }else{
                up.ui.toast('请先同意协议');
            }
        } else {
            up.ui.toast('至少选择一张卡');
        }
    });

});

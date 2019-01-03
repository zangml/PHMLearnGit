<%--
  Created by IntelliJ IDEA.
  User: zangmenglei
  Date: 2018/12/10
  Time: 12:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script src="http://how2j.cn/study/js/jquery/2.0.0/jquery.min.js"></script>
    <link href="http://how2j.cn/study/css/bootstrap/3.3.6/bootstrap.min.css" rel="stylesheet">
    <script src="http://how2j.cn/study/js/bootstrap/3.3.6/bootstrap.min.js"></script>
    <script src="https://cdn.bootcss.com/echarts/3.8.5/echarts.min.js"></script>
    <script src="http://echarts.baidu.com/resource/echarts-gl-latest/dist/echarts-gl.min.js"></script>
    <link href="<%=basePath%>assets/styles/lab_1.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=basePath%>assets/css/style.css">
    <script  src="<%=basePath%>assets/js/index.js"></script>
    <title>${lab.title}</title>
</head>
<body>
<div id="bg" class="categoryWithCarousel">
    <div class="headbar"  >
        <div class="head ">
            <!--<span class="glyphicon glyphicon-th-list" style="margin-left:10px"></span> -->
            <span style="margin-left:10px" ><a href="/labs">开发者实验室</a></span>
        </div>
        <div class="rightMenu">

                <span>
                <a href="/labs">
                    实验列表
                </a></span>
            <span>
                <a id="a1" href="nowhere">
                    个人工作台
                </a></span>

        </div>
    </div>

    <div class="divcss5" style="position: relative;">
        <div class="divcss5_left" style="position: ab;"  >
            <div class="container">

                <div id="selectedAttr" class="row" style="margin-top: 20px;">
                    <button id="attr" onclick="easyAttribute(this)" class="btn btn-success" style="display: none;margin-left: 20px"></button>
                </div>
                <div class="row" style="margin-top: 20px;">
                    <div class="col-sm-3" style="margin-left: 100px;">
                        <div class="input-group-btn">
                            <button id="btnType" type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">单一属性预测分析 <span class="caret"></span></button>
                            <ul class="dropdown-menu">
                                <li><a href="#" onclick="changeType(4,'单一属性预测分析')">单一属性预测分析</a></li>
                                <li><a href="#" onclick="changeType(5,'特征重要性分析')">特征重要性分析</a></li>
                                <li><a href="#" onclick="changeType(6,'特征降维二维散点图')">特征降维二维散点图</a></li>
                                <li><a href="#" onclick="changeType(7,'特征降维三维散点图')">特征降维三维散点图</a></li>
                            </ul>
                        </div><!-- /btn-group -->
                    </div>

                    <div class="col-sm-2" id="attribute1">
                        <div class="input-group-btn">
                            <button id="btnAttribute1" type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${attributes[0]} <span class="caret"></span></button>
                            <ul class="dropdown-menu">
                                <c:forEach items="${attributes}" var="name">
                                    <li><a href="#" onclick="changeAttribute1(this)">${name}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>

                    <div class="col-sm-2" id="attribute2">
                        <div class="input-group-btn">
                            <button id="btnAttribute2" type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${attributes[0]} <span class="caret"></span></button>
                            <ul class="dropdown-menu">
                                <c:forEach items="${attributes}" var="name">
                                    <li><a href="#" onclick="changeAttribute2(this)">${name}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>

                    <button id="submit" data-toggle="modal" data-target="#ajaxloader2" data-backdrop="static" onclick="submit()" class="btn btn-primary">查看</button>
                </div>
                <div id="echart" style="width: 900px;height: 700px;margin-top: 20px"></div>
            </div>
        </div>

        <div class="divcss5_right" style="position: a;">
    <span>
                <a id="a2">

                    ${lab.title}</a>
                    &nbsp&nbsp &nbsp&nbsp &nbsp&nbsp &nbsp&nbsp
                    <hr>
                   <div class="border1">
                    <a href="nowhere" style="color: green">&#8730</a>
                    <a class="a3" ><strong> 1、数据预处理</strong></a>
                   </div><p></p>
                   <div class="border1">
                    <a href="nowhere" style="color: green">&#8730</a>
                    <a class="a3" ><strong> 2、特征提取</strong></a>
                   </div><p></p>
                    <p style="color: white">3.特征可视化</p>

                <c:forEach items="${labviews}" var="item">
                    <p style="color: white">${item.name}:${item.des}</p>
                </c:forEach>
                <a href="/learn/lab4/${lab.id}/${instance}" class="button1" style="background-color: white" ><b>下一步</b></a>
                <button id="submitAttri" onclick="submitAttri()" class="btn btn-primary" style="margin-left: 30px;">提交</button>
                <p></p>
                <div class="border1"><a class="a3" href="#nowhere"><strong> 4.算法选择及调参</strong></a></div>
                <div class="border1"><a class="a3" href="#nowhere"><strong> 5.划分训练集和测试集</strong></a></div>
                <div class="border1"><a class="a3" href="#nowhere"><strong> 6.实验结果及分析</strong></a></div>
                </span>
        </div>

    </div>
</div>
<div id="ajaxloader2" class="modal" style="display: none;margin-top: 170px;">
    <div class="outer"></div>
    <div class="inner"></div>
</div>
</body>

<script language="JavaScript">
    var myChart = echarts.init(document.getElementById('echart'),"light");
    var option = {};
    myChart.setOption(option);
    var index = 0;
    myChart.on('click',function (param) {
        var clone = $("#attr").clone().css("display","inline").attr('id',param.data).attr('name','feature'+index).text(param.name);
        clone.insertBefore("#attr")
        index++;
    })

    var type = 0;
    var attribute1 = 'wind_speed';
    var attribute2 = 'wind_speed';
    $("#attribute1").hide();
    $("#attribute2").hide();
    function changeType(a,b) {
        type = a;
        $("#btnType").text(b);
        if (type==1||type==4){
            $("#attribute2").hide();
            $("#attribute1").show();
        }else if (type==2){
            $("#attribute2").show();
            $("#attribute1").show();
        } else {
            $("#attribute2").hide();
            $("#attribute1").hide();
        }
    }

    function changeAttribute1(view) {
        attribute1 = view.text;
        $("#btnAttribute1").text(attribute1);
        console.error(attribute1);
    }

    function changeAttribute2(view) {
        attribute2 = view.text;
        $("#btnAttribute2").text(attribute2);
        console.error(attribute2)
    }

    function submit() {
        var url = "/design/${lab.id}/attribute/"+type+"?attribute1="+attribute1+"&attribute2="+attribute2;
        console.error(url)
        $.ajax({
            type:"GET",
            url:url,
            async:true,
            success:function (data) {
                console.log(data);
                option = $.parseJSON(data);
                $('#ajaxloader2').modal('hide')
                myChart.clear()
                myChart.setOption(option);
            },
            error:function (e) {
                $('#ajaxloader2').modal('hide')
                alert("error"+e);
            }
        })
    }
    function easyAttribute(v) {
        attribute1 = v.textContent;
        submit()
    }

    function submitAttri() {
        var param = "";
        $("#selectedAttr").children().each(function () {
            param = param+$(this).attr("name")+"="+$(this).html()+"&"
        })
        var url = "/learn/${lab.id}/${instance}/feature/handle?"+param;
        console.log(url)
        $.ajax({
            type:"POST",
            url:url,
            async:true,
            success:function (data) {
                console.log(data)
                if (data.status == 0){
                    location.href = "/learn/lab2/${lab.id}/${instance}"
                }
            },
            error:function (e) {
                console.log(e)
            }
        })
    }
</script>
</html>

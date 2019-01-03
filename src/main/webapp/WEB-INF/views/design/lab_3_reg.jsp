<%--
  Created by IntelliJ IDEA.
  User: zangmenglei
  Date: 2018/12/10
  Time: 12:15 PM
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
    <script  src="<%=basePath%>assets/js/index.js"></script>
    <link rel="stylesheet" href="<%=basePath%>assets/css/style.css">
    <link href="<%=basePath%>assets/styles/lab_1.css" rel="stylesheet">
    <title>${lab.title}</title>
</head>
<body>
<div class="categoryWithCarousel">
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
                <div class="row" style="margin-top: 30px;">

                    <div class="col-sm-3" style="margin-left: 100px;">
                        <div class="input-group-btn">
                            <button id="btnType" type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">单一属性预测分析<span class="caret"></span></button>
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
                    <button class="btn btn-success" data-toggle="modal" data-target="#myModal" id="addBtn">添加</button>
                    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                    </button>
                                    <h4 class="modal-title" id="myModalLabel">
                                        添加用户可见的可视化项
                                    </h4>
                                </div>
                                <div class="modal-body">
                                    <div id="wrapper" class="row" style="margin-left: 8px;margin-right: 8px">
                                        <div id="typeSelector"class="input-group-btn">
                                            <button  id="btnType1" type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">单一属性预测分析 <span class="caret"></span></button>
                                            <ul class="dropdown-menu">
                                                <li><a href="#" onclick="changeSelectType(4,'单一属性预测分析')">单一属性预测分析</a></li>
                                                <li><a href="#" onclick="changeSelectType(5,'特征重要性分析')">特征重要性分析</a></li>
                                                <li><a href="#" onclick="changeSelectType(6,'回归特征降维二维散点图')">特征降维二维散点图</a></li>
                                                <li><a href="#" onclick="changeSelectType(7,'回归特征降维三维散点图')">特征降维三维散点图</a></li>

                                            </ul>
                                        </div><!-- /btn-group -->
                                    </div>
                                    <hr />
                                    <textarea  id="modalText" rows="8" name="des" class="form-control" placeholder="请输入您的实验数据特征观察结果"></textarea>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭
                                    </button>
                                    <button id="btnSubmit" type="button" class="btn btn-primary">
                                        提交更改
                                    </button>
                                </div>
                            </div><!-- /.modal-content -->
                        </div><!-- /.modal -->
                    </div>

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
                    <a class="a3"><strong> 1、数据预处理</strong></a>
                  </div><p></p>
                  <div class="border1">
                    <a href="nowhere" style="color: green">&#8730</a>
                    <a class="a3" ><strong> 2、特征提取</strong></a>
                  </div><p></p>
                    <p style="color: white">3.特征可视化</p>

                    <p style="color: white">点击添加，添加用户可见的可视化项。并对每个可视化项添加说明</p>
            </br>
            <a href="/design/${lab.id}/lab_4" class="button1" style="background-color: white" ><b>下一步</b></a> </br>
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

    var typeSelect = 0;

    function changeSelectType(a,b) {
        typeSelect = a;
        $("#btnType1").text(b);
        typeSelect = a;
    }

    $("#btnSubmit").click(function (id) {
        var des = $("#modalText").val();
        var url = "/design/${lab.id}/part1/view/add/"+typeSelect+"?des="+des;
        console.error(url)
        $.ajax({
            type:"POST",
            url:url,
            async:true,
            success:function (data) {
                console.log(data);
                alert("添加成功")
                $("#myModal").modal('hide')
            },
            error:function (e) {
                alert("error"+e)
            }
        })
    });

    $("#addBtn").click(function () {
        var url = "/design/${lab.id}/part1/view/list"
        $.ajax({
            type:"POST",
            url:url,
            ansync:true,
            success:function (data) {
                var list = data.data;
                $("button").remove(".btn-danger");
                for(var i=0;i<list.length;i++){
                    $("#typeSelector").append('<button class="btn btn-danger" style="margin-left: 4px">'+list[i].name+'</button>');
                }
                console.log(data);
            },
            error:function (e) {
                console.log(e)
            }
        })
    })

    var myChart = echarts.init(document.getElementById('echart'),"light");
    var option = {};
    myChart.setOption(option);

    var type = 0;
    var attribute1 = '${attributes[0]}';
    var attribute2 = '${attributes[0]}';
    $("#attribute1").hide();
    $("#attribute2").hide();
    function    changeType(a,b) {
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
        console.error(attribute1)
    }

    function changeAttribute2(view) {
        attribute2 = view.text;
        $("#btnAttribute2").text(attribute2);
        console.error(attribute2)
    }

    // $("#ajaxloader2").modal({
    //     backdrop:false,
    //     show:false
    // })
    function submit() {
        var url = "/design/${lab.id}/attribute/"+type+"?attribute1="+attribute1+"&attribute2="+attribute2;
        console.error(url)
        $.ajax({
            type:"POST",
            url:url,
            async:true,
            scriptCharset: 'utf-8',
            success:function (data) {
                console.log(data);
                $('#ajaxloader2').modal('hide')
                option = $.parseJSON(data);
                myChart.clear()
                myChart.setOption(option);
            },
            error:function (e) {
                // hidediv();
                $('#ajaxloader2').modal('hide')
                alert("error"+e)
            }
        })
    }
</script>
</html>
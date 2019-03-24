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
    <script  src="<%=basePath%>assets/js/index.js"></script>
    <link rel="stylesheet" href="<%=basePath%>assets/css/style.css">
    <title>${lab.title}</title>
</head>
<style>
    hr{
        width: 100%;
        height: 1px;
        color:rgb(135,135,135);
        border-top:1px solid #555555;
    }
    div.categoryWithCarousel{
        width: 100%;
        position:relative;
    }
    div.categoryWithCarousel div.headbar{
        background-color: rgb(48,54,69);
        height: 60px;
        width: 100%;
    }
    div.categoryWithCarousel div.head{
        width: 250px;
        background-color: rgb(48,54,69);
        height: 60px;
        line-height: 60px;
        font-size: 25px;
        font-weight: bold;
        margin-left: 00px;
        margin-right: 30px;
        display: inline-block;
    }
    div.categoryWithCarousel div.head a{
        color: rgb(113,177,194);
    }
    div.categoryWithCarousel div.rightMenu{
        display: inline-block;
    }
    div.categoryWithCarousel div.rightMenu a{
        font-size: 16px;
        color: rgb(163,166,174);
        text-decoration:none;
    }
    div.categoryWithCarousel div.rightMenu a:hover{
        font-size: 16px;
        color: rgb(163,166,174);
        text-decoration:none;
        color: white;
    }

    #a1{
        font-size: 16px;
        color: white;
        text-decoration:none;
    }
    #a2{
        font-size: 16px;
        color: white;
        text-decoration:none;
        text-align: center;
    }
    a.a3{
        font-size: 15px;
        color: rgb(59,60,61);
        text-decoration:none;
        text-align: center;
    }
    div.border1{
        padding-bottom: 3px;
        border-bottom: 1px solid rgb(59,60,61);
        width: 90%;
    }
    div.categoryWithCarousel div.rightMenu span{
        line-height: 60px;
        margin: 0px 20px 0px 20px;
    }
    div.categoryWithCarousel div.rightMenu img{
        height: 30px;
    }
    div.carousel-of-product{
        height: 510px;
        width:1024px;
        margin:0px auto;
    }
    img.carouselImage{
        height: 510px !important;
    }
    div.carouselBackgroundDiv{
        width:100%;
        height:510px;
        background-color:#E8E8E8;
        position:absolute;
        top:36px;
        z-index:-1;
    }
    #left{
        background: rgb(245,245,245);
        width: 300px;
        height: 900px;
        float: left;
        position: absolute;
        overflow: auto;
        top: 60px;

    }
    #right{
        background: yellow;
        width: 1000px;
        height: 500px;
        float: right;
        position: absolute;
        overflow: auto;
        top: 60px;

    }
    .divcss5{ width:100%;padding:0px;border:0px solid #F00}

    .divcss5_left{
        margin-left: 0px;
        width:65%;
        height: 900px;
        border:0px solid #00F;
        /*overflow: scroll;*/
        overflow-x: auto;
        background:rgb(29,31,32);
        display: block;
        padding: 60px 20px 10px 20px;
        float: left;
    }
    .divcss5_right{
        /*margin-left:700px; */
        width:35%;
        height: 900px;
        border:0px solid rgb(39,41,42);
        /* overflow: scroll; */
        padding:20px 10px 20px 10px;
        background:rgb(39,41,42);
        display: block;
        float: left;
    }
    .clear{ clear:both}

    .button {
        background-color: #4CAF50;
        border: none;
        color: white;
        padding: 5px 2px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 4px 3px;
        cursor: pointer;
        border-radius: 4px;
        float: right;
    }
    .button1{
        background-color: white;
        border: none;
        color: black;
        padding: 5px 2px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 4px 3px;
        cursor: pointer;
        border-radius: 4px;
    }
    .text1 {
        width:200px;     /* 这里是输入框的长度 */
        height:40px;   /* 这里是输入框的宽度 */
        position:relative;   /* 这里规定输入框的定位类型 */
        top:10px;   /* 这里规定输入框于原位置向下偏移100px  */
        left:0px;    /*  这里规定输入框于原位置向右偏移300px  */
        background:rgb(29,31,32);
        border: 0px;
    }
    .text2 {
        width:200px;     /* 这里是输入框的长度 */
        height:40px;   /* 这里是输入框的宽度 */
        position:relative;   /* 这里规定输入框的定位类型 */
        top:0px;   /* 这里规定输入框于原位置向下偏移100px  */
        left:0px;    /*  这里规定输入框于原位置向右偏移300px  */
        background:rgb(39,41,42);
        border: 0px;
    }
    table,th,td
    {
        border:1px solid white;
    }
</style>
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
        <div class="divcss5_left"  >
            <div class="container">
                <div style="font-size:13px; color:rgb(170,170,170); ">
                    <p>
                        特征提取的目的是通过合适的数据分析方法从原始数据中提取与建模相关的有效特征，从而减小数据量，提高算法效率
                    </p>
                    <p>
                        降维的目的在于减少计算量，提高计算效率，提高模型的泛化能力，常用的方法是基于PCA的降维
                    </p>
                    <p>
                        常用的特征提取方法包括时域、频域和时频域特征提取
                    </p>
                    <p>
                        时域特征包括：RMS（有效值）、峰峰值、峭度、歪度、均值、均方根、脉冲因数、波峰因数等
                    </p>
                    <p>
                        频域特征提取包括：频带能量提取和特征频率表提取
                    </p>
                    <p> 时频域特征提取方法有：短时傅里叶变换、小波变换等</p>

                </div>
                <div>
                    <p style="font-size:15px; color:rgb(170,170,170);">
                        本次实验采用的特征提取方法：
                    </p>
                <c:forEach items="${vos}" var="vo">
                    <div class="panel col-md-3" style="margin-left: 5px">
                        <div class="panel-primary panel-heading">
                            <p align="center">${vo.feature.name}</p>
                        </div>
                        <div class="panel-body">
                            <form id="feature${vo.feature.id}">
                                <c:forEach items="${vo.paramList}" var="item">
                                    <div class="input-group">
                                        <span class="input-group-addon" title="${item.des}">${item.name}</span>
                                        <input type="text" class="form-control" name="${item.shell}" value="${item.defaultValue}">
                                    </div>
                                </c:forEach>
                            </form>
                            <button data-toggle="modal" data-target="#ajaxloader2" data-backdrop="static" onclick="addFeature(${vo.feature.id})" class="btn btn-primary pull-right">提交</button>
                        </div>
                    </div>
                </c:forEach>
                </div>

            </div>
        </div>

        <div class="divcss5_right"  >
    <span>
                <a id="a2" href="#nowhere">${lab.title}</a>
                    &nbsp&nbsp &nbsp&nbsp &nbsp&nbsp &nbsp&nbsp
                   <a href="#" class="button">重置进度</a>
                    <a href="#" class="button" style="background-color: red">结束实验</a>
                    <hr>
                  <div class="border1">
                    <a href="nowhere" style="color: green">&#8730</a>
                    <a class="a3" ><strong> 1、数据预处理</strong></a>
                  </div><p></p>
                    <p style="color: white">2、特征提取 &nbsp&nbsp &nbsp&nbsp &nbsp&nbsp<span><a href="https://v.youku.com/v_show/id_XMzkwOTE3MzQ1Ng==.html?spm=a2h0j.11185381.listitem_page1.5!5~A" target="_blank">点我查看更多</a></span></p>
                   <p style="color: white">${des}</p>
               <a href="/learn/lab3/${labId}/${instance}" class="button1" style="background-color: white" ><b>已完成，下一步</b></a> </br>
                     <p></p>
                <div class="border1"><a class="a3" href="#nowhere"><strong> 3.特征可视化</strong></a></div>
                <div class="border1"><a class="a3" href="#nowhere"><strong> 4.算法选择及调参</strong></a></div>
                <div class="border1"><a class="a3" href="#nowhere"><strong> 5.划分测试集和训练集</strong></a></div>
                <div class="border1"><a class="a3" href="#nowhere"><strong> 6.查看训练结果</strong></a></div>
                </span>
        </div>
    </div>
</div>
<div id="ajaxloader2" style="display: none;margin-top: 170px;">
    <div class="outer"></div>
    <div class="inner"></div>
</div>
</body>
<script language="JavaScript">
    function addFeature(featureId) {
        var url = "/learn/${labId}/${instance}/feature/"+featureId;
        console.log(url)
        var formId = "#feature"+featureId;
        $.ajax({
            type:"POST",
            url:url,
            async:true,
            data:$(formId).serialize(),
            success:function (data) {
                $('#ajaxloader2').modal('hide')
                if(data.status==0){
                    alert("特征提取成功")
                }
                console.log(data)
            },
            error:function (e) {
                $('#ajaxloader2').modal('hide')
                console.log(e)
                alert("error"+e)
            }
        })
    }

</script>

</html>
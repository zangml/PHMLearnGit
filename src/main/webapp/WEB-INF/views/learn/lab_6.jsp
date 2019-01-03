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
    <script src="http://echarts.baidu.com/dist/echarts.min.js"></script>
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
        height: 1500px;
        border:0px solid #00F;
        /*overflow: scroll;*/
        overflow-x: auto;
        background:rgb(29,31,32);
        display: block;
        padding: 10px 20px 10px 20px;
        float: left;
    }
    .divcss5_right{
        /* margin-left:700px; */
        width:35%;
        height: 1500px;
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
        <div class="divcss5_left"  >
            <h2 style="color: white">实验结果</h2>
            <table class="table">
                <tbody>
                <c:forEach items="${res}" var="line">
                    <tr style="color: white">
                        <c:forEach items="${line}" var="item">
                            <th style="color: white">${item}</th>
                        </c:forEach>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <br>
            <div style="margin-left: 100px;">
                <div class="input-group-btn">
                    <button id="btnType" type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${classNames.get(0)} <span class="caret"></span></button>
                    <ul class="dropdown-menu">
                        <c:forEach items="${classNames}" var="name" varStatus="status">
                            <li onclick="changeType(${status.index},'${name}')">${name}</li>
                        </c:forEach>
                    </ul>
                </div><!-- /btn-group -->
            </div>
            <br>
            <div id="echart" style="width: 700px;height: 600px;margin-top: 20px;margin-left: 30px;"></div>
        </div>

        <div class="divcss5_right"  >
    <span>
                <a id="a2" href="#nowhere">

                    ${lab.title}</a>
                    &nbsp&nbsp &nbsp&nbsp
                   <a href="/labs/group/${groupId}/${groupInstanceId}" class="button">查看</a>

                    <hr>
                  <div class="border1">
                    <a href="nowhere" style="color: green">&#8730</a>
                    <a class="a3"><strong> 1、数据预处理</strong></a>
                  </div><p></p>
                  <div class="border1">
                    <a href="nowhere" style="color: green">&#8730</a>
                    <a class="a3" ><strong> 2、特征提取</strong></a>
                  </div><p></p>
                  <div class="border1">
                    <a href="nowhere" style="color: green">&#8730</a>
                    <a class="a3" ><strong> 3、特征可视化</strong></a>
                  </div><p></p>
                   <div class="border1">
                    <a href="nowhere" style="color: green">&#8730</a>
                    <a class="a3" ><strong> 4、算法选择及调优</strong></a>
                  </div><p></p>
                  <div class="border1">
                    <a href="nowhere" style="color: green">&#8730</a>
                    <a class="a3" ><strong> 5、划分测试数据和训练数据</strong></a>
                  </div><p></p>
        <p></p>
                 <p style="color: white">6、查看训练结果</p>
                </span>

        </div>
    </div>
</div>
</body>

<script language="JavaScript">

    var myChart = echarts.init(document.getElementById('echart'),"dark");
    var option = ${options}[0];
    console.log(option)
    myChart.setOption(option);

    function changeType(index,name) {
        $("#btnType").text(name);
        option = ${options}[index];
        myChart.setOption(option);
    }
</script>
</html>
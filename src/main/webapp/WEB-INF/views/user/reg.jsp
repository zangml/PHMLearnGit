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
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=basePath%>assets/styles/login.css" rel="stylesheet">

    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <title>PHM Learn</title>
</head>
<body class="zhi  no-auth">
<div class="index-main">
    <div class="index-main-body">
        <div class="index-header">
            <h1 class="logo hide-text"><img src="assets/images/res/nk.png" alt=""></h1>
            <h2 class="subtitle">
                ${msg}
            </h2>
        </div>
        <div class="desk-front sign-flow clearfix sign-flow-simple">
            <div class="view view-signin" data-za-module="SignInForm" style="display: block;">
                <form action="/reg/" id="regloginform" method="post">
                    <input type="hidden" name="_xsrf" value="21aa1c8d254df2899b23ab9afbd62a53">
                    <div class="group-inputs">
                        <div class="input-wrapper">
                            <input type="text" name="name" aria-label="用户名" placeholder="用户名" required="">
                        </div>
                        <div class="email input-wrapper">
                            <input type="text" name="email" aria-label="手机号或邮箱" placeholder="邮箱" required="">
                        </div>
                        <div class="input-wrapper">
                            <input type="password" name="password" aria-label="密码" placeholder="密码" required="">
                        </div>
                        <div class="input-wrapper">
                            <input type="password" name="repassword" aria-label="密码" placeholder="确认密码" required="">
                        </div>
                    </div>
                    <input type="hidden" name="next" value="${next}"/>
                    <div class="button-wrapper command clearfix">
                        <button class="sign-button submit pull-right"  type="submit" onclick="form=document.getElementById('regloginform');form.action='/reg/'">注册</button>
                    </div>
                    <div class="signin-misc-wrapper clearfix">
                        <label class="remember-me">
                            <input type="checkbox" name="rememberme" checked="" value="true"> 记住我
                        </label>
                        <a class="unable-login" href="#">无法登录?</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <%@include file="_head.jsp"%>
    <!-- Custom css for registration-->
    <link href="../css/signin.css" rel="stylesheet">
</head>

<body id="page-top">
<!-- Navigation -->
<nav class="navbar navbar-expand-lg bg-secondary fixed-top text-uppercase" id="mainNav">
    <div class="container">
        <a class="navbar-brand js-scroll-trigger" href="../index.html#page-top">RSS-2-KINDLE</a>
        <button class="navbar-toggler navbar-toggler-right text-uppercase bg-primary text-white rounded" type="button"
                data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive"
                aria-expanded="false" aria-label="Toggle navigation">
            Menu
            <i class="fa fa-bars"></i>
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item mx-0 mx-lg-1">
                    <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="login">Sign In</a>
                </li>
                <li class="nav-item mx-0 mx-lg-1">
                    <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="../index.html#howto">Howto</a>
                </li>
                <li class="nav-item mx-0 mx-lg-1">
                    <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="../index.html#about">About</a>
                </li>
                <li class="nav-item mx-0 mx-lg-1">
                    <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger"
                       href="../index.html#contact">Contact</a>
                </li>
            </ul>
        </div>
    </div>
</nav>


<header class="masthead bg-primary text-white text-center">
    <div class="container">
        <form:form class="form-signin" action="register" modelAttribute="newUserForm" method="post" autocomplete="off">
            <h2 class="form-signin-heading">Please sign up</h2>
            <form:label path="username" class="sr-only">Username</form:label>
            <form:input type="text" id="username" path="username" class="form-control" placeholder="Username" />
            <form:errors path="username" cssClass="error"/>
            <form:label path="email" class="sr-only">E-mail</form:label>
            <form:input type="email" path="email" class="form-control" placeholder="e-mail" />
            <form:errors path="email" cssClass="error"/>
            <form:label path="password" class="sr-only">Password</form:label>
            <form:input type="password" path="password" class="form-control" placeholder="Password" minlength="6" />
            <form:errors path="password" cssClass="error"/>
            <form:label path="confirmPassword" class="sr-only">Confirm Password</form:label>
            <form:input type="password" path="confirmPassword" class="form-control" placeholder="Confirm password" minlength="6" />
            <form:errors path="confirmPassword" cssClass="error"/>

            <button class="btn btn-outline-light btn-block btn-lg" type="submit">SIGN UP</button>
        </form:form>
    </div>
</header>

<!-- Footer -->
<footer class="footer text-center">
    <div class="container">
        <div class="row">
            <div class="col-md-4 mb-5 mb-lg-0">
                <h4 class="text-uppercase mb-4">Location</h4>
                <p class="lead mb-0">Wellington
                    <br>New Zealand, 6022</p>
            </div>
            <div class="col-md-4 mb-5 mb-lg-0">
                <h4 class="text-uppercase mb-4">Around the Web</h4>
                <ul class="list-inline mb-0">
                    <li class="list-inline-item">
                        <a class="btn btn-outline-light btn-social text-center rounded-circle" href="#">
                            <i class="fab fa-fw fa-facebook-f"></i>
                        </a>
                    </li>
<%--
                    <li class="list-inline-item">
                        <a class="btn btn-outline-light btn-social text-center rounded-circle" href="#">
                            <i class="fa fa-fw fa-google-plus"></i>
                        </a>
                    </li>
--%>
                    <li class="list-inline-item">
                        <a class="btn btn-outline-light btn-social text-center rounded-circle" href="#">
                            <i class="fab fa-fw fa-twitter"></i>
                        </a>
                    </li>
                    <li class="list-inline-item">
                        <a class="btn btn-outline-light btn-social text-center rounded-circle" href="#">
                            <i class="fab fa-fw fa-linkedin-in"></i>
                        </a>
                    </li>
<%--
                    <li class="list-inline-item">
                        <a class="btn btn-outline-light btn-social text-center rounded-circle" href="#">
                            <i class="fa fa-fw fa-dribbble"></i>
                        </a>
                    </li>
--%>
                </ul>
            </div>
            <div class="col-md-4">
                <h4 class="text-uppercase mb-4">About Roundkick Studio</h4>
                <p class="lead mb-0">Wellcome to <a href="https://roundkick.studio">Roundkick Studio</a>. We develop
                    software for people</p>
            </div>
        </div>
    </div>
</footer>

<div class="copyright py-4 text-center text-white">
    <div class="container">
        <small>Copyright &copy; <a href="https://roundkick.studio">Roundkick Studio</a> 2018</small>
    </div>
</div>

<!-- Scroll to Top Button (Only visible on small and extra-small screen sizes) -->
<div class="scroll-to-top d-lg-none position-fixed ">
    <a class="js-scroll-trigger d-block text-center text-white rounded" href="#page-top">
        <i class="fa fa-chevron-up"></i>
    </a>
</div>

</body>

</html>

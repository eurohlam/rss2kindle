<%--
  User: eurohlam
  Date: 13/02/2018
--%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- Navigation -->
<nav class="navbar navbar-expand-lg bg-secondary fixed-top text-uppercase" id="mainNav">
    <div class="container">
        <a class="navbar-brand js-scroll-trigger" href="#">RSS-2-KINDLE</a>
        <button class="navbar-toggler navbar-toggler-right text-uppercase bg-primary text-white rounded" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
            Menu
            <i class="fa fa-bars"></i>
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">
            <form id="logout" action="<c:url value="logout"/>" method="post">
                <ul class="navbar-nav ml-auto">
                    <li class = "nav-item dropdown active mx-0 mx-lg-1">
                        <a class="nav-link dropdown-toggle py-3 px-0 px-lg-3 rounded js-scroll-trigger" id="navbarDropdownMenuLink" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                            <%=username%> <span class="caret"></span>
                        </a>
                        <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                            <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="javascript:{}" onclick="document.getElementById('logout').submit(); return false;">Log out</a>
                        </div>
                    </li>
                    <li class="nav-item mx-0 mx-lg-1">
                        <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="../index.html#portfolio">Portfolio</a>
                    </li>
                    <li class="nav-item mx-0 mx-lg-1">
                        <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="../index.html#about">About</a>
                    </li>
                    <li class="nav-item mx-0 mx-lg-1">
                        <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="../index.html#contact">Contact</a>
                    </li>
                </ul>
                <security:csrfInput/>
            </form>
        </div>
    </div>
</nav>

<!--header class="header clearfix">
    <nav>
        <form id="logout" action="<c:url value="logout"/>" method="post">
            <ul class="nav nav-pills pull-right">
                <li role="presentation" class = "dropdown active">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                        <%=username%> <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <li role="presentation">
                            <a href="javascript:{}" onclick="document.getElementById('logout').submit(); return false;">Log out</a>
                        </li>
                    </ul>
                </li>
                <li role="separator" class="divider"></li>
                <li role="presentation"><a href="../index.html">Home</a></li>
                <li role="presentation"><a href="../about.html">About</a></li>
                <li role="presentation"><a href="../contact.html">Contact</a></li>
            </ul>
            <security:csrfInput/>
        </form>
    </nav>
    <h3 class="text-muted">RSS-2-KINDLE</h3>
</header-->

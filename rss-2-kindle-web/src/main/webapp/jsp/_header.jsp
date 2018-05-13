<%--
  User: eurohlam
  Date: 13/02/2018
--%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<header class="header clearfix">
    <!-- Navigation -->
    <form id="logout" action="<c:url value="logout"/>" method="post">
        <nav class="navbar navbar-expand-lg bg-secondary fixed-top text-uppercase" id="mainNav">
            <div class="container">
                <a class="navbar-brand js-scroll-trigger" href="../index.html#page-top">RSS-2-KINDLE</a>
                <button class="navbar-toggler navbar-toggler-right text-uppercase bg-primary text-white rounded"
                        type="button" data-toggle="collapse" data-target="#navbarResponsive"
                        aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
                    Menu
                    <i class="fa fa-bars"></i>
                </button>
                <div class="collapse navbar-collapse" id="navbarResponsive">
                    <ul class="navbar-nav ml-auto">
                        <li class="nav-item dropdown active mx-0 mx-lg-1">
                            <a class="nav-link dropdown-toggle py-3 px-0 px-lg-3 rounded js-scroll-trigger"
                               id="navbarDropdownMenuLink" data-toggle="dropdown" href="#" role="button"
                               aria-haspopup="true" aria-expanded="false">
                                <%=username%> <span class="caret"></span>
                            </a>
                            <div class="dropdown-menu bg-secondary" aria-labelledby="navbarDropdownMenuLink">
                                <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="javascript:{}"
                                   onclick="document.getElementById('logout').submit(); return false;">Log out</a>
                            </div>
                        </li>
                        <li class="nav-item mx-0 mx-lg-1">
                            <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger"
                               href="../index.html#portfolio">Portfolio</a>
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
        <security:csrfInput/>
    </form>
</header>

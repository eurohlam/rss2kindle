<%--
  User: eurohlam
  Date: 13/02/2018
--%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header class="header clearfix">
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
</header>

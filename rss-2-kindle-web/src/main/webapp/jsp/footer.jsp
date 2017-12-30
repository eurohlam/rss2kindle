<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%--
  User: eurohlam
  Date: 7/11/2017
  Time: 16:10
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="sticky-footer">
    <div class="container">
        <div class="row">
            <form action="<c:url value="logout"/>" method="post">
                <input type="submit" value="Logoff"/> (also clears any remember-me cookie)
                <security:csrfInput/>
            </form>
            <p align="center">&copy; Created by Eurohlam. 2017</p>
        </div>
    </div>
</footer>

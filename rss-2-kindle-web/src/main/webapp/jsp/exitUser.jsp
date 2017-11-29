<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>

<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page pageEncoding="UTF-8" %>

<html>
  <head>
    <title>Exit User</title>
  </head>

  <body>
    <h1>Exit User</h1>

    <c:if test="${not empty param.login_error}">
      <span style="color: red; ">
        Your 'Exit User' attempt was not successful, try again.<br/><br/>
        Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
      </span>
    </c:if>

      <table>
        <tr><td>Current User:</td><td>

<%
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) { %>

        <%= auth.getPrincipal().toString() %>

 <% } %>
         </td></tr>
      </table>
      <input type="hidden" name="<c:out value="${_csrf.parameterName}"/>" value="<c:out value="${_csrf.token}"/>"/>
  </body>
</html>

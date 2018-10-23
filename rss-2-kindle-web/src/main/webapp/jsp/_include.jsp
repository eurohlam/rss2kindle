<%@ page import="java.util.List" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" %>
<%!
    String username = null;
    List<String> roles = null;
%>

<%
    if (request.getAttribute("username") != null) {
        username = request.getAttribute("username").toString();
    }
    if (request.getAttribute("roles") != null) {
        roles = (List<String>)request.getAttribute("roles");
    }
%>
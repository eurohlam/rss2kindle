<%@ page import="org.springframework.security.core.userdetails.UserDetails" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" %>
<%! String username = null; %>
<%

        if (request.getAttribute("username")!= null)
            username = request.getAttribute("username").toString();
        else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {

                if (auth.getPrincipal() instanceof UserDetails) {
                    UserDetails ud = (UserDetails) auth.getPrincipal();
                    username = ud.getUsername();
                }
            }
        }
%>
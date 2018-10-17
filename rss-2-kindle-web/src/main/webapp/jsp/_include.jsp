<%@ page import="org.springframework.security.core.userdetails.UserDetails" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.roag.model.Roles" %>
<%@ page import="org.springframework.security.core.authority.SimpleGrantedAuthority" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" %>
<%!
    String username = null;
    String role = null;
%>

<%
    if (request.getAttribute("username") != null) {
        username = request.getAttribute("username").toString();
    }
    if (request.getAttribute("role") != null) {
        username = request.getAttribute("role").toString();
    }

    if (username == null || role == null){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {

            if (auth.getPrincipal() instanceof UserDetails) {
                UserDetails ud = (UserDetails) auth.getPrincipal();
                username = ud.getUsername();
                if (ud.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ROLE_ADMIN.toString()))) {
                    role = Roles.ROLE_ADMIN.toString();
                } else {
                    role = Roles.ROLE_USER.toString();
                }
            }
        }
    }
%>
<%@ page import="org.roag.web.RestClient" %>
<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%--
Created by eurohlam on 01.06.18
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    ApplicationContext context = RequestContextUtils.findWebApplicationContext(request);
    RestClient client = context.getBean(RestClient.class);
    for (String s: request.getParameterMap().keySet()) {
        out.println(s + ":  " + request.getParameter(s));
    }

    String name = request.getParameter("name");
    String phone = request.getParameter("phone");
    String email = request.getParameter("email");
    String message = request.getParameter("message");
    client.sendEmailToAny("info@roundkick.studio", "Request from " + name + " phone: " + phone, email, name, message);

%>

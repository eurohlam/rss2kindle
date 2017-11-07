<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.userdetails.UserDetails" %><%--
  User: eurohlam
  Date: 19/10/2017
  Time: 13:59
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>RSS-2-Kindle Management</title>
    <meta name="viewport" content="width = device-width, initial-scale = 1.0">

    <!-- JQuery -->
    <script src="../js/jquery-3.1.1.js"></script>

    <!-- Bootstrap -->
    <link href="../bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="../bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="../bootstrap/js/bootstrap.min.js"></script>
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <!-- Custom css -->
    <link href="../css/sticky-footer.css" rel="stylesheet">

</head>

<body>
<script>
    var rootURL = '/rss2kindle/rest/users';

    $(document).ready(function () {
        $('#all_users').append('<p>Getting users. Please wait...</p>');
        $.getJSON(rootURL, function (data) {
            var table = '<table class="table table-hover">' +
                '<tr><th>#</th>' +
                '<th>username</th>' +
                '<th>date created</th>' +
                '<th>status</th>'+
                '<th>subscribers</th></tr>';

            $.each(data, function (i, item) {
                var tr;
                if (item.status === 'locked')
                    tr='<tr class="danger"><td>';
                /*
                 else if (item.status === 'suspended')
                 tr='<tr class="warning"><td>';
                 */
                else
                    tr='<tr class="active"><td>';

                table = table + tr
                    + i + '</td><td>'
                    + item.username + '</td><td>'
                    + item.dateCreated + '</td><td>'
                    + item.status + '</td><td>';
                var subscribers = item.subscribers;
                subscrTable='<table width="100%"><tr><td>';
                for (j = 0; j < subscribers.length; j++) {
                    subscrTable = subscrTable + '<a href="' + subscribers[j].email + '">' + subscribers[j].email + '</a></td><td>';
                    if (subscribers[j].status === 'active')
                        subscrTable = subscrTable + '<label></label><input type="checkbox" checked disabled />'+ subscribers[j].status + '</label>';
                    else
                        subscrTable = subscrTable + '<label></label><input type="checkbox" disabled />' + subscribers[j].status + '</label>';

                    subscrTable = subscrTable  + '<td/></tr>';
                }
                subscrTable = subscrTable + '</table>';

                table = table + subscrTable + '</td></tr>';
            });
            table = table + '</table>';
            $('#all_users').append(table);
        })
    });
</script>
<header role="banner">
    <h1>RSS-2-Kindle rules</h1>
    <h2>
        <%-- TODO: just for test--%>
        <%
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) { %>
        <%= auth.getPrincipal().toString() %><br/>
        <%= auth.getCredentials() %><br/>
        <%= auth.getDetails() %>
        <%

                if (auth.getPrincipal() instanceof UserDetails) {
                    UserDetails ud = (UserDetails)auth.getPrincipal();
                %>
                    <%= ud.getUsername()%>
                    <%= ud.getPassword()%>
                <%
                }
            }
        %>
    </h2>
</header>

<div class="container">
    <nav class="navbar navbar-default" role="navigation">
        <ul class="nav nav-tabs">
            <li role="presentation" class="active"><a href="#">My Profile</a></li>
            <li role="presentation"><a href="subscribers.jsp">Subscriber Management</a></li>
            <li role="presentation"><a href="service.html">Services</a></li>
        </ul>
    </nav>
</div>

<div class="container">
    <div class="table-responsive" id="all_users">

    </div>
</div>

<%--
<footer class="footer">
    <div class="container">
        <div class="row">
            <p align="center">Created by Eurohlam. 2016</p>
        </div>
    </div>
</footer>
--%>
<%@include file="footer.jsp"%>

</body>
</html>
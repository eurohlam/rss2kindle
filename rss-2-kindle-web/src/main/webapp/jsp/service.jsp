<%--
  User: eurohlam
  Date: 2/12/17
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="include.jsp"%>

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
<script>
    var rootURL = '/rss2kindle/rest/service/<%=username%>';
    $(document).ready(function () {
        $("#run_all").click(function () {
            runPollingForUser();
            return false;
        });

        //error view
        $(document).ajaxError(function (event, request, settings) {
            $("#errorview").append("<h1>Error in getting data.</h1>");
        })
    });

    function runPollingForUser() {
        $.getJSON(rootURL, function (data) {
            $('#getresult').append('<p>Result jopa</p>');
        });
    }
</script>

<body>
<header role="banner">
    <h1>RSS-2-Kindle rules</h1>
</header>

<div class="container">
    <nav class="navbar navbar-default" role="navigation">
        <ul class="nav nav-tabs">
            <li role="presentation"><a href="profile">My Profile</a></li>
            <li role="presentation"><a href="subscribers">Subscriber Management</a></li>
            <li role="presentation" class="active"><a href="#">Services</a></li>
        </ul>
    </nav>
</div>

<div class="container">
    <article>
        <button id="run_all" type="button" class="btn btn-primary btn-lg btn-block">Poll my subscriptions immediately</button>
    </article>
</div>

<div class="col-md-6">
    <div id="getresult" class="table-responsive">

    </div>
    <p id="errorview">Jopa</p>
</div>


<%@include file="footer.jsp"%>

</body>
</html>
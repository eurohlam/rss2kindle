<%--
  User: eurohlam
  Date: 2/12/17
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="include.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>RSS-2-KINDLE Run Services </title>
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

<div class="container-fluid">
    <header class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
                <li role="presentation" class="active"><a href="../index.html">Home</a></li>
                <li role="presentation"><a href="#">About</a></li>
                <li role="presentation"><a href="#">Contact</a></li>
            </ul>
        </nav>
        <h3 class="text-muted">RSS-2-KINDLE</h3>
    </header>
    <hr/>

    <div class="row">
        <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
            <ul class="nav nav-pills flex-column">
                <li role="presentation"><a href="profile">My Profile</a></li>
                <li role="presentation"><a href="subscribers">Subscriber Management</a></li>
                <li role="presentation" class="active"><a href="#">Services</a></li>
            </ul>
        </nav>
        <main role="main" class="col-sm-9 col-md-10">
            <section class="row text-center placeholders">
                <div class="col-md-8">
                    <button id="run_all" type="button" class="btn btn-primary btn-lg btn-block">Poll my subscriptions immediately</button>
                </div>
            </section>

            <div class="col-md-6">
                <div id="getresult" class="table-responsive">

                </div>
            </div>
        </main>
    </div>

</div>




<%@include file="footer.jsp"%>

</body>
</html>
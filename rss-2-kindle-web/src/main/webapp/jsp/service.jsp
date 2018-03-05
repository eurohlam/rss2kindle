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
    <link href="../css/profile-theme.css" rel="stylesheet">

</head>
<body>
<script>
    var rootURL = 'rest/service/<%=username%>';
    $(document).ready(function () {
        $("#run_all").click(function () {
            runPollingForUser();
            return false;
        });

        //Show ajax error messages
        $(document).ajaxError(function (event, request, settings, thrownError) {
            $('#alerts_panel').html('<div class="alert alert-danger alert-dismissible" role="alert">'
                + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<strong>Error! </strong>Ajax error: code:' + request.status + ' calling url: ' + settings.url + ' method: ' + settings.type + ' ' + thrownError + '</div>');
        });
    });

    function runPollingForUser() {
        $.getJSON(rootURL, function (data) {
        })
            .done(function () {
                $('#alerts_panel').html('<div class="alert alert-success alert-dismissible" role="alert">'
                    + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                    + '<strong>Success! </strong>Polling process has been launched</div>');
                return true;
            })
            .fail(function () {
                $('#alerts_panel').html('<div class="alert alert-danger alert-dismissible" role="alert">'
                    + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                    + '<strong>Error! </strong>Unexpected problem</div>');
                return false;
            });
    }
</script>

<div class="container-fluid">
    <%@include file="header.jsp"%>

    <div class="row">
        <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
            <ul class="nav nav-pills nav-stacked">
                <li role="presentation"><a href="profile">My Profile</a></li>
                <li role="presentation"><a href="subscribers">Subscriber Management</a></li>
                <li role="presentation" class="active"><a href="#">Services</a></li>
            </ul>
        </nav>

        <main role="main" class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <h1 class="page-header">Polling subscriptions</h1>
            <section class="row placeholders">
                <div id="alerts_panel"></div>
                <div class="alert alert-info" role="alert">
                    All subscriptions are polled every day automatically at 02 am.
                    If you wish to poll you subscriptions right now just push the button below
                </div>
                <button id="run_all" type="button" class="btn btn-primary">
                    <span class="glyphicon glyphicon-import" aria-hidden="true"></span>
                    Poll my subscriptions immediately
                </button>
            </section>

        </main>
    </div>

</div>

<%@include file="footer.jsp"%>

</body>
</html>
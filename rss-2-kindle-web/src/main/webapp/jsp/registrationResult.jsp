<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>RSS-2-KINDLE Service</title>
    <meta name="viewport" content="width = device-width, initial-scale = 1.0">

    <!-- JQuery -->
    <script src="../js/jquery.min.js"></script>

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
    <link href="../css/landing-theme.css" rel="stylesheet">

</head>

<body>
<div class="container">
    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
                <li role="presentation" class="active"><a href="#">Home</a></li>
                <li role="presentation"><a href="#">About</a></li>
                <li role="presentation"><a href="#">Contact</a></li>
            </ul>
        </nav>
        <h3 class="text-muted">RSS-2-KINDLE</h3>
    </div>

    <div class="jumbotron">
        <h1>Thank you for using RSS-2-KINDLE service!</h1>
        <p class="lead">User <span style="color: red;">${username}</span> has been registered successfully.
            The confirmation will be sent to email <span style="color: red;">${email}</span>.
        You can start working with your account right now if you sign in</p>
        <div class="row">
            <div class="col-lg-8">
                <p><a id="btn_signin" class="btn btn-lg btn-success" href="login" role="button">Sign in</a></p>
            </div>
        </div>
    </div>



    <footer class="footer">
        <p>&copy; Created by Eurohlam. 2017</p>
    </footer>

</div> <!-- /container -->

</body>
</html>
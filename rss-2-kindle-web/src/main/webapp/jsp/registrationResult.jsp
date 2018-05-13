<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>RSS-2-KINDLE Service</title>
    <meta name="viewport" content="width = device-width, initial-scale = 1.0">

    <!-- Custom fonts for this template -->
    <link href="../vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css">

    <!-- Bootstrap -->
    <link href="../vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom css -->
    <link href="../css/signin.css" rel="stylesheet">
    <link href="../css/freelancer.min.css" rel="stylesheet">

</head>

<body>
<div class="container">
    <header class="header clearfix">
        <!-- Navigation -->
        <nav class="navbar navbar-expand-lg bg-secondary fixed-top text-uppercase" id="mainNav">
            <div class="container">
                <a class="navbar-brand js-scroll-trigger" href="../index.html#page-top">RSS-2-KINDLE</a>
                <button class="navbar-toggler navbar-toggler-right text-uppercase bg-primary text-white rounded" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
                    Menu
                    <i class="fa fa-bars"></i>
                </button>
                <div class="collapse navbar-collapse" id="navbarResponsive">
                    <ul class="navbar-nav ml-auto">
                        <li class="nav-item mx-0 mx-lg-1">
                            <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="../index.html#portfolio">Portfolio</a>
                        </li>
                        <li class="nav-item mx-0 mx-lg-1">
                            <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="../index.html#about">About</a>
                        </li>
                        <li class="nav-item mx-0 mx-lg-1">
                            <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="../index.html#contact">Contact</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </header>

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
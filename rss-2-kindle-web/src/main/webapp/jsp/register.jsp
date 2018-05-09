<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>RSS-2-Kindle Service</title>
    <meta name="viewport" content="width = device-width, initial-scale = 1.0">

    <!-- Custom fonts for this template -->
    <link href="../vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css">

    <!-- Bootstrap -->
    <link href="../vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom css -->
    <link href="../css/signin.css" rel="stylesheet">
    <link href="../css/landing-theme.css" rel="stylesheet">

</head>
<body>

<div class="container">
    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
                <li role="presentation" class="active"><a href="../index.html">Home</a></li>
                <li role="presentation"><a href="../index.html#about">About</a></li>
                <li role="presentation"><a href="../index.html#contact">Contact</a></li>
            </ul>
        </nav>
        <h3 class="text-muted">RSS-2-KINDLE</h3>
    </div>

    <div id="signup" class="jumbotron">
        <form:form class="form-signin" action="register" commandName="newUserForm" method="post" autocomplete="off">
            <h2 class="form-signin-heading">Please sign up</h2>
            <form:label path="username" class="sr-only">Username</form:label>
            <form:input type="text" id="username" path="username" class="form-control" placeholder="Username"/>
            <form:errors path="username" cssClass="error"/>
            <form:label path="email" class="sr-only">E-mail</form:label>
            <form:input type="email" path="email" class="form-control" placeholder="e-mail"/>
            <form:errors path="email" cssClass="error"/>
            <form:label path="password" class="sr-only">Password</form:label>
            <form:input type="password" path="password" class="form-control" placeholder="Password" />
            <form:errors path="password" cssClass="error"/>
            <form:label path="confirmPassword" class="sr-only">Confirm Password</form:label>
            <form:input type="password" path="confirmPassword" class="form-control" placeholder="Confirm password" />
            <form:errors path="confirmPassword" cssClass="error"/>

            <button class="btn btn-lg btn-success btn-block" type="submit">Sign up</button>
        </form:form>

    </div>

    <footer class="footer">
        <p>Copyright &copy; Roundkick Studio. 2018</p>
    </footer>
</div>

<!-- Bootstrap core JavaScript -->
<script src="../vendor/jquery/jquery.min.js"></script>
<script src="../vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

</body>

</html>

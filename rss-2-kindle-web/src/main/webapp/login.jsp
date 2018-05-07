<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>RSS-2-Kindle Service</title>
    <meta name="viewport" content="width = device-width, initial-scale = 1.0">

    <!-- Custom fonts for this template -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css">

    <!-- Bootstrap -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="vendor/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">

    <!-- Custom css -->
    <%--<link href="css/sticky-footer.css" rel="stylesheet">--%>
    <link href="css/signin.css" rel="stylesheet">
    <link href="css/landing-theme.css" rel="stylesheet">

</head>
<body>

<div class="container">
    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
                <li role="presentation" class="active"><a href="index.html">Home</a></li>
                <li role="presentation"><a href="index.html#about">About</a></li>
                <li role="presentation"><a href="index.html#contact">Contact</a></li>
            </ul>
        </nav>
        <h3 class="text-muted">RSS-2-KINDLE</h3>
    </div>

    <div class="jumbotron">
        <form class="form-signin" action="<c:url value='login'/>" method="post">
            <h2 class="form-signin-heading">Please sign in</h2>
            <label for="username" class="sr-only">Username</label>
            <input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
            <label for="password" class="sr-only">Password</label>
            <input type="password" id="password" name="password" class="form-control" placeholder="Password" required>
            <input type="hidden" name="<c:out value="${_csrf.parameterName}"/>" value="<c:out value="${_csrf.token}"/>"/>
<%--
            <div class="checkbox">
                <label>
                    <input type="checkbox" value="remember-me"> Remember me
                </label>
            </div>
--%>
            <c:if test="${not empty param.login_error}">
                <span style="color: red;">
                Incorrect username or password, try again.<br/>
                Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
                </span>
            </c:if>
            <button class="btn btn-lg btn-success btn-block" type="submit">Sign in</button>
        </form>
    </div>

    <footer class="footer">
        <p>Copyright &copy; Roundkick Studio. 2018</p>
    </footer>
</div> <!-- /container -->

<!-- Bootstrap core JavaScript -->
<script src="vendor/jquery/jquery.min.js"></script>
<script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<!-- Custom scripts for this template -->
<script src="js/freelancer.min.js"></script>
</body>

</html>

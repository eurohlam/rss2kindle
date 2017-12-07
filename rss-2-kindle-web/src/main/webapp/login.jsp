<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>RSS-2-Kindle Service</title>
    <meta name="viewport" content="width = device-width, initial-scale = 1.0">

    <!-- JQuery -->
    <script src="js/jquery-3.1.1.js"></script>

    <!-- Bootstrap -->
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <!-- Custom css -->
    <link href="css/sticky-footer.css" rel="stylesheet">
    <link href="css/signin.css" rel="stylesheet">

</head>
<body>

<div class="container">

    <form class="form-signin" action="<c:url value='login'/>" method="post">
        <h2 class="form-signin-heading">Please sign in</h2>
        <label for="username" class="sr-only">Username</label>
        <input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
        <label for="password" class="sr-only">Password</label>
        <input type="password" id="password" name="password" class="form-control" placeholder="Password" required>
        <input type="hidden" name="<c:out value="${_csrf.parameterName}"/>" value="<c:out value="${_csrf.token}"/>"/>
        <div class="checkbox">
            <label>
                <input type="checkbox" value="remember-me"> Remember me
            </label>
        </div>
        <c:if test="${not empty param.login_error}">
            <span style="color: red;">
            Your login attempt was not successful, try again.<br/>
            Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
            </span>
        </c:if>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
    </form>

    <%--<footer class="sticky-footer">--%>
        <%--<p>&copy; Created by eurohlam. 2017</p>--%>
    <%--</footer>--%>
</div> <!-- /container -->
<%@include file="jsp/footer.jsp" %>

</body>

</html>

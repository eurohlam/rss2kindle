<%@ include file="/jsp/_include.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
<head>
    <title>RSS-2-Kindle Administration</title>

    <meta name="viewport" content="width = device-width, initial-scale = 1.0">
    <security:csrfMetaTags/>

    <!-- Bootstrap -->
    <link href="../vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="../vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css">

    <!-- Theme CSS -->
    <link href="../css/freelancer.css" rel="stylesheet">

    <!-- Custom css -->
    <link href="../css/simple-sidebar.css" rel="stylesheet">

    <!-- JQuery -->
    <script src="../vendor/jquery/jquery.min.js"></script>

</head>

<body>
<script>
    var username = '${username}';
    var rootURL = 'rest/admin/users/';
    var userData;
    var csrf_token = $("meta[name='_csrf']").attr("content");
    var csrf_header = $("meta[name='_csrf_header']").attr("content");
    var csrf_headers = {};
    csrf_headers[csrf_header] = csrf_token;

    $(document).ready(function () {
        $.getJSON(rootURL, function (data) {
            var table = '<table class="table table-hover">' +
                '<tr><th>#</th>' +
                '<th>Username</th>' +
                '<th>Contact email</th>' +
                '<th>Created</th>' +
                '<th>Modified</th>' +
                '<th>Last logged in</th>' +
                '<th>Status</th>';

            $.each(data, function (i, item) {
                var tr;
                if (item.status === 'locked')
                    tr = '<tr class="danger"><td>';

                else
                    tr = '<tr class="active"><td>';

                table = table + tr
                    + i + '</td><td>'
                    + item.username + '</td><td>'
                    + item.email + '</td><td>'
                    + item.dateCreated + '</td><td>'
                    + item.dateModified + '</td><td>'
                    + item.previousLogin + '</td><td>'
                    + item.status + '</td></tr>';

            });
            table = table + '</table>';
            $('#all_users').append(table);
        })
    });
</script>
<div class="container-fluid">
    <%@include file="../_header.jsp" %>

    <div id="wrapper" class="row">

        <%@include file="_adminAside.jsp" %>

        <main id="page-content-wrapper">
            <div class="container-fluid">
                <div class="row" style="padding-top: 5rem; padding-bottom: 15rem">
                    <div id="all_users" class="col-xl-6 text-left" id="subscribers_view" style="padding-left: 2rem; padding-right: 2rem">
                        <h3 class="sub-header">Users</h3>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="../_footer.jsp"/>
</div>

<!-- Bootstrap core JavaScript -->
<%--<script src="../vendor/jquery/jquery.min.js"></script>--%>
<script src="../vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

<!-- Plugin JavaScript -->
<script src="../vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="../vendor/magnific-popup/jquery.magnific-popup.min.js"></script>

<!-- Custom scripts for this template -->
<script src="../js/freelancer.min.js"></script>


</body>
</html>

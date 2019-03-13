<%--
  User: eurohlam
  Date: 9/01/2019
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="_include.jsp" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>RSS-2-KINDLE Subscribers Management</title>

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
    var rootURL = 'rest/profile/${username}/${subscriber}';
    var userData;

    $(document).ready(function () {
        $.getJSON(rootURL, function (data) {
            userData = data;

            var rssTable = '<table class="table table-hover"><thead>' +
                '<tr><th>#</th>' +
                '<th>rss</th>' +
                '<th>status</th>' +
                '<th>last polling date</th>' +
                '<th>error message</th>' +
                '<th>retry count</th>' +
                '</tr></thead><tbody>';

            $.each(data.rsslist, function (i, item) {
                var tr;
                if (item.status === 'dead')
                    tr = '<tr class="table-danger"><td>';
                else if (item.status === 'offline')
                    tr = '<tr class="table-warning"><td>';
                else
                    tr = '<tr class="table-light"><td>';

                rssTable += tr + (i + 1) + '</td><td>'
                    + item.rss + '</td><td>'
                    + item.status + '</td><td>'
                    + item.lastPollingDate + '</td><td>'
                    + item.errorMessage + '</td><td>'
                    + item.retryCount + '</td></tr>'

            });
            rssTable += '</tbody></table>';
            $("#details").html(rssTable);
        })
    });

</script>


<div class="container-fluid">
    <%@include file="_header.jsp" %>

    <div id="wrapper" class="row">
        <%@include file="_aside.jsp"%>

        <main id="page-content-wrapper">
            <div class="container-fluid">
                <h5>Details for subscription:</h5>
                <h1>${subscriber}</h1>
                <hr class="star-dark"/>
                <div class="container-fluid">
                    <div class="btn-toolbar bg-light" role="toolbar" aria-label="">
                        <div class="btn-group mr-2" role="group" aria-label="">
                            <button id="lock_btn" class="navbar-brand" type="submit">
                                <img src="../img/icons/if_pause-circle_2561308.svg" width="30" height="30"
                                     class="d-inline-block align-top" alt="Lock">
                            </button>
                            <button id="unlock_btn" class="navbar-brand" type="submit">
                                <img src="../img/icons/if_play-circle_2561292.svg" width="30" height="30"
                                     class="d-inline-block align-top" alt="Unlock">
                            </button>
                        </div>
                        <div class="btn-group" role="group">
                            <button id="remove_btn" class="navbar-brand" type="submit">
                                <img src="../img/icons/if_trash_2561481.svg" width="30" height="30"
                                     class="d-inline-block align-top" alt="Remove">
                            </button>
                        </div>
                    </div>
                    <div id="alerts_panel" class="row"></div>
                    <div id="details" class="row" ></div>
                </div>
            </div>
        </main>
    </div>


    <jsp:include page="_footer.jsp"/>
</div>

<!-- Bootstrap core JavaScript -->
<script src="../vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

<!-- Plugin JavaScript -->
<script src="../vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="../vendor/magnific-popup/jquery.magnific-popup.min.js"></script>

<!-- Custom scripts for this template -->
<script src="../js/freelancer.min.js"></script>

</body>
</html>

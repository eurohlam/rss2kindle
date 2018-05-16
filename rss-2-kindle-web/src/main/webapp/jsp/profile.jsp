<%--
  User: eurohlam
  Date: 19/10/2017
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="_include.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>RSS-2-KINDLE Profile</title>
    <meta name="viewport" content="width = device-width, initial-scale = 1.0">

    <!-- Custom Fonts -->
    <link href="../vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet"
          type="text/css">

    <!-- Bootstrap -->
    <link href="../vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Theme CSS -->
    <link href="../css/freelancer.css" rel="stylesheet">

    <!-- Custom css -->
    <link href="../css/simple-sidebar.css" rel="stylesheet">

    <!-- JQuery -->
    <script src="../vendor/jquery/jquery.min.js"></script>
</head>

<body id="page-top">
<script>
    var rootURL = 'rest/profile/${username}';
    var userData;

    $(document).ready(function () {
        $.getJSON(rootURL, function (data) {
            userData = data;

            var subscribersTable = '<table class="table table-hover"><thead>' +
                '<tr><th>#</th>' +
                '<th>subscriber</th>' +
                '<th>email</th>' +
                '<th>status</th>' +
                '<th>number of subscriptions</th>' +
                '</tr></thead><tbody>';

            var rssTable = '<table class="table table-hover"><thead>' +
                '<tr><th>#</th>' +
                '<th>subscription</th>' +
                '<th>status</th>' +
                '<th>send to</th>' +
                '</tr></thead><tbody>';
            var rssNumber = 0;
            var suspendedSubscribersnumber = 0;
            var deadRssNumber = 0;
            var offlineRssNumber = 0;

            $.each(data.subscribers, function (i, item) {
                var tr;
                if (item.status === 'suspended') {
                    tr = '<tr class="table-danger"><td>';
                    suspendedSubscribersnumber++;
                }
                else
                    tr = '<tr class="table-light"><td>';

                subscribersTable += tr + (i + 1) + '</td><td>'
                    + item.name + '</td><td>'
                    + item.email + '</td><td>'
                    + item.status + '</td><td>';

                var rss = item.rsslist;
                subscribersTable += rss.length + '</td></tr>';

                for (j = 0; j < rss.length; j++) {
                    if (rss[j].status === 'dead') {
                        tr = '<tr class="table-danger"><td>';
                        deadRssNumber++;
                    }
                    else if (rss[j].status === 'offline') {
                        tr = '<tr class="table-warning"><td>';
                        offlineRssNumber++;
                    }
                    else
                        tr = '<tr class="table-light"><td>';

                    rssNumber++;

                    rssTable += tr + rssNumber + '</td><td>'
                        + '<a href="' + rss[j].rss + '">' + rss[j].rss + '</a></td><td>'
                        + rss[j].status + '</td><td>'
                        + item.email + '</td></tr>';
                }

            });
            subscribersTable += '</tbody></table>';
            rssTable += '</tbody></table>';

            $('#dashboard_user_status').append('<h5>User status: ' + data.status + '</h5>');
            $('#dashboard_user_info').append(
                'Contact email: ' + data.email + '<br>' +
                'Created: ' + data.dateCreated + '<br>' +
                'Modified: ' + data.dateModified + '<br>' +
                'Last logged in: ' + data.previousLogin + '<br>'
            );

            $('#dashboard_subscribers_status').append('<h5>Number of subscribers: ' + data.subscribers.length + '</h5>');
            $('#dashboard_subscribers_info').append(
                'Active subscribers:' + (data.subscribers.length - suspendedSubscribersnumber) + '<br>' +
                'Suspended subscribers: ' + suspendedSubscribersnumber + '<br><br><br>'
            );

            $('#dashboard_subscriptions_status').append('<h5>Number of subscriptions: ' + rssNumber + '</h5>');
            $('#dashboard_subscriptions_info').append(
                'Active subscriptions:' + (rssNumber - deadRssNumber - offlineRssNumber) + '<br>' +
                'Dead subscriptions: ' + deadRssNumber + '<br>' +
                'Offline subscriptions: ' + offlineRssNumber + '<br><br>'
            );

            $('#subscribers_view').append(subscribersTable);
            $('#subscriptions_view').append(rssTable);
        })
    });

</script>


<div class="container-fluid">
    <%@include file="_header.jsp" %>

    <div id="wrapper" class="row">

        <%@include file="_aside.jsp" %>

        <main id="page-content-wrapper">
            <div class="container-fluid">
                <h1 class="page-header">${username} dashboard</h1>
                <div class="row text-center" style="padding-top: 3rem; padding-bottom: 3rem">
                    <div class="col-md-4 col-lg-4" style="background-color: #c7ddef">
                        <div id="dashboard_user_status"></div>
                        <h4>User info</h4>
                        <div class="text-muted" id="dashboard_user_info"></div>
                    </div>
                    <div class="col-md-4 col-lg-4" style="background-color: #f0ad4e">
                        <div id="dashboard_subscribers_status"></div>
                        <h4>Subscribers</h4>
                        <div class="text-muted" id="dashboard_subscribers_info"></div>
                    </div>
                    <div class="col-md-4 col-lg-4" style="background-color: #c9e2b3">
                        <div id="dashboard_subscriptions_status"></div>
                        <h4>Subscriptions</h4>
                        <div class="text-muted" id="dashboard_subscriptions_info"></div>
                    </div>
                </div>
                <div class="row" style="padding-top: 5rem; padding-bottom: 15rem">
                    <div class="col-xl-6 text-left" id="subscribers_view" style="padding-left: 2rem; padding-right: 2rem">
                        <h3 class="sub-header">Subscribers</h3>
                    </div>
                    <div class="col-xl-6 text-left" id="subscriptions_view" style="padding-left: 2rem; padding-right: 2rem">
                        <h3 class="sub-header">Subscriptions</h3>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="_footer.jsp"/>
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
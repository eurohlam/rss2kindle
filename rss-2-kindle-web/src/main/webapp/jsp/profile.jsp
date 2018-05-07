<%--
  User: eurohlam
  Date: 19/10/2017
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="include.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>RSS-2-KINDLE Profile</title>
    <meta name="viewport" content="width = device-width, initial-scale = 1.0">

    <!-- JQuery -->
    <script src="../js/jquery.min.js"></script>

    <!-- Custom Fonts -->
    <link href="../font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css">

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

    <!-- Theme CSS -->
    <link href="../css/freelancer.css" rel="stylesheet">
    <!-- Theme JavaScript -->
    <script src="../js/freelancer.min.js"></script>

    <!-- Custom css -->
    <link href="../css/sticky-footer.css" rel="stylesheet">
    <link href="../css/profile-theme.css" rel="stylesheet">

</head>

<body>
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
                    tr = '<tr class="danger"><td>';
                    suspendedSubscribersnumber++;
                }
                else
                    tr = '<tr class="active"><td>';

                subscribersTable += tr + (i + 1) + '</td><td>'
                    + item.name + '</td><td>'
                    + item.email + '</td><td>'
                    + item.status + '</td><td>';

                var rss = item.rsslist;
                subscribersTable += rss.length + '</td></tr>';

                for (j = 0; j < rss.length; j++) {
                    if (rss[j].status === 'dead') {
                        tr = '<tr class="danger"><td>';
                        deadRssNumber++;
                    }
                    else if (rss[j].status === 'offline') {
                        tr = '<tr class="warning"><td>';
                        offlineRssNumber++;
                    }
                    else
                        tr = '<tr class="active"><td>';

                    rssNumber++;

                    rssTable += tr + rssNumber + '</td><td>'
                        + '<a href="' + rss[j].rss + '">' + rss[j].rss + '</a></td><td>'
                        + rss[j].status + '</td><td>'
                        + item.email +'</td></tr>';
                }

            });
            subscribersTable += '</tbody></table>';
            rssTable += '</tbody></table>';

            $('#dashboard_user_status').append('<h4>User status: ' +data.status + '</h4>');
            $('#dashboard_user_info').append(
                'Contact email: ' + data.email + '<br>' +
                'Created: ' + data.dateCreated + '<br>' +
                'Modified: ' + data.dateModified + '<br>' +
                'Last logged in: ' + data.previousLogin + '<br>'
            );

            $('#dashboard_subscribers_status').append('<h4>Number of subscribers: ' + data.subscribers.length + '</h4>');
            $('#dashboard_subscribers_info').append(
                'Active subscribers:' + (data.subscribers.length - suspendedSubscribersnumber) + '<br>' +
                'Suspended subscribers: ' + suspendedSubscribersnumber + '<br><br><br>'
            );

            $('#dashboard_subscriptions_status').append('<h4>Number of subscriptions: ' + rssNumber + '</h4>');
            $('#dashboard_subscriptions_info').append(
                'Active subscriptions:' + (rssNumber - deadRssNumber - offlineRssNumber) + '<br>' +
                'Dead subscriptions: ' + deadRssNumber + '<br>' +
                'Offline subscriptions: ' + offlineRssNumber +'<br><br>'
            );

            $('#subscribers_view').append(subscribersTable);
            $('#subscriptions_view').append(rssTable);
        })
    });

</script>

<div class="container-fluid">
    <%@include file="header.jsp"%>

    <div class="row">
        <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
            <ul class="nav nav-pills nav-stacked">
                <li role="presentation" class="active"><a href="#">My Profile</a></li>
                <li role="presentation"><a href="subscribers">Subscriber Management</a></li>
                <li role="presentation"><a href="service">Services</a></li>
            </ul>
        </nav>
        <main role="main" class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <h1 class="page-header">${username} dashboard</h1>
            <section class="row text-center placeholders">
                <div class="col-md-4 col-sm-4 placeholder" style="background-color: #c7ddef">
                    <div id="dashboard_user_status"></div>
                    <h3>User info</h3>
                    <div class="text-muted" id="dashboard_user_info"></div>
                </div>
                <div class="col-md-4 col-sm-4 placeholder" style="background-color: #f0ad4e">
                    <div id="dashboard_subscribers_status"></div>
                    <h3>Subscribers</h3>
                    <div class="text-muted" id="dashboard_subscribers_info"></div>
                </div>
                <div class="col-md-4 col-sm-4 placeholder" style="background-color: #c9e2b3">
                    <div id="dashboard_subscriptions_status"></div>
                    <h3>Subscriptions</h3>
                    <div class="text-muted" id="dashboard_subscriptions_info"></div>
                </div>
            </section>
            <section class="row placeholders">
                <div class="col-md-6 placeholder text-left" id="subscribers_view">
                    <h2 class="sub-header">Subscribers</h2>
                </div>
                <div class="col-md-6 placeholder text-left" id="subscriptions_view">
                    <h2 class="sub-header">Subscriptions</h2>
                </div>
            </section>
        </main>
    </div>

</div>

<%@include file="footer.jsp" %>

</body>
</html>
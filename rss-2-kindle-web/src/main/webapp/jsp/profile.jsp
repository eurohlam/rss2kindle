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
    var rootURL = '/rss2kindle/rest/profile/<%=username%>';
    var userData;

    $(document).ready(function () {
        $('#profile_view').append('<p id="message">Getting subscribers. Please wait...</p>');
        $.getJSON(rootURL, function (data) {
            userData = data;

            $('#dashboard_user_data').append(
                 'User status: ' +data.status
            );
            $('#dashboard_user_info').append(
                'Created: ' + data.dateCreated + '<br/>' +
                'Modified: ' + data.dateModified + '<br/>' +
                'Last logged in: ' + data.lastLogin
            );

            $('#dashboard_subscribers_data').append('Number of subscribers: ' + data.subscribers.length);

            var subscribersTable = '<table class="table table-hover"><thead>' +
                '<tr><th>#</th>' +
                '<th>subscriber</th>' +
                '<th>email</th>' +
                '<th>status</th>' +
                '</tr></thead><tbody>';

            var rssTable = '<table class="table table-hover"><thead>' +
                '<tr><th>#</th>' +
                '<th>subscription</th>' +
                '<th>status</th>' +
                '</tr></thead><tbody>';
            var rssNumber = 0;

            $.each(data.subscribers, function (i, item) {
                var tr;
                if (item.status === 'suspended')
                    tr = '<tr class="danger"><td>';
                else
                    tr = '<tr class="active"><td>';

                subscribersTable += tr + (i + 1) + '</td><td>'
                    + item.name + '</td><td>'
                    + item.email + '</td><td>'
                    + item.status + '</td><td>';
                subscribersTable += '</td></tr>';

                var rss = item.rsslist;
                for (j = 0; j < rss.length; j++) {
                    if (rss[j].status === 'dead')
                        tr = '<tr class="danger"><td>';
                    else
                        tr = '<tr class="active"><td>';

                    rssTable += tr + rssNumber + '</td><td>'
                        + '<a href="' + rss[j].rss + '">' + rss[j].rss + '</a></td><td>'
                        + rss[j].status + '</td></tr>';
                    rssNumber++;
                }

            });
            subscribersTable += '</tbody></table>';
            rssTable += '</tbody></table>';

            $('#message').remove();
            $('#dashboard_subscriptions_data').append("Number of subscriptions: " + rssNumber);
            $('#subscribers_view').append(subscribersTable);
            $('#subscriptions_view').append(rssTable);
        })
    });

    function getUserData() {
        return userData;
    }
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
            <h1 class="page-header"><%=username%> dashboard</h1>
            <section class="row text-center placeholders">
                <div class="col-md-4 col-sm-4 placeholder">
                    <div id="dashboard_user_data"></div>
                    <h4>User info</h4>
                    <div class="text-muted" id="dashboard_user_info"></div>
                </div>
                <div class="col-md-4 col-sm-4 placeholder">
                    <div id="dashboard_subscribers_data"></div>
                    <h4>Subscribers</h4>
                    <div class="text-muted">Something else</div>
                </div>
                <div class="col-md-4 col-sm-4 placeholder">
                    <div id="dashboard_subscriptions_data"></div>
                    <h4>Subscriptions</h4>
                    <div class="text-muted">Something else</div>
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
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
                '<p>Created: ' + data.dateCreated + '</p>' +
                '<p>Status: ' + data.status + '</p>');

            $('#dashboard_subscribers_data').append('<p>' + data.subscribers.length + '</p>');

            var table = '<table class="table table-hover"><thead>' +
                '<tr><th>#</th>' +
                '<th>title</th>' +
                '<th>email</th>' +
                '<th>status</th>' +
                '<th>rss</th></tr></thead><tbody>';

            $.each(data.subscribers, function (i, item) {
                var tr;
                if (item.status === 'locked')
                    tr = '<tr class="danger"><td>';
                /*
                 else if (item.status === 'suspended')
                 tr='<tr class="warning"><td>';
                 */
                else
                    tr = '<tr class="active"><td>';

                table = table + tr
                    + i + '</td><td>'
                    + item.name + '</td><td>'
                    + item.email + '</td><td>'
                    + item.status + '</td><td>';
                var rss = item.rsslist;
                rssTable = '<table width="100%"><tr><td>';
                for (j = 0; j < rss.length; j++) {
                    rssTable = rssTable + '<a href="' + rss[j].rss + '">' + rss[j].rss + '</a></td><td>';
                    if (rss[j].status === 'active')
                        rssTable = rssTable + '<label></label><input type="checkbox" checked disabled />' + rss[j].status + '</label>';
                    else
                        rssTable = rssTable + '<label></label><input type="checkbox" disabled />' + rss[j].status + '</label>';

                    rssTable = rssTable + '<td/></tr>';
                }
                rssTable = rssTable + '</table>';

                table = table + rssTable + '</td></tr>';
            });
            table = table + '</tbody></table>';
            $('#message').remove();
            $('#profile_view').append(table);
        })
    });

    function getUserData() {
        return userData;
    }
</script>

<div class="container-fluid">
    <header class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
                <li role="presentation" class="active"><a href="../index.html">Home</a></li>
                <li role="presentation"><a href="#">About</a></li>
                <li role="presentation"><a href="#">Contact</a></li>
            </ul>
        </nav>
        <h3 class="text-muted">RSS-2-KINDLE</h3>
    </header>

    <div class="row">
        <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
            <ul class="nav nav-pills flex-column">
                <li role="presentation" class="active"><a href="#">My Profile</a></li>
                <li role="presentation"><a href="subscribers">Subscriber Management</a></li>
                <li role="presentation"><a href="service">Services</a></li>
            </ul>
        </nav>
        <main role="main" class="col-sm-9 col-md-10">
            <h1><%=username%> dashboard</h1>
            <section class="row text-center placeholders">
                <div class="col-6 col-sm-3 placeholder">
                    <div id="dashboard_user_data"></div>
                    <h4>Profile data</h4>
                    <div class="text-muted">General Information</div>
                </div>
                <div class="col-6 col-sm-3 placeholder">
                    <div id="dashboard_subscribers_data"></div>
                    <h4>Number of subscribers</h4>
                    <div class="text-muted">Something else</div>
                </div>
                <div class="col-6 col-sm-3 placeholder">
                    <div id="dashboard_subscriptions_data">
                        <p>Tralala</p>
                    </div>
                    <h4>Number of subscriptions</h4>
                    <div class="text-muted">Something else</div>
                </div>
            </section>
            <h1>Subscribers</h1>
            <div class="table-responsive" id="profile_view">

            </div>
        </main>
    </div>

</div>

<%@include file="footer.jsp" %>

</body>
</html>
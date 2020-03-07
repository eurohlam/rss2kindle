<%--
  User: eurohlam
  Date: 19/10/2017
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="_include.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <%@include file="_head.jsp" %>
</head>

<body id="page-top">
<script>
    var rootURL = 'rest/profile/${username}';
    var userData;

    $(document).ready(function () {
        $.getJSON(rootURL, function (data) {
            userData = data;

            var subscribersTable = $('<table>')
                .addClass('table table-hover')
                .append('<thead>' +
                    '<tr><th>#</th>' +
                    '<th>subscriber</th>' +
                    '<th>email</th>' +
                    '<th>status</th>' +
                    '<th>number of subscriptions</th>' +
                    '</tr></thead>')
                .append('<tbody>');

            var rssTable = $('<table>').addClass('table table-hover')
                .append('<thead>' +
                    '<tr><th>#</th>' +
                    '<th>subscription</th>' +
                    '<th>status</th>' +
                    '<th>send to</th>' +
                    '</tr></thead>')
                .append('<tbody>');
            var rssNumber = 0;
            var suspendedSubscribersnumber = 0;
            var deadRssNumber = 0;
            var offlineRssNumber = 0;

            $.each(data.subscribers, function (i, item) {
                var rss = item.rsslist;
                var tr = $('<tr>');

                if (item.status === 'suspended') {
                    tr.addClass('table-danger');
                    suspendedSubscribersnumber++;
                }
                else {
                    tr.addClass('table-light');
                }

                tr.append('<td>' + (i +1) + '</td>')
                    .append('<td><a href="subscriberDetails?subscriber=' + item.email + '">' + item.name + '</a></td>')
                    .append('<td><a href="subscriberDetails?subscriber=' + item.email + '">' + item.email + '</a></td>')
                    .append('<td>' + item.status + '</td>')
                    .append('<td>'+ rss.length + '</td>');
                subscribersTable.append(tr);

                for (j = 0; j < rss.length; j++) {
                    var rssTr = $('<tr>');
                    if (rss[j].status === 'dead') {
                        rssTr.addClass('table-danger');
                        deadRssNumber++;
                    }
                    else if (rss[j].status === 'offline') {
                        rssTr.addClass('table-warning');
                        offlineRssNumber++;
                    }
                    else {
                        rssTr.addClass('table-light');
                    }

                    rssNumber++;

                    rssTr.append('<td>' + rssNumber + '</td>')
                        .append('<td><a href="' + rss[j].rss + '" target="_blank">' + rss[j].rss + '</a></td>')
                        .append('<td>' + rss[j].status + '</td>')
                        .append('<td>' + item.email + '</td>');
                    rssTable.append(rssTr);
                }

            });

            $('#dashboard_user_status').append('User status: ' + data.status);
            $('#dashboard_user_info').append(
                'Contact email: ' + data.email + '<br>' +
                'Created: ' + data.dateCreated + '<br>' +
                'Modified: ' + data.dateModified + '<br>' +
                'Last logged in: ' + data.previousLogin + '<br>'
            );

            $('#dashboard_subscribers_status').append('Number of subscribers: ' + data.subscribers.length);
            $('#dashboard_subscribers_info').append(
                'Active subscribers:' + (data.subscribers.length - suspendedSubscribersnumber) + '<br>' +
                'Suspended subscribers: ' + suspendedSubscribersnumber + '<br><br><br>'
            );

            $('#dashboard_subscriptions_status').append('Number of subscriptions: ' + rssNumber);
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


<div>
    <%@include file="_header.jsp" %>

    <div id="wrapper" class="d-flex">

        <%@include file="_aside.jsp" %>

        <main id="page-content-wrapper">
            <div class="container-fluid">
                <div class="text-center">
                    <h5>Profile overview for:</h5>
                    <h1>${username}</h1>
                    <hr class="star-dark"/>
                </div>
                <div class="row text-center">
                    <div class="col-md-4 col-lg-4" style="padding-top: 1rem">
                        <div class="card" style="background-color: #c7ddef">
                            <h6 id="dashboard_user_status" class="card-header"></h6>
                            <h4 class="card-title">User info</h4>
                            <div class="card-text text-muted" id="dashboard_user_info"></div>
                        </div>
                    </div>
                    <div class="col-md-4 col-lg-4" style="padding-top: 1rem">
                        <div class="card" style="background-color: #f0ad4e">
                            <h6 id="dashboard_subscribers_status" class="card-header"></h6>
                            <h4 class="card-title">Subscribers</h4>
                            <div class="card-text text-muted" id="dashboard_subscribers_info"></div>
                        </div>
                    </div>
                    <div class="col-md-4 col-lg-4" style="padding-top: 1rem">
                        <div class="card"  style="background-color: #c9e2b3">
                            <h6 id="dashboard_subscriptions_status" class="card-header"></h6>
                            <h4 class="card-title">Subscriptions</h4>
                            <div class="card-text text-muted" id="dashboard_subscriptions_info"></div>
                        </div>
                    </div>
                </div>
                <div class="row" style="padding-top: 2rem">
                    <div class="col-xl-6 text-left" style="padding-top: 1rem">
                        <div class="card">
                            <h4 class="card-header">Subscribers</h4>
                            <div id="subscribers_view" class="table-responsive"></div>
                        </div>
                    </div>
                    <div class="col-xl-6 text-left" style="padding-top: 1rem">
                        <div class="card">
                            <h4 class="card-header">Subscriptions</h4>
                            <div id="subscriptions_view" class="table-responsive"></div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="_footer.jsp"/>
</div>

</body>
</html>
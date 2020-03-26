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

        function showSubscribersTable(subscribers, maxPerPage, pageNumber) {
            if (!maxPerPage || maxPerPage < 0) {
                maxPerPage = 10;
            }
            if (!pageNumber || pageNumber < 0) {
                pageNumber = 1;
            }
            var startIndex = (pageNumber - 1) * maxPerPage;
            var endIndex = pageNumber * maxPerPage;

            var subscribersTable = $('<table>')
                .addClass('table table-hover')
                .append('<thead>\
                            <tr><th>#</th>\
                            <th>subscriber</th>\
                            <th>email</th>\
                            <th>status</th>\
                            <th>number of subscriptions</th>\
                        </tr></thead>')
                .append('<tbody>');

            $.each(subscribers, function (i, subscriber) {
                if (i >= startIndex && i < endIndex) {
                    var tr = $('<tr>');

                    if (subscriber.status === 'suspended') {
                        tr.addClass('table-danger');
                    } else {
                        tr.addClass('table-light');
                    }

                    tr.append('<td>' + (i + 1) + '</td>')
                        .append('<td><a href="subscriberDetails?subscriber=' + subscriber.email + '">' + subscriber.name + '</a></td>')
                        .append('<td><a href="subscriberDetails?subscriber=' + subscriber.email + '">' + subscriber.email + '</a></td>')
                        .append('<td>' + subscriber.status + '</td>')
                        .append('<td>' + subscriber.rsslist.length + '</td>');
                    subscribersTable.append(tr);
                }
            });
            $('#subscribers_view').html(subscribersTable);

            //add pagination bar
            if (subscribers.length > maxPerPage) {
                generatePaginationBar($('#subscribers_pagination'), subscribers, maxPerPage, pageNumber);
                $('#subscribers_pagination a').click(function (event) {
                    event.preventDefault();
                    var button = $(event.currentTarget);
                    var clickedPageNumber = button.data('page');
                    showSubscribersTable(subscribers, maxPerPage, clickedPageNumber);
                });
            }

        } //end of showSubscribersTable

        function showSubscriptionsTable(subscriptions, maxPerPage, pageNumber) {
            if (!maxPerPage || maxPerPage < 0) {
                maxPerPage = 10;
            }
            if (!pageNumber || pageNumber < 0) {
                pageNumber = 1;
            }
            var startIndex = (pageNumber - 1) * maxPerPage;
            var endIndex = pageNumber * maxPerPage;

            var rssTable = $('<table>').addClass('table table-hover')
                .append('<thead>\
                            <tr><th>#</th>\
                            <th>subscription</th>\
                            <th>status</th>\
                            <th>send to</th>\
                         </tr></thead>')
                .append('<tbody>');

            $.each(subscriptions, function (i, rss) {
                if (i >= startIndex && i < endIndex) {
                    var rssTr = $('<tr>');
                    if (rss.status === 'dead') {
                        rssTr.addClass('table-danger');
                    } else if (rss.status === 'offline') {
                        rssTr.addClass('table-warning');
                    } else {
                        rssTr.addClass('table-light');
                    }

                    rssTr.append('<td>' + (i + 1) + '</td>')
                        .append('<td><a href="' + rss.rss + '" target="_blank">' + rss.rss + '</a></td>')
                        .append('<td>' + rss.status + '</td>')
                        .append('<td>' + rss.email + '</td>');
                    rssTable.append(rssTr);
                }
            });
            $('#subscriptions_view').html(rssTable);

            //add pagination bar
            if (subscriptions.length > maxPerPage) {
                generatePaginationBar($('#subscription_pagination'), subscriptions, maxPerPage, pageNumber);
                $('#subscription_pagination a').click(function (event) {
                    event.preventDefault();
                    var button = $(event.currentTarget);
                    var clickedPageNumber = button.data('page');
                    showSubscriptionsTable(subscriptions, maxPerPage, clickedPageNumber);
                });
            }
        } // end of showSubscriptionsTable

        function generatePaginationBar(parentUl, linkList, maxPerPage, pageNumber) {
            var numberOfPages = Math.ceil(linkList.length / maxPerPage);
            parentUl.empty();

            if (pageNumber === 1) {
                //disable previous button
                parentUl.append('<li class="page-item disabled">\
                             <span class="page-link">\
                                <span aria-hidden="true">&laquo;</span>\
                             </span>\
                         </li>');
            } else {
                parentUl.append('<li class="page-item">\
                             <a class="page-link" href="#" data-page="' + (pageNumber - 1) + '" aria-label="Previous">\
                                <span aria-hidden="true">&laquo;</span>\
                             </a>\
                         </li>');
            }
            for (i = 1; i <= numberOfPages; i++) {
                if (pageNumber === i) {
                    //highlight current page button
                    parentUl.append('<li class="page-item active" aria-label="page" >\
                                    <span class="page-link">'
                        + i + '<span class="sr-only">(current)</span>\
                                    </span>\
                               </li>');
                } else {
                    parentUl.append('<li class="page-item">\
                                  <a class="page-link" href="#" data-page="' + i + '">' + i + '</a>\
                              </li>');
                }
            }
            if (pageNumber === numberOfPages) {
                //disable next button
                parentUl.append('<li class="page-item disabled">\
                             <span class="page-link">\
                                 <span aria-hidden="true">&raquo;</span>\
                             </span>\
                          </li>');
            } else {
                parentUl.append('<li class="page-item">\
                             <a class="page-link" href="#" data-page="' + (pageNumber + 1) + '" aria-label="Next">\
                                 <span aria-hidden="true">&raquo;</span>\
                             </a>\
                          </li>');
            }
        }

        function showUserSummary(user) {
            $('#dashboard_user_status').append('User status: ' + user.status);
            $('#dashboard_user_info').append(
                'Contact email: ' + user.email + '<br>' +
                'Created: ' + user.dateCreated + '<br>' +
                'Modified: ' + user.dateModified + '<br>' +
                'Last logged in: ' + user.previousLogin + '<br>'
            );
        }

        function showSubscribersSummary(subscribers) {
            var activeCount = 0;
            $.each(subscribers, function (i, subscriber) {
                if (subscriber.status === 'active') {
                    activeCount++;
                }
            });
            $('#dashboard_subscribers_status').append('Number of subscribers: ' + subscribers.length);
            $('#dashboard_subscribers_info').append(
                'Active subscribers:' + activeCount + '<br>' +
                'Suspended subscribers: ' + (subscribers.length - activeCount) + '<br><br><br>'
            );
        }

        function showSubscriptionsSummary(subscribers) {
            var rssNumber = 0;
            var deadRssNumber = 0;
            var offlineRssNumber = 0;
            $.each(subscribers, function (i, subscriber) {
                $.each(subscriber.rsslist, function (r, rss) {
                    if (rss.status === 'dead') {
                        deadRssNumber++;
                    } else if (rss.status === 'offline') {
                        offlineRssNumber++;
                    }
                    rssNumber++;
                });
            });
            $('#dashboard_subscriptions_status').append('Number of subscriptions: ' + rssNumber);
            $('#dashboard_subscriptions_info').append(
                'Active subscriptions:' + (rssNumber - deadRssNumber - offlineRssNumber) + '<br>' +
                'Dead subscriptions: ' + deadRssNumber + '<br>' +
                'Offline subscriptions: ' + offlineRssNumber + '<br><br>'
            );
        }

        function showDashboard() {
            $.getJSON(rootURL, function (data) {
                userData = data;
                showUserSummary(userData);
                showSubscribersSummary(userData.subscribers);
                showSubscriptionsSummary(userData.subscribers);
                showSubscribersTable(userData.subscribers, 10, 1);

                var rssList = [];
                $.each(userData.subscribers, function (i, subscriber) {
                    $.each(subscriber.rsslist, function(r, rss) {
                        rss.email = subscriber.email;
                        rssList.push(rss);
                    });
                });
                showSubscriptionsTable(rssList, 10, 1);
            })
        }

        showDashboard();
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
                        <div class="card" style="background-color: #c9e2b3">
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
                            <nav class="card-footer" aria-label="subscribers">
                                <ul id="subscribers_pagination" class="pagination justify-content-end"></ul>
                            </nav>
                        </div>
                    </div>
                    <div class="col-xl-6 text-left" style="padding-top: 1rem">
                        <div class="card">
                            <h4 class="card-header">Subscriptions</h4>
                            <div id="subscriptions_view" class="table-responsive"></div>
                            <nav class="card-footer" aria-label="subscriptions">
                                <ul id="subscription_pagination" class="pagination justify-content-end"></ul>
                            </nav>
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
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
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet"
          type="text/css">

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

        reloadRssTable();

        function reloadRssTable() {
            $.getJSON(rootURL, function (data) {
                userData = data;

                var rssTable = '<table class="table table-hover"><thead>' +
                    '<tr>' +
                    '<th><input type="checkbox" class="form-check-input" id="select_all_checkbox"/></th>' +
                    '<th>#</th>' +
                    '<th>rss</th>' +
                    '<th>status</th>' +
                    '<th>last polling date</th>' +
                    '<th>error message</th>' +
                    '<th>retry count</th>' +
                    '</tr></thead><tbody>';

                $.each(data.rsslist, function (i, item) {
                    var tr;
                    if (item.status === 'dead') {
                        tr = '<tr class="table-danger">';
                    }
                    else if (item.status === 'offline') {
                        tr = '<tr class="table-warning">';
                    }
                    else {
                        tr = '<tr class="table-light">';
                    }

                    tr += '<td><input type="checkbox" class="form-check-input" id="' + item.rss + '"/></td><td>';

                    rssTable += tr + (i + 1) + '</td><td>'
                        + item.rss + '</td><td>'
                        + item.status + '</td><td>'
                        + item.lastPollingDate + '</td><td>'
                        + item.errorMessage + '</td><td>'
                        + item.retryCount + '</td></tr>'

                });
                rssTable += '</tbody></table>';
                $("#details").html(rssTable);
            });
        }//end of reloadRssTable

        $('#subscribers_form').submit(function (e) {
            e.preventDefault();
            var srcButtonId = $(document.activeElement).attr('id');
            var operation;
            var method;
            var message;
            if (srcButtonId == 'lock_btn') {
                operation = '/lock';
                message = 'Users locked successfully';
                method = 'GET';
            } else if (srcButtonId == 'unlock_btn') {
                operation = '/unlock';
                message = 'Users unlocked successfully';
                method = 'GET';
            } else if (srcButtonId == 'remove_btn') {
                operation = '/remove';
                message = 'Users removed successfully';
                method = 'DELETE';
            }
            $("input:checked[id!='select_all_checkbox']").each(function (index) {
                var checkedRss = $(this).attr('id');
                /*$.ajax({
                    url: rootURL + checkedUser + operation,
                    type: method,
                    dataType: 'json',
                    headers: csrf_headers
                })
                    .done(function () {
                        showAlert('success', message);
                        reloadUsersTable();
                        return true;
                    })
                    .fail(function () {
                        showAlert('error', 'Failed updating user ' + checkedUser);
                        return false;
                    })*/
            }); //each

        }); //subscribers_form.submit

        $(function () {
            $('[data-toggle="tooltip"]').tooltip()
        });

    }); //end of $(document).ready(function ())

</script>


<div class="container-fluid">
    <%@include file="_header.jsp" %>

    <div id="wrapper" class="row">
        <%@include file="_aside.jsp" %>

        <main id="page-content-wrapper">
            <div class="container-fluid">
                <div class="text-center">
                    <h5>Details of subscriptions for subscriber:</h5>
                    <h1>${subscriber}</h1>
                    <hr class="star-dark"/>
                </div>
                <div class="container-fluid">
                    <form id="subscribers_form" action="" method="post">
                        <div class="btn-toolbar bg-light" role="toolbar" aria-label="">
                            <div class="btn-group mr-2" role="group" aria-label="">
                                <button id="unlock_btn" class="navbar-brand btn-outline-primary" type="submit" data-toggle="tooltip" data-placement="top" title="Activate subscriptions">
                                    <img src="../img/icons/if_play-circle_2561292.svg" width="30" height="30"
                                         class="d-inline-block align-top" alt="Unlock">
                                </button>
                                <button id="lock_btn" class="navbar-brand btn-outline-warning" type="submit" data-toggle="tooltip" data-placement="top" title="Deactivate subscriptions">
                                    <img src="../img/icons/if_pause-circle_2561308.svg" width="30" height="30"
                                         class="d-inline-block align-top" alt="Lock">
                                </button>
                            </div>
                            <div class="btn-group" role="group">
                                <button id="remove_btn" class="navbar-brand btn-outline-danger" type="submit" data-toggle="tooltip" data-placement="top" title="Remove subscriptions">
                                    <img src="../img/icons/if_trash_2561481.svg" width="30" height="30"
                                         class="d-inline-block align-top" alt="Remove">
                                </button>
                            </div>
                        </div>
                        <div id="alerts_panel" class="row"></div>
                        <div id="details" class="row"></div>
                    </form>
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

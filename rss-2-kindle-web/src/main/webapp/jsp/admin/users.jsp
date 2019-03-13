<%@ include file="/jsp/_include.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
<head>
    <title>RSS-2-KINDLE Users Management</title>

    <meta name="viewport" content="width = device-width, initial-scale = 1.0">
    <security:csrfMetaTags/>

    <!-- Bootstrap -->
    <link href="../../vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="../../vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet"
          type="text/css">

    <!-- Theme CSS -->
    <link href="../../css/freelancer.css" rel="stylesheet">

    <!-- Custom css -->
    <link href="../../css/simple-sidebar.css" rel="stylesheet">

    <!-- JQuery -->
    <script src="../../vendor/jquery/jquery.min.js"></script>

</head>

<body>
<script>
    var adminUsername = '${username}';
    var rootURL = '../rest/admin/';
    var csrf_token = $("meta[name='_csrf']").attr("content");
    var csrf_header = $("meta[name='_csrf_header']").attr("content");
    var csrf_headers = {};
    csrf_headers[csrf_header] = csrf_token;

    $(document).ready(function () {

        reloadUsersTable();

        function reloadUsersTable() {
            $.getJSON(rootURL + 'users', function (data) {
                var table = '<table class="table table-hover">' +
                    '<tr>' +
                    '<th><input type="checkbox" class="form-check-input" id="select_all_checkbox"/></th>' +
                    '<th>#</th>' +
                    '<th>username</th>' +
                    '<th>contact email</th>' +
                    '<th>created</th>' +
                    '<th>modified</th>' +
                    '<th>last logged in</th>' +
                    '<th>status</th>' +
                    '<th>roles</th>';

                $.each(data, function (i, item) {
                    var tr;
                    if (item.status === 'locked') {
                        tr = '<tr class="table-danger">'
                            + '<td><input type="checkbox" class="form-check-input" id="' + item.username + '"/></td><td>';
                    } else if (item.username === adminUsername) {
                        tr = '<tr class="table-active"><td></td><td>';
                    } else {
                        tr = '<tr class="table-light">'
                        + '<td><input type="checkbox" class="form-check-input" id="' + item.username + '"/></td><td>';
                    }
                    table = table + tr
                        + (i + 1) + '</td><td>'
                        + item.username + '</td><td>'
                        + item.email + '</td><td>'
                        + item.dateCreated + '</td><td>'
                        + item.dateModified + '</td><td>'
                        + item.previousLogin + '</td><td>'
                        + item.status + '</td><td>'
                        + item.roles + '</td></tr>'

                });
                table = table + '</table>';
                $('#all_users').html(table);

                $("#select_all_checkbox").change(function (e) {
                    if ($("#select_all_checkbox").is(':checked')) {
                        $("input[type='checkbox']").each(function (index) {
                            $(this).prop('checked', true); //check all
                        });
                    } else {
                        $("input[type='checkbox']").each(function (index) {
                            $(this).prop('checked', false); //uncheck all
                        });
                    }
                }); //select_all_checkbox.change

            })
        } //reloadUsersTable

        $('#users_form').submit(function (e) {
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
                var checkedUser = $(this).attr('id');
                $.ajax({
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
                    })
            }); //each

        }); //users_form.submit


    }); //end of $(document).ready(function ())

    function showAlert(type, text) {
        if (type == 'error') {
            $('#alerts_panel').html('<div class="alert alert-danger alert-dismissible" role="alert">'
                + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<strong>Error! </strong>' + text + '</div>');
        } else if (type == 'warning') {
            $('#alerts_panel').html('<div class="alert alert-warning alert-dismissible" role="alert">'
                + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<strong>Warning! </strong>' + text + '</div>');
        } else {
            $('#alerts_panel').html('<div class="alert alert-success alert-dismissible" role="alert">'
                + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<strong>Success! </strong> ' + text + '</div>');
        }
    }

</script>
<div class="container-fluid">
    <%@include file="../_header.jsp" %>

    <div id="wrapper" class="row">

        <%@include file="_adminAside.jsp" %>

        <main id="page-content-wrapper">
            <div class="container-fluid">
                <div class="row" style="padding-top: 5rem; padding-bottom: 15rem">
                    <form id="users_form" action="" method="post">
                        <div class="text-left" id="subscribers_view" style="padding-left: 2rem; padding-right: 2rem">
                            <h3>User management</h3>
                            <nav class="navbar navbar-light bg-light">
                                <button id="lock_btn" class="navbar-brand" type="submit">
                                    <img src="../../img/icons/if_pause-circle_2561308.svg" width="30" height="30"
                                         class="d-inline-block align-top" alt="Lock">
                                </button>
                                <button id="unlock_btn" class="navbar-brand" type="submit">
                                    <img src="../../img/icons/if_play-circle_2561292.svg" width="30" height="30"
                                         class="d-inline-block align-top" alt="Unlock">
                                </button>
                                <button id="remove_btn" class="navbar-brand" type="submit">
                                    <img src="../../img/icons/if_trash_2561481.svg" width="30" height="30"
                                         class="d-inline-block align-top" alt="Remove">
                                </button>
                            </nav>
                            <div id="alerts_panel"></div>
                            <div id="all_users"></div>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="../_footer.jsp"/>
</div>

<!-- Bootstrap core JavaScript -->
<%--<script src="../vendor/jquery/jquery.min.js"></script>--%>
<script src="../../vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

<!-- Plugin JavaScript -->
<script src="../../vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="../../vendor/magnific-popup/jquery.magnific-popup.min.js"></script>

<!-- Custom scripts for this template -->
<script src="../../js/freelancer.min.js"></script>


</body>
</html>

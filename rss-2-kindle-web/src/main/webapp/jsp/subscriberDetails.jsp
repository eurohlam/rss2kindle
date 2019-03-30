<%--
  User: eurohlam
  Date: 9/01/2019
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="_include.jsp" %>
<html lang="en">
<head>
    <%@include file="_head.jsp"%>
</head>

<body id="page-top">
<script>
    var rootURL = 'rest/profile/${username}';
    var userData;
    var csrf_token = $("meta[name='_csrf']").attr("content");
    var csrf_header = $("meta[name='_csrf_header']").attr("content");
    var csrf_headers = {};
    csrf_headers[csrf_header] = csrf_token;

    $(document).ready(function () {

        //enable bootstrap tooltip
        $(function () {
            $('[data-toggle="tooltip"]').tooltip()
        });

        reloadRssTable();

        function reloadRssTable() {
            $.getJSON(rootURL+ '/${subscriber}', function (data) {
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
                        + '<a href="' + item.rss + '" target="_blank">' + item.rss + '</a></td><td>'
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
            var message;
            if (srcButtonId == 'deactivate_btn') {
                operation = 'deactivate';
                message = 'Subscriptions deactivated successfully';
            } else if (srcButtonId == 'activate_btn') {
                operation = 'activate';
                message = 'Subscriptions activated successfully';
            } else if (srcButtonId == 'remove_btn') {
                operation = 'remove';
                message = 'Subscriptions removed successfully';
            }
            $("input:checked[id!='select_all_checkbox']").each(function (index) {
                var checkedRss = $(this).attr('id');
                $.each(userData.rsslist, function (i, item) {
                    if (checkedRss === item.rss) {
                        if (operation === 'activate') {
                            item.status = 'active';
                            item.errorMessage = 'Manually activated by user';
                            item.retryCount = '0';
                        }
                        if (operation === 'deactivate') {
                            item.status = 'offline';
                            item.errorMessage = 'Manually deactivated by user';
                        }
                        if (operation === 'remove') {
                            delete userData.rsslist[i]; //TODO: removing rss from list does not work properly
                        }
                    }
                });
            }); //each

            $.ajax({
                url: rootURL + "/update",
                type: 'PUT',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(userData),
                headers: csrf_headers
            })
                .done(function () {
                    showAlert('success', message);
                    reloadRssTable();
                    return true;
                })
                .fail(function () {
                    showAlert('error', 'Update failed');
                    return false;
                });

        }); //subscribers_form.submit

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
                                <button id="activate_btn" class="navbar-brand btn-outline-primary" type="submit" data-toggle="tooltip" data-placement="top" title="Activate subscriptions">
                                    <i class="far fa-play-circle fa-2x"></i>
                                </button>
                                <button id="deactivate_btn" class="navbar-brand btn-outline-warning" type="submit" data-toggle="tooltip" data-placement="top" title="Deactivate subscriptions">
                                    <i class="far fa-pause-circle fa-2x"></i>
                                </button>
                            </div>
                            <div class="btn-group" role="group">
                                <button id="remove_btn" class="navbar-brand btn-outline-danger" type="submit" data-toggle="tooltip" data-placement="top" title="Remove subscriptions">
                                    <i class="far fa-trash-alt fa-2x"></i>
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


</body>
</html>

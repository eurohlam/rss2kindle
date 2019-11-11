<%--
  User: eurohlam
  Date: 9/01/2019
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="_include.jsp" %>
<html lang="en">
<head>
    <%@include file="_head.jsp" %>
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
            $('[data-tooltip="tooltip"]').tooltip()
        });

        reloadRssTable();

        function reloadRssTable() {
            $.getJSON(rootURL + '/${subscriber}', function (data) {
                userData = data;

                var rssTable = $('<table>')
                    .addClass('table table-hover')
                    .append('<thead>' +
                        '<tr>' +
                        '<th style="vertical-align: initial"><input type="checkbox" class="form-check-input" id="select_all_checkbox"/></th>' +
                        '<th>#</th>' +
                        '<th>rss</th>' +
                        '<th>status</th>' +
                        '<th>last polling date</th>' +
                        '<th>error message</th>' +
                        '<th>retry count</th>' +
                        '</tr></thead>')
                    .append('<tbody>');

                $.each(data.rsslist, function (i, item) {
                    var tr = $('<tr>');
                    if (item.status === 'dead') {
                        tr.addClass('table-danger');
                    }
                    else if (item.status === 'offline') {
                        tr.addClass('table-warning');
                    }
                    else {
                        tr.addClass('table-light');
                    }

                    tr.append('<td><input type="checkbox" class="form-check-input" id="' + item.rss + '"/></td>'
                        + '<td>' + (i + 1) + '</td>'
                        + '<td><a href="' + item.rss + '" target="_blank">' + item.rss + '</a></td>'
                        + '<td>' + item.status + '</td>'
                        + '<td>' + item.lastPollingDate + '</td>'
                        + '<td>' + item.errorMessage + '</td>'
                        + '<td>' + item.retryCount + '</td>');

                    rssTable.append(tr);
                });
                $("#details").html(rssTable);

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
            });
        }//end of reloadRssTable

        $('#subscribers_form').submit(function (e) {
            e.preventDefault();
            var srcButtonId = $(document.activeElement).attr('id');
            var operation;
            var message;
            if (srcButtonId == 'add_btn') {
                operation = 'add';
                message = 'Subscriptions added successfully';
                $('#addModal').modal('show');
                $('#add_subscriptions_form').submit(function (e) {
                    e.preventDefault();
                    //validate rss list
                    var rsslistField = $('#rss_list');
                    rsslistField.popover('dispose');
                    if ($('#rss_list option').length === 0) {
                        rsslistField.popover(
                            {
                                content: 'At least one RSS is required',
                                trigger: 'manual',
                                placement: 'auto'
                            });
                        rsslistField.popover('show');

                        return false;
                    }

                    var newRssList = userData.rsslist;
                    $('#rss_list option').each(function (i) {
                        newRssList.push({'rss': $(this).val(), 'status': 'active'});
                    });
                    userData.rsslist = newRssList;
                    $().runAjax(rootURL + '/update',
                        'PUT',
                        JSON.stringify(userData),
                        'New subscriptions have been added successfully',
                        'Adding new subscriptions failed',
                        reloadRssTable
                    );
                    $('#addModal').modal('hide');
                    rsslistField.empty();
                });
            } else if (srcButtonId == 'deactivate_btn') {
                operation = 'deactivate';
                message = 'Subscriptions deactivated successfully';
                $('#deactivateModal').modal('show');
                $('#deactivate_subscriptions_form').submit(function (e) {
                    e.preventDefault();
                    updateSubscriptions(operation, message);
                    $('#deactivateModal').modal('hide');
                });
            } else if (srcButtonId == 'activate_btn') {
                operation = 'activate';
                message = 'Subscriptions activated successfully';
                $('#activateModal').modal('show');
                $('#activate_subscriptions_form').submit(function (e) {
                    e.preventDefault();
                    updateSubscriptions(operation, message);
                    $('#activateModal').modal('hide');
                });
            } else if (srcButtonId == 'remove_btn') {
                operation = 'remove';
                message = 'Subscriptions removed successfully';
                $('#removeModal').modal('show');
                $('#remove_subscriptions_form').submit(function (e) {
                    e.preventDefault();
                    updateSubscriptions(operation, message);
                    $('#removeModal').modal('hide');
                });
            }

        }); //subscribers_form.submit

        function updateSubscriptions(operation, message) {
            var updatedRssList = userData.rsslist;
            $("input:checked[id!='select_all_checkbox']").each(function (index) {
                var checkedRss = $(this).attr('id');
                $.each(updatedRssList, function (i, item) {
                    if (checkedRss === item.rss) {
                        if (operation === 'activate') {
                            item.status = 'active';
                            item.errorMessage = 'Manually activated by user';
                            item.retryCount = '0';
                        } else if (operation === 'deactivate') {
                            item.status = 'offline';
                            item.errorMessage = 'Manually deactivated by user';
                        } else if (operation === 'remove') {
                            delete updatedRssList[i];
                            //removing null value from rsslist
                            updatedRssList = updatedRssList.filter(function (x) {
                                return x !== null
                            });
                        }
                    }
                });
            }); //each
            userData.rsslist = updatedRssList;

            $().runAjax(rootURL + '/update', 'PUT', JSON.stringify(userData), message, 'Update of subscriptions failed', reloadRssTable);

        } //end of updateSubscriptions

        $('#btn_addrss').click(function (event) {
            var newRssField = $('#new_rss');
            var rss = newRssField.val();
            newRssField.popover(
                {
                    content: 'Entered text does not look like a valid URL. Please correct it and try again',
                    trigger: 'manual',
                    placement: 'auto'
                });
            newRssField.popover('hide');
            if (validateURL(rss)) {
                $('#rss_list').append('<option value = "' + rss + '">' + rss + '</option>');
            }
            else {
                newRssField.data('bs.popover').config.content = rss + ' does not look like a valid URL. Please correct it and try again';
                newRssField.popover('show');
            }
        });

        $('#btn_deleterss').click(function (event) {
            $('#rss_list option:selected').remove();
        });
    }); //end of $(document).ready(function ())


</script>


<div>
    <%@include file="_header.jsp" %>

    <div id="wrapper" class="d-flex">
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
                            <div class="btn-group" role="group">
                                <button id="add_btn" class="navbar-brand btn-outline-info" type="submit"
                                        data-tooltip="tooltip" data-placement="top" title="New subscription">
                                    <i class="fas fa-plus-circle fa-2x"></i>
                                </button>
                            </div>
                            <div class="btn-group mr-2" role="group" aria-label="">
                                <button id="activate_btn" class="navbar-brand btn-outline-primary" type="submit"
                                        data-tooltip="tooltip" data-placement="top" title="Activate subscriptions">
                                    <i class="far fa-play-circle fa-2x"></i>
                                </button>
                                <button id="deactivate_btn" class="navbar-brand btn-outline-warning" type="submit"
                                        data-tooltip="tooltip" data-placement="top" title="Deactivate subscriptions">
                                    <i class="far fa-pause-circle fa-2x"></i>
                                </button>
                            </div>
                            <div class="btn-group" role="group">
                                <button id="remove_btn" class="navbar-brand btn-outline-danger" type="submit"
                                        data-tooltip="tooltip" data-placement="top" title="Remove subscriptions">
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

<!--Modal windows -->
<!-- Add modal -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-labelledby="addModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="addModalLabel">Add new subscriptions</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="get" action="#" id="add_subscriptions_form">
                <div class="modal-body">
                    <div class="form-group">
                        <label for="rss_list" class="control-label">Subscriptions:</label>
                        <select class="form-control" id="rss_list" size="7"></select>
                        <div class="form-group">
                            <label for="new_rss" class="control-label">Add new subscription (RSS):</label>
                            <input type="url" class="form-control" id="new_rss"/>
                            <div class="btn-group-xs" role="group">
                                <button type="button" class="btn btn-primary" id="btn_addrss">+</button>
                                <button type="button" class="btn btn-primary" id="btn_deleterss">-</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Add</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- Activate modal -->
<div class="modal fade" id="activateModal" tabindex="-1" role="dialog" aria-labelledby="activateModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="activateModalLabel">Activate subscriptions</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="get" id="activate_subscriptions_form" action="#">
                <div class="modal-body">
                    <div id="activate_alert_box" class="alert alert-danger" role="alert">
                        <strong>Warning!</strong>
                        You are going to activate some subscriptions.<br>
                        Do you confirm?
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="btn_activate">Activate</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- Deactivate modal -->
<div class="modal fade" id="deactivateModal" tabindex="-1" role="dialog" aria-labelledby="deactivateModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="deactivateModalLabel">Deactivate subscriptions</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="get" id="deactivate_subscriptions_form" action="#">
                <div class="modal-body">
                    <div id="deactivate_alert_box" class="alert alert-danger" role="alert">
                        <strong>Warning!</strong>
                        You are going to deactivate some subscriptions.<br>
                        Do you confirm?
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="btn_deactivate">Deactivate</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- Remove modal -->
<div class="modal fade" id="removeModal" tabindex="-1" role="dialog" aria-labelledby="removeModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="removeModalLabel">Remove subscriptions</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="get" id="remove_subscriptions_form" action="#">
                <div class="modal-body">
                    <div id="remove_alert_box" class="alert alert-danger" role="alert">
                        <strong>Warning!</strong>
                        You are going to remove some subscriptions.<br>
                        Do you confirm?
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="btn_remove">Remove</button>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>

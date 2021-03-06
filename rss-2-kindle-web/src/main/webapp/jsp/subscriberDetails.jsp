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
            $.getJSON(rootURL + '/${subscriberId}', function (data) {
                userData = data;
                showSubscriptionsTable(data.rsslist, 10, 1);
            });
        }//end of reloadRssTable

        function showSubscriptionsTable(subscriptions, maxPerPage, pageNumber) {
            if (!maxPerPage || maxPerPage < 0) {
                maxPerPage = 10;
            }
            if (!pageNumber || pageNumber < 0) {
                pageNumber = 1;
            }
            var startIndex = (pageNumber - 1) * maxPerPage;
            var endIndex = pageNumber * maxPerPage;

            var rssTable = $('<table>')
                .addClass('table table-hover')
                .append(
                    '<thead><tr>\
                        <th style="vertical-align: initial">\
                            <input type="checkbox" class="form-check-input" id="select_all_checkbox"/>\
                        </th>\
                        <th>#</th>\
                        <th>rss</th>\
                        <th>status</th>\
                        <th>last polling date</th>\
                        <th>error message</th>\
                        <th>retry count</th>\
                    </tr></thead>')
                .append('<tbody>');

            $.each(subscriptions, function (i, rss) {
                if (i >= startIndex && i < endIndex) {
                    var tr = $('<tr>');
                    if (rss.status === 'dead') {
                        tr.addClass('table-danger');
                    }
                    else if (rss.status === 'offline') {
                        tr.addClass('table-warning');
                    }
                    else {
                        tr.addClass('table-light');
                    }

                    tr.append(
                        '<td><input type="checkbox" class="form-check-input" id="' + rss.rss + '"/></td>\
                         <td>' + (i + 1) + '</td>\
                         <td><a href="' + rss.rss + '" target="_blank">' + rss.rss + '</a></td>\
                         <td>' + rss.status + '</td>\
                         <td>' + rss.lastPollingDate + '</td>\
                         <td>' + rss.errorMessage + '</td>\
                         <td>' + rss.retryCount + '</td>');

                    rssTable.append(tr);
                }
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

            //add pagination bar
            if (subscriptions.length > maxPerPage) {
                $().generatePaginationBar($('#subscription_pagination'), subscriptions, maxPerPage, pageNumber);
                $('#subscription_pagination a').click(function (event) {
                    event.preventDefault();
                    var button = $(event.currentTarget);
                    var clickedPageNumber = button.data('page');
                    showSubscriptionsTable(subscriptions, maxPerPage, clickedPageNumber);
                });
            } else {
                $('#subscription_pagination').empty();
            }
        } // end of showSubscriptionsTable

        $('#subscribers_form').submit(function (e) {
            e.preventDefault();
            var srcButtonId = $(document.activeElement).attr('id');
            if (srcButtonId == 'add_btn') {
                $('#addModal').modal('show');
            } else if (srcButtonId == 'deactivate_btn') {
                $('#deactivateModal').modal('show');
            } else if (srcButtonId == 'activate_btn') {
                $('#activateModal').modal('show');
            } else if (srcButtonId == 'remove_btn') {
                $('#removeModal').modal('show');
            }

        }); //subscribers_form.submit

        $('#add_subscriptions_form').submit(function (ev) {
            ev.preventDefault();
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

        $('#activate_subscriptions_form').submit(function (ev) {
            ev.preventDefault();
            var operation = 'activate';
            var message = 'Subscriptions activated successfully';
            updateSubscriptions(operation, message);
            $('#activateModal').modal('hide');
        });

        $('#deactivate_subscriptions_form').submit(function (ev) {
            ev.preventDefault();
            var operation = 'deactivate';
            var message = 'Subscriptions deactivated successfully';
            updateSubscriptions(operation, message);
            $('#deactivateModal').modal('hide');
        });

        $('#remove_subscriptions_form').submit(function (ev) {
            ev.preventDefault();
            var operation = 'remove';
            var message = 'Subscriptions removed successfully';
            updateSubscriptions(operation, message);
            $('#removeModal').modal('hide');
        });

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
                    <h1>${subscriberName}</h1>
                    <h2>${subscriberId}</h2>
                    <hr class="star-dark"/>
                </div>
                <div class="container-fluid card">
                    <form id="subscribers_form" action="" method="post">
                        <div class="btn-toolbar bg-light card-header" role="toolbar" aria-label="">
                            <div class="btn-group" role="group">
                                <button id="add_btn" class="navbar-brand btn-outline-info" type="submit"
                                        data-toggle="modal"  data-target="#addModal"
                                        data-tooltip="tooltip" data-placement="top" title="New subscription">
                                    <i class="fas fa-plus-circle fa-2x"></i>
                                </button>
                            </div>
                            <div class="btn-group mr-2" role="group" aria-label="">
                                <button id="activate_btn" class="navbar-brand btn-outline-primary" type="submit"
                                        data-toggle="modal"  data-target="#activateModal"
                                        data-tooltip="tooltip" data-placement="top" title="Activate subscriptions">
                                    <i class="far fa-play-circle fa-2x"></i>
                                </button>
                                <button id="deactivate_btn" class="navbar-brand btn-outline-warning" type="submit"
                                        data-toggle="modal"  data-target="#deactivateModal"
                                        data-tooltip="tooltip" data-placement="top" title="Deactivate subscriptions">
                                    <i class="far fa-pause-circle fa-2x"></i>
                                </button>
                            </div>
                            <div class="btn-group" role="group">
                                <button id="remove_btn" class="navbar-brand btn-outline-danger" type="submit"
                                        data-toggle="modal"  data-target="#removeModal"
                                        data-tooltip="tooltip" data-placement="top" title="Remove subscriptions">
                                    <i class="far fa-trash-alt fa-2x"></i>
                                </button>
                            </div>
                        </div>
                        <div class="card-body">
                            <div id="alerts_panel" class="row"></div>
                            <div id="details" class="row table-responsive"></div>
                        </div>
                        <nav class="card-footer" aria-label="subscriptions">
                            <ul id="subscription_pagination" class="pagination justify-content-end"></ul>
                        </nav>
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

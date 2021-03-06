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
    var username = '${username}';
    var rootURL = 'rest/profile/';
    var userData;
    var csrf_token = $("meta[name='_csrf']").attr("content");
    var csrf_header = $("meta[name='_csrf_header']").attr("content");
    var csrf_headers = {};
    csrf_headers[csrf_header] = csrf_token;

    $(document).ready(function () {

        reloadSubscribersTable();

        //show subscribers table for edit
        function reloadSubscribersTable() {
            $.getJSON(rootURL + username, function (data) {
                userData = data;
                showSubscribersTable(data.subscribers, 10, 1);
            });
        } //end of reloadSubscribersTable

        function showSubscribersTable(subscribers, maxPerPage, pageNumber) {
            if (!maxPerPage || maxPerPage < 0) {
                maxPerPage = 10;
            }
            if (!pageNumber || pageNumber < 0) {
                pageNumber = 1;
            }
            var startIndex = (pageNumber - 1) * maxPerPage;
            var endIndex = pageNumber * maxPerPage;

            var table = $('<table>')
                .addClass('table table-hover')
                .append(
                    '<thead>\
                        <tr><th>#</th>\
                        <th>name</th>\
                        <th>email</th>\
                        <th>status</th>\
                        <th>action</th></tr>\
                    </thead>')
                .append('<tbody>');

            $.each(subscribers, function (i, subscriber) {
                if (i >= startIndex && i < endIndex) {
                    var tr = $('<tr>');
                    if (subscriber.status === 'suspended') {
                        tr.addClass('table-danger');
                    } else {
                        tr.addClass('table-light');
                    }

                    tr.append('<td>' + (i + 1) + '</td>\
                            <td><a href="subscriberDetails?subscriberId=' + subscriber.email
                                        + '&subscriberName=' + subscriber.name + '">' + subscriber.name + '</a></td>\
                            <td><a href="subscriberDetails?subscriberId=' + subscriber.email
                                        + '&subscriberName=' + subscriber.name + '">' + subscriber.email + '</a></td>\
                            <td>' + subscriber.status + '</td>');

                    var btnDiv = $('<div>')
                        .addClass('btn-group')
                        .attr({'role': 'group'});
                    btnDiv.append(
                        '<button id="btn_update" type="button" class="btn btn-outline-primary"\
                                data-toggle="modal" data-target="#updateModal"\
                                data-name="' + subscriber.name + '" data-email="' + subscriber.email + '" data-status="' + subscriber.status + '">\
                            <span data-tooltip="tooltip" data-placement="top" title="Edit subscriber">\
                                <i class="far fa-edit fa-lg"></i>\
                            </span>\
                         </button>');

                    if (subscriber.status === 'suspended') {
                        btnDiv.append(
                            '<button id="btn_resume" type="button" class="btn btn-warning"\
                                    data-toggle="modal"  data-target="#resumeModal"\
                                    data-name="' + subscriber.name + '" data-email="' + subscriber.email + '">\
                                <span data-tooltip="tooltip" data-placement="top" title="Resume subscriber">\
                                    <i class="far fa-play-circle fa-lg"></i>\
                                </span>\
                             </button>');
                    } else {
                        btnDiv.append(
                            '<button id="btn_suspend" type="button" class="btn btn-outline-warning"\
                                data-toggle="modal" data-target="#suspendModal"\
                                data-name="' + subscriber.name + '" data-email="' + subscriber.email + '">\
                                <span data-tooltip="tooltip" data-placement="top" title="Suspend subscriber">\
                                    <i class="far fa-pause-circle fa-lg"></i>\
                                </span>\
                             </button>');
                    }

                    btnDiv.append(
                        '<button id="btn_remove" type="button" class="btn btn-outline-danger"\
                            data-toggle="modal" data-target="#removeModal"\
                            data-name="' + subscriber.name + '" data-email="' + subscriber.email + '">\
                            <span data-tooltip="tooltip" data-placement="top" title="Remove subscriber">\
                                <i class="far fa-trash-alt fa-lg"></i>\
                            </span>\
                         </button>');

                    tr.append($('<td>').append(btnDiv));
                    table.append(tr);
                }
            });
            $('#edit_subscriber').html(table);

            //add pagination bar
            if (subscribers.length > maxPerPage) {
                $().generatePaginationBar($('#subscribers_pagination'), subscribers, maxPerPage, pageNumber);
                $('#subscribers_pagination a').click(function (event) {
                    event.preventDefault();
                    var button = $(event.currentTarget);
                    var clickedPageNumber = button.data('page');
                    showSubscribersTable(subscribers, maxPerPage, clickedPageNumber);
                });
            } else {
                $('#subscribers_pagination').empty();
            }
        }


        //activate the first tab by default
        //TODO: change active tab in depends on input parameter
        $('#operationsTab a:first').tab('show');

        //show modal for update
        $('#updateModal').on('show.bs.modal', function (event) {
            var button = $(event.relatedTarget); // Button that triggered the modal
            var email = button.data('email'); // Extract info from data-* attributes
            var name = button.data('name');
            var modal = $(this);
            modal.find('.modal-title').text('Update subscriber ' + name);
            $('#update_subscriber_name').val(name);
            $('#update_subscriber_email').val(email);
            $('#update_subscriber_status').val(button.data('status'));

            var rssTable = '';
            $.each(userData.subscribers, function (i, item) {
                if (item.email === email) {
                    var rss = item.rsslist;
                    for (j = 0; j < rss.length; j++) {
                        rssTable = rssTable + '<option value="' + rss[j].rss + '">' + rss[j].rss + '</option>';
                    }
                }
            });
            $('#update_subscriber_rsslist').html(rssTable);
        });

        $('#btn_update_subscriber_addrss').click(function (event) {
            var _update_subscriber_addrss = $('#update_subscriber_addrss');
            var rss = _update_subscriber_addrss.val();
            _update_subscriber_addrss.popover(
                {
                    content: 'Entered text does not look like a valid URL. Please correct it and try again',
                    trigger: 'manual',
                    placement: 'auto'
                });
            _update_subscriber_addrss.popover('hide');
            if (validateURL(rss)) {
                $('#update_subscriber_rsslist').append('<option value = "' + rss + '">' + rss + '</option>');
            }
            else {
                _update_subscriber_addrss.data('bs.popover').config.content = rss + ' does not look like a valid URL. Please correct it and try again';
                _update_subscriber_addrss.popover('show');
            }
        });

        $('#btn_update_subscriber_deleterss').click(function (event) {
            $('#update_subscriber_rsslist option:selected').remove();
        });


        //show modal for suspend
        $('#suspendModal').on('show.bs.modal', function (event) {
            var button = $(event.relatedTarget); // Button that triggered the modal
            var email = button.data('email'); // Extract info from data-* attributes
            var name = button.data('name');
            var modal = $(this);
            modal.find('.modal-title').text('Suspend subscriber ' + name);
            $('#suspend_subscriber_email').val(email);
            $('#suspend_subscriber_name').val(name);
            $('#suspend_alert_box').html('<strong>Warning!</strong> You are going to suspend subscriber <strong>' + name + '</strong> associated with email <strong>' + email + '</strong>.</br>Do you confirm?');
        });

        //show modal for resume
        $('#resumeModal').on('show.bs.modal', function (event) {
            var button = $(event.relatedTarget); // Button that triggered the modal
            var email = button.data('email'); // Extract info from data-* attributes
            var name = button.data('name');
            var modal = $(this);
            modal.find('.modal-title').text('Resume subscriber ' + name);
            $('#resume_subscriber_email').val(email);
            $('#resume_subscriber_name').val(name);
            $('#resume_alert_box').html('<strong>Warning!</strong> You are going to resume subscriber <strong>' + name + '</strong> associated with email <strong>' + email + '</strong>.</br>Do you confirm?');
        });

        //show modal for remove
        $('#removeModal').on('show.bs.modal', function (event) {
            var button = $(event.relatedTarget); // Button that triggered the modal
            var email = button.data('email'); // Extract info from data-* attributes
            var name = button.data('name');
            var modal = $(this);
            modal.find('.modal-title').text('Remove subscriber ' + name);
            $('#remove_subscriber_name').val(name);
            $('#remove_subscriber_email').val(email);
            $('#remove_alert_box').html('<strong>Warning!</strong> You are going to remove subscriber <strong>' + name + '</strong> associated with email <strong>' + email + '</strong>.</br>Do you confirm?');
        });

        //update subscriber on submit
        $('#update_subscriber_form').submit(function (e) {
            e.preventDefault();
            var email = $('#update_subscriber_email').val();
            var name = $('#update_subscriber_name').val();
            var status = $('#update_subscriber_status').val();
            //validate rss list
            var _update_subscriber_rsslist = $('#update_subscriber_rsslist');
            _update_subscriber_rsslist.popover('dispose');
            if ($('#update_subscriber_rsslist option').length === 0) {
                _update_subscriber_rsslist.popover(
                    {
                        content: 'At least one RSS is required',
                        trigger: 'manual',
                        placement: 'auto'
                    });
                _update_subscriber_rsslist.popover('show');

                return false;
            }

            var updateJson = {};
            updateJson.email = email;
            updateJson.name = name;
            updateJson.status = status;
            updateJson.rsslist = [];
            $('#update_subscriber_rsslist option').each(function (i) {
                updateJson.rsslist[i] = {'rss': $(this).val(), 'status': 'active'};
            });

            $().runAjax(rootURL + username + '/update',
                'PUT',
                JSON.stringify(updateJson),
                'Subscriber <strong>' + name + '</strong> has been updated successfully',
                'Subscriber <strong>' + name + '</strong> updating failed',
                reloadSubscribersTable
            );
            $('#updateModal').modal('hide');

        });

        //suspend subscriber on submit
        $('#suspend_subscriber_form').submit(function (e) {
            var email = $('#suspend_subscriber_email').val();
            var name = $('#suspend_subscriber_name').val();
            e.preventDefault();
            $().runGetJson(rootURL + username + '/' + email + '/suspend',
                'Subscriber <strong>' + name + '</strong> has been suspended',
                'Subscriber <strong>' + name + '</strong> suspending failed',
                reloadSubscribersTable
            );
            $('#suspendModal').modal('hide');
        });

        //resume subscriber on submit
        $('#resume_subscriber_form').submit(function (e) {
            var email = $('#resume_subscriber_email').val();
            var name = $('#resume_subscriber_name').val();
            e.preventDefault();
            $().runGetJson(rootURL + username + '/' + email + '/resume',
                'Subscriber <strong>' + name + '</strong> has been resumed',
                'Subscriber <strong>' + name + '</strong> resuming failed',
                reloadSubscribersTable
            );
            $('#resumeModal').modal('hide');
        });

        //add new subscriber on submit
        $('#new_subscriber_form').submit(function (e) {
            e.preventDefault();
            var _new_subscriber_email = $('#new_subscriber_email');
            var _new_subscriber_rsslist = $('#new_subscriber_rsslist');
            var email = _new_subscriber_email.val();
            var name = $('#new_subscriber_name').val();

            //create validation popovers
            _new_subscriber_email.popover(
                {
                    content: 'Subscriber with such email already exists',
                    trigger: 'manual',
                    placement: 'auto'
                });
            _new_subscriber_rsslist.popover(
                {
                    content: 'At least one RSS is required',
                    trigger: 'manual',
                    placement: 'auto'
                });

            //validate email
            var isEmailValid = true;
            $.each(userData.subscribers, function (i, item) {
                if (item.email === email) {
                    //we need to update content directly via jquery
                    _new_subscriber_email.data('bs.popover').config.content = 'Subscriber with email ' + email + ' already exists';
                    isEmailValid = false;
                    return false;
                }
            });
            if (!isEmailValid) {
                _new_subscriber_email.popover('show');
                return false;
            }
            _new_subscriber_email.popover('hide');

            //validate rss list
            if ($('#new_subscriber_rsslist option').length === 0) {
                _new_subscriber_rsslist.popover('show');
                return false;
            }
            _new_subscriber_rsslist.popover('hide');

            //prepare json
            var updateJson = {};
            updateJson.email = email;
            updateJson.name = name;
            updateJson.status = 'active';
            updateJson.rsslist = [];
            $('#new_subscriber_rsslist option').each(function (i) {
                updateJson.rsslist[i] = {'rss': $(this).val(), 'status': 'active'};
            });

            $().runAjax(rootURL + username + '/new',
                'PUT',
                JSON.stringify(updateJson),
                'New subscriber <strong>' + name + '</strong> has been added successfully',
                'New subscriber <strong>' + name + '</strong> creation failed',
                reloadSubscribersTable
            );
        });

        $('#btn_new_subscriber_addrss').click(function (event) {
            var _new_subscriber_addrss = $('#new_subscriber_addrss');
            var rss = _new_subscriber_addrss.val();
            _new_subscriber_addrss.popover(
                {
                    content: 'Entered text does not look like a valid URL. Please correct it and try again',
                    trigger: 'manual',
                    placement: 'auto'
                });
            _new_subscriber_addrss.popover('hide');
            if (validateURL(rss)) {
                $('#new_subscriber_rsslist').append('<option value = "' + rss + '">' + rss + '</option>');
            }
            else {
                _new_subscriber_addrss.data('bs.popover').config.content = rss + ' does not look like a valid URL. Please correct it and try again';
                _new_subscriber_addrss.popover('show');
            }
        });

        $('#btn_new_subscriber_deleterss').click(function (event) {
            $('#new_subscriber_rsslist option:selected').remove();
        });

        //remove subscriber on submit
        $('#remove_subscriber_form').submit(function (e) {
            var email = $('#remove_subscriber_email').val();
            var name = $('#remove_subscriber_name').val();
            e.preventDefault();
            $().runAjax(rootURL + username + '/' + email + '/remove',
                'DELETE',
                '',
                'Subscriber <strong>' + name + '</strong> has been removed',
                'Subscriber <strong>' + name + '</strong> removal failed',
                reloadSubscribersTable
            );
            $('#removeModal').modal('hide');
        });

        //Show ajax error messages
        $(document).ajaxError(function (event, request, settings, thrownError) {
            showAlert('error', 'Ajax error: code:' + request.status + ": " + request.responseText + " calling url: " + settings.url + " method: " + settings.type + " " + thrownError);
        });

        //enable bootstrap tooltip
        //TODO: we can't use data-toggle here because of modal windows, but data-tooltip does not work as well
        // because buttons are generated dynamicaly
        $(function () {
            $('[data-tooltip="tooltip"]').tooltip();
        });

    });//end of $(document).ready(function ())

</script>

<div>
    <%@include file="_header.jsp" %>

    <div id="wrapper" class="d-flex">

        <%@include file="_aside.jsp" %>

        <main id="page-content-wrapper">
            <div class="text-center">
                <h1>Subscribers management</h1>
                <hr class="star-dark"/>
            </div>
            <ul class="nav nav-tabs" id="operationsTab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link" id="edit-tab" data-toggle="tab" href="#edit" role="tab" aria-controls="profile"
                       aria-selected="false">Edit subscribers</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" id="new-tab" data-toggle="tab" href="#new" role="tab"
                       aria-controls="home" aria-selected="true">New subscriber</a>
                </li>
            </ul>
            <div class="row" style="padding-top: 3rem; padding-left: 2rem">
                <div class="tab-content table-responsive-sm" id="operationsTabContent">
                    <div id="alerts_panel"></div>
                    <div class="tab-pane fade active" id="new" role="tabpanel" aria-labelledby="new-tab">
                        <div class="card">
                            <h2 class="card-header">Add new subscriber</h2>
                            <form method="get" id="new_subscriber_form" action="#page-top">
                                <div class="card-body">
                                    <div class="form-group">
                                        <label for="new_subscriber_email">Email</label>
                                        <input type="email" id="new_subscriber_email" required class="form-control"/>
                                    </div>
                                    <div class="form-group">
                                        <label for="new_subscriber_name">Name</label>
                                        <input type="text" id="new_subscriber_name" required class="form-control"/>
                                    </div>
                                    <div class="form-group">
                                        <label for="new_subscriber_rsslist">Subscriptions</label>
                                        <select class="form-control" id="new_subscriber_rsslist" size="5"></select>
                                        <div class="form-group">
                                            <label for="new_subscriber_addrss" class="control-label">Add new
                                                subscription (RSS):</label>
                                            <input type="url" class="form-control" id="new_subscriber_addrss"/>
                                            <div class="btn-group-xs" role="group">
                                                <button type="button" class="btn btn-primary"
                                                        id="btn_new_subscriber_addrss">+
                                                </button>
                                                <button type="button" class="btn btn-primary"
                                                        id="btn_new_subscriber_deleterss">-
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-footer">
                                    <button type="submit" class="btn btn-primary">Create</button>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div class="tab-pane fade" id="edit" role="tabpanel" aria-labelledby="edit-tab">
                        <div class="card">
                            <h2 class="card-header">Edit subscribers</h2>
                            <div id="edit_subscriber"></div>
                            <nav class="card-footer" aria-label="subscribers">
                                <ul id="subscribers_pagination" class="pagination justify-content-end"></ul>
                            </nav>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <%@include file="_footer.jsp" %>
</div>

<!--Modal windows -->
<!-- Update modal -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-labelledby="updateModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="updateModalLabel">Update subscriber and subscriptions</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="get" action="#" id="update_subscriber_form">
                <div class="modal-body">
                    <div class="form-group">
                        <label for="update_subscriber_email" class="control-label">Subscriber email:</label>
                        <input type="email" class="form-control" id="update_subscriber_email" readonly/>
                        <label for="update_subscriber_status" class="control-label">Status:</label>
                        <input type="text" class="form-control" id="update_subscriber_status" readonly/>
                    </div>
                    <div class="form-group">
                        <label for="update_subscriber_name" class="control-label">Subscriber name:</label>
                        <input type="text" class="form-control" id="update_subscriber_name" required/>
                    </div>
                    <div class="form-group">
                        <label for="update_subscriber_rsslist" class="control-label">Subscriptions:</label>
                        <select class="form-control" id="update_subscriber_rsslist" size="7"></select>
                        <div class="form-group">
                            <label for="update_subscriber_addrss" class="control-label">Add new subscription
                                (RSS):</label>
                            <input type="url" class="form-control" id="update_subscriber_addrss"/>
                            <div class="btn-group-xs" role="group">
                                <button type="button" class="btn btn-primary" id="btn_update_subscriber_addrss">+
                                </button>
                                <button type="button" class="btn btn-primary" id="btn_update_subscriber_deleterss">-
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Suspend modal -->
<div class="modal fade" id="suspendModal" tabindex="-1" role="dialog" aria-labelledby="suspendModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="suspendModalLabel">Suspend subscriber</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="get" id="suspend_subscriber_form" action="#">
                <div class="modal-body">
                    <div id="suspend_alert_box" class="alert alert-warning" role="alert"></div>
                    <input hidden type="text" id="suspend_subscriber_name"/>
                    <input hidden type="email" id="suspend_subscriber_email"/>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary" name="btn_suspend">Suspend</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Resume modal -->
<div class="modal fade" id="resumeModal" tabindex="-1" role="dialog" aria-labelledby="resumeModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="resumeModalLabel">Suspend subscriber</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="get" id="resume_subscriber_form" action="#">
                <div class="modal-body">
                    <div id="resume_alert_box" class="alert alert-warning" role="alert"></div>
                    <input hidden type="text" id="resume_subscriber_name"/>
                    <input hidden type="email" id="resume_subscriber_email"/>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary" name="btn_resume">Resume</button>
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
                <h4 class="modal-title" id="removeModalLabel">Remove subscriber</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="get" id="remove_subscriber_form" action="#">
                <div class="modal-body">
                    <div id="remove_alert_box" class="alert alert-danger" role="alert"></div>
                    <input hidden type="text" id="remove_subscriber_name"/>
                    <input hidden type="email" id="remove_subscriber_email"/>
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
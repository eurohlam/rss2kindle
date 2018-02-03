<%--
  User: eurohlam
  Date: 19/10/2017
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="include.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>RSS-2-KINDLE Subscribers Management</title>

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
    <script src="../js/profile.js"></script>

</head>
<body>
<script>
    var username='<%=username%>';
    var rootURL = '/rss2kindle/rest/profile/';
    var userData;

    $(document).ready(function () {

        //show subscribers table for edit
        $.getJSON(restURL + username, function (data) {
            userData = data;
            var table = '<table class="table table-hover"><thead>' +
                '<tr><th>#</th>' +
                '<th>title</th>' +
                '<th>email</th>' +
                '<th>status</th>' +
                '<th>action</th></tr></thead><tbody>';

            $.each(data.subscribers, function (i, item) {
                var tr;
                if (item.status === 'suspended')
                    tr='<tr class="danger"><td>';
                else
                    tr = '<tr class="active"><td>';

                table = table + tr
                    + i + '</td><td>'
                    + item.name + '</td><td>'
                    + item.email + '</td><td>'
                    + item.status + '</td><td>'
                    + '<div class="btn-group" role="group">'
                    + '<button id="btn_update" type="button" class="btn btn-primary" data-toggle="modal" data-target="#updateModal" data-name="' + item.name + '" data-email="' + item.email +'">Update</button>';

                if (item.status === 'suspended')
                    table = table + '<button id="btn_resume" type="button" class="btn btn-warning" data-toggle="modal" data-target="#resumeModal" data-name="' + item.name + '" data-email="' + item.email +'">Resume</button>'
                else
                    table = table + '<button id="btn_suspend" type="button" class="btn btn-warning" data-toggle="modal" data-target="#suspendModal" data-name="' + item.name + '" data-email="' + item.email +'">Suspend</button>';

                table = table + '<button id="btn_remove" type="button" class="btn btn-danger" data-toggle="modal" data-target="#removeModal" data-name="' + item.name + '" data-email="' + item.email +'">Remove</button></div></td></tr>';

/*
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
*/
            });
            table = table + '</tbody></table>';
            $('#edit').append(table);
        });

        //activate the first tab by default
        $('#operationsTab a:first').tab('show');

        //show modal for update
        $('#updateModal').on('show.bs.modal', function (event) {
            var button = $(event.relatedTarget); // Button that triggered the modal
            var email = button.data('email'); // Extract info from data-* attributes
            var name = button.data('name');
            var rsslist = button.data('rss');//TODO: rsslist
            var modal = $(this);
            modal.find('.modal-title').text('Update subscriber ' + name);
            $('#update_subscriber_name').val(name);
            $('#update_subscriber_email').val(email);
            $('#tarea').text(getSubscriberById(email));
//            $('#subscription-list').()
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

        //suspend subscriber on submit
        //TODO: need to show correct tab after submit
        $('#suspend_subscriber_form').submit(function () {
            var email = $('#suspend_subscriber_email').val();
            var name = $('#suspend_subscriber_name').val();
            $.getJSON(rootURL + username + '/' + email + '/suspend', function (data) {
                showAlert('success', 'Subscriber <strong>' + name + '</strong> has been suspended');
            });
            return true;
        });

        //resume subscriber on submit
        $('#resume_subscriber_form').submit(function () {
            var email = $('#resume_subscriber_email').val();
            var name = $('#resume_subscriber_name').val();
            $.getJSON(rootURL + username + '/' + email + '/resume', function (data) {
                showAlert('success', 'Subscriber <strong>' + name + '</strong> has been resumed');
            });
            return true;
        });

        //add new subscriber on submit
        $('#new_subscriber_form').submit(function () {
            var name = $('#new_subscriber_name').val();
            $.post(rootURL + username + '/new',
                {
                    email: $('#new_subscriber_email').val(),
                    name: name,
                    rss: $('#new_subscriber_rss').val()
                },
                function (data) {
                    showAlert('success', 'New subscriber <strong>' + name + '</strong> has been added successfully');
                    return true;
                },
                'json');
            return false;
        });

        //remove subscriber on submit
        $('#remove_subscriber_form').submit(function () {
            var email = $('#remove_subscriber_email').val();
            var name = $('#remove_subscriber_name').val();
            $.getJSON(rootURL + username + '/' + email +'/remove', function (data) {
                showAlert('success', 'Subscriber <strong>' + name +'</strong> has been removed');
            });
            return true;
        });

        //Show ajax error messages
        $(document).ajaxError(function (event, request, settings, thrownError) {
            showAlert('error', 'Internal error: ' + thrownError + settings + request);
        });

        function getSubscriberById(id)
        {
            $.each(userData.subscribers, function (i, item) {
                if (item.email == id)
                    return item;
            });
            return false;
        }
    });

    function showAlert(type, text){
        if (type == 'error') {
            $('#alerts_panel').html('<div class="alert alert-danger alert-dismissible" role="alert">'
                + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<strong>Error! </strong>'+ text + '</div>');
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


    function isEmptyText(text) {
        if (text == null || text == '' || text == 'undefined') {
            return true;
        }
        return false;
    }

    //TODO: deprecated
    function findSubscriberById(id) {
        $('#getresult').append('<p>trying to get data</p>');
        if (!isEmptyText(id)) {
            $.getJSON(rootURL + '/' + id, function (data) {
                //TODO: it should be array
                var table = '<table class="table table-hover">' +
                    '<tr>' +
                    '<th>email</th>' +
                    '<th>name</th>' +
                    '<th>status</th>' +
                    '<th>rss</th></tr>';

                var tr;
                if (data.status === 'terminated')
                    tr = '<tr class="danger"><td>';
                else if (data.status === 'suspended')
                    tr = '<tr class="warning"><td>';
                else
                    tr = '<tr class="active"><td>';

                table = table + tr
                    + data.email + '</td><td>'
                    + data.name + '</td><td>'
                    + data.status + '</td><td>';
                var rsslist = data.rsslist;
                for (j = 0; j < rsslist.length; j++)
                    table = table + rsslist[j].rss + '  status=' + rsslist[j].status + '<br/>';

                table = table + '</td></tr>';
                table = table + '</table>';
                $('#getresult').append(table);

                //show edit form
                $('#editemail').val(data.email);
                $('#editname').val(data.name);
                $('#editrss').val(data.rsslist[0].rss);
                $('#getsubscrview').hide('fast');
                $('#editsubscrview').show('fast');
            })
        }
        else {
            $('#getresult').append('<p>email is empty</p>');
        }
    }

    //TODO: deprecated
    function editSubscriber() {
        $.post(rootURL + '/update',
            {
                email: $('#editemail').val(),
                name: $('#editname').val(),
                rss: $('#editrss').val()
            },
            function (data) {
//                $('#getresult').append('<p>Result ' + data.updateOfExisting + ' n=' + data.n + '</p>');
                $('#getresult').append('<p>Result ' + data + '</p>');
            },
            'json');
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
    <hr/>
    <div class="row">
        <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
            <ul class="nav nav-pills flex-column">
                <li role="presentation"><a href="profile">My Profile</a></li>
                <li role="presentation" class="active"><a href="#">Subscriber Management</a></li>
                <li role="presentation"><a href="service">Services</a></li>
            </ul>
        </nav>
        <main role="main" class="col-sm-9 col-md-10">
            <ul class="nav nav-tabs" id="operationsTab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link active" id="new-tab" data-toggle="tab" href="#new" role="tab" aria-controls="home" aria-selected="true">New subscriber</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="edit-tab" data-toggle="tab" href="#edit" role="tab" aria-controls="profile" aria-selected="false">Edit subscribers</a>
                </li>
            </ul>
            <div class="tab-content" id="operationsTabContent">
                <div id="alerts_panel"></div>
                <div class="tab-pane fade active" id="new" role="tabpanel" aria-labelledby="new-tab">
                    <h3>Add new subscriber</h3>
                    <form method="get" id="new_subscriber_form" action="#">
                        <div class="form-group">
                            <label for="new_subscriber_email">Email</label>
                            <input type="email" id="new_subscriber_email" required class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="new_subscriber_name">Name</label>
                            <input type="text" id="new_subscriber_name" required class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="new_subscriber_rss">Subscription (RSS)</label>
                            <p><input type="url" id="new_subscriber_rss" required class="form-control"/></p>
                        </div>
                        <div class="form-group">
                            <%--<label for="starttime">Start date</label>--%>
                            <%--<p><input type="date" id="starttime" class="form-control"/></p>--%>
                            <security:csrfInput/>
                            <input type="submit" value="Create" class="btn btn-primary"/>
                        </div>
                    </form>
                </div>
                <div class="tab-pane fade" id="edit" role="tabpanel" aria-labelledby="edit-tab">
                    <h3>Edit subscriber</h3>
                    <div id="edit_alerts"></div>
                </div>
            </div>
        </main>
    </div>

</div>

<!--Modal windows -->
<!-- Update modal -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-labelledby="updateModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="updateModalLabel">Update subscriber and subscriptions</h4>
            </div>
            <form method="post" action="#" id="update_subscriber_form">
                <div class="modal-body">
                        <div class="form-group">
                            <label for="update_subscriber_email" class="control-label">Subscriber email:</label>
                            <input type="email" class="form-control" id="update_subscriber_email"/>
                        </div>
                        <div class="form-group">
                            <label for="update_subscriber_name" class="control-label">Subscriber name:</label>
                            <input type="text" class="form-control" id="update_subscriber_name"/>
                        </div>
                        <div class="form-group">
                            <label for="subscription-list" class="control-label">Subscriptions:</label>
                            <textarea class="form-control" id="tarea"></textarea>
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
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="suspendModalLabel">Suspend subscriber</h4>
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
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="resumeModalLabel">Suspend subscriber</h4>
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
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="removeModalLabel">Remove subscriber</h4>
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

<%@include file="footer.jsp"%>
</body>
</html>
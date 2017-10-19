<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %><%--
  User: eurohlam
  Date: 19/10/2017
  Time: 13:59
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>RSS-2-Kindle Management</title>

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
    var rootURL = '/rss2kindle/rest/profile';

    $(document).ready(function () {

        //toggling
        $("#getsubscr").click(function (event) {
            $("#newsubscrview").hide('fast');
            $("#removesubscrview").hide('fast');
            $('#editsubscrview').hide('fast');
            $('#suspendsubscrview').hide('fast');
            $("#getsubscrview").show('slow');
        });
        $("#newsubscr").click(function (event) {
            $("#removesubscrview").hide('fast');
            $("#getsubscrview").hide('fast');
            $('#editsubscrview').hide('fast');
            $('#suspendsubscrview').hide('fast');
            $("#newsubscrview").show('slow');
        });
        $("#removesubscr").click(function (event) {
            $("#newsubscrview").hide('fast');
            $("#getsubscrview").hide('fast');
            $('#editsubscrview').hide('fast');
            $('#suspendsubscrview').hide('fast');
            $("#removesubscrview").show('slow');
        });
        $("#suspendsubscr").click(function (event) {
            $("#newsubscrview").hide('fast');
            $("#getsubscrview").hide('fast');
            $('#editsubscrview').hide('fast');
            $("#removesubscrview").hide('fast');
            $('#suspendsubscrview').show('slow');
        });

        //on submit
        $("#get_subscr_form").submit(function () {
            findById($("#email").val());
            return false;
        });
        $("#edit_subscr_form").submit(function () {
            editSubscriber();
            return false;
        });
        $("#remove_subscr_form").submit(function () {
            removeById($("#removeemail").val());
            return false;
        });
        $("#new_subscr_form").submit(function () {
            newSubscriber();
            return false;
        });
        $("#suspend_subscr_form").submit(function () {
            suspendSubscriber($("#suspendemail").val());
            return false;
        });

        //error view
        $(document).ajaxError(function (event, request, settings) {
            $("#errorview").append("<h1>Error in getting data.</h1>");
        })
    });

    function isEmptyText(text) {
        if (text == null || text == '' || text == 'undefined') {
            return true;
        }
        return false;
    }

    function findById(id) {
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

    function removeById(id) {
        $.getJSON(rootURL + '/' + id + '/remove', function (data) {
            $('#getresult').append('<p>' + id + '</p><p>Result jopa' + '</p>');
        });
    }

    function newSubscriber() {
        $.post(rootURL + '/new',
            {
                email: $("#newemail").val(),
                name: $("#name").val(),
                rss: $("#rss").val()
            },
            function (data) {
//                $('#getresult').append('<p>Result ' + data.updateOfExisting + ' n=' + data.n + '</p>');
                $('#getresult').append('<p>Result ' + data + '</p>');
            },
            "json");
    }

    function editSubscriber() {
        $.post(rootURL + '/update',
            {
                email: $("#editemail").val(),
                name: $("#editname").val(),
                rss: $("#editrss").val()
            },
            function (data) {
//                $('#getresult').append('<p>Result ' + data.updateOfExisting + ' n=' + data.n + '</p>');
                $('#getresult').append('<p>Result ' + data + '</p>');
            },
            "json");
    }

    function suspendSubscriber(id) {
        $.getJSON(rootURL + '/' + id + '/suspend', function (data) {
            $('#getresult').append('<p>' + id + '</p><p>Result jopa' + '</p>');
        });
    }

</script>

<header role="banner">
    <h1>RSS-2-Kindle rules</h1>
    <h2>
        <%-- TODO: just for test--%>
        <%
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) { %>
            <%= auth.getPrincipal().toString() %><br/>
            <%= auth.getCredentials() %><br/>
            <%= auth.getDetails() %>
        <%
            }
        %>
    </h2>
</header>

<div class="container">
    <nav class="navbar navbar-default" role="navigation">
        <ul class="nav nav-tabs">
            <li role="presentation"><a href="../main.html">Home</a></li>
            <li role="presentation" class="active"><a href="../subscriber.html">Subscriber Management</a></li>
            <li role="presentation"><a href="../service.html">Services</a></li>
        </ul>
    </nav>
</div>

<div class="container" id="forms">
    <div class="row" id="forms_row">

        <div class="col-md-3" id="forms_aside">
            <div class="list-group">
                <button type="button" class="list-group-item" id="getsubscr">Edit subscriber</button>
                <button type="button" class="list-group-item" id="newsubscr">New subscriber</button>
                <button type="button" class="list-group-item" id="removesubscr">Remove subsciber</button>
                <button type="button" class="list-group-item" id="suspendsubscr">Suspend/resume subsciber</button>
            </div>
        </div>

        <article>
            <div class="col-md-3" id="forms_col">
                <section id="getsubscrview" hidden>
                    <h3>Get subscriber</h3>
                    <form method="GET" id="get_subscr_form" action="">
                        <label for="email">Enter email of subscriber</label>
                        <div class="input-group">
                            <input type="email" id="email" required class="form-control"/>
                            <span class="input-group-btn">
                                <input type="submit" value="Fetch" class="btn btn-default"/>
                            </span>
                        </div>
                    </form>
                </section>
                <section id="editsubscrview" hidden>
                    <h3>Edit subscriber</h3>
                    <form method="POST" id="edit_subscr_form" action="">
                        <div class="input-group">
                            <label for="editemail">Email</label>
                            <p><input type="email" id="editemail" readonly class="form-control"/><p/>
                            <label for="editname">Name</label>
                            <p><input type="text" id="editname" required class="form-control"/><p/>
                            <label for="editrss">RSS</label>
                            <p><input type="url" id="editrss" required class="form-control"/><p/>
                            <p><input type="submit" value="Apply" class="btn btn-default"/></p>
                        </div>
                    </form>
                </section>
                <section id="newsubscrview" hidden>
                    <h3>Add new subscriber</h3>
                    <form method="POST" id="new_subscr_form" action="">
                        <div class="input-group">
                            <label for="newemail">Email</label>
                            <p><input type="email" id="newemail" required class="form-control"/></p>
                            <label for="name">Name</label>
                            <p><input type="text" id="name" required class="form-control"/></p>
                            <label for="rss">RSS</label>
                            <p><input type="url" id="rss" required class="form-control"/></p>
                            <label for="starttime">Start date</label>
                            <p><input type="date" id="starttime" class="form-control"/></p>
                            <input type="submit" value="Create" class="btn btn-default"/>
                        </div>
                    </form>
                </section>
                <section id="removesubscrview" hidden>
                    <h3>Remove subscriber</h3>
                    <form method="GET" id="remove_subscr_form" action="">
                        <label for="removeemail">Enter email of subscriber</label>
                        <div class="input-group">
                            <input type="email" id="removeemail" required class="form-control"/>
                            <span class="input-group-btn">
                                <input type="submit" value="Remove" class="btn btn-default"/>
                            </span>
                        </div>
                    </form>
                </section>
                <section id="suspendsubscrview" hidden>
                    <h3>Suspend or resume subscriber</h3>
                    <form method="GET" id="suspend_subscr_form" action="">
                        <label for="suspendemail">Enter email of subscriber</label>
                        <div class="input-group">
                            <input type="email" id="suspendemail" required class="form-control"/>
                            <div class="input-group-btn">
                                <button type="submit" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action <span class="caret"></span></button>
                                <ul class="dropdown-menu dropdown-menu-right">
                                    <li><a href="#">Suspend</a></li>
                                    <li><a href="#">Resume</a></li>
                                </ul>
                            </div>
                        </div>
                        <!--<p><input type="submit" value="Apply"/></p>-->
                    </form>
                </section>
            </div>

            <div class="col-md-6">
                <div id="getresult" class="table-responsive">

                </div>
                <p id="errorview">Jopa</p>
            </div>
        </article>
    </div>
</div>

<!--<aside>This aside</aside>-->

<footer class="footer">
    <div class="container">
        <div class="row">
            <p align="center">Created by Eurohlam. 2016</p>
        </div>
    </div>
</footer>
</body>
</html>
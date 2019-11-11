<%--
  User: eurohlam
  Date: 2/12/17
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="_include.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <%@include file="_head.jsp"%>
</head>

<body id="page-top">
<script>
    var rootURL = 'rest/service/${username}';
    $(document).ready(function () {
        $("#run_all").click(function () {
            runPollingForUser();
        });

        //Show ajax error messages
        $(document).ajaxError(function (event, request, settings, thrownError) {
            $('#alerts_panel').html('<div class="alert alert-danger alert-dismissible" role="alert">'
                + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<strong>Error! </strong>Ajax error: code:' + request.status + ' calling url: ' + settings.url + ' method: ' + settings.type + ' ' + thrownError + '</div>');
        });
    });

    function runPollingForUser() {
        $.getJSON(rootURL, function (data) {
            $('#alerts_panel').html('<div class="alert alert-success alert-dismissible" role="alert">'
                + '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<strong>Success! </strong>Polling process has been launched: ' + data.status + '</div>');
            return true;
        });
    }
</script>

<div>
    <%@include file="_header.jsp" %>

    <div id="wrapper" class="d-flex">

        <%@include file="_aside.jsp" %>

        <main id="page-content-wrapper">
            <div class="container-fluid">
                <div class="text-center">
                    <h5>Available services:</h5>
                    <h1>Polling subscriptions</h1>
                    <hr class="star-dark"/>
                </div>
                <div class="container-fluid">
                    <div id="alerts_panel" class="row"></div>
                    <div class="alert alert-info row" role="alert">
                        All subscriptions are scheduled for polling every day at 02:00 am.
                        If you wish to poll you subscriptions right now just push the button below
                    </div>
                    <div class="text-center row">
                        <button id="run_all" type="button" class="btn btn-primary btn-xl">
                            <i class="fa fa-download mr-2"></i>
                            Poll my subscriptions immediately
                        </button>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="_footer.jsp"/>
</div>


</body>
</html>
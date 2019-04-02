/**
 * Created by eurohlam on 27/01/18.
 */

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

(function($) {

    $.fn.runGetJson = function (url, successMessage, errorMessage, alwaysRunFunction) {
        $.getJSON(url, function (data) {
        })
            .done(function () {
                showAlert('success', successMessage);
                return true;
            })
            .fail(function () {
                showAlert('error', errorMessage);
                return false;
            })
            .always( function () {
                if (alwaysRunFunction) {
                    alwaysRunFunction();
                }
            });
    }; // end of runGetJson

    $.fn.runAjax = function (url, type, json, successMessage, errorMessage, alwaysRunFunction) {
        $.ajax({
            url: url,
            contentType: 'application/json',
            type: type,
            data: json,
            dataType: 'json',
            headers: csrf_headers
        })
            .done(function () {
                showAlert('success', successMessage);
                return true;
            })
            .fail(function () {
                showAlert('error', errorMessage);
                return false;
            })
            .always( function () {
                if (alwaysRunFunction) {
                    alwaysRunFunction();
                }
            });
    }; //end  of runAjax

}) (jQuery);
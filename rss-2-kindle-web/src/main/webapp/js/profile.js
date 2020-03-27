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

function validateURL(url) {
    var regexp = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g;
    return url.match(regexp);
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

    $.fn.generatePaginationBar = function (parentUl, linkList, maxPerPage, pageNumber) {
        var numberOfPages = Math.ceil(linkList.length / maxPerPage);
        parentUl.empty();

        if (pageNumber === 1) {
            //disable previous button
            parentUl.append('<li class="page-item disabled">\
                             <span class="page-link">\
                                <span aria-hidden="true">&laquo;</span>\
                             </span>\
                         </li>');
        } else {
            parentUl.append('<li class="page-item">\
                             <a class="page-link" href="#" data-page="' + (pageNumber - 1) + '" aria-label="Previous">\
                                <span aria-hidden="true">&laquo;</span>\
                             </a>\
                         </li>');
        }
        for (i = 1; i <= numberOfPages; i++) {
            if (pageNumber === i) {
                //highlight current page button
                parentUl.append('<li class="page-item active" aria-label="page" >\
                                    <span class="page-link">'
                    + i + '<span class="sr-only">(current)</span>\
                                    </span>\
                               </li>');
            } else {
                parentUl.append('<li class="page-item">\
                                  <a class="page-link" href="#" data-page="' + i + '">' + i + '</a>\
                              </li>');
            }
        }
        if (pageNumber === numberOfPages) {
            //disable next button
            parentUl.append('<li class="page-item disabled">\
                             <span class="page-link">\
                                 <span aria-hidden="true">&raquo;</span>\
                             </span>\
                          </li>');
        } else {
            parentUl.append('<li class="page-item">\
                             <a class="page-link" href="#" data-page="' + (pageNumber + 1) + '" aria-label="Next">\
                                 <span aria-hidden="true">&raquo;</span>\
                             </a>\
                          </li>');
        }
    }; //end of generatePaginationBar

}) (jQuery);
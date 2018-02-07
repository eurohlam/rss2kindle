/**
 * Created by eurohlam on 27/01/18.
 */

var restURL = '/rss2kindle/rest/profile/';

function getSubscribersAsTable(username)
{
    var table = '<table class="table table-hover"><thead>' +
        '<tr><th>#'+username+'</th>' +
        '<th>title</th>' +
        '<th>email</th>' +
        '<th>status</th>' +
        '<th>rss</th></tr></thead><tbody>';

    $.getJSON(restURL + username, function (data) {
        userData = data;

        $.each(data.subscribers, function (i, item) {
            var tr;
            if (item.status === 'locked')
                tr = '<tr class="danger"><td>';
            /*
             else if (item.status === 'suspended')
             tr='<tr class="warning"><td>';
             */
            else
                tr = '<tr class="active"><td>';

            table = table + tr
                + i + '</td><td>'
                + item.name + '</td><td>'
                + item.email + '</td><td>'
                + item.status + '</td><td>';
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
        });
        console.log(table);
    });
    table = table + '</tbody></table>';
    console.log("Final table: " + table);
    return table;
}
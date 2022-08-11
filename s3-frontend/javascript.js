$(document).ready(function () {
    console.log("ready!");

    var banner = $("#banner-message");
    var button = $("#submit_button");
    var resultsTable = $("#results table tbody");


    const interval = setInterval(function () {
        console.log("Getting cluster state")
        getClusterState()
    }, 1000);


    button.on("click", function () {
        banner.addClass("alt");
        getClusterState()
    });


    function getClusterState() {
        $.ajax({
            method: "GET",
            contentType: "application/json",
            url: "/cluster/summary",
            dataType: "json",
            success: onHttpResponse,
            error: onNotFound
        });
    }

    function onHttpResponse(data, status) {
        if (status === "success") {
            console.log(data);
            addResults(data)
        } else {
            alert("Error connecting to the server " + status);
        }
    }

    function onNotFound(data, status) {
        resultsTable.children('tr:not(:first)').remove();
        resultsTable.append("<tr>" +
            "<td id='aa'>" + "LEADER" + "</td>" +
            "<td>" + "UNAVAILABLE" + "</td>" +
            "<td>" + "REELECTION" + "</td>" +
            "<td>" + "IN" + "</td>" +
            "<td>" + "PROGRESS" + "</td>" +
            "</tr>");

    }

    function addResults(data) {

        resultsTable.children('tr:not(:first)').remove();

        data.forEach(data => {
            row = data
            var id = row.leaderElectionZnode;
            var followingId = row.predecessorLeaderElectionZnode;
            var status = row.clusterStatus
            var address = row.address

            var button = document.createElement("button");
            button.innerText = "KILL";
            button.addEventListener("click", function () {
                $.ajax({
                    method: "POST",
                    contentType: "application/json",
                    url: "http://localhost:8080/kill?address=" + address,
                    dataType: "json",
                });
            })

            resultsTable.append("<tr>" +
                "<td id='aa'>" + id + "</td>" +
                "<td>" + status + "</td>" +
                "<td>" + followingId + "</td>" +
                "<td>" + address + "</td>" +
                "<td id=" + id + "></td>" +
                "</tr>");

            $("#" + id).append(button)

        })

    }
});

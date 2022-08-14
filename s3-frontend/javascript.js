const GET_GIF_URL = "https://x7t7f93zpk.execute-api.us-east-1.amazonaws.com/test1/s3?key=image-processing-app-destination/"


async function updateGIF(res, gifType) {
    //should prent res
    console.log("res " + res)
    const blob = await res.blob()

    const div = document.getElementById(gifType);
    const img = div.getElementsByTagName('img')[0];
    const button = div.getElementsByTagName('button')[0];


    //update img url
    const url = URL.createObjectURL(blob)
    img.src = url


    updateDownloadButton(url, button);


}


function updateDownloadButton(url, button) {
    //for downloading gif on click
    let a = document.createElement('a');
    a.download = 'MyGif';
    a.href = url
    a.dataset.downloadurl = ['image/gif', a.download, a.href].join(':');
    button.innerText = "Download"


    //clone and replace, to delete old event listeners on button
    var buttonClone = button.cloneNode(true);
    buttonClone.addEventListener("click", function () {
        a.click()
    })
    button.replaceWith(buttonClone);
}

function updateDownloadButtonOnStartUp(gifType, url) {
    var div = document.getElementById(gifType);
    var button = div.getElementsByTagName('button')[0];
    updateDownloadButton(url, button)
}


function startShortPollingFor(id, gifType, interval) {
    let objectKey = gifType + id;
    //should print objtect url
    console.log("fetching: " + objectKey)
    fetchGif(objectKey)
        .then(response => {
            if (response.status == "200") {
                console.log("Stopping short polling on 200")
                clearInterval(interval)
                updateGIF(response, gifType)
            } else if (response.status == "404") {
                console.log("404, result not yet available")
            }
        })
        .catch(
            error => {
                console.log("Error getting GIF ->")
                console.log(error)
            }
        )
}

function startShortPollingForGif(id) {
    console.log("Initializing short polling for " + id)

    const interval = setInterval(function () {
        startShortPollingFor(id, "resized-", interval);
    }, 2000);

    const interval2 = setInterval(function () {
        startShortPollingFor(id, "resized2-", interval2);
    }, 2000);

    const interval3 = setInterval(function () {
        startShortPollingFor(id, "resized3-", interval3);
    }, 2000);
}


function fetchGif(id) {
    console.log("executing get for url: " + id)
    return fetch(GET_GIF_URL + id)
}



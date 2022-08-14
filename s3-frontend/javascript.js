const GET_GIF_URL = "https://x7t7f93zpk.execute-api.us-east-1.amazonaws.com/test1/s3?key=image-processing-app-destination/"

async function settingGif(id, buttonId, imgId) {
    const res = await fetchGif(id)
    console.log(res)
    const blob = await res.blob()
    const img =  document.getElementById(imgId)
    img.src = URL.createObjectURL(blob)

    img.width = 600
    img.heigh = 600

    var button = setDownloadButton(blob, buttonId)

    //document.getElementById(buttonId).replaceWith(button)
    //document.getElementById(imgId).replaceWith(img)

}


function startShortPollingFor(id, interval, buttonId, imgId) {
    fetchGif(id)
        .then(response => {
            if (response.status == "200") {
                console.log("Stopping short polling on 200")
                clearInterval(interval)
                settingGif(id, buttonId, imgId)
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
        console.log("fetching")
        startShortPollingFor("resized-" + id, interval, "Button", "GifImg");
    }, 2000);

    const interval2 = setInterval(function () {
        console.log("fetching")
        startShortPollingFor("resized-" + id, interval2, "Button2", "GifImg2");
    }, 2000);

    const interval3 = setInterval(function () {
        console.log("fetching")
        startShortPollingFor("resized-" + id, interval3, "Button3", "GifImg3");
    }, 2000);
}


function fetchGif(id) {
    return fetch(GET_GIF_URL + id)
}

function setDownloadButton(blob, buttonId) {
    let a = document.createElement('a');
    a.download = 'myGif';
    a.href = window.URL.createObjectURL(blob);
    a.dataset.downloadurl = ['image/gif', a.download, a.href].join(':');

    let button = document.getElementById(buttonId)
    button.innerText = "Download gif"

    button.addEventListener("click", function () {
        a.click()
    })

    return button

}


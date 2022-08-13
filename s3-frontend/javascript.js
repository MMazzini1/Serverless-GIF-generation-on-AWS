const GET_GIF_URL = "https://x7t7f93zpk.execute-api.us-east-1.amazonaws.com/test1/s3?key=image-processing-app-destination/"

async function settingGif(id) {
    const res = await fetchGif(id)
    console.log(res)
    const blob = await res.blob()
    const img = new Image()
    img.id = "GifImg"
    img.src = URL.createObjectURL(blob)

    img.width = 600
    img.heigh = 600

    var button = createDownloadButton(blob)

    document.getElementById("download").appendChild(button)
    document.getElementById("img").prepend(img)
}

async function replacingGif(id) {
    const res = await fetchGif(id)
    console.log(res)
    const blob = await res.blob()
    const img = new Image()
    img.id = "GifImg"
    img.src = URL.createObjectURL(blob)

    img.width = 600
    img.heigh = 600

    var button = createDownloadButton(blob)

    document.getElementById("Button").replaceWith(button)
    document.getElementById("GifImg").replaceWith(img)

}


function startShortPollingForGif(id) {
    console.log("Initializing short polling for " + id)
    const interval = setInterval(function () {
        console.log("fetching")
        fetchGif(id)
            .then(response => {
                if (response.status == "200"){
                    console.log("Stopping short polling on 200")
                    clearInterval(interval)
                    replacingGif(id)
                }else if (response.status == "404"){
                    console.log("404, result not yet available")
                }
            })
            .catch(
                error => {
                    console.log("Error getting GIF ->")
                    console.log(error)
                }
            )

    }, 2000);
}


function fetchGif(id) {
    return fetch(GET_GIF_URL + id)
}

function createDownloadButton(blob) {
    let a = document.createElement('a');
    a.download = 'myGif';
    a.href = window.URL.createObjectURL(blob);
    a.dataset.downloadurl = ['image/gif', a.download, a.href].join(':');

    let button = document.createElement('button');
    button.id = "Button"
    button.innerText = "Download gif"

    button.addEventListener("click", function () {
        a.click()
    })

    return button

}


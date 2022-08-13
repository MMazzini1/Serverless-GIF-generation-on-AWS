
const  GET_GIF_URL = "https://x7t7f93zpk.execute-api.us-east-1.amazonaws.com/test1/s3?key=image-processing-app-uploads/"




async function settingGif(id){
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

async function replacingGif(id){
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
    console.log("Initializing short polling for " +  id)
    const interval = setInterval(function () {
        console.log("fetching")
        fetchGif(id)
            .then(response => {
                console.log("Stopping short polling")
                replacingGif(id)
                clearInterval(interval)
            })
            .catch(error => console.log("Error getting GIF"))
    }, 2000);
}



function fetchGif(id) {
    return fetch(GET_GIF_URL + id,
        {headers: {'accept': ' image/gif'}})
        .catch(error => console.log("ERROR getting S3 GIF " + error))
}

function createDownloadButton(blob){
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


function onNotFound(data, status) {
    console.log("error")
}


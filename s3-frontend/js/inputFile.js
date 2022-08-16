
function returnFileSize(number) {
    if (number < 1024) {
        return number + 'bytes';
    } else if (number >= 1024 && number < 1048576) {
        return (number / 1024).toFixed(2) + 'KB';
    } else if (number >= 1048576) {
        return (number / 1048576).toFixed(2) + 'MB';
    }
}


function setInputFileListener(inputFile) {
    inputFile.addEventListener('change', (event) => {
        const files = event.target.files;
        console.log('files', files);

        for (const file of files) {
            const name = file.name;
            const type = file.type ? file.type : 'NA';
            const size = file.size;
            const lastModified = file.lastModified;
            console.log({file, name, type, size, lastModified});

            const feedback = document.getElementById('feedback');


            console.log(type)
            var button = document.getElementById('generatebtn');
            if (size > 3 * 1024 * 1024) {
                msg = `The allowed file size is 3MB. The file you are trying to upload is of ${returnFileSize(size)}`;
                feedback.innerText = msg
                button.disabled = true;
                return;
            }

            if (type != "image/jpeg" && type != "image/png") {
                msg = `The allowed file types are jpg/png`;
                feedback.innerText = msg
                button.disabled = true;
                return;
            }

            button.disabled = false;
            feedback.innerText = "Click on GENERATE"

        }
    });
}


const handleSubmit = (event) => {
    event.preventDefault();


    formData = new FormData();
    let file = inputFile.files[0];
    formData.append("files", file);
    console.log("input file: " + file)


    console.log('files size', file.size);

    const feedback = document.getElementById("generationFeedback");


    feedback.innerText = "Uploading file to S3..."
    fetch("https://x7t7f93zpk.execute-api.us-east-1.amazonaws.com/test1/fileupload", {
        method: "post",
        body: formData,
    })
        .then(response => response.json())
        .then(json => {
            console.log(json)
            feedback.innerText = "Generating GIFs..."

            const imgs = document.getElementsByClassName("gif-img");
            for (img of imgs){
                img.src ="gifs/Spinner-1s-416px.gif"

            }


            startShortPollingForGif(json.id)
        })
        .catch(
            error => {
                console.log("Something went wrong!", error)
                feedback.innerText = "Upload failed, try again."
            })
        .finally(() => {
            formData = new FormData();
        })

};


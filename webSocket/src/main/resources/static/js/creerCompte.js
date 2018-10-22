$(document).ready(() => {
    const btnImage = $('#btnImage');
    const videoCam = $('#uneVideo');
    const profilePic = $('#uneImage');
    const vidCanvas = $('#vidCanvas');
    const btnVideo = $('#btnVideo');
    const isChrome = !!window.chrome && !!window.chrome.webstore;

    let imageCapture;
    let mediaStreamTrack;

    videoCam.hide();

    let isCapturing = false;

    btnVideo.click(() => {
        if (!isCapturing) {
            console.log('start capture');
            startCapture();
        } else {
            endCapture();
        }

        isCapturing = !isCapturing;
    });

    function startCapture(){
        toggleUi();
        const successHandler = isChrome ? chromeStartVid : mozStartVid;
        var constraints = { video: { width: 150, height: 200 } };

        navigator.mediaDevices.getUserMedia(constraints)
            .then(successHandler)
            .catch(function(err) { console.log(err.name + ": " + err.message); });
    }


    function chromeStartVid(mediaStream) {
        mediaStreamTrack = mediaStream.getVideoTracks()[0];
        imageCapture = new ImageCapture(mediaStreamTrack);
        mozStartVid(mediaStream);
    }

    function mozStartVid(mediaStream) {
        videoCam[0].srcObject = mediaStream;
        videoCam[0].onloadedmetadata = () => videoCam[0].play();
    }

    function endCapture() {
        toggleUi();
        vidCanvas[0].width =   100;
        vidCanvas[0].height =  75;

        if (isChrome) {
            imageCapture.grabFrame()
                .then(imageBitmap => {
                    afficherImage(imageBitmap, imageCapture.track)
                })
                .catch(error => console.error('grabFrame() error:', error));
        } else {
            videoCam[0].width = videoCam[0].videoWidth;
            videoCam[0].height = videoCam[0].videoHeight;

            afficherImage(videoCam[0], videoCam[0].srcObject);
        }
    }

    function afficherImage(image, stream) {
        vidCanvas[0].getContext('2d').drawImage(image, 0, 0, image.width, image.height ,0,0, 100,75 );
        stream.stop();

        image = vidCanvas[0].toDataURL("image/jpeg");
        avatar =  image;
        profilePic[0].src = image;
        $('#hidPic').val(image);
    }

    function toggleUi() {
        btnImage.prop('disabled', !isCapturing);
        btnVideo.find('img').attr('src', '/open-iconic/svg/' + (isCapturing ? 'video' : 'camera-slr') + '.svg');
        btnVideo.toggleClass("btn-default");
        btnVideo.toggleClass("btn-success");
        $('#uneVideo').toggle();
        $('#uneImage').toggle();
    }
});
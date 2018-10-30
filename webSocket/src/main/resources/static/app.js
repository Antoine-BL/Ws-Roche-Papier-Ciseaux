$(document).ready(() => {
    let wsu;
    let envoyerPub = true;
    let user = null;
    let pretAConnecter = false;

    const inputTopics = Object.freeze({
        PUBLIC: '/app/public/chat',
        PRIVATE: '/app/private/chat',
    });

    const outputTopics = Object.freeze({
        PUBLIC: '/topic/public/chat',
        PRIVATE: '/topic/private/chat',
    });

    $('#connexionMessagerie').show();
    $('#messagerie').hide();
    $('#btnConnexionMessagerie').click(connexion);
    $('#btnEnvoyer').click(send);

    $("#frmEnvoyer").on('submit', function (e) {
        e.preventDefault();
    });

    $.ajax("/api/monCompte", {
        success: (data) => {
            user = data;
            wsu = new WebSocketClient($('#tbMessage'), $('#messagerie'), user);
            pretAConnecter = true;
        },
        error: () => {
            wsu = new WebSocketClient($('#tbMessage'), $('#messagerie'));
            pretAConnecter = true;
        }
    });

    function connexion() {
        if (pretAConnecter) {
            wsu.connect('/webSocket', subscribe);
        }
    }

    function send() {
        if (envoyerPub) {
            wsu.sendTo(inputTopics.PUBLIC);
        } else {
            wsu.sendTo(inputTopics.PRIVATE);
        }
        wsu.clear();
    }

    function setConnection(connected) {
        $('#btnConnexionMessagerie').prop("disabled", connected);
        $('#btnEnvoyer').prop("disabled", !connected);
        $('#tbMessage').prop("disabled", !connected);
        $('#ddBtn').prop("disabled", !connected);
        $('#connexionMessagerie').toggle(!connected);
        $('#messagerie').toggle(connected);
    }

    function subscribe() {
        setConnection(true);
        wsu.subscribeTo(outputTopics.PUBLIC, '');
        if ($('#estAuth').val() === "true") {
            wsu.subscribeTo(outputTopics.PRIVATE, '');
        }
    }

    $("#menuDD a").click(function(){

        $("#ddBtn").text($(this).text());
        $("#ddBtn").val($(this).text());
        envoyerPub = $(this).text() == 'Public';
    });
});